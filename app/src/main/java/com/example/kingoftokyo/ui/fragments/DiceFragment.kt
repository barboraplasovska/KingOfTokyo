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
import androidx.fragment.app.Fragment
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.enums.DiceFace
import com.example.kingoftokyo.ui.components.DiceView

class DiceFragment : Fragment() {

    private lateinit var diceModels: List<DiceModel>
    private lateinit var rollButton: Button
    private lateinit var validateButton: Button

    private var hasRolled = false  // Flag to track if dice have been rolled

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dice_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        diceModels = List(6) { DiceModel(DiceFace.ONE, false) }

        rollButton = view.findViewById(R.id.rollButton)
        validateButton = view.findViewById(R.id.validateButton)

        setupDice(view)

        rollButton.setOnClickListener {
            if (!hasRolled) {
                // First roll, allow rolling the dice and enable locking
                rollAllDice()
                rollButton.text = "Reroll"
                validateButton.visibility = View.VISIBLE
                hasRolled = true
            } else {
                // If already rolled, re-roll the unlocked dice
                rollAllDice()
            }
        }

        validateButton.setOnClickListener {
            validateDice()
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
        if (!hasRolled) {
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

    private fun validateDice() {
        // FIXME: Logic for validating dice
    }
}
