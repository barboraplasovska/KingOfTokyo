package com.example.kingoftokyo.ui.fragments

import CardModel
import PlayerModel
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.renderscript.Script.LaunchOptions
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.enums.ModalType
import com.example.kingoftokyo.core.viewModels.MainViewModel
import com.example.kingoftokyo.core.enums.PlayerType
import com.example.kingoftokyo.core.enums.ToastType
import kotlinx.coroutines.coroutineScope
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
    private lateinit var openCardModalButton: ImageButton

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
        setupOpenCardModalButton()
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

        monsterCards = listOf(demonFragment, dragonFragment, robotFragment, lizardFragment)
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
        leaveTokyoButton = view.findViewById(R.id.leaveTokyoButton)
        stayInTokyoButton = view.findViewById(R.id.stayTokyoButton)
        openCardModalButton = view.findViewById(R.id.openCardModalButton)
    }

    // =======================
    // Setup Functions
    // =======================

    private fun setupValidateDiceButton() {
        diceFragment.onValidateDiceClick = {
            viewLifecycleOwner.lifecycleScope.launch {
                mainViewModel.playerApplyDiceEffects(diceFragment.diceModels)
                if (mainViewModel.wasBotPlayerHit(diceFragment.diceModels)) {
                    val player = mainViewModel.getHumanPlayer()
                    val loss = mainViewModel.getPlayerLoss(diceFragment.diceModels)

                    if (player.isInTokyo) {
                        displayCustomToast("Everyone except ${player.monsterName} was hit!", ToastType.PLAYER_HIT, heartNb = loss)
                    } else {
                        val name = mainViewModel.getTokyoPlayer()?.monsterName ?: "Bot"
                        displayCustomToast("${name} was hit!", ToastType.PLAYER_HIT, heartNb = loss)
                    }
                    mainViewModel.botPlayerHit()
                    delay(2000)
                }
                mainViewModel.playerEnterTokyo()

                updateAllPlayers()
                delay(1000)

                diceFragment.disableDiceAndButtons()

                showCardsModalForPlayer()

                openCardModalButton.visibility = View.VISIBLE
            }
        }
    }

    private fun setupFinishTurnButton() {
        finishTurnButton.setOnClickListener {
            updateAllPlayers()
            mainViewModel.nextPlayer()
           // mainViewModel.resetCards() // reset cards after player playing
        }
    }

    private fun setupStayInTokyoButton() {
        stayInTokyoButton.setOnClickListener {
            // (It was bots turn, I stay in tokyo, so bot continues turn)
            leaveTokyoButton.visibility = View.GONE
            stayInTokyoButton.visibility = View.GONE

            viewLifecycleOwner.lifecycleScope.launch {
                mainViewModel.currentPlayer.value?.let {
                    botBuyCards(mainViewModel.currentPlayer.value!!.monsterName)
                }

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

                mainViewModel.currentPlayer.value?.let {
                    botBuyCards(mainViewModel.currentPlayer.value!!.monsterName)
                }

                updateAllPlayers()
                delay(2000)

                mainViewModel.nextPlayer()
            }
        }
    }

    private fun setupOpenCardModalButton() {
        openCardModalButton.setOnClickListener {
            showCardsModalForPlayer()
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
                handleBotTurn(currentPlayer.monsterName)
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

    private fun handleBotTurn(monsterName: String) {
        openCardModalButton.visibility = View.GONE
        finishTurnButton.visibility = View.GONE
        viewLifecycleOwner.lifecycleScope.launch {
            diceFragment.setBotTurn()
            botRollDice()
            delay(1000)
            botRerollDice()
            botApplyDiceEffects()

            val loss = mainViewModel.getPlayerLoss(diceFragment.diceModels)

            val tokyoPlayerName = mainViewModel.getTokyoPlayer()?.monsterName
            val isHumanPlayerHit = mainViewModel.wasHumanPlayerHit(diceFragment.diceModels)
            val isBotPlayerHit = mainViewModel.wasBotPlayerHit(diceFragment.diceModels)

            if (tokyoPlayerName == monsterName) {
                displayCustomToast("Everyone except ${monsterName} was hit!", ToastType.PLAYER_HIT, heartNb = loss)
            } else {
                val name = tokyoPlayerName ?: "Bot"
                if (isHumanPlayerHit) {
                    displayCustomToast("You were hit!", ToastType.PLAYER_HIT, heartNb = loss)
                } else if (isBotPlayerHit) {
                    displayCustomToast("$name was hit!", ToastType.PLAYER_HIT, heartNb = loss)
                }
            }

            if (isHumanPlayerHit) {
                handleHumanPlayerWasHit()
            } else if (isBotPlayerHit) {
                handleBotPlayerWasHit()
            }
            botBuyCards(monsterName)
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

    private suspend fun botBuyCards(monsterName: String) {
        val affordedCard = mainViewModel.botBuyCards()
        if (affordedCard != null) {
            showCardsModalForBot(monsterName)
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
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
    private fun showCardsModalForPlayer() {
        val dialogFragment = CardsModalFragment.newInstance(ModalType.PLAYER_VIEW)
        dialogFragment.onDismissCallback = {
           updateAllPlayers()

           finishTurnButton.visibility = View.VISIBLE // he can finish his turn now
        }

        dialogFragment.onValidateCardsCallback = {
            if (mainViewModel.hasUserSelectedCard()) {
                if (mainViewModel.canUserBuyCard()) {
                    mainViewModel.playerApplyCardEffect()

                    openCardModalButton.visibility = View.GONE

                    dialogFragment.forceDismiss()

                    viewLifecycleOwner.lifecycleScope.launch {
                        mainViewModel.getSelectedCard()?.let { selectedCard ->
                            showPurchasedCardForPlayer(selectedCard)
                        }
                    }
                } else {
                    var player = mainViewModel.getHumanPlayer()
                    displayCustomToast(
                        "You can't afford this card!",
                        ToastType.PLAYER_CANT_AFFORD_CARD,
                        energyNb = player.energyPoints
                    )
                }
            } else {
                displayCustomToast(
                    "You must select a card!",
                    ToastType.WARNING,
               )
            }
        }

        dialogFragment.show(childFragmentManager, "PlayerCardModal")
    }

    private suspend fun showCardsModalForBot(monsterName: String) {
        val dialogFragment = CardsModalFragment.newInstance(ModalType.BOT_VIEW, botName = monsterName)

        dialogFragment.onDismissCallback = {
            updateAllPlayers()
           // mainViewModel.resetCards()
        }


        dialogFragment.show(childFragmentManager, "BotCardModal")
        delay(3000)
        dialogFragment.forceDismiss()
    }

    private suspend fun showPurchasedCardForPlayer(card: CardModel) {
        val dialogFragment = CardsModalFragment.newInstance().apply {
            modalType = ModalType.PLAYER_PURCHASE
            purchasedCard = card
        }

        dialogFragment.show(childFragmentManager, "PlayerPurchasedCardModal")
        delay(2000)
        dialogFragment.forceDismiss()
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

    private fun displayCustomToast(
        message: String,
        type: ToastType,
        description: String? = null,
        heartNb: Int? = null,
        energyNb: Int? = null
    ) {
        // Inflate the custom toast layout
        val toastLayout = layoutInflater.inflate(R.layout.custom_toast_layout, null)

        // Get references to the views in the layout
        val toastMessage = toastLayout.findViewById<TextView>(R.id.toast_message)
        val toastMessageWithContent = toastLayout.findViewById<TextView>(R.id.toast_message_with_content)
        val toastDescription = toastLayout.findViewById<TextView>(R.id.toast_description)

        val heartLayout = toastLayout.findViewById<LinearLayout>(R.id.heart_layout)
        val energyLayout = toastLayout.findViewById<LinearLayout>(R.id.energy_layout)

        val heartText = toastLayout.findViewById<TextView>(R.id.heart_text)
        val energyText = toastLayout.findViewById<TextView>(R.id.energy_text)

        // Hide all views initially
        toastMessage.visibility = View.GONE
        toastMessageWithContent.visibility = View.GONE
        toastDescription.visibility = View.GONE
        heartLayout.visibility = View.GONE
        energyLayout.visibility = View.GONE

        // Handle the different ToastTypes
        when (type) {
            ToastType.MESSAGE -> {
                toastMessage.text = message
                toastMessage.visibility = View.VISIBLE
            }
            ToastType.WARNING -> {
                toastMessage.text = message
                toastMessage.setTextColor(Color.RED)
                toastMessage.visibility = View.VISIBLE
            }
            ToastType.MESSAGE_DESCRIPTION -> {
                toastMessageWithContent.text = message
                toastMessageWithContent.visibility = View.VISIBLE

                description?.let {
                    toastDescription.text = it
                    toastDescription.visibility = View.VISIBLE
                }
            }
            ToastType.PLAYER_HIT -> {
                toastMessageWithContent.text = message
                toastMessageWithContent.visibility = View.VISIBLE

                heartNb?.let {
                    heartText.text = "-$it"
                    heartLayout.visibility = View.VISIBLE
                }
            }
            ToastType.PLAYER_CANT_AFFORD_CARD -> {
                toastMessageWithContent.text = message
                toastMessageWithContent.visibility = View.VISIBLE
                toastMessageWithContent.setTextColor(Color.RED)

                energyNb?.let {
                    energyText.text = "You have $it"
                    energyLayout.visibility = View.VISIBLE
                }
            }
        }

        // Create and show the toast
        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = toastLayout
        toast.setGravity(Gravity.TOP, 0, (Resources.getSystem().displayMetrics.heightPixels / 5))
        toast.show()
    }

}

