//package com.navinfo.volvo.di.module
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.navinfo.volvo.di.key.ViewModelKey
//import com.navinfo.volvo.ui.MainActivityViewModel
//import com.navinfo.volvo.ui.fragments.home.HomeViewModel
//import com.navinfo.volvo.ui.fragments.login.LoginViewModel
//import com.navinfo.volvo.ui.fragments.message.ObtainMessageViewModel
//import dagger.Binds
//import dagger.Module
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import dagger.multibindings.IntoMap
//
//@InstallIn(SingletonComponent::class)
//@Module
//abstract class ViewModelModule {
//
//    @Binds
//    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
//
//    @IntoMap
//    @Binds
//    @ViewModelKey(MainActivityViewModel::class)
//    abstract fun bindMainViewModel(viewModel: MainActivityViewModel): ViewModel
//
//    @IntoMap
//    @Binds
//    @ViewModelKey(LoginViewModel::class)
//    abstract fun bindLoginFragmentViewModel(viewModel: LoginViewModel): ViewModel
//
//    @IntoMap
//    @Binds
//    @ViewModelKey(HomeViewModel::class)
//    abstract fun bindMessageFragmentViewModel(viewModel: HomeViewModel): ViewModel
//
//    @IntoMap
//    @Binds
//    @ViewModelKey(ObtainMessageViewModel::class)
//    abstract fun bindObtainMessageFragmentViewModel(viewModel: ObtainMessageViewModel): ViewModel
//
//
//}