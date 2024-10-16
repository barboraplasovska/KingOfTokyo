import com.example.kingoftokyo.core.enums.DiceFace

class DiceModel (
    var face: DiceFace,
    var isLocked: Boolean,
){
    fun roll() {
        if (!isLocked) {
            face = DiceFace.values().random()
        }
    }

    // Lock or unlock the dice
    fun lock() {
        isLocked = true
    }

    fun unlock() {
        isLocked = false
    }
}