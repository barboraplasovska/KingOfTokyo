package com.example.kingoftokyo.core.services

import CardModel
import com.example.kingoftokyo.core.enums.EffectType
import com.example.kingoftokyo.core.enums.TargetType

class CardService {
   val cards: List<CardModel> = listOf(
      CardModel(
         name = "Alien Metabolism",
         price = 3,
         effectType = EffectType.LIFE_POINTS,
         effectAmount = 2,
         description = "Recover 2 life points.",
         targetType = TargetType.SELF
      ),
      CardModel(
         name = "Complete Destruction",
         price = 5,
         effectType = EffectType.LIFE_POINTS,
         effectAmount = -1,  // Damage dealt
         description = "Deal 1 damage to all other players.",
         targetType = TargetType.OTHER_PLAYERS
      ),
      CardModel(
         name = "Evacuation Orders",
         price = 3,
         effectType = EffectType.LIFE_POINTS,
         effectAmount = -1,  // Damage dealt
         description = "Deal 1 damage to all players in Tokyo.",
         targetType = TargetType.PLAYERS_IN_TOKYO
      ),
      CardModel(
         name = "Healing Ray",
         price = 4,
         effectType = EffectType.LIFE_POINTS,
         effectAmount = 2,
         description = "Recover 2 life points.",
         targetType = TargetType.SELF
      ),
      CardModel(
         name = "Shrink Ray",
         price = 4,
         effectType = EffectType.LIFE_POINTS,
         effectAmount = -1,  // Damage dealt
         description = "Deal 1 damage to a specific opponent.",
         targetType = TargetType.OPPONENT
      )
   )

}