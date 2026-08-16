package com.kxondeo.appkxondeorecipes

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirebaseManager {
    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION_SHARES = "shared_folders"

    fun shareFolderRealtime(folder: Folder, recipes: List<Recipe>, onComplete: (String?) -> Unit) {
        val shareId = "KX-" + (1000..9999).random().toString()
        val folderRecipes = recipes.filter { it.folderId == folder.id }
        
        val data = hashMapOf(
            "folder" to folder,
            "recipes" to folderRecipes,
            "lastUpdated" to System.currentTimeMillis()
        )

        db.collection(COLLECTION_SHARES).document(shareId)
            .set(data)
            .addOnSuccessListener { 
                Log.d("Firebase", "Carpeta compartida con ID: $shareId")
                onComplete(shareId) 
            }
            .addOnFailureListener { e -> 
                Log.e("Firebase", "Error al compartir", e)
                onComplete(null) 
            }
    }

    fun listenToSharedFolder(shareId: String): Flow<KxondeoSharePackage?> = callbackFlow {
        val docRef = db.collection(COLLECTION_SHARES).document(shareId)
        
        val subscription = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                try {
                    val folderData = snapshot.get("folder") as Map<String, Any>
                    val recipesData = snapshot.get("recipes") as List<Map<String, Any>>
                    
                    val folder = Folder(
                        id = (folderData["id"] as Long).toInt(),
                        nameEs = folderData["nameEs"] as String,
                        nameEn = folderData["nameEn"] as String,
                        parentFolderId = (folderData["parentFolderId"] as? Long)?.toInt()
                    )
                    
                    val recipes = recipesData.map { r ->
                        Recipe(
                            id = (r["id"] as Long).toInt(),
                            title = r["title"] as String,
                            description = r["description"] as String,
                            ingredients = r["ingredients"] as List<String>,
                            steps = r["steps"] as List<String>,
                            portions = (r["portions"] as Long).toInt(),
                            folderId = (r["folderId"] as? Long)?.toInt(),
                            imageUrl = r["imageUrl"] as String
                        )
                    }
                    
                    trySend(KxondeoSharePackage(folder, recipes))
                } catch (e: Exception) {
                    Log.e("Firebase", "Error parseando datos", e)
                    trySend(null)
                }
            } else {
                trySend(null)
            }
        }

        awaitClose { subscription.remove() }
    }

    fun getTeams(): Flow<List<TeamMember>> = callbackFlow {
        val subscription = db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val members = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(TeamMember::class.java)
                } ?: emptyList()
                trySend(members)
            }
        awaitClose { subscription.remove() }
    }

    fun addTeamMember(uid: String, name: String, onComplete: () -> Unit) {
        val member = TeamMember(uid, name)
        db.collection("users").document(uid).set(member)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener { e -> Log.e("Firebase", "Error adding member", e) }
    }

    fun sendToUser(uid: String, pkg: KxondeoSharePackage) {
        val shareId = "SHARE-" + System.currentTimeMillis()
        val data = hashMapOf(
            "recipientUid" to uid,
            "folder" to pkg.folder,
            "recipes" to pkg.recipes,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection(COLLECTION_SHARES).document(shareId).set(data)
            .addOnSuccessListener { Log.d("Firebase", "Shared with user $uid") }
            .addOnFailureListener { e -> Log.e("Firebase", "Error sharing with user", e) }
    }
}
