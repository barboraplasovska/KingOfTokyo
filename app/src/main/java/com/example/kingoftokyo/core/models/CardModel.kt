import com.example.kingoftokyo.core.enums.EffectType

class CardModel(
    val name: String,
    val price: Int,
    val effectType: EffectType,
    val effectAmount: Int
) {}