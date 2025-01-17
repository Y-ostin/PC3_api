package com.tecsup.lab10.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.lab10.data.SerieApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun SeriesApp() {
    val urlBase = "http://192.168.15.15:8000/"
    val retrofit = Retrofit.Builder()
        .baseUrl(urlBase)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService = retrofit.create(SerieApiService::class.java)

    // Crear el ViewModel usando el Factory
    val viewModel: SeriesViewModel = viewModel(
        factory = ViewModelFactory(apiService)
    )

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.padding(top=40.dp),
        topBar = { BarraSuperior() },
        bottomBar = { BarraInferior(navController) },
        floatingActionButton = { BotonFAB(navController) },
        content = { paddingValues ->
            Contenido(paddingValues, navController, viewModel)
        }
    )
}

@Composable
fun BotonFAB(navController: NavHostController) { // Removido el parámetro servicio
    val cbeState by navController.currentBackStackEntryAsState()
    val rutaActual = cbeState?.destination?.route
    if (rutaActual == "series") {
        FloatingActionButton(
            containerColor = Color.Magenta,
            contentColor = Color.White,
            onClick = { navController.navigate("serieNuevo") }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "SERIES APP",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun BarraInferior(navController: NavHostController) {
    NavigationBar(
        containerColor = Color.LightGray
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = navController.currentDestination?.route == "inicio",
            onClick = { navController.navigate("inicio") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Favorite, contentDescription = "Series") },
            label = { Text("Series") },
            selected = navController.currentDestination?.route == "series",
            onClick = { navController.navigate("series") }
        )
    }
}

@Composable
fun Contenido(
    pv: PaddingValues,
    navController: NavHostController,
    viewModel: SeriesViewModel  // Cambiado de SerieApiService a SeriesViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(pv)
    ) {
        NavHost(
            navController = navController,
            startDestination = "inicio"
        ) {
            composable("inicio") {
                ScreenInicio()
            }
            composable("series") {
                ContenidoSeriesListado(navController, viewModel)
            }
            composable("serieNuevo") {
                ContenidoSerieEditar(navController, viewModel, 0)
            }
            composable(
                "serieVer/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                ContenidoSerieEditar(
                    navController = navController,
                    viewModel = viewModel,
                    pid = it.arguments!!.getInt("id")
                )
            }
            composable(
                "serieDel/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                ContenidoSerieEliminar(
                    navController = navController,
                    viewModel = viewModel,
                    id = it.arguments!!.getInt("id")
                )
            }
        }
    }
}