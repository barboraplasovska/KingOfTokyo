package com.example.kingoftokyo.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.viewModels.MainViewModel
import com.example.kingoftokyo.core.services.GameService
import com.example.kingoftokyo.core.services.BotService
import com.example.kingoftokyo.core.enums.PlayerType
import com.example.kingoftokyo.ui.fragments.MonsterCardFragment

class GameFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("GameFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_game, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize game with selected monster index
        val selectedMonster = arguments?.getInt("selectedMonster") ?: 0
        mainViewModel.startGame(selectedMonster)

        // Observe changes in player data and update the UI accordingly
        mainViewModel.players.observe(viewLifecycleOwner) { players ->
            val demonPlayer = players[0]
            val dragonPlayer = players[1]
            val lizardPlayer = players[2]
            val robotPlayer = players[3]

            // Find each MonsterCardFragment and pass the respective PlayerModel
            (childFragmentManager.findFragmentById(R.id.demonCard) as? MonsterCardFragment)?.setMonsterData(demonPlayer)
            (childFragmentManager.findFragmentById(R.id.dragonCard) as? MonsterCardFragment)?.setMonsterData(dragonPlayer)
            (childFragmentManager.findFragmentById(R.id.robotCard) as? MonsterCardFragment)?.setMonsterData(robotPlayer)
            (childFragmentManager.findFragmentById(R.id.lizardCard) as? MonsterCardFragment)?.setMonsterData(lizardPlayer)
        }
    }
}
