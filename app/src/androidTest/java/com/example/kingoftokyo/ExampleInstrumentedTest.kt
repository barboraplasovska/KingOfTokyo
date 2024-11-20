package com.example.kingoftokyo

import PlayerModel
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.kingoftokyo.core.enums.PlayerType
import com.example.kingoftokyo.core.services.GameService

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.kingoftokyo", appContext.packageName)
    }

    @Test
    fun testVictoryPointsGain() {
        val player = PlayerModel("monster", 3,
            2, 5, true, listOf(), PlayerType.HUMAN)
        val gameService = GameService()
        println("created gameService")
        var list = mutableListOf(0, 4, 1)
        println("created dices list")
        gameService.applyVictoryEffects(player, list)
        assertEquals(player.victoryPoints, 8)
    }
}