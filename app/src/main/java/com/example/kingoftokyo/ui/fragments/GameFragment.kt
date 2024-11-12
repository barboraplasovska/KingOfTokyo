package com.example.kingoftokyo.ui.fragments

import PlayerModel
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.viewModels.MainViewModel
import com.example.kingoftokyo.core.services.GameService
import com.example.kingoftokyo.core.services.BotService
import com.example.kingoftokyo.core.enums.PlayerType
import com.example.kingoftokyo.ui.fragments.MonsterCardFragment

class GameFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var demon: FragmentContainerView
    private lateinit var dragon: FragmentContainerView
    private lateinit var lizard: FragmentContainerView
    private lateinit var robot: FragmentContainerView
    private lateinit var diceFragment: DiceFragment
    private lateinit var finishTurnButton: Button

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

        diceFragment = DiceFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.diceFragmentContainer, diceFragment)
            .commitNow()

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

            var currentPlayer = mainViewModel.getCurrentPlayer()
            if (currentPlayer.playerType == PlayerType.BOT) {
                diceFragment.setBotTurn()
            } else {
                diceFragment.setPlayerTurn()
            }
        }


        demon = view.findViewById(R.id.demonCard)
        dragon = view.findViewById(R.id.dragonCard)
        lizard = view.findViewById(R.id.lizardCard)
        robot = view.findViewById(R.id.robotCard)
        finishTurnButton = view.findViewById(R.id.finishTurnButton)

        when (selectedMonster) {
            0 -> setBackgroundMonster(demon, R.drawable.monster_player_selected)
            1 -> setBackgroundMonster(dragon, R.drawable.monster_player_selected)
            2 -> setBackgroundMonster(lizard, R.drawable.monster_player_selected)
            3 -> setBackgroundMonster(robot, R.drawable.monster_player_selected)
        }

        finishTurnButton.setOnClickListener {
            botTurn()
            mainViewModel.botTurn()
        }
    }

    private fun botTurn() {
        finishTurnButton.visibility = View.GONE
        diceFragment.setBotTurn()
    }

    private fun setBackgroundMonster(card: FragmentContainerView, drawable: Int) {
        card.setBackgroundResource(drawable)
    }

    private fun showGameOverModal(player: PlayerModel) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.game_over_modal)

        val titleTextView = dialog.findViewById<TextView>(R.id.modalTitle)
        titleTextView.text = "${player.monsterName} is the King of Tokyo"

        var monsterImage: ImageView = dialog.findViewById(R.id.monsterImage)
        when (player.monsterName) {
            "Demon" -> monsterImage.setImageResource(R.drawable.demon)
            "Dragon" -> monsterImage.setImageResource(R.drawable.dragon)
            "Lizard" -> monsterImage.setImageResource(R.drawable.lizard)
            "Robot" -> monsterImage.setImageResource(R.drawable.robot)
        }

        val dismissButton = dialog.findViewById<Button>(R.id.dismissButton)
        dismissButton.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.action_gameFragment_to_welcomeFragment)
        }

        dialog.show()
    }
}
