package com.minhaaprovacao.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.minhaaprovacao.data.model.Instituicao
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class InstituicaoRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("instituicoes")

    fun getInstituicoes(): Flow<List<Instituicao>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val lista = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Instituicao::class.java)?.copy(id = doc.id)
                }
                trySend(lista)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun getInstituicaoById(id: String): Instituicao? {
        val doc = collection.document(id).get().await()
        return doc.toObject(Instituicao::class.java)?.copy(id = doc.id)
    }

    suspend fun addInstituicao(instituicao: Instituicao) {
        collection.add(instituicao).await()
    }

    suspend fun updateInstituicao(instituicao: Instituicao) {
        if (instituicao.id.isNotEmpty()) {
            collection.document(instituicao.id).set(instituicao).await()
        }
    }

    suspend fun deleteInstituicao(id: String): Result<Unit> {
        val vagasQuery = firestore.collection("oportunidades")
            .whereEqualTo("instituicaoId", id)
            .get()
            .await()

        if (!vagasQuery.isEmpty) {
            return Result.failure(Exception("Não é possível excluir esta instituição pois existem vagas vinculadas a ela."))
        }

        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
