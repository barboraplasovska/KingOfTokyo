package com.example.kingoftokyo.core.services

import CardModel
import DiceModel
import PlayerModel
import com.example.kingoftokyo.core.enums.DiceFace

class GameService {

    fun rollDice(diceList: List<DiceModel>) {
        diceList.forEach { dice ->
            if (!dice.isLocked) {
                dice.roll()
            }
        }
    }

    fun applyCardEffect(card: CardModel, currentPlayer: PlayerModel, playerList: List<PlayerModel>, gameService: GameService) {
        when (card.targetType) {
            TargetType.SELF -> {
                applyEffect(card, currentPlayer, playerList, gameService)
            }
            TargetType.ALL_PLAYERS -> {
                playerList.forEach { applyEffect(card, it, playerList, gameService) }
            }
            TargetType.OTHER_PLAYERS -> {
                playerList.filter { it != currentPlayer }.forEach { applyEffect(card, it, playerList, gameService) }
            }
            TargetType.PLAYERS_IN_TOKYO -> {
                playerList.filter { it.isInTokyo }.forEach { applyEffect(card, it, playerList, gameService) }
            }
            TargetType.OPPONENT -> {
                val opponent = playerList.firstOrNull { it != currentPlayer && !it.isInTokyo }
                opponent?.let { applyEffect(card, it, playerList, gameService) }
            }
        }
    }

    fun applyEffect(card: CardModel, player: PlayerModel, playerList: List<PlayerModel>) {
        when (card.effectType) {
            LIFE_POINTS -> {
                applyHeartEffect(player, card.effectAmount)
            }
            ENERGY_POINTS -> {
                .applyLightningEffect(player, card.effectAmount)
            }
            VICTORY_POINTS -> {
                gameService.applyVictoryEffects(player, card.effectAmount)
            }
            else -> {
                // Do nothing
                break
            }
        }
    }


    fun applyDiceEffects(currentPlayer: PlayerModel, playerList: List<PlayerModel>, diceList: List<DiceModel>) {
        val victoryDices = MutableList(3) { 0 }
        diceList.forEach { dice ->
            when (dice.face) {
                DiceFace.CLAW -> applyClawEffect(currentPlayer, playerList)
                DiceFace.HEART -> applyHeartEffect(currentPlayer)
                DiceFace.LIGHTNING -> applyLightningEffect(currentPlayer)
                DiceFace.ONE -> victoryDices[0] += 1
                DiceFace.TWO -> victoryDices[1] += 1
                DiceFace.THREE -> victoryDices[2] += 1
            }
        }
        applyVictoryEffects(currentPlayer, victoryDices)
    }


   private fun applyClawEffect(currentPlayer: PlayerModel, playerList: List<PlayerModel>, points: Int = 1) {
       val isInTokyo = currentPlayer.isInTokyo
       for (player in playerList) {
            if (player.monsterName == currentPlayer.monsterName)
                continue
           if ((isInTokyo && !player.isInTokyo) || (!isInTokyo && player.isInTokyo)) {
               player.takeDamage(points)
           }
       }
   }

    private fun applyHeartEffect(currentPlayer: PlayerModel, points: Int = 1) {
        if (!currentPlayer.isInTokyo) {
            currentPlayer.heal(points)
        }
    }

    private fun applyLightningEffect(currentPlayer: PlayerModel, points: Int = 1) {
        currentPlayer.gainEnergy(points)
    }

     fun applyVictoryEffects(currentPlayer: PlayerModel, dices: MutableList<Int>) {
        var totalPoints : Int = 0
        for (i in 0..dices.size) {
            var nbDices = dices[i]
            if (nbDices >= 3) {
                totalPoints += (i + 1)
                nbDices -= 3
                totalPoints += nbDices
            }
        }
        currentPlayer.gainVictoryPoints(totalPoints)
    }
}
