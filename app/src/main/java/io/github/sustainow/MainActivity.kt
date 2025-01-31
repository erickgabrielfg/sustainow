package io.github.sustainow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups3
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import io.github.sustainow.domain.model.UserState
import io.github.sustainow.presentation.theme.AppTheme
import io.github.sustainow.presentation.ui.ConsumptionMainScreen
import io.github.sustainow.presentation.ui.HomeScreen
import io.github.sustainow.presentation.ui.LoginScreen
import io.github.sustainow.presentation.ui.SignUpScreen
import io.github.sustainow.presentation.ui.utils.Route
import io.github.sustainow.presentation.viewmodel.HomeViewModel
import io.github.sustainow.presentation.viewmodel.LoginViewModel
import io.github.sustainow.presentation.viewmodel.SignUpViewModel
import io.github.sustainow.service.auth.AuthService
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable object Home

@Serializable object Authentication

@Serializable object Login

@Serializable object SignUp

// consume routes

@Serializable object Consume

@Serializable object ConsumptionMainPage

@Serializable object ExpectedEnergyConsumption

@Serializable object ExpectedWaterConsumption

@Serializable object ExpectedCarbonFootprint

@Serializable object RealEnergyConsumption

@Serializable object RealWaterConsumption

@Serializable object ColetiveActions

@Serializable object SearchCollectiveActions

@Serializable object Routines

