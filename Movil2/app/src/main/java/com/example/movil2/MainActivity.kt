package com.example.movil2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.movil2.data.local.Sicenetdatabase
import com.example.movil2.data.remote.RetrofitClient
import com.example.movil2.data.repository.Sicenetlocalrepository
import com.example.movil2.data.repository.SicenetRepository
import com.example.movil2.ui.carga.CargaAcademicaScreen
import com.example.movil2.ui.carga.CargaAcademicaViewModel
import com.example.movil2.ui.califinal.CalifFinalScreen
import com.example.movil2.ui.califinal.CalifFinalViewModel
import com.example.movil2.ui.califunidades.CalifUnidadesScreen
import com.example.movil2.ui.califunidades.CalifUnidadesViewModel
import com.example.movil2.ui.kardex.KardexScreen
import com.example.movil2.ui.kardex.KardexViewModel
import com.example.movil2.ui.login.LoginScreen
import com.example.movil2.ui.login.LoginViewModel
import com.example.movil2.ui.profile.ProfileScreen
import com.example.movil2.ui.profile.ProfileViewModel
import com.example.movil2.ui.theme.Movil2Theme
import kotlinx.coroutines.launch

// ---- Menú items ----
data class DrawerItem(val route: String, val label: String, val icon: ImageVector)

val drawerItems = listOf(
    DrawerItem("profile",         "Perfil",                      Icons.Default.Person),
    DrawerItem("carga",           "Carga Académica",             Icons.Default.Book),
    DrawerItem("kardex",          "Kardex",                      Icons.Default.List),
    DrawerItem("calif_unidades",  "Calificaciones por Unidad",   Icons.Default.Star),
    DrawerItem("calif_final",     "Calificaciones Finales",      Icons.Default.CheckCircle),
)

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
                    val context = LocalContext.current
                    val remoteRepo = SicenetRepository()

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // Mostrar Drawer solo si NO estamos en login
                    val showDrawer = currentRoute != null && currentRoute != "login"

                    if (showDrawer) {
                        AppDrawer(
                            navController = navController,
                            currentRoute = currentRoute,
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        ) {
                            AppNavHost(navController, remoteRepo)
                        }
                    } else {
                        AppNavHost(navController, remoteRepo)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    navController: NavHostController,
    currentRoute: String?,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "SICENET",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(drawerItems.find { it.route == currentRoute }?.label ?: "SICENET") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun AppNavHost(navController: NavHostController, remoteRepo: SicenetRepository) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            val loginViewModel = LoginViewModel(remoteRepo, context)
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("profile") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("profile") {
            val dao = Sicenetdatabase.getDatabase(context).sicenetDao()
            val localRepo = Sicenetlocalrepository(dao)
            val profileViewModel = ProfileViewModel(remoteRepo, localRepo)
            ProfileScreen(
                viewModel = profileViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("carga") {
            val vm = CargaAcademicaViewModel(context)
            CargaAcademicaScreen(viewModel = vm)
        }

        composable("kardex") {
            val vm = KardexViewModel(context)
            KardexScreen(viewModel = vm)
        }

        composable("calif_unidades") {
            val vm = CalifUnidadesViewModel(context)
            CalifUnidadesScreen(viewModel = vm)
        }

        composable("calif_final") {
            val vm = CalifFinalViewModel(context)
            CalifFinalScreen(viewModel = vm)
        }
    }
}