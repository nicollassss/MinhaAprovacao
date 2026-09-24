package com.minhaaprovacao.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.minhaaprovacao.data.model.StatusOportunidade
import com.minhaaprovacao.ui.viewmodel.InstituicaoViewModel
import com.minhaaprovacao.ui.viewmodel.OportunidadeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadeFormScreen(
    oportunidadeId: String?,
    oportunidadeViewModel: OportunidadeViewModel,
    instituicaoViewModel: InstituicaoViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToInstituicoes: () -> Unit
) {
    val oportunidades by oportunidadeViewModel.filteredOportunidades.collectAsState()
    val instituicoes by instituicaoViewModel.instituicoes.collectAsState()
    val isLoading by oportunidadeViewModel.isLoading.collectAsState()
    val errorMessage by oportunidadeViewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedInstituicaoId by remember { mutableStateOf("") }
    var selectedInstituicaoNome by remember { mutableStateOf("") }
    var nomeCurso by remember { mutableStateOf("") }
    var quantidadeVagas by remember { mutableStateOf("") }
    var dataHoraProva by remember { mutableStateOf<Date?>(null) }
    var selectedStatus by remember { mutableStateOf(StatusOportunidade.INTERESSE.name) }
    var observacoes by remember { mutableStateOf("") }

    var expandedInstituicaoDropdown by remember { mutableStateOf(false) }
    var expandedStatusDropdown by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    LaunchedEffect(oportunidadeId) {
        if (!oportunidadeId.isNullOrBlank()) {
            // Find in current list or fetch
            val op = oportunidades.find { it.id == oportunidadeId }
            if (op != null) {
                selectedInstituicaoId = op.instituicaoId
                selectedInstituicaoNome = op.instituicaoNome
                nomeCurso = op.nomeCurso
                quantidadeVagas = op.quantidadeVagas.toString()
                dataHoraProva = op.dataHoraProva
                selectedStatus = op.status
                observacoes = op.observacoes ?: ""
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            oportunidadeViewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (oportunidadeId.isNullOrBlank()) "Nova Oportunidade" else "Editar Oportunidade") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (instituicoes.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Nenhuma instituição cadastrada!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Você precisa cadastrar ao menos uma instituição antes de adicionar uma vaga ou processo seletivo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onNavigateToInstituicoes) {
                            Text("Cadastrar Instituição")
                        }
                    }
                }
            }

            // Instituição Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedInstituicaoDropdown,
                onExpandedChange = { expandedInstituicaoDropdown = it }
            ) {
                OutlinedTextField(
                    value = selectedInstituicaoNome.ifBlank { "Selecione a Instituição *" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Instituição Vinculada *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedInstituicaoDropdown) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedInstituicaoDropdown,
                    onDismissRequest = { expandedInstituicaoDropdown = false }
                ) {
                    instituicoes.forEach { inst ->
                        DropdownMenuItem(
                            text = { Text(inst.nome) },
                            onClick = {
                                selectedInstituicaoId = inst.id
                                selectedInstituicaoNome = inst.nome
                                expandedInstituicaoDropdown = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = nomeCurso,
                onValueChange = { nomeCurso = it },
                label = { Text("Nome do Curso ou Vaga *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = quantidadeVagas,
                onValueChange = { quantidadeVagas = it },
                label = { Text("Quantidade de Vagas (Inteiro positivo) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            // Date & Time Picker field
            OutlinedTextField(
                value = dataHoraProva?.let { dateFormat.format(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Data e Horário da Prova *") },
                placeholder = { Text("DD/MM/AAAA HH:MM") },
                trailingIcon = {
                    IconButton(onClick = {
                        val calendar = Calendar.getInstance()
                        dataHoraProva?.let { calendar.time = it }

                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        val selectedCal = Calendar.getInstance().apply {
                                            set(year, month, dayOfMonth, hourOfDay, minute, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }
                                        dataHoraProva = selectedCal.time
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Selecionar Data e Hora")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val calendar = Calendar.getInstance()
                        dataHoraProva?.let { calendar.time = it }

                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        val selectedCal = Calendar.getInstance().apply {
                                            set(year, month, dayOfMonth, hourOfDay, minute, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }
                                        dataHoraProva = selectedCal.time
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
            )

            // Status Dropdown
            val statusEnumObj = StatusOportunidade.fromNameOrDescricao(selectedStatus)
            ExposedDropdownMenuBox(
                expanded = expandedStatusDropdown,
                onExpandedChange = { expandedStatusDropdown = it }
            ) {
                OutlinedTextField(
                    value = statusEnumObj.descricao,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status do Acompanhamento *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatusDropdown) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedStatusDropdown,
                    onDismissRequest = { expandedStatusDropdown = false }
                ) {
                    StatusOportunidade.entries.forEach { st ->
                        DropdownMenuItem(
                            text = { Text(st.descricao) },
                            onClick = {
                                selectedStatus = st.name
                                expandedStatusDropdown = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = observacoes,
                onValueChange = { observacoes = it },
                label = { Text("Observações (Opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    oportunidadeViewModel.saveOportunidade(
                        id = oportunidadeId,
                        instituicaoId = selectedInstituicaoId,
                        instituicaoNome = selectedInstituicaoNome,
                        nomeCurso = nomeCurso,
                        quantidadeVagasStr = quantidadeVagas,
                        dataHoraProva = dataHoraProva,
                        status = selectedStatus,
                        observacoes = observacoes,
                        onSuccess = onNavigateBack
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading && instituicoes.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (oportunidadeId.isNullOrBlank()) "Cadastrar Oportunidade" else "Salvar Alterações")
                }
            }
        }
    }
}
