package com.kxondeo.appkxondeorecipes

import kotlinx.serialization.Serializable

@Serializable
data class Folder(
    val id: Int,
    val nameEs: String,
    val nameEn: String,
    val parentFolderId: Int? = null
) {
    fun getName(isSpanish: Boolean): String = if (isSpanish) nameEs else nameEn
}

@Serializable
data class Recipe(
    val id: Int,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val portions: Int,
    val folderId: Int? = null,
    val imageUrl: String = ""
)

@Serializable
data class TeamMember(
    val uid: String,
    val name: String
)

@Serializable
data class KxondeoSharePackage(
    val folder: Folder? = null,
    val recipes: List<Recipe> = emptyList()
)
