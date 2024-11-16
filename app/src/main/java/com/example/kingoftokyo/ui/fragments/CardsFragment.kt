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
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.services.CardService

class CardsFragment : DialogFragment() {

    private lateinit var card1View: FragmentContainerView
    private lateinit var card2View: FragmentContainerView
    private lateinit var card1: CardModel
    private lateinit var card2: CardModel
    private lateinit var validateButton: Button
    private lateinit var cancelButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Retourne le layout de CardsFragment
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize
        card1View = view.findViewById(R.id.cardsFragmentCard1)
        card2View = view.findViewById(R.id.cardsFragmentCard2)
        validateButton = view.findViewById(R.id.cardsFragmentValidateButton)
        cancelButton = view.findViewById<Button>(R.id.cardsFragmentCancelButton)

        val cardList = CardService
        val randomCards = cardList.shuffled().take(2)
        card1 = randomCards[0]
        card2 = randomCards[1]

        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(): CardsFragment {
            return CardsFragment()
        }
    }
}
