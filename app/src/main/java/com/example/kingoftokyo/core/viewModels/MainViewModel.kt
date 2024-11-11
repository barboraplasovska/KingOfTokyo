package com.example.kingoftokyo.core.viewModels

import DiceModel
import PlayerModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kingoftokyo.core.enums.PlayerType
import com.example.kingoftokyo.core.services.BotService
import com.example.kingoftokyo.core.services.GameService

class MainViewModel : ViewModel() {

    val players: MutableLiveData<List<PlayerModel>> = MutableLiveData()
    private var _players = mutableListOf<PlayerModel>()
    val diceList: MutableList<DiceModel> = mutableListOf()
    var currentPlayerIndex: Int = 0

    private val gameService: GameService
    private val botService: BotService

     init {
        gameService = GameService()
        botService = BotService(gameService)
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
                    lifePoints = 0,
                    energyPoints = 0,
                    victoryPoints = 0,
                    isInTokyo = false,
                    cards = emptyList(),
                    playerType = if (selectedMonster == 3) PlayerType.HUMAN else PlayerType.BOT
                )
            )
        )
        players.postValue(_players)
    }

    fun botTurn() {
        for (i in 1.._players.size) {
            currentPlayerIndex = (currentPlayerIndex + 1) % _players.size
            val currentPlayer = _players[currentPlayerIndex]
            if (currentPlayer.playerType == PlayerType.BOT) {
                botService.takeTurn(currentPlayer, _players, diceList)
            }

            if (isGameOver()) break
        }
        currentPlayerIndex = 0
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


}