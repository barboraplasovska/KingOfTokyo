package com.example.kingoftokyo.core.services;

import DiceModel
import PlayerModel
import com.example.kingoftokyo.core.enums.DiceFace
import com.example.kingoftokyo.core.models.DiceCount
import kotlin.random.Random

class BotService(private val gameService: GameService) {

    fun rollDice(diceList: List<DiceModel>) {
        // Step 1: Roll the dice
        gameService.rollDice(diceList)
    }

    fun decideReroll(diceList: List<DiceModel>, currentPlayer: PlayerModel) {
        // Step 2: Decide if the bot wants to re-roll based on the initial roll
        val (claws, hearts, lightnings, numbers) = categorizeDice(diceList)

        diceList.forEach { dice ->
            if (!dice.isLocked) {
                if (shouldReroll(dice.face, currentPlayer, claws, hearts, lightnings, numbers)) {
                    dice.roll()
                } else {
                    dice.lock()
                }
            }
        }
    }

    private fun categorizeDice(diceList: List<DiceModel>): DiceCount {
        var claws = 0
        var hearts = 0
        var lightnings = 0
        var numbers = 0

        diceList.forEach { dice ->
            when (dice.face) {
                DiceFace.CLAW -> claws++
                DiceFace.HEART -> hearts++
                DiceFace.LIGHTNING -> lightnings++
                else -> numbers++
            }
        }

        return DiceCount(claws, hearts, lightnings, numbers)
    }

    private fun shouldReroll(
        face: DiceFace,
        currentPlayer: PlayerModel,
        claws: Int,
        hearts: Int,
        lightnings: Int,
        numbers: Int
    ): Boolean {
        // Prioritize healing if low on health and not in Tokyo
        if (currentPlayer.lifePoints < 4 && !currentPlayer.isInTokyo) {
            // If we need healing and there are no hearts, reroll non-heart faces
            if (hearts == 0) {
                return face != DiceFace.HEART
            } else {
                // Keep hearts if we're low on health
                return face == DiceFace.HEART
            }
        }

        // If in Tokyo, prioritize attacking or gaining victory points
        when (face) {
            DiceFace.CLAW -> {
                if (currentPlayer.isInTokyo) {
                    // Keep claws to attack
                    return claws > 3  // If less than 3 claws, reroll
                }
                return claws > 4
            }
            DiceFace.HEART -> {
                if (currentPlayer.isInTokyo) {
                    // In Tokyo, healing is less of a priority unless critically low on health
                    return true // cannot heal in Tokyo
                } else {
                    return currentPlayer.lifePoints > 6
                }
            }
            DiceFace.LIGHTNING -> {
                // If in Tokyo, energy is useful for card purchases
                return Random.nextBoolean()  // Energy has some randomness
            }
            else -> {
                // Randomly reroll numbers
                return Random.nextBoolean()
            }
        }
    }

    fun applyDiceEffect(botPlayer: PlayerModel, allPlayers: List<PlayerModel>, diceList: List<DiceModel>) {
        // Step 3: Apply dice effects
        gameService.applyDiceEffects(botPlayer, allPlayers, diceList)
    }

    fun decideLeaveTokyo(botPlayer: PlayerModel, allPlayers: List<PlayerModel>) {
        // Step 4: Determine if the bot stays in or leaves Tokyo (if they’re in Tokyo)
        if (botPlayer.isInTokyo && shouldLeaveTokyo(botPlayer)) {
            botPlayer.isInTokyo = false
        }
    }

    fun enterTokyo(botPlayer: PlayerModel, allPlayers: List<PlayerModel>) {
        // Step 4: Determine if the enters tokyo (if no one is there)
        if (!botPlayer.isInTokyo && shouldEnterTokyo(botPlayer, allPlayers)) {
            botPlayer.isInTokyo = true
        }
    }

    fun buyCard() {
        // Step 5: Card-buying logic
    }

    private fun shouldLeaveTokyo(botPlayer: PlayerModel): Boolean {
        // If life points are less than 2, definitely leave
        if (botPlayer.lifePoints <= 2) {
            return true
        }

        val leaveChance = when {
            botPlayer.lifePoints < 3 -> 0.4
            botPlayer.lifePoints == 3 -> 0.2
            else -> 0.1
        }

        return Math.random() < leaveChance
    }


    private fun shouldEnterTokyo(botPlayer: PlayerModel, allPlayers: List<PlayerModel>): Boolean {
        // Enter Tokyo if no one is there
        return allPlayers.none { it.isInTokyo }
    }
}
