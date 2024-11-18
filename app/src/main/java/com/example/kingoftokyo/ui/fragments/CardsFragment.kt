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
    private lateinit var card1: FragmentContainerView
    private lateinit var card2: FragmentContainerView
    private lateinit var cards: List<FragmentContainerView>
    private lateinit var validateButton: Button
    private lateinit var cancelButton: Button
    private var selectedCard: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CardsFragment", "onViewCreated called")
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize
        selectedCard = 0
        mainViewModel.startCards(selectedCard)

        card1 = view.findViewById(R.id.cardsFragmentCard1)
        card2 = view.findViewById(R.id.cardsFragmentCard2)
        cards = listOf(card1, card2)

        validateButton = view.findViewById(R.id.cardsFragmentValidateButton)
        cancelButton = view.findViewById(R.id.cardsFragmentCancelButton)

        setBackgroundCard(cards[selectedCard], R.drawable.monster_card_selected_background)

        cancelButton.setOnClickListener {
            dismiss()
        }

        validateButton.setOnClickListener {
            // TODO : validate button, cards
            dismiss()
        }

        mainViewModel.cards.observe(viewLifecycleOwner) { cards ->
            val card1 = cards[0]
            val card2 = cards[1]

            (childFragmentManager.findFragmentById(R.id.cardsFragmentCard1) as? CardFragment)?.setCardData(card1)
            (childFragmentManager.findFragmentById(R.id.cardsFragmentCard2) as? CardFragment)?.setCardData(card2)
        }
    }

    private fun setBackgroundCard(card: FragmentContainerView, drawable: Int) {
        card.setBackgroundResource(drawable)
    }

    companion object {
        fun newInstance(): CardsFragment {
            return CardsFragment()
        }
    }
}
