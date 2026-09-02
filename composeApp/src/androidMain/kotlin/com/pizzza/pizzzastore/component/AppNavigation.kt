package com.pizzza.pizzzastore.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pizzza.pizzzastore.ui.AppViewModel
import com.pizzza.pizzzastore.ui.StoreViewModel
import com.pizzza.pizzzastore.ui.orders.OrderScreen
import com.pizzza.pizzzastore.ui.products.ProductScreen
import com.pizzza.pizzzastore.ui.products.EditPizzaScreen
import com.pizzza.pizzzastore.ui.products.EditOtherProductScreen
import com.pizzza.pizzzastore.ui.menu.MenuOptionsScreen
import com.pizzza.pizzzastore.ui.branches.BranchScreen
import com.pizzza.pizzzastore.ui.branches.EditBranchScreen
import com.pizzza.pizzzastore.ui.menu.ConfigNotiScreen
import com.pizzza.pizzzastore.ui.menu.ListUserScreen
import com.pizzza.pizzzastore.ui.splash.SplashScreen

@Composable
fun AppNavigation(
    viewModel: AppViewModel,
    storeViewModel: StoreViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            SplashScreen(
                viewModel = viewModel,
                onFinished = {
                    navController.navigate(Orders) {
                        popUpTo<Splash> { inclusive = true }
                    }
                }
            )
        }
        
        composable<Orders> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            OrderScreen(viewModel, onNavigateToMenuOptions = {
                navController.navigate(MenuOptions)
            })
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
                onBack = { navController.popBackStack() }
            )
        }
        composable<Register> {
            // Placeholder for Register Screen
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pantalla de Registro (En construcción)")
            }
        }
        composable<Products> {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            ProductScreen(
                viewModel = storeViewModel,
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
            EditBranchScreen(
                viewModel = storeViewModel,
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
