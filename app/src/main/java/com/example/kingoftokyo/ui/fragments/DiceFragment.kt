package com.example.kingoftokyo.ui.fragments

import DiceModel
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.enums.DiceFace
import com.example.kingoftokyo.ui.components.DiceView

class DiceFragment : Fragment() {

    lateinit var diceModels: List<DiceModel>
    private lateinit var rollButton: Button
    private lateinit var validateButton: Button
    private lateinit var buttonContainer: LinearLayout

    private var hasRolled = 0
    private var isBotTurn = false
    var onValidateDiceClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dice_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diceModels = List(6) { DiceModel(DiceFace.values().random(), false) }

        rollButton = view.findViewById(R.id.rollButton)
        validateButton = view.findViewById(R.id.validateButton)
        buttonContainer = view.findViewById(R.id.buttonContainer)

        setupDice(view)

        rollButton.setOnClickListener {
            if (hasRolled == 0) {
                // First roll, allow rolling the dice and enable locking
                rollAllDice()
                rollButton.text = "Reroll"
                validateButton.visibility = View.VISIBLE
                hasRolled++
            }
            else if (hasRolled <= 3){
                // If already rolled, re-roll the unlocked dice
                rollAllDice()
                hasRolled++
                if (hasRolled >= 3) {
                    // Can't roll the dices more than 3 times
                    rollButton.isEnabled = false
                    rollButton.alpha = 0.5f
                }
            }
        }

        validateButton.setOnClickListener {
            onValidateDiceClick?.invoke()
        }
    }

    private fun setupDice(view: View) {
        val diceViews = listOf(
            view.findViewById<DiceView>(R.id.dice1),
            view.findViewById(R.id.dice2),
            view.findViewById(R.id.dice3),
            view.findViewById(R.id.dice4),
            view.findViewById(R.id.dice5),
            view.findViewById(R.id.dice6)
        )

        diceViews.forEachIndexed { index, diceView ->
            diceView.bind(diceModels[index]) {
                onDiceClick(index)
            }
        }
    }

    private fun onDiceClick(index: Int) {
        if (hasRolled == 0 || isBotTurn) {
            // Prevent locking until the first roll
            return
        }

        val dice = diceModels[index]
        if (dice.isLocked) {
            dice.unlock()
        } else {
            dice.lock()
        }

        updateDiceView(index)
    }

    private fun updateDiceView(index: Int) {
        val diceView = view?.findViewById<DiceView>(R.id.dice1 + index)
        diceView?.updateLockState(diceModels[index].isLocked)
        diceView?.setDiceImage(diceModels[index].face)
    }

    private fun rollAllDice() {
        diceModels.filter { !it.isLocked }.forEach { it.roll() }
        diceModels.forEachIndexed { index, diceModel ->
            updateDiceView(index)
        }
    }

    fun setBotTurn() {
        buttonContainer.visibility = View.GONE
        isBotTurn = true
    }

    fun setPlayerTurn() {
        buttonContainer.visibility = View.VISIBLE
        isBotTurn = false
    }

    fun resetDice() {
        // Reset each dice model with a random face and unlocked state
        diceModels = List(6) { DiceModel(DiceFace.values().random(), false) }
        hasRolled = 0
        rollButton.isEnabled = true
        rollButton.alpha = 1.0f
        rollButton.text = "Roll"
        validateButton.visibility = View.GONE

        // Update each dice view to reflect the reset state
        diceModels.forEachIndexed { index, diceModel ->
            updateDiceView(index)
        }
    }
}
