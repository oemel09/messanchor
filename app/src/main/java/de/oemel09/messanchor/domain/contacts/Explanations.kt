package de.oemel09.messanchor.domain.contacts

import android.content.Context
import android.content.SharedPreferences
import de.oemel09.messanchor.R

const val DELETED_EXPLANATION_ADD_CONTACTS = "DELETED_EXPLANATION_ADD_CONTACTS"
const val DELETED_EXPLANATION_SWIPE_TO_REMOVE = "DELETED_EXPLANATION_SWIPE_TO_REMOVE"

class Explanations(private val context: Context, private val prefs: SharedPreferences) {

    fun getExplanations(): List<Explanation> {
        val explanations = arrayListOf<Explanation>()
        if (!prefs.getBoolean(DELETED_EXPLANATION_ADD_CONTACTS, false)) {
            explanations.add(
                Explanation(
                    DELETED_EXPLANATION_ADD_CONTACTS,
                    context.getString(R.string.explanation_add_contacts),
                    null
                )
            )
        }
        if (!prefs.getBoolean(DELETED_EXPLANATION_SWIPE_TO_REMOVE, false)) {
            explanations.add(
                Explanation(
                    DELETED_EXPLANATION_SWIPE_TO_REMOVE,
                    context.getString(R.string.explanation_swipe_to_remove),
                    null
                )
            )
        }
        return explanations
    }
}
