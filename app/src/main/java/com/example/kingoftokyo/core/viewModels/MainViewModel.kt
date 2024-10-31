package com.example.kingoftokyo.core.viewModels

import DiceModel
import PlayerModel
import com.example.kingoftokyo.core.enums.PlayerType
import com.example.kingoftokyo.core.services.BotService
import com.example.kingoftokyo.core.services.GameService

class MainViewModel(
        var gameService: GameService,
        var botService: BotService,
        var players: MutableList<PlayerModel>,
        var diceList: MutableList<DiceModel>
    ) {

    var currentPlayerIndex: Int = 0

    fun startGame() {
        currentPlayerIndex = 0
    }

    fun botTurn() {
        for (i in 1..players.size) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size

            var currentPlayer = players[currentPlayerIndex]
            if (currentPlayer.playerType == PlayerType.BOT) {
                botService.takeTurn(currentPlayer, players, diceList)
            }

            if (isGameOver())
                break
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
        // Player has over 20 victory points => he wins
        if (players.any { it.victoryPoints >= 20 }) {
            return true
        }

        // Only one player has life points > 0 => he wins
        val alivePlayers = players.count { it.lifePoints > 0 }
        return alivePlayers <= 1
    }


}