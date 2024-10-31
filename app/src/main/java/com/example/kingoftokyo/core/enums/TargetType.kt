package com.example.kingoftokyo.core.enums

enum class TargetType {
    SELF,              // Applies only to the current player
    ALL_PLAYERS,       // Applies to all players
    OTHER_PLAYERS,     // Applies to all players except the current player
    PLAYERS_IN_TOKYO,  // Applies only to players in Tokyo
    OPPONENT,          // Applies to a specific opponent (could be randomly selected or based on criteria)
}
