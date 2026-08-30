package com.pizzza.pizzzastore.component

import androidx.compose.runtime.Composable
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
import com.pizzza.pizzzastore.ui.splash.SplashScreen

@Composable
fun AppNavigation(
    viewModel: AppViewModel,
    storeViewModel: StoreViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> {
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
            OrderScreen(viewModel, onNavigateToMenuOptions = {
                navController.navigate(MenuOptions)
            })
        }
        composable<MenuOptions> {
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
                onBack = { navController.popBackStack() }
            )
        }
        composable<ConfigNoti> {
            ConfigNotiScreen(
                appViewModel = viewModel,
                storeViewModel = storeViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable<Products> {
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
            EditPizzaScreen(
                viewModel = storeViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable<EditOtherProduct> {
            EditOtherProductScreen(
                viewModel = storeViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable<Branches> {
            BranchScreen(
                viewModel = storeViewModel,
                onNavigateToEdit = {
                    navController.navigate(EditBranch)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable<EditBranch> {
            EditBranchScreen(
                viewModel = storeViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
