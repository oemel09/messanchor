package de.oemel09.messanchor.ui.home

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import de.oemel09.messanchor.domain.contacts.*
import de.oemel09.messanchor.domain.messengers.Messenger
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val contactLiveData = MutableLiveData<List<ContactListItem>>()
    private val contactItems = mutableListOf<ContactListItem>()
    private val contactManager = ContactManager(getApplication())

    init {
        contactLiveData.value = contactItems
    }

    internal fun getContacts(): MutableLiveData<List<ContactListItem>> {
        return contactLiveData
    }

    @SuppressLint("StaticFieldLeak")
    internal fun loadContacts(filter: String?, loadContactListener: LoadContactListener) {
        object : AsyncTask<Unit, Unit, List<ContactListItem>>() {

            override fun doInBackground(vararg units: Unit?): List<ContactListItem> {
                return if (filter == null) {
                    contactManager.loadListedContacts()
                } else {
                    contactManager.loadContacts(filter)
                }
            }

            override fun onPostExecute(c: List<ContactListItem>?) {
                val oldSize = contactItems.size
                contactItems.clear()
                contactItems.addAll(c!!)
                loadContactListener.onContactsLoaded(oldSize, c.size)
            }
        }.execute()
    }

    internal fun updateCustomMessenger(position: Int, customMessenger: Messenger) {
        val item = contactItems[position]
        if (item is Contact) {
            contactManager.changeCustomMessenger(item, customMessenger)
        }
    }

    internal fun removeItem(position: Int, context: Context) {
        val item = contactItems.removeAt(position)
        if (item is Contact) {
            contactManager.hideContact(item)
        } else {
            val explanation = item as Explanation
            val editor = context.getSharedPreferences(CONTACTS, Context.MODE_PRIVATE)
            editor.edit().putBoolean(explanation.key, true).apply()
        }
    }

    internal fun addItem(position: Int) {
        val item = contactItems[position]
        if (item is Contact) {
            contactManager.showContact(item)
        }
    }

    internal fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                val item1 = contactItems[i]
                val item2 = contactItems[i + 1]
                if (item1 is Contact && item2 is Contact) {
                    val tmpPriority: Int = item1.priority
                    item1.priority = item2.priority
                    item2.priority = tmpPriority
                    contactManager.swapContactPriority(item1, item2)
                }
                Collections.swap(contactItems, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                val item1 = contactItems[i]
                val item2 = contactItems[i - 1]
                if (item1 is Contact && item2 is Contact) {
                    val tmpPriority: Int = item1.priority
                    item1.priority = item2.priority
                    item2.priority = tmpPriority
                    contactManager.swapContactPriority(item1, item2)
                }
                Collections.swap(contactItems, i, i - 1)
            }
        }
    }

    fun getContactItem(position: Int): ContactListItem {
        return contactItems[position]
    }

    interface LoadContactListener {
        fun onContactsLoaded(oldSize: Int, newSize: Int)
    }
}
