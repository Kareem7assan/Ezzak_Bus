package com.aait.ezakbus.modules

import com.aait.ezakbus.network.repository.countries_repo.LocalRepo
import com.aait.ezakbus.network.repository.countries_repo.RepoCountriesImp
import com.aait.ezakbus.ui.view_model.RegisterViewModel
import com.aait.ezakbus.network.repository.countries_repo.RemoteRepo
import com.aait.ezakbus.network.repository.other_repos.RemoteRepos
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
  viewModel { RegisterViewModel(get(named("both")),get(named("repo")),androidContext()) }
    single(named("both")) {
        RepoCountriesImp(
            get(named("local")),
            get(named("remote"))
        )
    }
    single(named("local")) { LocalRepo() }
    single(named("remote")) { RemoteRepo() }
    single(named("repo")) { RemoteRepos() }
    single { RemoteRepos() }

}
