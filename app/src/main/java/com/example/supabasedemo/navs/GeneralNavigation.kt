package com.example.supabasedemo.navs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.supabasedemo.model.Persons
import com.example.supabasedemo.screens.Auth
import com.example.supabasedemo.screens.Hello
import com.example.supabasedemo.screens.MainScreen
import com.example.supabasedemo.screens.PersonInfoScreen
import com.example.supabasedemo.viewmodel.PersonsViewmodel

//TODO: Настроить навигацию

@Composable
fun GeneralNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            Auth(navController)
            //Ввод адреса сервера
        }
        composable("helloScreen") {
            Hello(navController)
            //Авторизация
        }
        composable("mainScreen") {
            MainScreen(navController)
            //Переход на главный экран
        }
        composable(
            "person/{personInfoId}",
            arguments = listOf(navArgument("personInfoId") { type = NavType.IntType })
        ) {
            val personInfoId: Int = it.arguments?.getInt("personInfoId") ?: 0
            val persons by PersonsViewmodel().persons.collectAsState(initial = listOf())
            persons.forEach { it ->
                if (it.id == personInfoId) {
                    PersonInfoScreen(person = it, navController)
                }
            }
        }
    }
}