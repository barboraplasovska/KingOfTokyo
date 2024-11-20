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

    // =======================
    // On Create View
    // =======================

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CardFragment", "onViewCreated called")
        return inflater.inflate(R.layout.card_view_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardImage = view.findViewById(R.id.cardImage)
        cardPrice = view.findViewById(R.id.cardPrice)
        cardName = view.findViewById(R.id.cardName)
        cardDescription = view.findViewById(R.id.cardDescription)

    }

    fun setCardData(card: CardModel) {
        cardPrice.text = card.price.toString()
        cardName.text = card.name
        cardDescription.text = card.description
        cardImage.setImageResource(when (card.name) {
            "Alien Metabolism" -> R.drawable.card_alien_metabolism
            "Complete Destruction" -> R.drawable.card_complete_destruction
            "Healing Ray" -> R.drawable.card_healing_ray
            "Solar Powered" -> R.drawable.card_solar_powered
            "Poison Lava" -> R.drawable.card_poison_lava
            "Energy Hoarder" -> R.drawable.card_energy_hoarder
            "Regeneration Thunder" -> R.drawable.card_regeneration_thunder
            "Tactical Retreat" -> R.drawable.card_tactical_retreat
            "Tokyo Meteor" -> R.drawable.card_meteor
            "Fortress" -> R.drawable.card_fortress
            "Wild Growth" -> R.drawable.card_wild_growth
            "Victory Parade" -> R.drawable.card_victory_parade
            "Tsunami" -> R.drawable.card_tsunami
            else -> R.drawable.tokyo

        })
    }
}
