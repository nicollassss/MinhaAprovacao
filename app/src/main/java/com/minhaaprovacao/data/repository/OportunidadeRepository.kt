package com.minhaaprovacao.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.minhaaprovacao.data.model.Oportunidade
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OportunidadeRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("oportunidades")

    fun getOportunidades(): Flow<List<Oportunidade>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val lista = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Oportunidade::class.java)?.copy(id = doc.id)
                }
                trySend(lista)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun getOportunidadeById(id: String): Oportunidade? {
        val doc = collection.document(id).get().await()
        return doc.toObject(Oportunidade::class.java)?.copy(id = doc.id)
    }

    suspend fun addOportunidade(oportunidade: Oportunidade) {
        collection.add(oportunidade).await()
    }

    suspend fun updateOportunidade(oportunidade: Oportunidade) {
        if (oportunidade.id.isNotEmpty()) {
            collection.document(oportunidade.id).set(oportunidade).await()
        }
    }

    suspend fun updateStatus(id: String, novoStatus: String) {
        if (id.isNotEmpty()) {
            collection.document(id).update("status", novoStatus).await()
        }
    }

    suspend fun deleteOportunidade(id: String) {
        if (id.isNotEmpty()) {
            collection.document(id).delete().await()
        }
    }
}
