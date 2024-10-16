package com.example.kingoftokyo.core.services

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

    fun applyDiceEffects(currentPlayer: PlayerModel, diceList: List<DiceModel>) {
        diceList.forEach { dice ->
            when (dice.face) {
                DiceFace.CLAW -> applyClawEffect(currentPlayer)
                DiceFace.HEART -> applyHeartEffect(currentPlayer)
                DiceFace.LIGHTNING -> applyLightningEffect(currentPlayer)
                DiceFace.ONE, DiceFace.TWO, DiceFace.THREE -> applyVictoryEffects(currentPlayer, dice.face)
            }
        }
    }


   private fun applyClawEffect(currentPlayer: PlayerModel) {
 // FIXME:
   }

    private fun applyHeartEffect(currentPlayer: PlayerModel) {
        // FIXME:
    }

    private fun applyLightningEffect(currentPlayer: PlayerModel) {
        // FIXME:
    }

    private fun applyVictoryEffects(currentPlayer: PlayerModel, diceFace: DiceFace) {
        // FIXME:
    }
}
