import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.kingoftokyo.R
import com.example.kingoftokyo.core.enums.ModalType
import com.example.kingoftokyo.core.viewModels.MainViewModel
import com.example.kingoftokyo.ui.fragments.CardFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CardsModalFragment : DialogFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var cardContainers: List<FrameLayout>
    private lateinit var validateButton: Button
    private lateinit var cancelButton: Button
    private lateinit var botBoughtTitle: TextView

    private val cardFragments = mutableListOf<CardFragment>() // List of CardFragments
    var selectedCard: Int? = null
    var onDismissCallback: (() -> Unit)? = null
    var onValidateCardsCallback: (() -> Unit)? = null
    var botName: String = "Bot"

    var modalType: ModalType = ModalType.PLAYER_VIEW
    var purchasedCard: CardModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cards_modal, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardContainers = listOf(
            view.findViewById(R.id.cardsFragmentCard1),
            view.findViewById(R.id.cardsFragmentCard2),
            view.findViewById(R.id.cardsFragmentCard3)
        )

        validateButton = view.findViewById(R.id.cardsFragmentValidateButton)
        cancelButton = view.findViewById(R.id.cardsFragmentCancelButton)
        botBoughtTitle = view.findViewById(R.id.botBoughtTitle)

        cardContainers.forEach { container ->
            val cardFragment = CardFragment()
            cardFragments.add(cardFragment)
            childFragmentManager.beginTransaction()
                .replace(container.id, cardFragment)
                .commit()
        }

        when (modalType) {
            ModalType.PLAYER_VIEW -> setupPlayerCardView()
            ModalType.BOT_VIEW -> setupBotCardView()
            ModalType.PLAYER_PURCHASE -> setupPlayerPurchaseView()
        }
    }

    fun forceDismiss() {
        dismiss()
    }

    private fun setupPlayerPurchaseView() {
        validateButton.visibility = View.GONE
        cancelButton.visibility = View.GONE
        botBoughtTitle.text = "You bought this card:"
        botBoughtTitle.visibility = View.VISIBLE

        mainViewModel.selectedCard.observe(viewLifecycleOwner) { selectedCard ->
            purchasedCard?.let { card ->
                cardFragments[0].setCardData(card)
                cardContainers[1].visibility = View.GONE
                cardContainers[2].visibility = View.GONE
            }
        }
    }

    private fun setupBotCardView() {
        validateButton.visibility = View.GONE
        cancelButton.visibility = View.GONE
        botBoughtTitle.visibility = View.VISIBLE
        botBoughtTitle.text = "$botName bought this card:"

        mainViewModel.cards.observe(viewLifecycleOwner) { cards ->
            cardFragments[0].setCardData(cards[0])
            cardContainers[1].visibility = View.GONE
            cardContainers[2].visibility = View.GONE
        }
    }

    private fun setupPlayerCardView() {
        botBoughtTitle.visibility = View.GONE

        mainViewModel.cards.observe(viewLifecycleOwner) { cards ->
            cards.forEachIndexed { index, card ->
                if (index < cardFragments.size) {
                    cardFragments[index].setCardData(card)
                    cardContainers[index].visibility = View.VISIBLE
                }
            }
        }

        setupCardClickListeners()
        validateButton.setOnClickListener {
            onValidateCardsCallback?.invoke()
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupCardClickListeners() {
        cardContainers.forEachIndexed { index, container ->
            container.setOnClickListener {
                selectCard(index)
            }
        }
    }

    private fun selectCard(cardIndex: Int) {
        selectedCard = cardIndex
        mainViewModel.setSelectedCard(cardIndex)
        updateCardSelection()
    }

    private fun updateCardSelection() {
        cardContainers.forEachIndexed { index, container ->
            container.setBackgroundResource(
                if (index == selectedCard) R.drawable.card_selected_background else 0
            )
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke()
    }

    companion object {
        fun newInstance(
            modalType: ModalType = ModalType.PLAYER_VIEW,
            botName: String = "bot",
            purchasedCard: CardModel? = null
        ): CardsModalFragment {
            return CardsModalFragment().apply {
                this.modalType = modalType
                this.botName = botName
                this.purchasedCard = purchasedCard
            }
        }
    }

}
