package app.krafted.jewelplinko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.krafted.jewelplinko.ui.*
import app.krafted.jewelplinko.ui.theme.JewelPlinkoTheme
import app.krafted.jewelplinko.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JewelPlinkoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val gameViewModel: GameViewModel = viewModel(factory = GameViewModel.Factory)
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen(onSplashComplete = { navController.navigate("home") { popUpTo("splash") { inclusive = true } } })
                        }
                        composable("home") {
                            HomeScreen(
                                onPlayClicked = { navController.navigate("bet") },
                                onLeaderboardClicked = { navController.navigate("leaderboard") }
                            )
                        }
                        composable("bet") {
                            val state by gameViewModel.uiState.collectAsState()
                            BetScreen(
                                coinBalance = state.coinBalance,
                                onStartSession = { bet, ballPackage ->
                                    if (gameViewModel.startSession(bet = bet, ballPackage = ballPackage)) {
                                        navController.navigate("game")
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("game") {
                            GameScreen(
                                onSessionComplete = { navController.navigate("result") },
                                vm = gameViewModel
                            )
                        }
                        composable("result") {
                            val state by gameViewModel.uiState.collectAsState()
                            SessionResultScreen(
                                state = state,
                                onBackToHome = {
                                    gameViewModel.resetSession()
                                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                                },
                                onPlayAgain = {
                                    gameViewModel.resetSession()
                                    navController.navigate("bet") { popUpTo("home") }
                                }
                            )
                        }
                        composable("leaderboard") {
                            LeaderboardScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
