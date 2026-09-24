Minha Aprovação

## Funcionalidades Principais

- **Instituições de Ensino**:
  - Cadastro, listagem, consulta de detalhes, edição e exclusão.
  - **Bloqueio de Exclusão**: Impede a exclusão de instituições que possuem vagas ou processos seletivos vinculados, exibindo um aviso claro ao usuário.
- **Oportunidades e Processos Seletivos**:
  - Vinculação com instituições cadastradas.
  - Registro de nome do curso/vaga, quantidade de vagas (número inteiro positivo), data e horário da prova com seletores nativos, status e observações opcionais.
  - **Status de Acompanhamento**: *Interesse*, *Inscrito*, *Aguardando resultado*, *Aprovado* e *Não aprovado*.
  - Atualização de status em tempo real.
  - **Busca e Filtros**: Busca instantânea por nome do curso/instituição e filtros por status através de chips dedicados.

---

## Tecnologias Utilizadas

- **Linguagem**: [Kotlin](https://kotlinlang.org/)
- **Interface**: [Jetpack Compose](https://developer.android.com/jetpack/compose) com [Material Design 3](https://m3.material.io/)
- **Navegabilidade**: Navigation Compose
- **Gerenciamento de Estado**: ViewModel e Kotlin StateFlow
- **Banco de Dados**: Firebase Cloud Firestore (persistência em tempo real)
- **Autenticação**: Firebase Auth (Autenticação Anônima integrada)
- **Build System**: Gradle com Kotlin DSL (`*.kts`)

---

## Identidade Visual

- **Cor Principal**: Azul-escuro acadêmico (`#1E3A8A`)
- **Cor de Destaque**: Verde (`#059669`)
- **Fundo**: Claro (`#F8FAFC`)
- **Componentes**: Cards com hierarquia clara, chips coloridos por status e textos totalmente em português do Brasil (`pt-BR`).

---

## Configuração e Execução

1. **Pré-requisitos**:
   - Android Studio (Koala / Ladybug ou superior).
   - JDK 17 ou superior.
   - Dispositivo Android (ou emulador) com API 26+ (Android 8.0+).

2. **Configuração do Firebase**:
   - Crie um projeto no [Firebase Console](https://console.firebase.google.com/).
   - Ative o **Cloud Firestore** e a **Autenticação Anônima** (*Sign-in method -> Anonymous*).
   - Baixe o arquivo `google-services.json` e coloque-o na pasta `app/` do projeto (`app/google-services.json`).

3. **Executando o Projeto**:
   - Abra o projeto no **Android Studio**.
   - Clique em **Sync Project with Gradle Files**.
   - Conecte seu dispositivo ou emulador e clique em **Run (▶)**.
