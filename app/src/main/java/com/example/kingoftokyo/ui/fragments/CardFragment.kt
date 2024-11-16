package com.example.kingoftokyo.ui.fragments

import CardModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kingoftokyo.R

class CardFragment : Fragment() {

    private lateinit var cardImage: ImageView
    private lateinit var cardPrice: TextView
    private lateinit var cardName: TextView
    private lateinit var cardDescription: TextView
    private lateinit var cardEffect: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.card_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CardFragment", "onViewCreated called")

        cardImage = view.findViewById(R.id.cardImage)
        cardPrice = view.findViewById(R.id.cardPrice)
        cardName = view.findViewById(R.id.cardName)
        cardDescription = view.findViewById(R.id.cardDescription)
        cardEffect = view.findViewById(R.id.cardEffect)
    }

    fun setCardData(card: CardModel) {
        cardImage.setImageResource(
            R.drawable.dragon
        )
        cardPrice.text = card.name.toString()
        cardName.text = card.name
        cardDescription.text = card.description
        cardEffect.text = card.effect.toString()
    }
}
