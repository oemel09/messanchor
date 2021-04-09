package de.oemel09.messanchor.domain.contacts

import android.content.Context
import android.content.SharedPreferences
import de.oemel09.messanchor.R
import de.oemel09.messanchor.domain.messengers.MessengerManager

const val DELETED_EXPLANATION_ADD_CONTACTS = "DELETED_EXPLANATION_ADD_CONTACTS"
const val DELETED_EXPLANATION_SWIPE_TO_REMOVE = "DELETED_EXPLANATION_SWIPE_TO_REMOVE"
const val DELETED_EXPLANATION_THREEMA = "DELETED_EXPLANATION_THREEMA"

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
        val messengerManager = MessengerManager(context)
        val threemaId = messengerManager.getThreemaId()
        if (!prefs.getBoolean(
                DELETED_EXPLANATION_THREEMA,
                false
            ) && messengerManager.isPackageInstalled(threemaId)
        ) {
            explanations.add(
                Explanation(
                    DELETED_EXPLANATION_THREEMA,
                    context.getString(R.string.explanation_threema_contacts),
                    context.packageManager.getLaunchIntentForPackage(threemaId)
                )
            )
        }
        return explanations
    }
}
