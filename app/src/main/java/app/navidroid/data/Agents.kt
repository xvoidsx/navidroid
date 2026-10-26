package app.navidroid.data

// The curated agent list for navidroid's Default AI Agent feature (v0.2.0).
// packageName is the app's Google Play package, verified against Play before
// release: the homescreen agent line launches it when installed, and opens
// its Play listing when it isn't. An empty package means "not yet verified";
// the picker hides such entries until the table is filled in, so the
// feature can never offer a button that leads nowhere.
data class Agent(val name: String, val packageName: String)

val CURATED_AGENTS = listOf(
    // Packages verified against Play listings / AppBrain Play mirrors,
    // 26 Oct 2026; spot-checked independently for Muse and Edge Gallery.
    Agent("Muse", "com.facebook.aura"),
    Agent("ChatGPT", "com.openai.chatgpt"),
    Agent("Grok", "ai.x.grok"),
    Agent("Claude", "com.anthropic.claude"),
    Agent("DeepSeek", "com.deepseek.chat"),
    Agent("Kimi", "com.moonshot.kimichat"),
    // Current flagship Qwen app; an older "Qwen Studio"
    // (ai.qwenlm.chat.android) listing also exists and is not this one.
    Agent("Qwen", "com.tongyi.intl"),
    Agent("Alice AI", "com.yandex.aliceapp"),
    Agent("Perplexity", "ai.perplexity.app.android"),
    // Google's app (Research at Google), not Microsoft's — the local-models
    // entry. On Play since Dec 2025; older GitHub-only articles are stale.
    Agent("Edge Gallery", "com.google.ai.edge.gallery"),
    // Current live Copilot listing; the older standalone consumer app
    // (com.microsoft.copilot) is flagged removed from Play.
    Agent("Copilot", "com.microsoft.office.officehubrow"),
    Agent("Gab AI", "ai.gab.gabaiapp"),
    Agent("Lumo", "me.proton.android.lumo"),
    Agent("Meta AI", "com.facebook.stella"),
    // Bard-era package, still the live Gemini app.
    Agent("Gemini", "com.google.android.apps.bard"),
    Agent("Pi", "ai.inflection.pi"),
    // Listing has since been renamed "Vibe by Mistral"; package unchanged.
    Agent("Le Chat", "ai.mistral.chat"),
    Agent("PocketPal AI", "com.pocketpalai"),
    // No current Play listing (official app removed Aug 2024; HuggingChat
    // is browser-only now). Kept with an empty package so the picker hides
    // it until a web fallback lands — never ship a dead Play URL for it.
    Agent("HuggingChat", ""),
    Agent("Poe", "com.poe.android")
)

fun agentForPackage(packageName: String): Agent? {
    if (packageName.isEmpty()) return null
    return CURATED_AGENTS.firstOrNull { it.packageName == packageName }
}
