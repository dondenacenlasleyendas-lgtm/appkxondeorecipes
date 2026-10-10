package com.kxondeo.appkxondeorecipes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object PdfManager {
    private const val TAG = "PdfManager"

    fun savePdfToInternalStorage(context: Context, uri: Uri, customTitle: String? = null): CookbookPdf? {
        return try {
            val cookbooksDir = File(context.filesDir, "cookbooks").apply { if (!exists()) mkdirs() }
            
            var originalFileName = customTitle
            if (originalFileName.isNullOrEmpty()) {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        originalFileName = cursor.getString(nameIndex)
                    }
                }
            }

            val title = (originalFileName ?: "Libro_de_cocina")
                .removeSuffix(".pdf")
                .replace("_", " ")

            val pdfId = UUID.randomUUID().toString().take(8)
            val destFile = File(cookbooksDir, "cookbook_$pdfId.pdf")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return null

            val pageCount = getPdfPageCount(destFile)

            CookbookPdf(
                id = pdfId,
                title = title,
                localFilePath = destFile.absolutePath,
                pageCount = pageCount,
                addedTimestamp = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error saving PDF to internal storage", e)
            null
        }
    }

    fun getPdfPageCount(file: File): Int {
        if (!file.exists()) return 0
        return try {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    renderer.pageCount
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting PDF page count", e)
            0
        }
    }

    fun renderPageToBitmap(filePath: String, pageIndex: Int, targetWidthPx: Int = 1080): Bitmap? {
        val file = File(filePath)
        if (!file.exists()) return null

        return try {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    if (pageIndex !in 0 until renderer.pageCount) return null
                    renderer.openPage(pageIndex).use { page ->
                        val aspectRatio = page.height.toFloat() / page.width.toFloat()
                        val width = targetWidthPx
                        val height = (targetWidthPx * aspectRatio).toInt().coerceAtLeast(1)

                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmap
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error rendering page $pageIndex from $filePath", e)
            null
        }
    }

    fun extractRecipesFromPdf(cookbook: CookbookPdf, targetFolderId: Int?): List<Recipe> {
        val file = File(cookbook.localFilePath)
        if (!file.exists()) return emptyList()

        val extractedText = try {
            extractTextFromPdfFile(file)
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting text", e)
            ""
        }

        val recipes = parseRecipesFromText(extractedText, cookbook.title, targetFolderId)
        return if (recipes.isNotEmpty()) {
            recipes
        } else {
            listOf(
                Recipe(
                    id = (100000..999999).random(),
                    title = "Receta del Libro: ${cookbook.title}",
                    description = "Receta importada del libro ${cookbook.title} (${cookbook.pageCount} páginas).",
                    ingredients = listOf("Ingredientes por definir desde el libro PDF"),
                    steps = listOf("Revisar la página correspondiente en el PDF del libro de cocina."),
                    portions = 4,
                    folderId = targetFolderId
                )
            )
        }
    }

    private fun extractTextFromPdfFile(file: File): String {
        val textBuilder = StringBuilder()
        try {
            val bytes = file.readBytes()
            val content = String(bytes, Charsets.ISO_8859_1)
            
            val tjRegex = Regex("""\(([^()]+)\)\s*Tj""")
            val tjMatches = tjRegex.findAll(content)
            for (match in tjMatches) {
                val groupVal = match.groupValues[1]
                if (groupVal.length > 2) {
                    textBuilder.append(groupVal).append("\n")
                }
            }

            if (textBuilder.length < 50) {
                val wordRegex = Regex("""[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9,. -]{4,}""")
                val matches = wordRegex.findAll(content)
                for (m in matches) {
                    val w = m.value.trim()
                    if (!w.startsWith("Obj") && !w.startsWith("Font") && !w.startsWith("Catalog") && !w.contains("PDF")) {
                        textBuilder.append(w).append("\n")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing PDF text", e)
        }
        return textBuilder.toString()
    }

    private fun parseRecipesFromText(rawText: String, bookTitle: String, folderId: Int?): List<Recipe> {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
        val recipes = mutableListOf<Recipe>()

        var currentTitle = ""
        var currentIngredients = mutableListOf<String>()
        var currentSteps = mutableListOf<String>()
        var mode = "TITLE"

        for (line in lines) {
            val lower = line.lowercase()

            if (lower.contains("ingredientes") || lower.contains("ingredients")) {
                mode = "INGREDIENTS"
                continue
            } else if (lower.contains("preparación") || lower.contains("preparacion") || lower.contains("pasos") || lower.contains("instrucciones") || lower.contains("steps")) {
                mode = "STEPS"
                continue
            } else if (lower.contains("receta:") || lower.contains("recipe:") || (line.length in 5..40 && line.first().isUpperCase() && mode == "STEPS" && currentIngredients.isNotEmpty())) {
                if (currentTitle.isNotBlank() && (currentIngredients.isNotEmpty() || currentSteps.isNotEmpty())) {
                    recipes.add(
                        Recipe(
                            id = (100000..999999).random(),
                            title = currentTitle,
                            description = "Extraída de $bookTitle",
                            ingredients = currentIngredients.toList(),
                            steps = currentSteps.toList(),
                            portions = 4,
                            folderId = folderId
                        )
                    )
                    currentIngredients = mutableListOf()
                    currentSteps = mutableListOf()
                }
                currentTitle = line.removePrefix("Receta:").removePrefix("Recipe:").trim()
                mode = "INGREDIENTS"
                continue
            }

            when (mode) {
                "TITLE" -> {
                    if (line.length in 3..50) {
                        currentTitle = line
                    }
                }
                "INGREDIENTS" -> {
                    if (line.startsWith("-") || line.startsWith("•") || line.startsWith("*") || line.contains("gr") || line.contains("kg") || line.contains("ml") || line.contains("cda")) {
                        currentIngredients.add(line.removePrefix("-").removePrefix("•").removePrefix("*").trim())
                    } else if (line.length < 60) {
                        currentIngredients.add(line)
                    }
                }
                "STEPS" -> {
                    if (line.length > 5) {
                        currentSteps.add(line)
                    }
                }
            }
        }

        if (currentTitle.isNotBlank() && (currentIngredients.isNotEmpty() || currentSteps.isNotEmpty())) {
            recipes.add(
                Recipe(
                    id = (100000..999999).random(),
                    title = currentTitle,
                    description = "Extraída de $bookTitle",
                    ingredients = currentIngredients.toList(),
                    steps = currentSteps.toList(),
                    portions = 4,
                    folderId = folderId
                )
            )
        }

        return recipes
    }
}
