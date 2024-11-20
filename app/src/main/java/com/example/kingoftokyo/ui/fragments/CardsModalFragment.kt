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
    private var selectedCard: Int? = null // Tracks the selected card
    var onDismissCallback: (() -> Unit)? = null
    var onValidateCallback: (() -> Unit)? = null
    var isForBot: Boolean = false
    var botName: String = "Bot"

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

        // Initialize card containers and buttons
        cardContainers = listOf(
            view.findViewById(R.id.cardsFragmentCard1),
            view.findViewById(R.id.cardsFragmentCard2)
        )
        validateButton = view.findViewById(R.id.cardsFragmentValidateButton)
        cancelButton = view.findViewById(R.id.cardsFragmentCancelButton)

        botBoughtTitle = view.findViewById(R.id.botBoughtTitle)
        botBoughtTitle.text = "$botName bought:"

        // Initialize CardFragments dynamically
        cardContainers.forEach { container ->
            val cardFragment = CardFragment()
            cardFragments.add(cardFragment)
            childFragmentManager.beginTransaction()
                .replace(container.id, cardFragment)
                .commit()
        }

        if (isForBot) {
            setupBotCardView()
        } else {
            setupPlayerCardView()
        }
    }

    fun forceDismiss() {
        dismiss()
    }

    private fun setupBotCardView() {
        validateButton.visibility = View.GONE
        cancelButton.visibility = View.GONE
        botBoughtTitle.visibility = View.VISIBLE

        // Display a single card for the bot
        mainViewModel.cards.observe(viewLifecycleOwner) { cards ->
            cardFragments[0].setCardData(cards[0]) // Assuming bot buys the first card
            cardContainers[1].visibility = View.GONE // Hide the second card container
        }
    }

    private fun setupPlayerCardView() {
        botBoughtTitle.visibility = View.GONE

        // Display multiple cards for the player
        mainViewModel.cards.observe(viewLifecycleOwner) { cards ->
            cards.forEachIndexed { index, card ->
                if (index < cardFragments.size) {
                    cardFragments[index].setCardData(card)
                }
            }
        }

        setupCardClickListeners()
        validateButton.setOnClickListener {
            validateCard()
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
        updateCardSelection()
    }

    private fun updateCardSelection() {
        cardContainers.forEachIndexed { index, container ->
            container.setBackgroundResource(
                if (index == selectedCard) R.drawable.card_selected_background else 0
            )
        }
    }

    private fun validateCard() {
        selectedCard?.let { cardIndex ->
            mainViewModel.selectedCard.value = cardIndex

            if (mainViewModel.applyCardEffect(cardIndex)) {
                displayToast("You used your energy!", false)
                mainViewModel.resetCards()
                onValidateCallback?.invoke()
                dismiss()
            } else {
                displayToast("Not enough energy!", true)
            }
        } ?: run {
            displayToast("Select a card!", true)
        }
    }

    private fun displayToast(text: String = "Undefined", isRed: Boolean = false) {
        val toastLayout = layoutInflater.inflate(R.layout.custom_toast_layout, null)
        val toastText = toastLayout.findViewById<TextView>(R.id.toast_message)
        toastText.text = text
        toastText.setTextColor(if (isRed) Color.RED else Color.BLACK)
        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = toastLayout
        toast.setGravity(Gravity.TOP, 0, (Resources.getSystem().displayMetrics.heightPixels / 5))
        toast.show()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke()
    }

    companion object {
        fun newInstance(isForBot: Boolean = false, botName: String = "bot"): CardsModalFragment {
            return CardsModalFragment().apply {
                this.isForBot = isForBot
                this.botName = botName
            }
        }
    }
}
