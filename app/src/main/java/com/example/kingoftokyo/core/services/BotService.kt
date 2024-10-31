package com.example.kingoftokyo.core.services;

import DiceModel
import PlayerModel
import com.example.kingoftokyo.core.enums.DiceFace
import kotlin.random.Random

class BotService(private val gameService: GameService) {

    fun takeTurn(botPlayer: PlayerModel, allPlayers: List<PlayerModel>, diceList: MutableList<DiceModel>) {
        // Step 1: Roll the dice
        gameService.rollDice(diceList)

        // Step 2: Decide if the bot wants to re-roll based on initial roll
        decideReroll(diceList)

        // Step 3: Apply dice effects
        gameService.applyDiceEffects(botPlayer, allPlayers, diceList)

        // Step 4: Determine if the bot stays in or leaves Tokyo (if they’re in Tokyo)
        if (botPlayer.isInTokyo && shouldLeaveTokyo(botPlayer)) {
            botPlayer.isInTokyo = false
        } else if (!botPlayer.isInTokyo && shouldEnterTokyo(botPlayer, allPlayers)) {
            botPlayer.isInTokyo = true
        }

        // Step 5: Card-buying logic
    }

    private fun decideReroll(diceList: MutableList<DiceModel>) {
        // Simple logic: reroll unlocked dice if they aren’t favorable
        diceList.forEach { dice ->
            if (!dice.isLocked && shouldReroll(dice.face)) {
                dice.roll()
            }
        }
    }

    private fun shouldReroll(face: DiceFace): Boolean {
        return when (face) {
            DiceFace.ONE, DiceFace.TWO, DiceFace.THREE -> Random.nextBoolean() // 50% chance to reroll for numbers
            DiceFace.CLAW -> false // Bots often prefer attacks
            DiceFace.HEART -> false // Bots usually keep hearts if low on health
            DiceFace.LIGHTNING -> false // Keep energy
        }
    }

    private fun shouldLeaveTokyo(botPlayer: PlayerModel): Boolean {
        // Leave Tokyo if low on life points
        return botPlayer.lifePoints < 3
    }

    private fun shouldEnterTokyo(botPlayer: PlayerModel, allPlayers: List<PlayerModel>): Boolean {
        // Enter Tokyo if no one is there
        return allPlayers.none { it.isInTokyo }
    }
}
