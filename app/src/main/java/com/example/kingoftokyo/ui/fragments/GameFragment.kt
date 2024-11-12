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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.viewModels.MainViewModel
import com.example.kingoftokyo.core.services.GameService
import com.example.kingoftokyo.core.services.BotService
import com.example.kingoftokyo.core.enums.PlayerType
import com.example.kingoftokyo.ui.fragments.MonsterCardFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var demon: FragmentContainerView
    private lateinit var dragon: FragmentContainerView
    private lateinit var lizard: FragmentContainerView
    private lateinit var robot: FragmentContainerView
    private lateinit var monsterCards: List<FragmentContainerView>
    private lateinit var diceFragment: DiceFragment
    private lateinit var finishTurnButton: Button
    private var selectedMonster: Int = 0

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
        selectedMonster = arguments?.getInt("selectedMonster") ?: 0
        mainViewModel.startGame(selectedMonster)

        diceFragment = DiceFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.diceFragmentContainer, diceFragment)
            .commitNow()

        demon = view.findViewById(R.id.demonCard)
        dragon = view.findViewById(R.id.dragonCard)
        lizard = view.findViewById(R.id.lizardCard)
        robot = view.findViewById(R.id.robotCard)
        monsterCards = listOf(demon, dragon, lizard, robot)
        finishTurnButton = view.findViewById(R.id.finishTurnButton)

        setBackgroundMonster(monsterCards[selectedMonster], R.drawable.monster_card_selected_background)

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

        mainViewModel.currentPlayer.observe(viewLifecycleOwner) { currentPlayer ->
            updateBg(currentPlayer)
            viewLifecycleOwner.lifecycleScope.launch {
                if (currentPlayer?.playerType == PlayerType.BOT) {
                    diceFragment.setBotTurn()
                    delay(4000)
                    mainViewModel.nextPlayer()
                } else {
                    diceFragment.setPlayerTurn()
                }
            }
        }


        finishTurnButton.setOnClickListener {
            botTurn()
            mainViewModel.nextPlayer()
            //mainViewModel.botTurn()
        }
    }

    private fun botTurn() {
        finishTurnButton.visibility = View.GONE
        diceFragment.setBotTurn()
    }

    private fun updateBg(currentPlayer: PlayerModel?) {
        if (currentPlayer == null)
            return
        if (currentPlayer.playerType == PlayerType.HUMAN) {
            setBackgroundMonster(monsterCards[selectedMonster], R.drawable.monster_player_selected)
            for (i in monsterCards.indices) {
                if (i == selectedMonster) {
                    continue
                }
                setBackgroundMonster(monsterCards[i], R.drawable.monster_card_background)
            }
        }
        else {
            setBackgroundMonster(monsterCards[selectedMonster], R.drawable.monster_card_selected_background)
            for (i in monsterCards.indices) {
                if (i == selectedMonster) {
                    continue
                }
                setBackgroundMonster(monsterCards[i], R.drawable.monster_card_background)
            }
            when (currentPlayer.monsterName) {
                "Demon" -> setBackgroundMonster(demon, R.drawable.monster_current_player_background)
                "Dragon" -> setBackgroundMonster(dragon, R.drawable.monster_current_player_background)
                "Lizard" -> setBackgroundMonster(lizard, R.drawable.monster_current_player_background)
                "Robot" -> setBackgroundMonster(robot, R.drawable.monster_current_player_background)
            }
        }
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