@Serializable object ViewRoutine

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var authService: AuthService

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()

                val userState by authService.user.collectAsState()

                val context = LocalContext.current

                var showUserMenu by remember {
                    mutableStateOf(false)
                }

                val coroutineScope = rememberCoroutineScope()

                val routes =
                    listOf(
                        Route(stringResource(R.string.home_route_text), Home, Icons.Default.Home),
                        Route(stringResource(R.string.consume_route_text), Consume, Icons.Default.VolunteerActivism),
                        Route(stringResource(R.string.colective_actions_route_text), ColetiveActions, Icons.Default.Groups3),
                        Route(stringResource(R.string.routines_route_text), Routines, Icons.Default.Today),
                    )

                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentScreen =
                    backStackEntry?.destination?.let {
                        when (it.route) {
                            Login::class.qualifiedName -> Login
                            SignUp::class.qualifiedName -> SignUp
                            else -> Home
                        }
                    } ?: Home

                val previousBackStackEntry = navController.previousBackStackEntry
                val previousScreen =
                    previousBackStackEntry?.destination?.let {
                        when (it.route) {
                            Login::class.qualifiedName -> Login
                            SignUp::class.qualifiedName -> SignUp
                            else -> Home
                        }
                    } ?: Home

                // Verifica se há uma tela anterior e se a rota atual não é Login nem SignUp
                val canNavigateBack =
                    previousBackStackEntry != null &&
                        previousScreen != Login &&
                        previousScreen != SignUp

                Scaffold(
                    topBar = {
                        if (currentScreen != Login && currentScreen != SignUp) {
                            TopAppBar(
                                title = {
                                    val logoResource = painterResource(id = R.drawable.sustainow_logo_transparent)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Spacer(modifier = Modifier.weight(1f)) // Adiciona espaço entre o botão de voltar e a logo
                                        // Tornar a logo clicável
                                        Image(
                                            logoResource,
                                            contentDescription = null,
                                            modifier = Modifier.requiredSize(150.dp, 150.dp),
                                        )
                                        Spacer(modifier = Modifier.weight(1f)) // Centraliza a logo
                                    }
                                },
                                colors =
                                    TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    ),
                                navigationIcon = {
                                    if (canNavigateBack) {
                                        IconButton(onClick = {
                                            navController.popBackStack()
                                        }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = context.getString(R.string.back),
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    when {
                                        userState is UserState.Logged ->
                                            if ((userState as UserState.Logged).user.profilePicture?.isNotEmpty() == true &&
                                                (userState as UserState.Logged).user.profilePicture !== null
                                            ) {
                                                val painter =
                                                    rememberAsyncImagePainter(model = (userState as UserState.Logged).user.profilePicture)
                                                IconButton(onClick = { showUserMenu = !showUserMenu }) {
                                                    Icon(
                                                        painter = painter,
                                                        contentDescription = context.getString(R.string.user_menu),
                                                    )
                                                    DropdownMenu(expanded = showUserMenu, onDismissRequest = { showUserMenu = false }) {
                                                        DropdownMenuItem(
                                                            text = { Text(context.getString(R.string.logout)) },
                                                            trailingIcon = {
                                                                Icon(
                                                                    Icons.AutoMirrored.Filled.ExitToApp,
                                                                    contentDescription = context.getString(R.string.logout),
                                                                )
                                                            },
                                                            onClick = {
                                                                coroutineScope.launch {
                                                                    authService.signOut()
                                                                }
                                                            },
                                                        )
                                                    }
                                                }
                                            } else {
                                                IconButton(onClick = { showUserMenu = !showUserMenu }) {
                                                    Icon(
                                                        Icons.Default.AccountCircle,
                                                        contentDescription = context.getString(R.string.user_menu),
                                                    )
                                                    DropdownMenu(expanded = showUserMenu, onDismissRequest = { showUserMenu = false }) {
                                                        DropdownMenuItem(
                                                            text = { Text(context.getString(R.string.logout)) },
                                                            trailingIcon = {
                                                                Icon(
                                                                    Icons.AutoMirrored.Filled.ExitToApp,
                                                                    contentDescription = context.getString(R.string.logout),
                                                                )
                                                            },
                                                            onClick = {
                                                                coroutineScope.launch {
                                                                    authService.signOut()
                                                                }
                                                            },
                                                        )
                                                    }
                                                }
                                            }
                                        else -> {
                                        }
                                    }
                                },
                            )
                        }
                    },
                    modifier = Modifier.safeDrawingPadding(),
                    bottomBar = {
                        val currentDestination = backStackEntry?.destination
                        if (currentScreen != Login && currentScreen != SignUp) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            ) {
                                routes.forEachIndexed { num, route ->
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                route.icon,
                                                contentDescription = route.name,
                                            )
                                        },
                                        label = {
                                            Text(route.name)
                                        },
                                        // if the graph base route is anywhere in the current hierarchy
                                        selected =
                                            currentDestination?.hierarchy?.any {
                                                it.hasRoute(route.content::class)
                                            } == true,
                                        colors =
                                            NavigationBarItemColors(
                                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                                selectedIndicatorColor = MaterialTheme.colorScheme.surface,
                                                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                                                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                                                disabledIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            ),
                                        onClick = {
                                            navController.navigate(route.content)
                                        },
                                    )
                                }
                            }
                        }
                    },
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = Home, modifier = Modifier.padding(innerPadding)) {
                        composable<Home> {
                            val homeViewModel: HomeViewModel by viewModels()
                            HomeScreen(viewModel = homeViewModel, userState = userState, redirectLogin = {
                                navController.navigate(Login) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                }
                            })
                        }
                        navigation<Consume>(startDestination = ConsumptionMainPage) {
                            composable<ConsumptionMainPage> {
                                ConsumptionMainScreen(navController = navController)
                            }
                            // TODO remove placeholder when creating each new screen
                            composable<ExpectedEnergyConsumption> { Text(text = "Consumo de energia") }
                            composable<ExpectedWaterConsumption> { Text(text = "Consumo de água") }
                            composable<ExpectedCarbonFootprint> { Text(text = "Pega de carbono") }
                            composable<RealEnergyConsumption> { Text(text = "Consumo de energia real") }
                            composable<RealWaterConsumption> { Text(text = "Consumo de água real") }
                        }
                        navigation<ColetiveActions>(startDestination = SearchCollectiveActions) {
                            composable<SearchCollectiveActions> { }
                        }
                        navigation<Routines>(startDestination = ViewRoutine) {
                            composable<ViewRoutine> { }
                        }
                        navigation<Authentication>(startDestination = SignUp) {
                            composable<Login> {
                                val loginViewModel: LoginViewModel by viewModels(
                                    extrasProducer = {
                                        defaultViewModelCreationExtras.withCreationCallback<LoginViewModel.Factory> { factory ->
                                            factory.create(
                                                navigateSignUp = {
                                                    navController.navigate(SignUp) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                                navigateSuccess = {
                                                    navController.navigate(Home) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                    }
                                                },
                                            )
                                        }
                                    },
                                )
                                LoginScreen(loginViewModel)
                            }
                            composable<SignUp> {
                                val signUpViewModel: SignUpViewModel by viewModels(
                                    extrasProducer = {
                                        defaultViewModelCreationExtras.withCreationCallback<SignUpViewModel.Factory> { factory ->
                                            factory.create(
                                                navigateLogin = {
                                                    navController.navigate(Login) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                                navigateSuccess = {
                                                    navController.navigate(Home) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                            )
                                        }
                                    },
                                )
                                SignUpScreen(signUpViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
