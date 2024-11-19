class CardModel(
    val name: String,
    val price: Int,
    val description: String,
    val effect: (PlayerModel, List<PlayerModel>, Int) -> Unit
) {
    fun deepCopy(): CardModel {
        return CardModel(
            name = this.name,
            price = this.price,
            description = this.description,
            effect = this.effect
        )
    }
}
