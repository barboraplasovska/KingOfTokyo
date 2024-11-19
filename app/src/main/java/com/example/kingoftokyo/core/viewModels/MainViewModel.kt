package com.example.kingoftokyo.core.viewModels

import DiceModel
import PlayerModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kingoftokyo.core.enums.PlayerType
import com.example.kingoftokyo.core.services.BotService
import com.example.kingoftokyo.core.services.GameService

class MainViewModel : ViewModel() {

    private val _players = mutableListOf<PlayerModel>()
    private var currentPlayerIndex: Int = 0

    private val _currentPlayer = MutableLiveData<PlayerModel>()
    val currentPlayer: LiveData<PlayerModel> = _currentPlayer

    private val gameService: GameService = GameService()
    private val botService: BotService = BotService(gameService)

    // Initialize the game with all players, and set the current player
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
                    energyPoints = 0,
                    victoryPoints = 0,
                    isInTokyo = false,
                    cards = emptyList(),
                    playerType = if (selectedMonster == 3) PlayerType.HUMAN else PlayerType.BOT
                )
            )
        )
        // Set the selected monster as the first current player
        currentPlayerIndex = selectedMonster
        updateCurrentPlayer()
    }

    // Helper to update the currentPlayer LiveData
    private fun updateCurrentPlayer() {
        val newPlayer = _players[currentPlayerIndex].deepCopy()
        _currentPlayer.postValue(newPlayer)
    }

    // Move to the next player's turn
    fun nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % _players.size
        updateCurrentPlayer()
    }

    // Handle the bot's turn
    fun botTurn(diceList: List<DiceModel>) {
        val currentPlayer = _players[currentPlayerIndex]
        botService.takeTurn(currentPlayer, _players, diceList.toMutableList())
    }

    // Handle the player's turn
    fun playerTurn(diceList: List<DiceModel>) {
        val currentPlayer = _players[currentPlayerIndex]
        if (currentPlayer.isInTokyo) {
            gameService.applyTokyoEffects(currentPlayer)
        }
        gameService.applyDiceEffects(currentPlayer, _players, diceList)
        enterTokyo()
        updateCurrentPlayer()
    }

    // Logic to enter Tokyo (or attempt to)
    fun enterTokyo() {
        val currentPlayer = _players[currentPlayerIndex]
        if (_players.none { it.isInTokyo }) {
            currentPlayer.isInTokyo = true
            currentPlayer.victoryPoints += 1
        }
        updateCurrentPlayer()
    }

    fun updatePlayer(victoryPoints: Int = 0) {
        val currentPlayer = _players[currentPlayerIndex]
        currentPlayer.victoryPoints += victoryPoints
        updateCurrentPlayer()
    }

    // Logic to leave Tokyo
    fun leaveTokyo() {
        val currentPlayer = _players[currentPlayerIndex]
        if (currentPlayer.isInTokyo) {
            currentPlayer.isInTokyo = false
        }
        updateCurrentPlayer()
    }

    // Logic to check if the game is over
    fun isGameOver(): Boolean {
        return _players.any { it.victoryPoints >= 20 } || _players.count { it.lifePoints > 0 } <= 1
    }

    // Return all players for initialization purposes
    fun getPlayers(): List<PlayerModel> {
        return _players
    }
}
