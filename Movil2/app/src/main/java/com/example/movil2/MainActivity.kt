package com.example.movil2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movil2.data.remote.RetrofitClient
import com.example.movil2.data.repository.SicenetRepository
import com.example.movil2.ui.login.LoginScreen
import com.example.movil2.ui.login.LoginViewModel
import com.example.movil2.ui.profile.ProfileScreen
import com.example.movil2.ui.profile.ProfileViewModel
import com.example.movil2.ui.theme.Movil2Theme
class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        RetrofitClient.init(applicationContext)

        setContent {
            Movil2Theme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController: NavHostController = rememberNavController()
                    val repository = SicenetRepository()

                    NavHost(navController = navController, startDestination = "login") {

                        composable("login") {
                            val loginViewModel = LoginViewModel(repository)
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    println("Login exitoso, navegando a perfil")
                                    navController.navigate("profile") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("profile") {
                            val profileViewModel = ProfileViewModel(repository)
                            println("Mostrando pantalla de perfil")
                            ProfileScreen(
                                viewModel = profileViewModel,
                                onLogout = {
                                    println("🔒 Cerrando sesión, regresando a login")
                                    navController.navigate("login") {
                                        popUpTo("profile") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
