package com.example.kingoftokyo.core.viewModels

import DiceModel
import PlayerModel
import com.example.kingoftokyo.core.services.GameService

class MainViewModel(
        var service: GameService,
        var players: MutableList<PlayerModel>,
        var diceList: MutableList<DiceModel>
    ) {

    var currentPlayerIndex: Int = 0

    fun nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size  // Cycle through players
    }
}