package com.tecsup.lab10.view

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import com.tecsup.lab10.data.SerieModel // Ajusta este import según tu package
import com.tecsup.lab10.data.SerieApiService // Ajusta este import según tu package

class SeriesViewModel(private val apiService: SerieApiService) : ViewModel() {

    // Lista de series
    private val _seriesList = mutableStateListOf<SerieModel>()
    val seriesList: List<SerieModel> = _seriesList

    // Cargar lista de series
    suspend fun loadSeries() {
        try {
            val series = apiService.selectSeries()
            _seriesList.clear()
            _seriesList.addAll(series)
        } catch (e: Exception) {
            // Manejar el error si es necesario
            e.printStackTrace()
        }
    }

    // Obtener una serie por ID
    suspend fun getSerie(id: String): SerieModel? {
        return try {
            val response = apiService.selectSerie(id)
            response.body()
        } catch (e: Exception) {
            null
        }
    }

    // Insertar serie
    suspend fun insertSerie(serie: SerieModel): Boolean {
        return try {
            val response = apiService.insertSerie(serie)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // Actualizar serie
    suspend fun updateSerie(id: String, serie: SerieModel): Boolean {
        return try {
            val response = apiService.updateSerie(id, serie)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // Eliminar serie
    suspend fun deleteSerie(id: String): Boolean {
        return try {
            val response = apiService.deleteSerie(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}