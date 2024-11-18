package com.example.kingoftokyo.core.viewModels

import CardModel
import DiceModel
import PlayerModel
import android.app.GameState
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kingoftokyo.core.enums.PlayerType
import com.example.kingoftokyo.core.services.BotService
import com.example.kingoftokyo.core.services.CardService
import com.example.kingoftokyo.core.services.GameService

class MainViewModel : ViewModel() {

    val players: MutableLiveData<List<PlayerModel>> = MutableLiveData()
    private var _players = mutableListOf<PlayerModel>()
    val diceList: MutableList<DiceModel> = mutableListOf()
    val currentPlayer: MutableLiveData<PlayerModel> = MutableLiveData()
    var currentPlayerIndex: Int = 0
        set(value) {
            field = value
            currentPlayer.postValue(_players.getOrNull(value))
        }
    val cards: MutableLiveData<List<CardModel>> = MutableLiveData()
    private var _cards = mutableListOf<CardModel>()
    val selectedCard: MutableLiveData<Int?> = MutableLiveData(null)
    var isCardFirstTime: Boolean = true

    private val gameService: GameService
    private val botService: BotService
    private val cardService: CardService

     init {
        gameService = GameService();
        botService = BotService(gameService);
        cardService = CardService();
    }

    fun getCurrentPlayer(): PlayerModel {
        return _players[currentPlayerIndex]
    }

    fun startGame(selectedMonster: Int) {
        _players.clear()
        _players.addAll(
            listOf(
                PlayerModel(
                    monsterName = "Demon",
                    lifePoints = 10,
                    energyPoints = 0,
                    victoryPoints = 0,
                    isInTokyo = false,
                    cards = emptyList(),
                    playerType = if (selectedMonster == 0) PlayerType.HUMAN else PlayerType.BOT
                ),
                PlayerModel(
                    monsterName = "Dragon",
                    lifePoints = 10,
                    energyPoints = 0,
                    victoryPoints = 0,
                    isInTokyo = false,
                    cards = emptyList(),
                    playerType = if (selectedMonster == 1) PlayerType.HUMAN else PlayerType.BOT
                ),
                PlayerModel(
                    monsterName = "Lizard",
                    lifePoints = 10,
                    energyPoints = 0,
                    victoryPoints = 0,
                    isInTokyo = false,
                    cards = emptyList(),
                    playerType = if (selectedMonster == 2) PlayerType.HUMAN else PlayerType.BOT
                ),
                PlayerModel(
                    monsterName = "Robot",
                    lifePoints = 10,
                    energyPoints = 10,
                    victoryPoints = 0,
                    isInTokyo = false,
                    cards = emptyList(),
                    playerType = if (selectedMonster == 3) PlayerType.HUMAN else PlayerType.BOT
                )
            )
        )
        players.postValue(_players)
        currentPlayerIndex = selectedMonster
        currentPlayer.postValue(_players[currentPlayerIndex])
    }

    fun nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % _players.size
        currentPlayer.postValue(_players[currentPlayerIndex])
    }

    fun botTurn() {
        val currentPlayer = _players[currentPlayerIndex]
        if (currentPlayer.playerType == PlayerType.BOT) {
            botService.takeTurn(currentPlayer, _players, diceList)
        }
    }

    fun lockDice(diceIndex: Int) {
        diceList[diceIndex].isLocked = true
    }

    fun rollDice() {
        gameService.rollDice(diceList)
    }

    fun isGameOver(): Boolean {
        return _players.any { it.victoryPoints >= 20 } || _players.count { it.lifePoints > 0 } <= 1
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
        Log.i("players", players[3].energyPoints.toString())
        card?.let {
            return cardService.applyCardEffect(currentPlayer, players, card)
        }
        return false
    }
}