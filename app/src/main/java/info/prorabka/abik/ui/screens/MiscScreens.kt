package info.prorabka.abik.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import info.prorabka.abik.data.MockRepo
import info.prorabka.abik.ui.*
import info.prorabka.abik.ui.theme.*

@Composable
fun NotificationsScreen(nav: NavController) {
    Scaffold(containerColor = IceBg, topBar = { AbikTopBar("Уведомления", onBack = { nav.popBackStack() }) }) { padding ->
        val list = MockRepo.notifications
        if (list.isEmpty()) EmptyState("Уведомлений нет")
        else LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(list) { n ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = if (n.unread) IceCardLight else IceCard)
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Text(n.icon, fontSize = 22.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(n.title, Modifier.weight(1f), color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                if (n.unread) Box(Modifier.size(8.dp)) { Badge {} }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(n.body, color = IceTextSecondary, fontSize = 13.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(n.time, color = IceTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(nav: NavController) {
    val team = MockRepo.selectedTeam
    val me = team.members.find { it.name == MockRepo.currentUserName }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Avatar(MockRepo.currentUserName, size = 80)
                Spacer(Modifier.height(8.dp))
                Text(MockRepo.currentUserName, color = IceText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(MockRepo.currentUserPhone, color = IceTextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                // игровой номер — без возможности редактирования
                Surface(shape = RoundedCornerShape(10.dp), color = IceCardLight) {
                    Column(
                        Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("ИГРОВОЙ НОМЕР", color = IceTextSecondary, fontSize = 10.sp)
                        Text("№${me?.number ?: "—"}", color = IcePrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text("Номер присваивает владелец или модератор", color = IceTextSecondary, fontSize = 11.sp)
            }
        }
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle("Личные данные", Modifier.padding(0.dp))
                InfoRow("Фамилия", "Прохоров")
                InfoRow("Имя", "Александр")
                InfoRow("Отчество", "Сергеевич")
                InfoRow("Дата рождения", "14.03.1988")
                InfoRow("Регион / город", "Московская область / Москва")
            }
        }
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle("Хоккейный профиль", Modifier.padding(0.dp))
                InfoRow("Амплуа", me?.position ?: "Нападающий")
                InfoRow("Хват", me?.grip ?: "Левый")
                InfoRow("Рост", "${me?.height ?: 183} см")
                InfoRow("Вес", "${me?.weight ?: 86} кг")
                InfoRow("Джерси / чехлы / гамаши", "${me?.jerseySize ?: "L"} / ${me?.coverSize ?: "M"} / ${me?.gaiterSize ?: "L"}")
                Text("Профиль виден другим участникам команд", color = IceTextSecondary, fontSize = 11.sp)
            }
        }
        OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Редактировать профиль", color = IcePrimary) }
        OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Сменить номер телефона", color = IcePrimary) }
    }
}

@Composable
fun SettingsScreen(nav: NavController) {
    var lang by remember { mutableStateOf("Русский") }
    Scaffold(containerColor = IceBg, topBar = { AbikTopBar("Настройки", onBack = { nav.popBackStack() }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionTitle("Язык", Modifier.padding(0.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Русский", "English").forEach {
                            FilterChip(selected = lang == it, onClick = { lang = it }, label = { Text(it) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IcePrimary.copy(alpha = 0.2f), selectedLabelColor = IcePrimary, containerColor = IceCard, labelColor = IceTextSecondary))
                        }
                    }
                }
            }
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                Column(Modifier.padding(16.dp)) {
                    SectionTitle("Уведомления", Modifier.padding(0.dp))
                    Text("Push-уведомления (FCM) включены для всех событий: приглашения, начисления, платежи, напоминания о долге. По ТЗ отключение не предусмотрено.", color = IceTextSecondary, fontSize = 13.sp)
                }
            }
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionTitle("Аккаунт", Modifier.padding(0.dp))
                    TextButton(onClick = { }) { Text("Согласие на обработку ПД (v1.2)", color = IcePrimary) }
                    TextButton(onClick = { }) { Text("Удалить аккаунт", color = Danger) }
                    Text("При удалении история сохраняется, аккаунт получает статус DELETED", color = IceTextSecondary, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.weight(1f))
            Text("Абик · версия 0.2.0-proto (mock)", color = IceTextSecondary, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
