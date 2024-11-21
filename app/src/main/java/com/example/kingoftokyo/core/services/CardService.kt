package com.example.kingoftokyo.core.services

import CardModel
import PlayerModel

class CardService {
   private val cards: List<CardModel> = listOf(
      CardModel(
         name = "Alien Metabolism",
         price = 2,
         description = "Recover 1 life points.",
         effect = { player, _, _ -> player.heal(1) }
      ),
      CardModel(
         name = "Complete Destruction",
         price = 5,
         description = "Deal 3 damage to all other players.",
         effect = { currentPlayer, playerList, _ ->
            var list = playerList.toMutableList()
            list.remove(currentPlayer)
            list.forEach { it.takeDamage(3) }
         }
      ),
      CardModel(
         name = "Healing Ray",
         price = 4,
         description = "Recover 2 life points.",
         effect = { player, _, _ -> player.heal(2) }
      ),
      CardModel(
         name = "Solar Powered",
         price = 3,
         description = "Gain 5 energy points.",
         effect = { player, _, _ -> player.gainEnergy(5) }
      ),
      CardModel(
         name = "Poison Lava",
         price = 3,
         description = "Deal 1 damage to all other players.",
         effect = { player, playerList, _ -> playerList.filter { it != player }.forEach { it.takeDamage(1) } }
      ),
      CardModel(
         name = "Energy Hoarder",
         price = 1,
         description = "Gain 3 energy points if you’re in Tokyo.",
         effect = { player, _, _ -> if (player.isInTokyo) player.gainEnergy(3) }
      ),
      CardModel(
         name = "Regeneration Thunder",
         price = 5,
         description = "Recover 1 life point for each energy point you have.",
         effect = { player, _, _ -> player.heal(player.energyPoints) }
      ),
      CardModel(
         name = "Tactical Retreat",
         price = 2,
         description = "Leave Tokyo and recover 1 life point.",
         effect = { player, _, _ -> player.isInTokyo = false; player.heal(1) }
      ),
      CardModel(
         name = "Tokyo Meteor",
         price = 7,
         description = "Deal 5 damage to each player in Tokyo.",
         effect = { _, playerList, _ -> playerList.filter { it.isInTokyo }.forEach { it.takeDamage(5) } }
      ),
      CardModel(
         name = "Fortress",
         price = 4,
         description = "Gain 2 life points.",
         effect = { player, _, _ -> player.heal(2) }
      ),
      CardModel(
         name = "Wild Growth",
         price = 4,
         description = "Gain 2 victory points and 1 life point.",
         effect = { player, _, _ -> player.gainVictoryPoints(2); player.heal(1) }
      ),
      CardModel(
         name = "Victory Parade",
         price = 6,
         description = "Gain 1 victory points for every 2 energy points you have.",
         effect = { player, _, _ -> player.gainVictoryPoints((player.energyPoints / 2) * 1) }
      ),
      CardModel(
         name = "Tsunami",
         price = 6,
         description = "Deal 3 damage to all players.",
         effect = { player, playerList, _ -> playerList.filter { it != player }.forEach { it.takeDamage(3) } }
      )
   )

   fun applyCardEffect(currentPlayer: PlayerModel, playerList: List<PlayerModel>, card: CardModel): Boolean {
      if (currentPlayer.energyPoints >= card.price) {
         card.effect(currentPlayer, playerList, 0)
         currentPlayer.energyPoints -= card.price
         return true
      }
      return false
   }

   fun getCards(): List<CardModel> {
      return cards;
   }
}
