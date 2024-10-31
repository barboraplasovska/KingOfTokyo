package com.example.kingoftokyo.core.services

import CardModel

class CardService {
   val cards: List<CardModel> = listOf(
      CardModel(
         name = "Alien Metabolism",
         price = 3,
         description = "Recover 2 life points.",
         effect = { player, _, _ -> player.heal(2) }
      ),
      CardModel(
         name = "Complete Destruction",
         price = 5,
         description = "Deal 1 damage to all other players.",
         effect = { currentPlayer, playerList, _ ->
            var list = playerList.toMutableList()
            list.remove(currentPlayer)
            list.forEach { it.takeDamage(1) }
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
         name = "Shrinking Ray",
         price = 5,
         description = "Deal 2 damage to one opponent.",
         effect = { _, playerList, targetIndex -> playerList[targetIndex].takeDamage(2) }
      ),
      CardModel(
         name = "Giant Brain",
         price = 6,
         description = "Gain 3 energy points.",
         effect = { player, _, _ -> player.gainEnergy(3) }
      ),
      CardModel(
         name = "Defense Shield",
         price = 4,
         description = "Negate the next attack against you.",
         effect = { player, _, _ -> player.takeDamage(0) } // Adjust this in your attack logic
      ),
      CardModel(
         name = "Poison Quills",
         price = 3,
         description = "Deal 1 damage to all opponents.",
         effect = { _, playerList, _ -> playerList.forEach { it.takeDamage(1) } }
      ),
      CardModel(
         name = "Nuclear Blast",
         price = 7,
         description = "Deal 4 damage to a player of your choice.",
         effect = { _, playerList, targetIndex -> playerList[targetIndex].takeDamage(4) }
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
         name = "Giant Smash",
         price = 6,
         description = "Deal 3 damage to a player of your choice.",
         effect = { _, playerList, targetIndex -> playerList[targetIndex].takeDamage(3) }
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
         description = "Gain 4 energy points, but take 1 damage.",
         effect = { player, _, _ -> player.gainEnergy(4); player.takeDamage(1) }
      ),
      CardModel(
         name = "Mind Control",
         price = 5,
         description = "Take control of another player’s next action.",
         effect = { _, playerList, targetIndex -> /* Control logic here */ }
      ),
      CardModel(
         name = "Meteor Shower",
         price = 7,
         description = "Deal 2 damage to each player in Tokyo.",
         effect = { _, playerList, _ -> playerList.filter { it.isInTokyo }.forEach { it.takeDamage(2) } }
      ),
      CardModel(
         name = "Fortress",
         price = 4,
         description = "Gain 1 life point for each card you have.",
         effect = { player, _, _ -> player.heal(player.cards.size) }
      ),
      CardModel(
         name = "Wild Growth",
         price = 4,
         description = "Gain 2 victory points and 1 life point.",
         effect = { player, _, _ -> player.gainVictoryPoints(2); player.heal(1) }
      ),
      CardModel(
         name = "Invisibility Cloak",
         price = 6,
         description = "Avoid damage for one turn.",
         effect = { player, _, _ -> /* Add logic to avoid damage */ }
      ),
      CardModel(
         name = "Speed Boost",
         price = 3,
         description = "Take an extra action this turn.",
         effect = { player, _, _ -> /* Logic for an extra action */ }
      ),
      CardModel(
         name = "Time Warp",
         price = 8,
         description = "Skip the next player’s turn.",
         effect = { _, playerList, targetIndex -> /* Skip logic for the next player */ }
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
      ),
      CardModel(
         name = "Electric Fence",
         price = 5,
         description = "All opponents take 1 damage when they attack you.",
         effect = { player, playerList, _ -> /* Logic to apply damage when attacked */ }
      ),
      CardModel(
         name = "Aerial Strike",
         price = 7,
         description = "Deal 5 damage to a player in Tokyo.",
         effect = { _, playerList, targetIndex -> playerList[targetIndex].takeDamage(5) }
      )
   )

}
