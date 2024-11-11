package com.example.kingoftokyo.ui.fragments

import PlayerModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kingoftokyo.R

class MonsterCardFragment : Fragment() {

    private lateinit var monsterImage: ImageView
    private lateinit var monsterName: TextView
    private lateinit var heartValue: TextView
    private lateinit var lightningValue: TextView
    private lateinit var trophyValue: TextView
    private lateinit var layout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.monster_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MonsterCardFragment", "onViewCreated called")

        monsterImage = view.findViewById(R.id.monsterImage)
        monsterName = view.findViewById(R.id.monsterName)
        heartValue = view.findViewById(R.id.heartValue)
        lightningValue = view.findViewById(R.id.lightningValue)
        trophyValue = view.findViewById(R.id.trophyValue)
    }

    fun setMonsterData(monster: PlayerModel) {
        monsterImage.setImageResource(
            when(monster.monsterName) {
                "Demon" -> R.drawable.demon
                "Dragon" -> R.drawable.dragon
                "Lizard" -> R.drawable.lizard
                else -> R.drawable.robot
            }
        )
        monsterName.text = monster.monsterName
        heartValue.text = monster.lifePoints.toString()
        lightningValue.text = monster.energyPoints.toString()
        trophyValue.text = monster.victoryPoints.toString()
    }

    fun setCurrentPlayerBg() {
        view?.setBackgroundResource(R.drawable.monster_current_player_background)
    }

    fun selectCurrentPlayer() {

       //view.setBackgroundResource(R.drawable.monster_player_selected)
    }

    fun setUser() {
        view?.setBackgroundResource(R.drawable.monster_card_selected_background)
    }

    fun unsetBg() {
        view?.setBackgroundResource(R.drawable.monster_card_background)
    }
}