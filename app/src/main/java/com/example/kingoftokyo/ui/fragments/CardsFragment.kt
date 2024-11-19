package com.example.kingoftokyo.ui.fragments

import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.viewModels.MainViewModel

class CardsFragment : DialogFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var card1Container: FrameLayout
    private lateinit var card2Container: FrameLayout
    private lateinit var validateButton: Button
    private lateinit var cancelButton: Button
    private var selectedCard: Int? = null

    private var cardFragment1: CardFragment? = null
    private var cardFragment2: CardFragment? = null

    var onDismissCallback: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CardsFragment", "onViewCreated called")
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        card1Container = view.findViewById(R.id.cardsFragmentCard1)
        card2Container = view.findViewById(R.id.cardsFragmentCard2)
        validateButton = view.findViewById(R.id.cardsFragmentValidateButton)
        cancelButton = view.findViewById(R.id.cardsFragmentCancelButton)

        mainViewModel.startCards()

        // Initialize fragments
        cardFragment1 = CardFragment()
        cardFragment2 = CardFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.cardsFragmentCard1, cardFragment1!!)
            .replace(R.id.cardsFragmentCard2, cardFragment2!!)
            .commit()

        clickCancelButton()
        clickValidateButton()
        setupCardClickListeners()

        mainViewModel.cards.observe(viewLifecycleOwner) { cards ->
            cardFragment1?.setCardData(cards[0])
            cardFragment2?.setCardData(cards[1])
        }
    }

    private fun setupCardClickListeners() {
        card1Container.setOnClickListener {
            selectCard(0)
        }

        card2Container.setOnClickListener {
            selectCard(1)
        }
    }

    private fun selectCard(cardNumber: Int) {
        selectedCard = cardNumber
        updateCardSelection()
    }

    private fun updateCardSelection() {
        if (selectedCard == 0) {
            card1Container.setBackgroundResource(R.drawable.card_selected_background)
            card2Container.setBackgroundResource(0)
        } else if (selectedCard == 1) {
            card2Container.setBackgroundResource(R.drawable.card_selected_background)
            card1Container.setBackgroundResource(0)
        }
    }

    private fun displayToast(text: String = "Undefined", isRed: Boolean = false) {
        val toastLayout = layoutInflater.inflate(R.layout.custom_toast_layout, null)
        val toastText = toastLayout.findViewById<TextView>(R.id.toast_message)
        toastText.text = text
        toastText.setTextColor(if (isRed) Color.RED else Color.BLACK)
        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = toastLayout
        toast.setGravity(Gravity.TOP, 0, (Resources.getSystem().displayMetrics.heightPixels / 5))
        toast.show()
    }

    private fun clickCancelButton() {
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun clickValidateButton() {
        validateButton.setOnClickListener {
            validateCard()
        }
    }

    private fun validateCard() {
        selectedCard?.let {
            mainViewModel.selectedCard.value = it

            if (mainViewModel.applyCardEffect(it)) {
                displayToast("You used your energy!", false)
                mainViewModel.resetCards()
                dismiss()
            } else {
                displayToast("Not enough energy!", true)
            }
        } ?: run {
            displayToast("Select a card!", true)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke()
    }

    companion object {
        fun newInstance(): CardsFragment {
            return CardsFragment()
        }
    }
}
