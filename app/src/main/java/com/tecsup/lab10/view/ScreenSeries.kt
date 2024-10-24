package com.tecsup.lab10.view

import android.util.Log
import androidx.annotation.RestrictTo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tecsup.lab10.data.SerieApiService
import com.tecsup.lab10.data.SerieModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ContenidoSerieEditar(
    navController: NavHostController,
    viewModel: SeriesViewModel,
    pid: Int = 0
) {
    val scope = rememberCoroutineScope()
    var id by remember { mutableStateOf(pid) }
    var name by remember { mutableStateOf("") }
    var releaseDate by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    // Cargar datos si se está editando
    LaunchedEffect(pid) {
        if (pid != 0) {
            viewModel.getSerie(pid.toString())?.let { serie ->
                name = serie.name
                releaseDate = serie.release_date
                rating = serie.rating.toString()
                category = serie.category
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Campo para el nombre de la serie
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre de la Serie") },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo para la fecha de lanzamiento
        TextField(
            value = releaseDate,
            onValueChange = { releaseDate = it },
            label = { Text("Fecha de lanzamiento (YYYY-MM-DD)") },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo para la calificación
        TextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Calificación (1-10)") },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo para la categoría
        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Categoría") },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Botón de guardar
        Button(
            onClick = {
                // Validar campos
                if (name.isNotEmpty() && releaseDate.isNotEmpty() && rating.isNotEmpty() && category.isNotEmpty()) {
                    val serie = SerieModel(
                        id = id,
                        name = name,
                        release_date = releaseDate,
                        rating = rating.toInt(),
                        category = category
                    )

                    scope.launch {
                        val success = if (id == 0) {
                            viewModel.insertSerie(serie)
                        } else {
                            viewModel.updateSerie(id.toString(), serie)
                        }

                        if (success) {
                            navController.navigate("series")
                        }
                    }
                }
            }
        ) {
            Text("Guardar")
        }
    }
}

@Composable
fun ContenidoSeriesListado(navController: NavHostController, viewModel: SeriesViewModel) {
    // Observamos la lista del ViewModel
    val seriesList = viewModel.seriesList

    // Cargamos las series al inicio
    LaunchedEffect(Unit) {
        viewModel.loadSeries()
    }

    LazyColumn {
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.1f)
                )
                Text(
                    text = "SERIE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.7f)
                )
                Text(
                    text = "Accion",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.2f)
                )
            }
        }

        items(seriesList) { item ->
            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.id}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.1f)
                )
                Text(
                    text = item.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.6f)
                )
                IconButton(
                    onClick = {
                        navController.navigate("serieVer/${item.id}")
                        Log.e("SERIE-VER", "ID = ${item.id}")
                    },
                    Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Ver",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                IconButton(
                    onClick = {
                        navController.navigate("serieDel/${item.id}")
                        Log.e("SERIE-DEL", "ID = ${item.id}")
                    },
                    Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Ver",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}


@Composable
fun ContenidoSerieEliminar(
    navController: NavHostController,
    viewModel: SeriesViewModel,
    id: Int
) {
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Está seguro de eliminar la Serie?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val success = viewModel.deleteSerie(id.toString())
                            if (success) {
                                navController.navigate("series")
                            }
                        }
                        showDialog = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("series")
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}