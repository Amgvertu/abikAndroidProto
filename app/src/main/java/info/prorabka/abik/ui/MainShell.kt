package info.prorabka.abik.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import info.prorabka.abik.data.MockRepo
import info.prorabka.abik.data.rub
import info.prorabka.abik.ui.theme.*

/**
 * Верхний тулбар по ТЗ: логотип команды, команда (выпадающий список),
 * баланс, значок уведомлений, меню.
 */
@Composable
fun MainTopBar(nav: NavController) {
    var teamMenu by remember { mutableStateOf(false) }
    var mainMenu by remember { mutableStateOf(false) }
    val team = MockRepo.selectedTeam
    val balance = MockRepo.myBalance(team)
    val unread = MockRepo.notifications.count { it.unread }

    Surface(color = IceSurface) {
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // логотип команды
            Box(
                Modifier.size(36.dp).clip(CircleShape).clickable { teamMenu = true },
                contentAlignment = Alignment.Center
            ) {
                Surface(shape = CircleShape, color = IceCardLight) {
                    Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                        Text(
                            team.name.replace("ХК ", "").replace("«", "").take(1),
                            color = IcePrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp
                        )
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            // команда (выпадающий список)
            Box {
                Row(
                    Modifier.clip(RoundedCornerShape(8.dp)).clickable { teamMenu = true }.padding(horizontal = 6.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        team.name, color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                        maxLines = 1, modifier = Modifier.widthIn(max = 130.dp)
                    )
                    Icon(Icons.Default.ArrowDropDown, null, tint = IceTextSecondary, modifier = Modifier.size(20.dp))
                }
                DropdownMenu(expanded = teamMenu, onDismissRequest = { teamMenu = false }) {
                    MockRepo.teams.forEach { t ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(t.name, fontWeight = if (t.id == team.id) FontWeight.Bold else FontWeight.Normal)
                                    if (t.id == team.id) { Spacer(Modifier.width(6.dp)); Text("✓", color = IcePrimary) }
                                }
                            },
                            onClick = { MockRepo.selectedTeamId = t.id; teamMenu = false }
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("+ Создать команду", color = IcePrimary) },
                        onClick = { teamMenu = false; nav.navigate("createTeam") }
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            // баланс
            val bColor = when {
                balance < 0 -> Danger
                balance > 0 -> Success
                else -> IceTextSecondary
            }
            Surface(shape = RoundedCornerShape(8.dp), color = bColor.copy(alpha = 0.14f)) {
                Text(
                    if (balance < 0) "−" + rub(balance) else rub(balance),
                    color = bColor, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }
            // уведомления
            IconButton(onClick = { nav.navigate("notifications") }) {
                BadgedBox(badge = { if (unread > 0) Badge { Text("$unread") } }) {
                    Icon(Icons.Default.Notifications, null, tint = IceText)
                }
            }
            // меню
            Box {
                IconButton(onClick = { mainMenu = true }) {
                    Icon(Icons.Default.Menu, null, tint = IceText)
                }
                DropdownMenu(expanded = mainMenu, onDismissRequest = { mainMenu = false }) {
                    DropdownMenuItem(text = { Text("Приглашения") }, leadingIcon = { Icon(Icons.Default.MailOutline, null) },
                        onClick = { mainMenu = false; nav.navigate("invitations") })
                    DropdownMenuItem(text = { Text("Шаблоны начислений") }, leadingIcon = { Icon(Icons.Default.ContentCopy, null) },
                        onClick = { mainMenu = false; nav.navigate("templates") })
                    DropdownMenuItem(text = { Text("Отчёты и экспорт") }, leadingIcon = { Icon(Icons.Default.Assessment, null) },
                        onClick = { mainMenu = false; nav.navigate("reports") })
                    DropdownMenuItem(text = { Text("Настройки") }, leadingIcon = { Icon(Icons.Default.Settings, null) },
                        onClick = { mainMenu = false; nav.navigate("settings") })
                    HorizontalDivider()
                    DropdownMenuItem(text = { Text("Выйти", color = Danger) }, leadingIcon = { Icon(Icons.Default.ExitToApp, null, tint = Danger) },
                        onClick = { mainMenu = false; nav.navigate("login") { popUpTo(0) { inclusive = true } } })
                }
            }
        }
    }
}

/** Нижнее меню по ТЗ: главная, абонемент, игры, команда, профиль */
@Composable
fun MainBottomBar(current: String?, onSelect: (String) -> Unit) {
    NavigationBar(containerColor = IceSurface) {
        val items = listOf(
            Triple("home", "Главная", Icons.Default.Home),
            Triple("subscription", "Абонемент", Icons.Default.Wallet),
            Triple("games", "Игры", Icons.Default.CalendarMonth),
            Triple("team", "Команда", Icons.Default.Groups),
            Triple("profile", "Профиль", Icons.Default.Person)
        )
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = current == route,
                onClick = { onSelect(route) },
                icon = { Icon(icon, null) },
                label = { Text(label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IcePrimary, selectedTextColor = IcePrimary,
                    unselectedIconColor = IceTextSecondary, unselectedTextColor = IceTextSecondary,
                    indicatorColor = IcePrimary.copy(alpha = 0.15f)
                )
            )
        }
    }
}
