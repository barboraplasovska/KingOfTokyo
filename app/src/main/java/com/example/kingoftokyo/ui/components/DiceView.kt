package com.example.kingoftokyo.ui.components

import DiceModel
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.enums.DiceFace

class DiceView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val diceImage: ImageView
    private val lockOverlay: View

    init {
        inflate(context, R.layout.dice_view_layout, this)
        diceImage = findViewById(R.id.diceImage)
        lockOverlay = findViewById(R.id.lockOverlay)
    }

    fun bind(diceModel: DiceModel, onClick: () -> Unit) {
        setDiceImage(diceModel.face)
        setDiceClickListener(onClick)
        updateLockState(diceModel.isLocked)
    }

    fun setDiceImage(face: DiceFace) {
        val resId = when(face) {
            DiceFace.ONE -> R.drawable.dice_one
            DiceFace.TWO -> R.drawable.dice_two
            DiceFace.THREE -> R.drawable.dice_three
            DiceFace.CLAW -> R.drawable.dice_claw
            DiceFace.HEART -> R.drawable.dice_heart
            DiceFace.LIGHTNING -> R.drawable.dice_lightning
        }
        diceImage.setImageResource(resId)
    }

    fun updateLockState(isLocked: Boolean) {
        lockOverlay.visibility = if (isLocked) View.VISIBLE else View.GONE
    }

    private fun setDiceClickListener(onClick: () -> Unit) {
        setOnClickListener { onClick() }
    }
}