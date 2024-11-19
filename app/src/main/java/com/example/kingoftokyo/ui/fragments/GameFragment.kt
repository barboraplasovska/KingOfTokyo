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
import android.widget.ImageButton
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

    private lateinit var gameFragmentTitle: TextView

    private lateinit var  demonCard: MonsterCardFragment
    private lateinit var  dragonCard: MonsterCardFragment
    private lateinit var  robotCard: MonsterCardFragment
    private lateinit var  lizardCard: MonsterCardFragment

    private lateinit var  demonFragment: FragmentContainerView
    private lateinit var dragonFragment: FragmentContainerView
    private lateinit var lizardFragment: FragmentContainerView
    private lateinit var robotFragment: FragmentContainerView

    private lateinit var monsterCards: List<FragmentContainerView>
    private lateinit var diceFragment: DiceFragment
    private lateinit var finishTurnButton: Button
    private lateinit var leaveTokyoButton: Button
    private lateinit var stayInTokyoButton: Button

    private lateinit var openCardsButton: ImageButton
    private var selectedMonster: Int = 0

    // =======================
    // On Create View
    // =======================

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
        gameFragmentTitle = view.findViewById(R.id.gameFragmentTitle)

        // Start the game
        mainViewModel.startGame(selectedMonster)

        // Initialize views
        initializeCardFragments(view)
        initializeDiceFragment()
        initializeButtons(view)

        // observe view models
        observeRound()
        observeIsGameOver()
        observeCurrentPlayer()

        // Setup buttons
        setupValidateDiceButton()
        setupFinishTurnButton()
        setupStayInTokyoButton()
        setupLeaveTokyoButton()
        setupCardsModalButton()
    }

    override fun onStart() {
        super.onStart()

        updateAllPlayers()
    }

    // =======================
    // Initialization
    // =======================

    private fun initializeCardFragments(view: View) {
        demonCard = childFragmentManager.findFragmentById(R.id.demonCard) as? MonsterCardFragment ?: return
        dragonCard = childFragmentManager.findFragmentById(R.id.dragonCard) as? MonsterCardFragment ?: return
        robotCard = childFragmentManager.findFragmentById(R.id.robotCard) as? MonsterCardFragment ?: return
        lizardCard = childFragmentManager.findFragmentById(R.id.lizardCard) as? MonsterCardFragment ?: return

        demonFragment = view.findViewById(R.id.demonCard)
        dragonFragment = view.findViewById(R.id.dragonCard)
        lizardFragment = view.findViewById(R.id.lizardCard)
        robotFragment = view.findViewById(R.id.robotCard)

        monsterCards = listOf(demonFragment, dragonFragment, lizardFragment, robotFragment)
        setBackgroundMonster(monsterCards[selectedMonster], R.drawable.monster_card_selected_background)
    }

    private fun initializeDiceFragment() {
        diceFragment = DiceFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.diceFragmentContainer, diceFragment)
            .commitNow()
    }

    private fun initializeButtons(view: View) {
        finishTurnButton = view.findViewById(R.id.finishTurnButton)
        openCardsButton = view.findViewById(R.id.openCardsButton)
        leaveTokyoButton = view.findViewById(R.id.leaveTokyoButton)
        stayInTokyoButton = view.findViewById(R.id.stayTokyoButton)
    }

    // =======================
    // Setup Functions
    // =======================

    private fun setupValidateDiceButton() {
        diceFragment.onValidateDiceClick = {
            mainViewModel.playerApplyDiceEffects(diceFragment.diceModels)

            if (mainViewModel.wasBotPlayerHit(diceFragment.diceModels)) {
                mainViewModel.botPlayerHit()
            }

            mainViewModel.playerEnterTokyo()

            updateAllPlayers()

            finishTurnButton.visibility = View.VISIBLE

            diceFragment.disableDiceAndButtons()
        }
    }

    private fun setupFinishTurnButton() {
        finishTurnButton.setOnClickListener {
            updateAllPlayers()
            mainViewModel.nextPlayer()
        }
    }

    private fun setupStayInTokyoButton() {
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
    }

    private fun setupLeaveTokyoButton() {
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

    private fun setupCardsModalButton() {
        openCardsButton.setOnClickListener {
            showCardsModal()
        }
    }

    // =======================
    // Observer Functions
    // =======================

    private fun observeRound() {
        mainViewModel.round.observe(viewLifecycleOwner) { round ->
            round?.let {
                gameFragmentTitle.text = "Round n°$round"
            }
        }
    }

    private fun observeIsGameOver() {
        mainViewModel.isGameOver.observe(viewLifecycleOwner) { isGameOver ->
            if (isGameOver) {
                showGameOverModal(mainViewModel.getWinner())
            }
        }
    }

    private fun observeCurrentPlayer() {
        mainViewModel.currentPlayer.observe(viewLifecycleOwner) { currentPlayer ->
            Log.d("GameFragment", "Current player: ${currentPlayer.monsterName}")
            updatePlayerBackground(currentPlayer)
            diceFragment.resetDice()

            if (currentPlayer.isInTokyo) {
                handleTokyoEntry(currentPlayer)
            }

            if (currentPlayer.playerType == PlayerType.BOT) {
                handleBotTurn()
            } else {
                diceFragment.setPlayerTurn()
            }

            updatePlayerCard(currentPlayer)
        }
    }

    // =======================
    // Game Logic
    // =======================

    private fun handleTokyoEntry(currentPlayer: PlayerModel) {
        mainViewModel.applyTokyoEffects()
        updatePlayerCard(currentPlayer)
    }

    private fun handleBotTurn() {
        finishTurnButton.visibility = View.GONE
        viewLifecycleOwner.lifecycleScope.launch {
            diceFragment.setBotTurn()
            botRollDice()
            delay(1000)
            botRerollDice()
            botApplyDiceEffects()
            handleBotHit()
        }
    }

    private fun botRollDice() {
        mainViewModel.botRollDice(diceFragment.diceModels)
        diceFragment.updateDice(diceFragment.diceModels)
        updateAllPlayers()
    }

    private suspend fun botRerollDice() {
        for (i in 0..2) {
            if (Random.nextBoolean()) {
                mainViewModel.botRerollDice(diceFragment.diceModels)
                diceFragment.updateDice(diceFragment.diceModels)
                updateAllPlayers()
                delay(1000)
            }
        }
    }

    private fun botApplyDiceEffects() {
        mainViewModel.botApplyDiceEffects(diceFragment.diceModels)
        updateAllPlayers()
    }

    private suspend fun handleBotHit() {
        if (mainViewModel.wasHumanPlayerHit(diceFragment.diceModels)) {
            handleHumanPlayerWasHit()
        } else {
            handleBotPlayerWasHit()
        }
    }

    private fun handleHumanPlayerWasHit() {
        leaveTokyoButton.visibility = View.VISIBLE
        leaveTokyoButton.isEnabled = true
        stayInTokyoButton.visibility = View.VISIBLE
    }

    private suspend fun handleBotPlayerWasHit() {
        if (mainViewModel.wasBotPlayerHit(diceFragment.diceModels)) {
            mainViewModel.botPlayerHit()
            mainViewModel.botEnterTokyo()
        }

        updateAllPlayers()
        delay(1000)
        mainViewModel.botBuyCards()
        updateAllPlayers()
        delay(2000)

        mainViewModel.nextPlayer()
    }

    // =======================
    // Getters
    // =======================

    private fun getMonsterFragment(monsterName: String) : FragmentContainerView {
        return when (monsterName) {
            "Demon" -> demonFragment
            "Dragon" -> dragonFragment
            "Lizard" ->  lizardFragment
            "Robot" -> robotFragment
            else -> demonFragment // will never happen
        }
    }

    private fun getMonsterCard(monsterName: String) : MonsterCardFragment {
        return when (monsterName) {
            "Demon" ->  demonCard
            "Dragon" -> dragonCard
            "Lizard" ->  lizardCard
            "Robot" -> robotCard
            else -> demonCard // will never happen
        }
    }

    private fun getMonsterImage(monsterName: String) : Int {
        return when (monsterName) {
            "Demon" -> R.drawable.demon
            "Dragon" -> R.drawable.dragon
            "Lizard" -> R.drawable.lizard
            "Robot" -> R.drawable.robot
            else -> R.drawable.demon // will never happen
        }
    }

    // =======================
    // Modals
    // =======================

    // Game over modal
    private fun showGameOverModal(player: PlayerModel) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.game_over_modal)

        val titleTextView = dialog.findViewById<TextView>(R.id.modalTitle)
        titleTextView.text = "${player.monsterName} is the King of Tokyo"

        val monsterImage: ImageView = dialog.findViewById(R.id.monsterImage)
        monsterImage.setImageResource(getMonsterImage(player.monsterName))

        val dismissButton = dialog.findViewById<Button>(R.id.dismissButton)
        dismissButton.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.action_gameFragment_to_welcomeFragment)
        }

        dialog.show()
    }

    // Card modal
    private fun showCardsModal() {
        val dialogFragment = CardsFragment.newInstance()
        dialogFragment.onDismissCallback = {
           updateAllPlayers()
        }
        dialogFragment.show(childFragmentManager, "CardsFragment")
    }

    // =======================
    // Private helper functions
    // =======================

    private fun updateAllPlayers() {
        for (player in mainViewModel.getPlayers()) {
            updatePlayerCard(player)
        }
    }

    private fun updatePlayerCard(player: PlayerModel) {
        val card: MonsterCardFragment = getMonsterCard(player.monsterName)

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

    private fun updatePlayerBackground(currentPlayer: PlayerModel?) {
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
            val monsterFragment = getMonsterFragment(currentPlayer.monsterName)
            setBackgroundMonster(monsterFragment, R.drawable.monster_current_player_background)
        }
    }

    private fun setBackgroundMonster(card: FragmentContainerView, drawable: Int) {
        card.setBackgroundResource(drawable)
    }
}
