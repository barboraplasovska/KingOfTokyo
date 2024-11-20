package com.example.kingoftokyo.core.viewModels

import CardModel
import DiceModel
import PlayerModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kingoftokyo.core.enums.DiceFace
import com.example.kingoftokyo.core.enums.PlayerType
import com.example.kingoftokyo.core.services.BotService
import com.example.kingoftokyo.core.services.CardService
import com.example.kingoftokyo.core.services.GameService

class MainViewModel : ViewModel() {

    // Live data
    private val _currentPlayer = MutableLiveData<PlayerModel>()
    val currentPlayer: LiveData<PlayerModel> = _currentPlayer

    private val _round = MutableLiveData<Int>()
    val round: LiveData<Int> = _round

    private val _isGameOver = MutableLiveData<Boolean>(false)
    val isGameOver: LiveData<Boolean> = _isGameOver

    val cards: MutableLiveData<List<CardModel>> = MutableLiveData()
    private var _cards = mutableListOf<CardModel>()

    // Services
    private val gameService: GameService = GameService()
    private val cardService: CardService = CardService();
    private val botService: BotService = BotService(gameService, cardService)

    // Attributes
    private val _players = mutableListOf<PlayerModel>()
    private var currentPlayerIndex: Int = 0
    private var _haveAppliedTokyoEffects = false
    private var _firstPlayer = 0

    val selectedCard: MutableLiveData<Int?> = MutableLiveData(null)
    var isCardFirstTime: Boolean = true

    // =======================
    // Initialization & Setup Functions
    // =======================

    fun startGame(selectedMonster: Int) {
        _players.clear()
        _players.addAll(
            listOf(
                PlayerModel(monsterName = "Demon", lifePoints = 10, energyPoints = 0, victoryPoints = 0, isInTokyo = false, cards = emptyList(), playerType = if (selectedMonster == 0) PlayerType.HUMAN else PlayerType.BOT),
                PlayerModel(monsterName = "Dragon", lifePoints = 10, energyPoints = 0, victoryPoints = 0, isInTokyo = false, cards = emptyList(), playerType = if (selectedMonster == 1) PlayerType.HUMAN else PlayerType.BOT),
                PlayerModel(monsterName = "Lizard", lifePoints = 10, energyPoints = 0, victoryPoints = 0, isInTokyo = false, cards = emptyList(), playerType = if (selectedMonster == 2) PlayerType.HUMAN else PlayerType.BOT),
                PlayerModel(monsterName = "Robot", lifePoints = 10, energyPoints = 0, victoryPoints = 0, isInTokyo = false, cards = emptyList(), playerType = if (selectedMonster == 3) PlayerType.HUMAN else PlayerType.BOT),
            )
        )
        currentPlayerIndex = selectedMonster
        _firstPlayer = selectedMonster
        updateCurrentPlayer()
        startCards()
    }

    fun startCards() {
        if (isCardFirstTime) {
            val temporaryCards = cardService.getCards().shuffled().take(2)
            _cards.clear()
            _cards.addAll(temporaryCards)
            cards.postValue(_cards)
            isCardFirstTime = false
        }
    }

    // =======================
    // Getters
    // =======================

    fun getHumanPlayer() : PlayerModel {
        return _players.first { it.playerType == PlayerType.HUMAN }
    }

