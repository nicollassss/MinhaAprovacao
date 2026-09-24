package com.minhaaprovacao.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhaaprovacao.data.model.Oportunidade
import com.minhaaprovacao.data.repository.OportunidadeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class OportunidadeViewModel(
    private val repository: OportunidadeRepository = OportunidadeRepository()
) : ViewModel() {

    private val _oportunidades = MutableStateFlow<List<Oportunidade>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    val filteredOportunidades: StateFlow<List<Oportunidade>> = combine(
        _oportunidades,
        _searchQuery,
        _statusFilter
    ) { lista, query, status ->
        lista.filter { op ->
            val matchesQuery = query.isBlank() || op.nomeCurso.contains(query, ignoreCase = true) || op.instituicaoNome.contains(query, ignoreCase = true)
            val matchesStatus = status.isNullOrBlank() || op.status.equals(status, ignoreCase = true)
            matchesQuery && matchesStatus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        fetchOportunidades()
    }

    fun fetchOportunidades() {
        viewModelScope.launch {
            repository.getOportunidades()
                .onStart { _isLoading.value = true }
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = "Erro ao carregar oportunidades: ${e.message}"
                }
                .collect { lista ->
                    _isLoading.value = false
                    _oportunidades.value = lista
                }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: String?) {
        _statusFilter.value = status
    }

    fun saveOportunidade(
        id: String?,
        instituicaoId: String,
        instituicaoNome: String,
        nomeCurso: String,
        quantidadeVagasStr: String,
        dataHoraProva: Date?,
        status: String,
        observacoes: String?,
        onSuccess: () -> Unit
    ) {
        if (instituicaoId.isBlank()) {
            _errorMessage.value = "Selecione uma instituição válida."
            return
        }
        if (nomeCurso.isBlank()) {
            _errorMessage.value = "O nome do curso ou vaga é obrigatório."
            return
        }
        val vagas = quantidadeVagasStr.toIntOrNull()
        if (vagas == null || vagas <= 0) {
            _errorMessage.value = "A quantidade de vagas deve ser um número inteiro positivo."
            return
        }
        if (dataHoraProva == null) {
            _errorMessage.value = "A data e horário da prova são obrigatórios."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val oportunidade = Oportunidade(
                    id = id ?: "",
                    instituicaoId = instituicaoId,
                    instituicaoNome = instituicaoNome,
                    nomeCurso = nomeCurso.trim(),
                    quantidadeVagas = vagas,
                    dataHoraProva = dataHoraProva,
                    status = status,
                    observacoes = observacoes?.trim().takeIf { !it.isNullOrBlank() }
                )

                if (id.isNullOrBlank()) {
                    repository.addOportunidade(oportunidade)
                    _successMessage.value = "Oportunidade cadastrada com sucesso!"
                } else {
                    repository.updateOportunidade(oportunidade)
                    _successMessage.value = "Oportunidade atualizada com sucesso!"
                }
                _isLoading.value = false
                onSuccess()
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Erro ao salvar oportunidade: ${e.message}"
            }
        }
    }

    fun updateStatus(id: String, novoStatus: String) {
        viewModelScope.launch {
            try {
                repository.updateStatus(id, novoStatus)
                _successMessage.value = "Status atualizado com sucesso!"
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao atualizar status: ${e.message}"
            }
        }
    }

    fun deleteOportunidade(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteOportunidade(id)
                _isLoading.value = false
                _successMessage.value = "Oportunidade excluída com sucesso!"
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Erro ao excluir oportunidade: ${e.message}"
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
