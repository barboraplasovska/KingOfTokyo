class CardModel(
    val name: String,
    val price: Int,
    val description: String,
    val effect: (PlayerModel, List<PlayerModel>, Int) -> Unit
)
