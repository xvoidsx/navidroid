package app.navidroid.ui

import android.app.AlertDialog
import android.content.Context
import app.navidroid.R
import app.navidroid.data.CURATED_AGENTS

// One picker shared by the homescreen agent line and the Settings row, so
// both always offer the same curated list. onSelected receives the chosen
// package, or "" for None (no default agent). Tapping an entry selects it
// immediately — no confirm button, one tap and you're back home.
fun showAgentPicker(context: Context, currentPackage: String, onSelected: (String) -> Unit) {
    val agents = CURATED_AGENTS.filter { it.packageName.isNotEmpty() }
    val names: List<CharSequence> = agents.map { it.name } + context.getString(R.string.agent_none)
    val checked = agents.indexOfFirst { it.packageName == currentPackage }
    AlertDialog.Builder(context)
        .setTitle(R.string.default_ai_agent)
        .setSingleChoiceItems(names.toTypedArray(), checked) { dialog, which ->
            val pkg = if (which < agents.size) agents[which].packageName else ""
            onSelected(pkg)
            dialog.dismiss()
        }
        .show()
}
