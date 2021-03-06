package de.oemel09.messanchor.domain.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat

class AddressBook(private val context: Context) {

    internal fun loadListedContacts(): MutableList<ContactListItem> {
        val contacts = mutableListOf<ContactListItem>()
        if (isReadContactsPermissionGiven()) {
            val cursor = context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            var priority = 0
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val lookupKey =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    contacts.add(Contact(lookupKey, name, null, false, priority))
                    priority++
                }
                cursor.close()
            }
        }
        return contacts
    }

    internal fun loadContacts(filter: String?): MutableList<Contact> {
        val contacts = mutableListOf<Contact>()
        if (isReadContactsPermissionGiven()) {
            var selection: String? = null
            var selectionArgs = emptyArray<String>()
            if (filter != null && filter.isNotEmpty()) {
                selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?"
                selectionArgs = arrayOf("%$filter%")
            }
            val cursor = context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val lookupKey =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    if (name != null) {
                        contacts.add(Contact(lookupKey, name))
                    }
                }
                cursor.close()
            }
        }
        return contacts
    }

    private fun isReadContactsPermissionGiven(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
