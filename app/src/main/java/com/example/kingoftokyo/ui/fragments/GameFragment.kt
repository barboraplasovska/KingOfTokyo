package com.example.kingoftokyo.ui.fragments

import PlayerModel
import android.app.Dialog
import android.content.res.ColorStateList
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
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var monsterPickerTitle: TextView

    private lateinit var demonCardFragment: MonsterCardFragment
    private lateinit var dragonCardFragment: MonsterCardFragment
    private lateinit var robotCardFragment: MonsterCardFragment
    private lateinit var lizardCardFragment: MonsterCardFragment

    private lateinit var demon: FragmentContainerView
    private lateinit var dragon: FragmentContainerView
    private lateinit var lizard: FragmentContainerView
    private lateinit var robot: FragmentContainerView

    private lateinit var monsterCards: List<FragmentContainerView>
    private lateinit var diceFragment: DiceFragment
    private lateinit var finishTurnButton: Button
    private lateinit var leaveTokyoButton: Button
    private lateinit var stayInTokyoButton: Button
    
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

        selectedMonster = arguments?.getInt("selectedMonster") ?: 0
        mainViewModel.startGame(selectedMonster)

        monsterPickerTitle = view.findViewById(R.id.monsterPickerTitle)

        demonCardFragment = childFragmentManager.findFragmentById(R.id.demonCard) as? MonsterCardFragment ?: return
        dragonCardFragment = childFragmentManager.findFragmentById(R.id.dragonCard) as? MonsterCardFragment ?: return
        robotCardFragment = childFragmentManager.findFragmentById(R.id.robotCard) as? MonsterCardFragment ?: return
        lizardCardFragment = childFragmentManager.findFragmentById(R.id.lizardCard) as? MonsterCardFragment ?: return

        finishTurnButton = view.findViewById(R.id.finishTurnButton)
        leaveTokyoButton = view.findViewById(R.id.leaveTokyoButton)
        stayInTokyoButton = view.findViewById(R.id.stayTokyoButton)

        diceFragment = DiceFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.diceFragmentContainer, diceFragment)
            .commitNow()

        diceFragment.onValidateDiceClick = {
            mainViewModel.playerApplyDiceEffects(diceFragment.diceModels)

            if (mainViewModel.wasBotPlayerHit(diceFragment.diceModels)) {
                mainViewModel.botPlayerHit()
            }

            mainViewModel.playerEnterTokyo()

            updateAllPlayers()

            finishTurnButton.visibility = View.VISIBLE
        }


        demon = view.findViewById(R.id.demonCard)
        dragon = view.findViewById(R.id.dragonCard)
        lizard = view.findViewById(R.id.lizardCard)
        robot = view.findViewById(R.id.robotCard)

        monsterCards = listOf(demon, dragon, robot, lizard)

        setBackgroundMonster(monsterCards[selectedMonster], R.drawable.monster_card_selected_background)

        mainViewModel.round.observe(viewLifecycleOwner) { round ->
            if (round != null) {
                monsterPickerTitle.text = "Round n°${round}"
            }
        }

        mainViewModel.isGameOver.observe(viewLifecycleOwner) { isGameOver ->
            if (isGameOver) {
                showGameOverModal(mainViewModel.getWinner())
            }
        }

        mainViewModel.currentPlayer.observe(viewLifecycleOwner) { currentPlayer ->
            Log.d("GameFragment", "Current player: ${currentPlayer.monsterName}")
            updateBg(currentPlayer)
            diceFragment.resetDice()

            if (currentPlayer.isInTokyo) {
                mainViewModel.applyTokyoEffects()
                updatePlayerCard(currentPlayer)
            }

            if (currentPlayer.playerType == PlayerType.BOT) {
                finishTurnButton.visibility = View.GONE
                viewLifecycleOwner.lifecycleScope.launch {
                    diceFragment.setBotTurn()
                    // step 1: bot rolls dice
                    mainViewModel.botRollDice(diceFragment.diceModels)
                    diceFragment.updateDice(diceFragment.diceModels)
                    updateAllPlayers()
                    delay(1000)

                    // step 2: bot rerolls dice
                    for (i in 0..2) {
                        val shouldReroll = Random.nextBoolean()

                        if (shouldReroll) {
                            mainViewModel.botRerollDice(diceFragment.diceModels)
                            diceFragment.updateDice(diceFragment.diceModels)
                            updateAllPlayers()
                            delay(1000)
                        }
                    }

                    // step 3: bot applies dice effects
                    mainViewModel.botApplyDiceEffects(diceFragment.diceModels)
                    updateAllPlayers()

                    // Was player hit ?
                    if (mainViewModel.wasHumanPlayerHit(diceFragment.diceModels)) {
                        // Yes, we wait if player wants to leave tokyo
                        leaveTokyoButton.visibility = View.VISIBLE
                        leaveTokyoButton.isEnabled = true
                        stayInTokyoButton.visibility = View.VISIBLE
                    } else {
                        if (mainViewModel.wasBotPlayerHit(diceFragment.diceModels)) {
                            // Bot was hit, decide to leave
                            mainViewModel.botPlayerHit()

                            // current can enter
                            mainViewModel.botEnterTokyo()
                        }

                        // not waiting
                        updateAllPlayers()
                        delay(1000)

                        // FIXME: cards shit
                        mainViewModel.botBuyCards()

                        updateAllPlayers()
                        delay(2000)

                        mainViewModel.nextPlayer()

                    }
                }
            } else {
                diceFragment.setPlayerTurn()
            }

            updatePlayerCard(currentPlayer)
        }

        finishTurnButton.setOnClickListener {
            updateAllPlayers()
            mainViewModel.nextPlayer()
        }

        stayInTokyoButton.setOnClickListener {
            // (It was bots turn, I stay in tokyo, so bot continues turn)
            leaveTokyoButton.visibility = View.GONE
            stayInTokyoButton.visibility = View.GONE

            viewLifecycleOwner.lifecycleScope.launch {
                // FIXME: cards shit
                mainViewModel.botBuyCards()
                updateAllPlayers()
                delay(2000)

                mainViewModel.nextPlayer()
            }
        }

        leaveTokyoButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                mainViewModel.humanLeaveTokyo()

                mainViewModel.botEnterTokyo()

                leaveTokyoButton.visibility = View.GONE
                stayInTokyoButton.visibility = View.GONE

                updateAllPlayers()
                delay(1000)

                // FIXME: cards shit
                mainViewModel.botBuyCards()
                updateAllPlayers()
                delay(2000)

                mainViewModel.nextPlayer()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        updateAllPlayers()
    }

    private fun updateAllPlayers() {
        for (player in mainViewModel.getPlayers()) {
            updatePlayerCard(player)
        }
    }

    private fun updatePlayerCard(player: PlayerModel) {
        var card: MonsterCardFragment = demonCardFragment
        var fragment: FragmentContainerView = demon
        when (player.monsterName) {
            "Demon" -> {
                card = demonCardFragment
                fragment = demon
            }
            "Dragon" -> {
                card = dragonCardFragment
                fragment = dragon
            }
            "Lizard" -> {
                card = lizardCardFragment
                fragment = lizard
            }
            "Robot" -> {
                card = robotCardFragment
                fragment = robot
            }
        }

        card.setMonsterData(player)

        if (player.isInTokyo) {
            card.inTokyo()
        } else {
            card.notInTokyo()
        }

        if (player.isDead()) {
            card.isDead()
        }
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
