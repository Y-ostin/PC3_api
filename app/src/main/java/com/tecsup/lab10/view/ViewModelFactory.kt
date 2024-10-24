package com.tecsup.lab10.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.lab10.data.SerieApiService // Ajusta este import según tu package

class ViewModelFactory(private val apiService: SerieApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SeriesViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}