    fun getTokyoPlayer() : PlayerModel? {
        return try {
            _players.first { it.isInTokyo }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    fun getWinner(): PlayerModel {
        return _players.first { it.victoryPoints >= 20 || it.lifePoints > 0 }
    }

    fun getPlayers(): List<PlayerModel> {
        return _players
    }

    fun getPlayerType() : PlayerType? {
        return _currentPlayer.value?.playerType
    }

    // =======================
    // Game State & Status Check Functions
    // =======================

    fun isGameOver(): Boolean {
        return _players.any { it.victoryPoints >= 20 } || _players.count { it.lifePoints > 0 } <= 1
    }

    fun wasHumanPlayerHit(diceList: List<DiceModel>) : Boolean {
        return diceList.any { it.face == DiceFace.CLAW } && getHumanPlayer().isInTokyo
    }

    fun wasBotPlayerHit(diceList: List<DiceModel>) : Boolean {
        val tokyoPlayer: PlayerModel = getTokyoPlayer() ?: return false
        return diceList.any { it.face == DiceFace.CLAW } && tokyoPlayer.playerType == PlayerType.BOT
    }

    // =======================
    // Game Actions
    // =======================

    fun nextPlayer() {
        if (isGameOver()) {
            Log.d("MainViewModel", "Game over")
            _isGameOver.postValue(true)
            return
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % _players.size

        if (currentPlayerIndex == _firstPlayer) {
            val r = _round.value ?: 0
            _round.postValue(r + 1)
        }

        while (_players[currentPlayerIndex].isDead()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % _players.size // if my next player is dead skip him
        }

        Log.d("MainViewModel", "round: ${_round.value}, nextPlayer is ${_players[currentPlayerIndex].monsterName}")

        _haveAppliedTokyoEffects = false
        updateCurrentPlayer()
    }

    fun applyTokyoEffects() {
        if (!_haveAppliedTokyoEffects) {
            val currentPlayer = _players[currentPlayerIndex]
            gameService.applyTokyoEffects(currentPlayer)
            _haveAppliedTokyoEffects = true
        }
    }

    // =======================
    // Player Actions
    // =======================

    // Handle the player's turn
    fun playerApplyDiceEffects(diceList: List<DiceModel>) {
        val currentPlayer = _players[currentPlayerIndex]
        gameService.applyDiceEffects(currentPlayer, _players, diceList)
    }

    fun playerEnterTokyo() {
        val currentPlayer = _players[currentPlayerIndex]
        if (_players.none { it.isInTokyo }) {
            Log.d("MainViewModel", "Player ${currentPlayer.monsterName} entered Tokyo")
            currentPlayer.isInTokyo = true
            currentPlayer.victoryPoints += 1
        }
    }

    fun humanLeaveTokyo() {
        val humanPlayer = getHumanPlayer()
        if (humanPlayer.isInTokyo) {
            humanPlayer.isInTokyo = false
        }
    }

    // =======================
    // Bot Actions
    // =======================

    fun botRollDice(diceList: List<DiceModel>) {
        botService.rollDice(diceList)
    }

    fun botRerollDice(diceList: List<DiceModel>) {
        botService.decideReroll(diceList, _players[currentPlayerIndex])
    }

    fun botApplyDiceEffects(diceList: List<DiceModel>) {
        val currentPlayer = _players[currentPlayerIndex]
        botService.applyDiceEffect(currentPlayer, _players, diceList)
    }

    fun botPlayerHit() {
        // tokyo player can leave if he wants
        val tokyoPlayer = getTokyoPlayer()
        if (tokyoPlayer != null) {
            botService.decideLeaveTokyo(tokyoPlayer, _players)
        }
    }

    fun botEnterTokyo() {
        val currentPlayer = _players[currentPlayerIndex]
        botService.enterTokyo(currentPlayer, _players)
    }

    fun botBuyCards(): CardModel? {
        val currentPlayer = _players[currentPlayerIndex]
        val players = _players
        val cards = _cards
        if (botService.buyCards(currentPlayer, players, cards)) {
            val bestAffordableCard = botService.bestAffordableCard(currentPlayer, cards)
            resetCards()
            return bestAffordableCard
        }

        return null
    }

    // =======================
    // Private helper functions
    // =======================

    // Helper to update the currentPlayer LiveData
    private fun updateCurrentPlayer() {
        val newPlayer = _players[currentPlayerIndex].deepCopy()
        _currentPlayer.postValue(newPlayer)
    }

    fun resetCards() {
        val newCards = cardService.getCards().shuffled().take(2)
        _cards.clear()
        _cards.addAll(newCards)
        cards.postValue(_cards)
    }

    fun applyCardEffect(selectedCard: Int): Boolean {
        val card = _cards.getOrNull(selectedCard)
        val currentPlayer = _players[currentPlayerIndex]
        val players = _players
        card?.let {
            return cardService.applyCardEffect(currentPlayer, players, card)
        }
        return false
    }
}
