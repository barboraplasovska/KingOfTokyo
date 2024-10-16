import com.example.kingoftokyo.core.enums.PlayerType

class PlayerModel(
    val monsterName: String,
    var lifePoints: Int,
    var energyPoints: Int,
    var victoryPoints: Int,
    var isInTokyo: Boolean,
    var cards: List<CardModel>,
    val playerType: PlayerType,
) {
    fun displayStats() {
        println("Monster Name: $monsterName")
        println("Life Points: $lifePoints")
        println("Energy Points: $energyPoints")
        println("Victory Points: $victoryPoints")
        println("Is in tokyo: $isInTokyo")
        println("Player type: $playerType")
    }

    fun takeDamage(damage: Int) {
        lifePoints -= damage
        if (lifePoints < 0) lifePoints = 0
    }

    fun heal(healing: Int) {
        lifePoints += healing
    }

    fun gainEnergy(energy: Int) {
        energyPoints += energy
    }

    fun spendEnergy(energy: Int): Boolean {
        return if (energyPoints >= energy) {
            energyPoints -= energy
            true
        } else {
            false
        }
    }

    fun gainVictoryPoints(points: Int) {
        victoryPoints += points
    }

    fun spendVictoryPoints(points: Int) {
        victoryPoints -= points;
    }
}
