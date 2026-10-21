package info.prorabka.abik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import info.prorabka.abik.ui.MainBottomBar
import info.prorabka.abik.ui.MainTopBar
import info.prorabka.abik.ui.screens.*
import info.prorabka.abik.ui.theme.AbikTheme
import info.prorabka.abik.ui.theme.IceBg

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AbikTheme {
                val nav = rememberNavController()
                val backStack by nav.currentBackStackEntryAsState()
                val route = backStack?.destination?.route
                val tabRoutes = listOf("home", "subscription", "games", "team", "profile")
                val showBars = route in tabRoutes

                Scaffold(
                    containerColor = IceBg,
                    topBar = { if (showBars) MainTopBar(nav) },
                    bottomBar = {
                        if (showBars) MainBottomBar(route) { dest ->
                            nav.navigate(dest) { popUpTo("home") { saveState = true }; launchSingleTop = true; restoreState = true }
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController = nav,
                        startDestination = "splash",
                        modifier = Modifier.padding(padding)
                    ) {
                        composable("splash") { SplashScreen(nav) }
                        composable("login") { LoginScreen(nav) }
                        composable("register") { RegisterScreen(nav) }
                        composable("forgot") { ForgotPasswordScreen(nav) }

                        composable("home") { HomeScreen(nav) }
                        composable("subscription") { SubscriptionScreen(nav) }
                        composable("games") { GamesScreen(nav) }
                        composable("team") { TeamInfoScreen(nav) }
                        composable("profile") { ProfileScreen(nav) }

                        composable(
                            "memberFinance/{memberId}",
                            arguments = listOf(navArgument("memberId") { type = NavType.IntType })
                        ) { MemberFinanceScreen(nav, it.arguments!!.getInt("memberId")) }
                        composable(
                            "memberProfile/{memberId}",
                            arguments = listOf(navArgument("memberId") { type = NavType.IntType })
                        ) { MemberProfileScreen(nav, it.arguments!!.getInt("memberId")) }

                        composable("createTeam") { CreateTeamScreen(nav) }
                        composable("invitations") { InvitationsScreen(nav) }
                        composable("notifications") { NotificationsScreen(nav) }
                        composable("settings") { SettingsScreen(nav) }
                        composable("chargeCreate") { CreateChargeScreen(nav) }
                        composable(
                            "paymentAdd?memberId={memberId}",
                            arguments = listOf(navArgument("memberId") { type = NavType.IntType; defaultValue = -1 })
                        ) { AddPaymentScreen(nav, it.arguments!!.getInt("memberId")) }
                        composable("templates") { TemplatesScreen(nav) }
                        composable("reports") { ReportsScreen(nav) }
                    }
                }
            }
        }
    }
}
