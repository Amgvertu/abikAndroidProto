package info.prorabka.abik.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import info.prorabka.abik.data.MockRepo
import info.prorabka.abik.ui.*
import info.prorabka.abik.ui.theme.*

@Composable
fun CreateTeamScreen(nav: NavController) {
    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var season by remember { mutableStateOf("") }
    Scaffold(containerColor = IceBg, topBar = { AbikTopBar("Новая команда", onBack = { nav.popBackStack() }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Обязательные поля: название, город, регион и логотип", color = IceTextSecondary, fontSize = 13.sp)
            OutlinedTextField(name, { name = it }, label = { Text("Название команды *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(city, { city = it }, label = { Text("Город *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(season, { season = it }, label = { Text("Сезон (необязательно)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Text("Загрузить логотип *", color = IcePrimary)
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { nav.popBackStack() },
                enabled = name.isNotBlank() && city.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IcePrimary, contentColor = IceOnPrimary)
            ) { Text("Создать и стать владельцем", fontSize = 16.sp) }
        }
    }
}

@Composable
fun InvitationsScreen(nav: NavController) {
    var tab by remember { mutableIntStateOf(0) }
    val incoming = MockRepo.invites.filter { it.incoming }
    val outgoing = MockRepo.invites.filter { !it.incoming }
    Scaffold(containerColor = IceBg, topBar = { AbikTopBar("Приглашения", onBack = { nav.popBackStack() }) }) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(selectedTabIndex = tab, containerColor = IceBg, contentColor = IcePrimary) {
                Tab(selected = tab == 0, onClick = { tab = 0 }) { Text("Входящие (${incoming.size})", Modifier.padding(14.dp), color = if (tab == 0) IcePrimary else IceTextSecondary) }
                Tab(selected = tab == 1, onClick = { tab = 1 }) { Text("Исходящие (${outgoing.size})", Modifier.padding(14.dp), color = if (tab == 1) IcePrimary else IceTextSecondary) }
            }
            val list = if (tab == 0) incoming else outgoing
            if (list.isEmpty()) EmptyState("Приглашений нет")
            else LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(list) { inv ->
                    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                        Column(Modifier.padding(14.dp)) {
                            Text(inv.teamName, color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(inv.person, color = IceTextSecondary, fontSize = 13.sp)
                            Text("от ${inv.date} · действует 7 дней", color = IceTextSecondary, fontSize = 12.sp)
                            Spacer(Modifier.height(10.dp))
                            if (inv.status != "Ожидает") {
                                Text("Статус: ${inv.status}", color = Success, fontSize = 13.sp)
                            } else if (inv.incoming) {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Button(onClick = { inv.status = "Принято" }, colors = ButtonDefaults.buttonColors(containerColor = IcePrimary, contentColor = IceOnPrimary), shape = RoundedCornerShape(10.dp)) { Text("Принять") }
                                    OutlinedButton(onClick = { inv.status = "Отклонено" }, shape = RoundedCornerShape(10.dp)) { Text("Отклонить", color = Danger) }
                                }
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedButton(onClick = { inv.status = "Отменено" }, shape = RoundedCornerShape(10.dp)) { Text("Отменить", color = Danger) }
                                    OutlinedButton(onClick = { }, shape = RoundedCornerShape(10.dp)) { Text("Повторить", color = IcePrimary) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
