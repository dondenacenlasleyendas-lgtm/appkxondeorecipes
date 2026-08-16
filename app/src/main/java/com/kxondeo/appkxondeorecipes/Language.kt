package com.kxondeo.appkxondeorecipes

enum class AppLanguage(val label: String) {
    English("English"),
    Spanish("Español"),
    Vietnamese("Tiếng Việt"),
    Norwegian("Norsk")
}

fun t(
    language: AppLanguage,
    en: String,
    es: String? = null,
    vi: String? = null,
    no: String? = null
): String {
    return when (language) {
        AppLanguage.English -> en
        AppLanguage.Spanish -> es ?: en
        AppLanguage.Vietnamese -> vi ?: en
        AppLanguage.Norwegian -> no ?: en
    }
}
