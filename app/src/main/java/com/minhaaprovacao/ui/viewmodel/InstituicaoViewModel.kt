package com.minhaaprovacao.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhaaprovacao.data.model.Instituicao
import com.minhaaprovacao.data.repository.InstituicaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class InstituicaoViewModel(
    private val repository: InstituicaoRepository = InstituicaoRepository()
) : ViewModel() {

    private val _instituicoes = MutableStateFlow<List<Instituicao>>(emptyList())
    val instituicoes: StateFlow<List<Instituicao>> = _instituicoes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        fetchInstituicoes()
    }

    fun fetchInstituicoes() {
        viewModelScope.launch {
            repository.getInstituicoes()
                .onStart { _isLoading.value = true }
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = "Erro ao carregar instituições: ${e.message}"
                }
                .collect { lista ->
                    _isLoading.value = false
                    _instituicoes.value = lista
                }
        }
    }

    fun saveInstituicao(
        id: String?,
        nome: String,
        cidade: String,
        bairro: String,
        site: String?,
        onSuccess: () -> Unit
    ) {
        if (nome.isBlank() || cidade.isBlank() || bairro.isBlank()) {
            _errorMessage.value = "Preencha todos os campos obrigatórios (Nome, Cidade, Bairro)."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val instituicao = Instituicao(
                    id = id ?: "",
                    nome = nome.trim(),
                    cidade = cidade.trim(),
                    bairro = bairro.trim(),
                    site = site?.trim().takeIf { !it.isNullOrBlank() }
                )

                if (id.isNullOrBlank()) {
                    repository.addInstituicao(instituicao)
                    _successMessage.value = "Instituição cadastrada com sucesso!"
                } else {
                    repository.updateInstituicao(instituicao)
                    _successMessage.value = "Instituição atualizada com sucesso!"
                }
                _isLoading.value = false
                onSuccess()
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Erro ao salvar instituição: ${e.message}"
            }
        }
    }

    fun deleteInstituicao(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteInstituicao(id)
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    _successMessage.value = "Instituição excluída com sucesso!"
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Erro ao excluir instituição."
                }
            )
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
