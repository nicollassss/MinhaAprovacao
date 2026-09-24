package com.minhaaprovacao.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.minhaaprovacao.ui.screen.InstituicaoFormScreen
import com.minhaaprovacao.ui.screen.InstituicoesListScreen
import com.minhaaprovacao.ui.screen.OportunidadeDetailScreen
import com.minhaaprovacao.ui.screen.OportunidadeFormScreen
import com.minhaaprovacao.ui.screen.OportunidadesListScreen
import com.minhaaprovacao.ui.viewmodel.InstituicaoViewModel
import com.minhaaprovacao.ui.viewmodel.OportunidadeViewModel

sealed class Screen(val route: String) {
    object OportunidadesList : Screen("oportunidades_list")
    object OportunidadeForm : Screen("oportunidade_form?oportunidadeId={oportunidadeId}") {
        fun createRoute(oportunidadeId: String? = null) =
            if (oportunidadeId != null) "oportunidade_form?oportunidadeId=$oportunidadeId" else "oportunidade_form"
    }
    object OportunidadeDetail : Screen("oportunidade_detail/{oportunidadeId}") {
        fun createRoute(oportunidadeId: String) = "oportunidade_detail/$oportunidadeId"
    }
    object InstituicoesList : Screen("instituicoes_list")
    object InstituicaoForm : Screen("instituicao_form?instituicaoId={instituicaoId}") {
        fun createRoute(instituicaoId: String? = null) =
            if (instituicaoId != null) "instituicao_form?instituicaoId=$instituicaoId" else "instituicao_form"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val instituicaoViewModel: InstituicaoViewModel = viewModel()
    val oportunidadeViewModel: OportunidadeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.OportunidadesList.route
    ) {
        composable(Screen.OportunidadesList.route) {
            OportunidadesListScreen(
                viewModel = oportunidadeViewModel,
                onNavigateToAdd = { navController.navigate(Screen.OportunidadeForm.createRoute()) },
                onNavigateToEdit = { id -> navController.navigate(Screen.OportunidadeForm.createRoute(id)) },
                onNavigateToDetail = { id -> navController.navigate(Screen.OportunidadeDetail.createRoute(id)) },
                onNavigateToInstituicoes = { navController.navigate(Screen.InstituicoesList.route) }
            )
        }

        composable(
            route = Screen.OportunidadeForm.route,
            arguments = listOf(navArgument("oportunidadeId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val oportunidadeId = backStackEntry.arguments?.getString("oportunidadeId")
            OportunidadeFormScreen(
                oportunidadeId = oportunidadeId,
                oportunidadeViewModel = oportunidadeViewModel,
                instituicaoViewModel = instituicaoViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToInstituicoes = { navController.navigate(Screen.InstituicoesList.route) }
            )
        }

        composable(
            route = Screen.OportunidadeDetail.route,
            arguments = listOf(navArgument("oportunidadeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val oportunidadeId = backStackEntry.arguments?.getString("oportunidadeId") ?: ""
            OportunidadeDetailScreen(
                oportunidadeId = oportunidadeId,
                viewModel = oportunidadeViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate(Screen.OportunidadeForm.createRoute(id)) }
            )
        }

        composable(Screen.InstituicoesList.route) {
            InstituicoesListScreen(
                viewModel = instituicaoViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate(Screen.InstituicaoForm.createRoute()) },
                onNavigateToEdit = { id -> navController.navigate(Screen.InstituicaoForm.createRoute(id)) }
            )
        }

        composable(
            route = Screen.InstituicaoForm.route,
            arguments = listOf(navArgument("instituicaoId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val instituicaoId = backStackEntry.arguments?.getString("instituicaoId")
            InstituicaoFormScreen(
                instituicaoId = instituicaoId,
                viewModel = instituicaoViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
