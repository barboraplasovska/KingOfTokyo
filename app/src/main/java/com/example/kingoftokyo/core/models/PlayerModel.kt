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
    fun deepCopy(): PlayerModel {
        return PlayerModel(
            monsterName = this.monsterName,
            lifePoints = this.lifePoints,
            energyPoints = this.energyPoints,
            victoryPoints = this.victoryPoints,
            isInTokyo = this.isInTokyo,
            playerType = this.playerType,
            cards = this.cards.map { it.deepCopy() },
        )
    }

    fun isDead() : Boolean {
        return lifePoints <= 0
    }

    fun isHuman() : Boolean {
        return playerType == PlayerType.HUMAN
    }

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
        if (lifePoints < 0) {
            lifePoints = 0
            isInTokyo = false // can't be in Tokyo if dead
        }
    }

    fun heal(healing: Int) {
        if (lifePoints < 10)
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
