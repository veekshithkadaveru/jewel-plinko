package app.krafted.jewelplinko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
                    val gameViewModel: GameViewModel = viewModel()
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
                            BetScreen(
                                onStartSession = {
                                    if (gameViewModel.startSession(bet = 50, ballPackage = 1)) {
                                        navController.navigate("game")
                                    }
                                }
                            )
                        }
                        composable("game") {
                            GameScreen(
                                onSessionComplete = { navController.navigate("result") },
                                vm = gameViewModel
                            )
                        }
                        composable("result") {
                            SessionResultScreen(
                                onBackToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                                onPlayAgain = { navController.navigate("bet") { popUpTo("home") } }
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
