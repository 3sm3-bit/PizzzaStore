package com.pizzza.pizzzastore.component

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pizzza.pizzzastore.ui.AppViewModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.pizzza.pizzzastore.ui.login.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.launch
import com.pizzza.pizzzastore.ui.branches.BranchScreen
import com.pizzza.pizzzastore.ui.branches.EditBranchScreen
import com.pizzza.pizzzastore.ui.login.LoginScreen
import com.pizzza.pizzzastore.ui.menu.ConfigNotiScreen
import com.pizzza.pizzzastore.ui.menu.ListUserScreen
import com.pizzza.pizzzastore.ui.menu.MenuOptionsScreen
import com.pizzza.pizzzastore.ui.orders.OrderScreen
import com.pizzza.pizzzastore.ui.products.CreateProductScreen
import com.pizzza.pizzzastore.ui.products.EditOtherProductScreen
import com.pizzza.pizzzastore.ui.products.EditPizzaScreen
import com.pizzza.pizzzastore.ui.products.ProductScreen
import com.pizzza.pizzzastore.ui.register.AddressScreen
import com.pizzza.pizzzastore.ui.register.RegisterScreen
import com.pizzza.pizzzastore.ui.splash.SplashScreen

@Composable
fun AppNavigation(
    viewModel: AppViewModel,
    storeViewModel: StoreViewModel
) {
    val navController = rememberNavController()
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val authViewModel: AuthViewModel = koinViewModel()
    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            SplashScreen(
                viewModel = viewModel,
                onFinished = { value ->
                    if (value) {
                        navController.navigate(Orders) {
                            popUpTo<Splash> { inclusive = true }
                        }
                    } else {
                        navController.navigate(Login) {
                            popUpTo<Splash> { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<Orders> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            OrderScreen(
                viewModel = viewModel,
                onNavigateToMenuOptions = {
                    navController.navigate(MenuOptions)
                },
                onLogout = {
                    navController.navigate(Login) {
                        popUpTo(Orders) { inclusive = true }
                    }
                }
            )
        }

        composable<Login> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            LoginScreen {
                navController.navigate(Orders) {
                    popUpTo<Splash> { inclusive = true }
                }
            }
        }

        composable<MenuOptions> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            MenuOptionsScreen(
                onNavigateToProducts = {
                    storeViewModel.getProductsList()
                    navController.navigate(Products)
                },
                onNavigateToBranches = {
                    navController.navigate(Branches)
                },
                onNavigateToConfigNoti = {
                    storeViewModel.getBranchesList()
                    navController.navigate(ConfigNoti)
                },
                onNavigateToListUser = {
                    storeViewModel.getUsersList()
                    navController.navigate(ListUser)
                },
                onReconnectPrinter = {
                    viewModel.reconnectPrinter()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<CreateProduct> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            CreateProductScreen(
                viewModel = storeViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<ConfigNoti> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            ConfigNotiScreen(
                appViewModel = viewModel,
                storeViewModel = storeViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<ListUser> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            ListUserScreen(
                viewModel = storeViewModel,
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToRegister = {
                    navController.navigate(Register)
                }
            )
        }

        composable<Register> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

            val backStackEntry = navController.currentBackStackEntry
            val address = backStackEntry?.savedStateHandle?.getStateFlow<String?>("address", null)?.collectAsState()
            val lat = backStackEntry?.savedStateHandle?.getStateFlow<String?>("latitude", null)?.collectAsState()
            val lng = backStackEntry?.savedStateHandle?.getStateFlow<String?>("longitude", null)?.collectAsState()

            RegisterScreen(
                viewModel = authViewModel,
                addressFromMap = address?.value,
                latFromMap = lat?.value,
                lngFromMap = lng?.value,
                onNavigateToAddressSelection = { currentLat, currentLng, currentAddress ->
                    navController.navigate(Address(currentLat, currentLng, currentAddress))
                },
                onRegisterSuccess = {
                    scope.launch {
                        storeViewModel.fetchUsers()
                        navController.popBackStack()
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Address> { backStackEntry ->
            val addressRoute = backStackEntry.toRoute<Address>()
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            AddressScreen(
                initialLat = addressRoute.initialLat,
                initialLng = addressRoute.initialLng,
                initialAddress = addressRoute.initialAddress,
                onConfirm = { address, lat, lng ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("address", address)
                    navController.previousBackStackEntry?.savedStateHandle?.set("latitude", lat)
                    navController.previousBackStackEntry?.savedStateHandle?.set("longitude", lng)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Products> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            ProductScreen(
                viewModel = storeViewModel,
                onNavigateToCreateProduct = {
                    navController.navigate(CreateProduct)
                },
                onNavigateToEditPizza = {
                    navController.navigate(EditPizza)
                },
                onNavigateToEditOther = {
                    navController.navigate(EditOtherProduct)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<EditPizza> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            EditPizzaScreen(
                viewModel = storeViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<EditOtherProduct> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            EditOtherProductScreen(
                viewModel = storeViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Branches> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            BranchScreen(
                viewModel = storeViewModel,
                onNavigateToEdit = {
                    navController.navigate(EditBranch)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<EditBranch> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            
            val backStackEntry = navController.currentBackStackEntry
            val address = backStackEntry?.savedStateHandle?.getStateFlow<String?>("address", null)?.collectAsState()
            val lat = backStackEntry?.savedStateHandle?.getStateFlow<String?>("latitude", null)?.collectAsState()
            val lng = backStackEntry?.savedStateHandle?.getStateFlow<String?>("longitude", null)?.collectAsState()

            EditBranchScreen(
                viewModel = storeViewModel,
                addressFromMap = address?.value,
                latFromMap = lat?.value,
                lngFromMap = lng?.value,
                onNavigateToAddressSelection = { currentLat, currentLng, currentAddress ->
                    navController.navigate(Address(currentLat, currentLng, currentAddress))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        activity.requestedOrientation = orientation
        onDispose { }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
