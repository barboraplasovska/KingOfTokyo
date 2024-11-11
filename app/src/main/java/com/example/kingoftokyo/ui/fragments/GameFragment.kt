package com.example.kingoftokyo.ui.fragments

import CardModel
import PlayerModel
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.enums.PlayerType

class GameFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the selectedMonster value from the arguments
        val selectedMonster = arguments?.getInt("selectedMonster") ?: 0

        // Use the selectedMonster as needed in your GameFragment
        Log.d("GameFragment", "Selected monster: $selectedMonster")
    }
}
