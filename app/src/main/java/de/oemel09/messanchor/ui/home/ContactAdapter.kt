package de.oemel09.messanchor.ui.home

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import de.oemel09.messanchor.R
import de.oemel09.messanchor.domain.contacts.Contact
import de.oemel09.messanchor.domain.contacts.ContactListItem
import de.oemel09.messanchor.domain.contacts.Explanation
import de.oemel09.messanchor.ui.settings.PREFS_APPEARANCE
import de.oemel09.messanchor.ui.settings.PREFS_SHOW_OPEN_CONTACT_PAGE

const val VIEW_TYPE_EXPLANATION = 0
const val VIEW_TYPE_CONTACT = 1

class ContactAdapter(
    private val context: Context,
    private val onContactClickListener: OnContactClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val showOpenContactPage =
        context.getSharedPreferences(PREFS_APPEARANCE, Context.MODE_PRIVATE).getBoolean(
            PREFS_SHOW_OPEN_CONTACT_PAGE, false
        )
    private lateinit var contacts: List<ContactListItem>
    var isSearchResult: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_EXPLANATION) {
            val rootView =
                LayoutInflater.from(context).inflate(R.layout.explanation_item, parent, false)
            ExplanationViewHolder(rootView, onContactClickListener)
        } else {
            val rootView =
                LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false)
            ContactViewHolder(rootView, onContactClickListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = contacts[position]
        return if (item is Explanation) VIEW_TYPE_EXPLANATION else VIEW_TYPE_CONTACT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = contacts[position]
        when (holder) {
            is ContactViewHolder -> bindContact(item as Contact, holder)
            is ExplanationViewHolder -> bindExplanation(item as Explanation, holder)
        }
    }

    private fun bindContact(contact: Contact, contactViewHolder: ContactViewHolder) {
        contactViewHolder.tvName.text = contact.name
        contactViewHolder.ibAddContact.visibility =
            if (contact.isListed) View.GONE else View.VISIBLE
        contactViewHolder.ibOpenContactPage.visibility =
            if (showOpenContactPage) View.VISIBLE else View.GONE
        if (contact.customMessenger == null) {
            contactViewHolder.ivMessengerIcon.setImageResource(R.drawable.ic_automatic)
            contactViewHolder.ivMessengerIcon.setBackgroundResource(R.drawable.round_button)
            val padding = context.resources.getDimensionPixelOffset(R.dimen.round_button_padding)
            contactViewHolder.ivMessengerIcon.setPadding(padding)
            contactViewHolder.ivMessengerIcon.imageTintList =
                ColorStateList.valueOf(context.getColor(R.color.primaryTextColor))
        } else {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            val icon = context.packageManager.getApplicationIcon(contact.customMessenger)
            contactViewHolder.ivMessengerIcon.imageTintList = null
            contactViewHolder.ivMessengerIcon.setImageDrawable(icon)
            contactViewHolder.ivMessengerIcon.setBackgroundResource(0)
            contactViewHolder.ivMessengerIcon.setPadding(0)
        }
    }

    private fun bindExplanation(
        explanation: Explanation,
        explanationViewHolder: ExplanationViewHolder
    ) {
        explanationViewHolder.tvExplanation.text = explanation.text
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun updateContacts(contacts: List<ContactListItem>) {
        this.contacts = contacts
    }

    class ContactViewHolder(
        itemView: View,
        private val onContactClickListener: OnContactClickListener
    ) :
        RecyclerView.ViewHolder(
            itemView
        ) {

        internal val tvName = itemView.findViewById<TextView>(R.id.contact_item_tv_contact_name)
        internal val ibAddContact =
            itemView.findViewById<ImageButton>(R.id.contact_item_ib_add_contact_to_list)
        internal val ibOpenContactPage =
            itemView.findViewById<ImageButton>(R.id.contact_item_ib_open_contact_page)
        internal val ivMessengerIcon =
            itemView.findViewById<ImageView>(R.id.contact_item_iv_messenger_icon)

        init {
            itemView.findViewById<View>(R.id.contact_item_root).setOnClickListener {
                onContactClickListener.onContactClick(adapterPosition)
            }
            ibAddContact.setOnClickListener {
                onContactClickListener.onAddContactClick(adapterPosition)
            }
            ibOpenContactPage.setOnClickListener {
                onContactClickListener.onOpenContactPageClick(adapterPosition)
            }
            ivMessengerIcon.setOnClickListener {
                onContactClickListener.onMessengerIconClick(adapterPosition)
            }
        }
    }

    class ExplanationViewHolder(
        itemView: View,
        private val onContactClickListener: OnContactClickListener
    ) :
        RecyclerView.ViewHolder(
            itemView
        ) {

        internal val tvExplanation =
            itemView.findViewById<TextView>(R.id.explanation_item_tv_explanation)

        init {
            itemView.findViewById<View>(R.id.explanation_item_root).setOnClickListener {
                onContactClickListener.onContactClick(adapterPosition)
            }
        }
    }

    interface OnContactClickListener {
        fun onContactClick(position: Int)
        fun onAddContactClick(position: Int)
        fun onOpenContactPageClick(position: Int)
        fun onMessengerIconClick(position: Int)
    }
}
