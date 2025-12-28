package com.android.cineverse.di

import org.android.cineverse.shared.data.local.DatabaseDriverFactory
import com.android.cineverse.ui.viewmodel.AndroidMoviesViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Provide Android-specific DatabaseDriverFactory with Context
    single { DatabaseDriverFactory(androidContext()) }
    
    viewModel { AndroidMoviesViewModel(get()) }
}
