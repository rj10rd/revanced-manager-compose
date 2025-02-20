package app.revanced.manager.di

import app.revanced.manager.ui.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::SettingsViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::AppSelectorViewModel)
    viewModelOf(::PatchesSelectorViewModel)
    viewModelOf(::PatchingScreenViewModel)
    viewModelOf(::ContributorsViewModel)
}