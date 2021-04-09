package de.oemel09.messanchor.domain.contacts

import android.content.Intent

data class Explanation(var key: String, var text: String, var intent: Intent?) : ContactListItem
