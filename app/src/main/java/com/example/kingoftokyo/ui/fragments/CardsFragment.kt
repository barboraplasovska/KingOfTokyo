package com.example.kingoftokyo.ui.fragments

import CardModel
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.enums.PlayerType
import com.example.kingoftokyo.core.services.CardService
import com.example.kingoftokyo.core.viewModels.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CardsFragment : DialogFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var card1Container: FrameLayout
    private lateinit var card2Container: FrameLayout
    private lateinit var validateButton: Button
    private lateinit var cancelButton: Button
    private var selectedCard: Int? = null

    private var cardFragment1: CardFragment? = null
    private var cardFragment2: CardFragment? = null

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
        setupCardClickListeners()

        // Initialize fragments
        cardFragment1 = CardFragment()
        cardFragment2 = CardFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.cardsFragmentCard1, cardFragment1!!)
            .replace(R.id.cardsFragmentCard2, cardFragment2!!)
            .commit()

        // Set click listeners
        cancelButton.setOnClickListener {
            dismiss()
        }
        validateButton.setOnClickListener {
            dismiss()
        }

        // Observe ViewModel to update card data
        mainViewModel.cards.observe(viewLifecycleOwner) { cards ->
            cardFragment1?.setCardData(cards[0])
            cardFragment2?.setCardData(cards[1])
        }
    }

    private fun setBackgroundCard(card: FragmentContainerView, drawable: Int) {
        card.setBackgroundResource(drawable)
    }

    private fun setupCardClickListeners() {
        card1Container.setOnClickListener {
            selectCard(1)
        }

        card2Container.setOnClickListener {
            selectCard(2)
        }
    }

    private fun selectCard(cardNumber: Int) {
        selectedCard = cardNumber
        updateCardSelection()
    }

    private fun updateCardSelection() {
        if (selectedCard == 1) {
            card1Container.setBackgroundResource(R.drawable.card_selected_background)
            card2Container.setBackgroundResource(R.drawable.card_unselected_background)
        } else if (selectedCard == 2) {
            card2Container.setBackgroundResource(R.drawable.card_selected_background)
            card1Container.setBackgroundResource(R.drawable.card_unselected_background)
        }
    }

    companion object {
        fun newInstance(): CardsFragment {
            return CardsFragment()
        }
    }
}
