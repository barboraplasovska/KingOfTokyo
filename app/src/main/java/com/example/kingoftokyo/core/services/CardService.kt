package com.example.kingoftokyo.core.services

import CardModel
import PlayerModel

class CardService {
   private val cards: List<CardModel> = listOf(
      CardModel(
         name = "Alien Metabolism",
         price = 3,
         description = "Recover 2 life points.",
         effect = { player, _, _ -> player.heal(2) }
      ),
      CardModel(
         name = "Complete Destruction",
         price = 5,
         description = "Deal 2 damage to all other players.",
         effect = { currentPlayer, playerList, _ ->
            var list = playerList.toMutableList()
            list.remove(currentPlayer)
            list.forEach { it.takeDamage(2) }
         }
      ),
      CardModel(
         name = "Evacuation Orders",
         price = 3,
         description = "Deal 1 damage to all players in Tokyo.",
         effect = { _, playerList, _ -> playerList.filter { it.isInTokyo }.forEach { it.takeDamage(1) } }
      ),
      CardModel(
         name = "Healing Ray",
         price = 4,
         description = "Recover 2 life points.",
         effect = { player, _, _ -> player.heal(2) }
      ),
      CardModel(
         name = "Solar Powered",
         price = 4,
         description = "Gain 2 energy points.",
         effect = { player, _, _ -> player.gainEnergy(2) }
      ),
      CardModel(
         name = "Poison Quills",
         price = 3,
         description = "Deal 1 damage to all opponents.",
         effect = { _, playerList, _ -> playerList.forEach { it.takeDamage(1) } }
      ),
      CardModel(
         name = "Energy Hoarder",
         price = 5,
         description = "Gain 3 energy points if you’re in Tokyo.",
         effect = { player, _, _ -> if (player.isInTokyo) player.gainEnergy(3) }
      ),
      CardModel(
         name = "Regeneration",
         price = 4,
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
         name = "Power Surge",
         price = 5,
         description = "Gain 2 energy points, but take 1 damage.",
         effect = { player, _, _ -> player.gainEnergy(7); player.takeDamage(1) }
      ),
      CardModel(
         name = "Meteor Shower",
         price = 7,
         description = "Deal 4 damage to each player in Tokyo.",
         effect = { _, playerList, _ -> playerList.filter { it.isInTokyo }.forEach { it.takeDamage(4) } }
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
         price = 5,
         description = "Gain 2 victory points for every 3 energy points you have.",
         effect = { player, _, _ -> player.gainVictoryPoints((player.energyPoints / 3) * 2) }
      ),
      CardModel(
         name = "Tsunami",
         price = 6,
         description = "Deal 3 damage to all players except yourself.",
         effect = { player, playerList, _ -> playerList.filter { it != player }.forEach { it.takeDamage(3) } }
      )
   )

   fun applyCardEffect(currentPlayer: PlayerModel, playerList: List<PlayerModel>, card: CardModel) {
      if (currentPlayer.energyPoints >= card.price) {
         currentPlayer.energyPoints -= card.price
         card.effect(currentPlayer, playerList, 0)
      }
   }

   fun getCards(): List<CardModel> {
      return cards;
   }
}
