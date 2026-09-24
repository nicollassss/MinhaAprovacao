package com.minhaaprovacao.data.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

enum class StatusOportunidade(val descricao: String) {
    INTERESSE("Interesse"),
    INSCRITO("Inscrito"),
    AGUARDANDO_RESULTADO("Aguardando resultado"),
    APROVADO("Aprovado"),
    NAO_APROVADO("Não aprovado");

    companion object {
        fun fromDescricao(desc: String): StatusOportunidade {
            return entries.find { it.descricao.equals(desc, ignoreCase = true) } ?: INTERESSE
        }
        
        fun fromNameOrDescricao(value: String): StatusOportunidade {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.descricao.equals(value, ignoreCase = true) } ?: INTERESSE
        }
    }
}

data class Oportunidade(
    @DocumentId
    val id: String = "",
    val instituicaoId: String = "",
    val instituicaoNome: String = "",
    val nomeCurso: String = "",
    val quantidadeVagas: Int = 0,
    val dataHoraProva: Date? = null,
    val status: String = StatusOportunidade.INTERESSE.name,
    val observacoes: String? = null
) {
    val statusEnum: StatusOportunidade
        get() = StatusOportunidade.fromNameOrDescricao(status)
}
