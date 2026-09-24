package com.minhaaprovacao.data.model

import com.google.firebase.firestore.DocumentId

data class Instituicao(
    @DocumentId
    val id: String = "",
    val nome: String = "",
    val cidade: String = "",
    val bairro: String = "",
    val site: String? = null
)
