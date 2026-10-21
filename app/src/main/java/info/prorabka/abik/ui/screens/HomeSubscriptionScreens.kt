package info.prorabka.abik.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import info.prorabka.abik.data.*
import info.prorabka.abik.ui.*
import info.prorabka.abik.ui.theme.*

// ---------- Главная ----------

@Composable
fun HomeScreen(nav: NavController) {
    val team = MockRepo.selectedTeam
    val myBalance = MockRepo.myBalance(team)
    val next = MockRepo.nextEvent(team.id)
    val debt = MockRepo.teamDebt(team)

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Привет, Александр! 👋", color = IceText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(team.name, color = IceTextSecondary, fontSize = 13.sp)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Мой баланс", if (myBalance < 0) "−" + rub(myBalance) else rub(myBalance),
                    if (myBalance < 0) Danger else Success, Modifier.weight(1f))
                StatCard("Долг команды", rub(debt), if (debt > 0) Danger else Success, Modifier.weight(1f))
            }
        }
        if (next != null) {
            item {
                Card(
                    Modifier.fillMaxWidth().clickable { nav.navigate("games") },
                    shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = IceCard)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Ближайшее событие", color = IceTextSecondary, fontSize = 12.sp)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (next.type == EventType.GAME) "🏒" else "🥅", fontSize = 26.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    if (next.type == EventType.GAME) "Игра: ${next.title}" else "Тренировка",
                                    color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp
                                )
                                Text("${next.date} · ${next.time}", color = IceTextSecondary, fontSize = 13.sp)
                                Text(next.place, color = IceTextSecondary, fontSize = 12.sp)
                            }
                        }
                        val myVote = next.poll.votes[MockRepo.currentUserName]
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (myVote != null) "Ваш ответ: $myVote" else "Вы ещё не ответили на опрос",
                            color = if (myVote != null) Success else Warning, fontSize = 12.sp
                        )
                    }
                }
            }
        }
        item { SectionTitle("Последние уведомления", Modifier.padding(0.dp)) }
        items(MockRepo.notifications.take(3)) { n ->
            Card(
                Modifier.fillMaxWidth().clickable { nav.navigate("notifications") },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (n.unread) IceCardLight else IceCard)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(n.icon, fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(n.title, color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(n.time, color = IceTextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ---------- Абонемент ----------

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubscriptionScreen(nav: NavController) {
    val team = MockRepo.selectedTeam
    val canManage = MockRepo.canManage(team)
    var contextMember by remember { mutableStateOf<Member?>(null) }

    Scaffold(
        containerColor = IceBg,
        floatingActionButton = {
            if (canManage) {
                FloatingActionButton(
                    onClick = { nav.navigate("chargeCreate") },
                    containerColor = IcePrimary, contentColor = IceOnPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) { Icon(Icons.Default.Add, null) }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            // шапка таблицы
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Абонемент · ${team.name}", color = IceTextSecondary, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("${team.members.size} чел.", color = IceTextSecondary, fontSize = 13.sp)
            }
            LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(team.members.sortedWith(compareBy({ it.role.ordinal }, { it.number ?: 999 })), key = { it.id }) { m ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { nav.navigate("memberFinance/${m.id}") },
                                onLongClick = { if (canManage) contextMember = m }
                            ),
                        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = IceCard)
                    ) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Avatar(m.name, size = 38)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(m.name, color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("№${m.number ?: "—"} · ${m.position}", color = IceTextSecondary, fontSize = 12.sp)
                            }
                            BalanceBadge(m.balance)
                        }
                    }
                }
            }
        }
    }

    // контекстное меню по длинному тапу
    contextMember?.let { m ->
        AlertDialog(
            onDismissRequest = { contextMember = null },
            containerColor = IceSurface,
            title = { Text(m.name, color = IceText) },
            text = { Text("№${m.number ?: "—"} · ${m.position}", color = IceTextSecondary) },
            confirmButton = {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { contextMember = null; nav.navigate("paymentAdd?memberId=${m.id}") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Success, contentColor = IceOnPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Внести платёж") }
                    OutlinedButton(
                        onClick = { contextMember = null; nav.navigate("chargeCreate") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)
                    ) { Text("Сделать начисление", color = IcePrimary) }
                    TextButton(onClick = { contextMember = null }, modifier = Modifier.fillMaxWidth()) { Text("Отмена", color = IceTextSecondary) }
                }
            }
        )
    }
}
