package info.prorabka.abik.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import info.prorabka.abik.data.*
import info.prorabka.abik.ui.*
import info.prorabka.abik.ui.theme.*

private const val DAYS_IN_MONTH = 31      // октябрь 2026
private const val FIRST_DAY_OFFSET = 3    // 1 октября 2026 — четверг (Пн=0)
private const val TODAY = 20

@Composable
fun GamesScreen(nav: NavController) {
    val team = MockRepo.selectedTeam
    val canManage = MockRepo.canManage(team)
    var tab by remember { mutableIntStateOf(0) }
    var selectedDay by remember { mutableIntStateOf(TODAY) }
    var editEvent by remember { mutableStateOf<TeamEvent?>(null) }

    val type = if (tab == 0) EventType.TRAINING else EventType.GAME
    val monthEvents = MockRepo.eventsOf(team.id, type)
    val eventDays = monthEvents.map { it.day }.toSet()
    val dayEvents = monthEvents.filter { it.day == selectedDay }

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab, containerColor = IceBg, contentColor = IcePrimary) {
            Tab(selected = tab == 0, onClick = { tab = 0 }) {
                Text("Тренировки", Modifier.padding(vertical = 12.dp), color = if (tab == 0) IcePrimary else IceTextSecondary)
            }
            Tab(selected = tab == 1, onClick = { tab = 1 }) {
                Text("Регулярка", Modifier.padding(vertical = 12.dp), color = if (tab == 1) IcePrimary else IceTextSecondary)
            }
        }

        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Октябрь 2026", color = IceText, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(8.dp))
                        // дни недели
                        Row(Modifier.fillMaxWidth()) {
                            listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach {
                                Text(it, Modifier.weight(1f), color = IceTextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        val cells: List<Int?> = List(FIRST_DAY_OFFSET) { null } + (1..DAYS_IN_MONTH).toList()
                        cells.chunked(7).forEach { week ->
                            Row(Modifier.fillMaxWidth()) {
                                week.forEach { day ->
                                    Box(Modifier.weight(1f).aspectRatio(1f).padding(2.dp), contentAlignment = Alignment.Center) {
                                        if (day != null) {
                                            val hasEvent = day in eventDays
                                            val isSelected = day == selectedDay
                                            Box(
                                                Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .then(
                                                        when {
                                                            isSelected -> Modifier.border(2.dp, IcePrimary, CircleShape)
                                                            hasEvent -> Modifier.border(1.5.dp, Warning, CircleShape)
                                                            else -> Modifier
                                                        }
                                                    )
                                                    .clickable { selectedDay = day },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "$day",
                                                    color = when {
                                                        isSelected -> IcePrimary
                                                        day == TODAY -> Warning
                                                        hasEvent -> IceText
                                                        else -> IceTextSecondary
                                                    },
                                                    fontSize = 13.sp,
                                                    fontWeight = if (hasEvent || day == TODAY) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                                repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).border(1.5.dp, Warning, CircleShape))
                            Spacer(Modifier.width(4.dp))
                            Text("— есть ${type.label.lowercase()}", color = IceTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }

            item {
                Text(
                    "События $selectedDay.10.2026",
                    color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp
                )
            }

            if (dayEvents.isEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                        Text(
                            "В этот день ${if (type == EventType.TRAINING) "тренировок" else "игр"} нет",
                            Modifier.padding(16.dp), color = IceTextSecondary, fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(dayEvents, key = { it.id }) { ev ->
                    EventCard(ev, canManage, onEdit = { editEvent = ev })
                }
            }
        }
    }

    editEvent?.let { ev ->
        EventEditDialog(ev, onClose = { editEvent = null })
    }
}

@Composable
private fun EventCard(ev: TeamEvent, canManage: Boolean, onEdit: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (ev.type == EventType.GAME) "🏒" else "🥅", fontSize = 24.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        if (ev.type == EventType.GAME) "Игра: ${ev.title}" else "Тренировка",
                        color = IceText, fontWeight = FontWeight.Bold, fontSize = 16.sp
                    )
                    Text("${ev.date} · ${ev.time}", color = IceText, fontSize = 14.sp)
                    Text("📍 ${ev.place}", color = IceTextSecondary, fontSize = 13.sp)
                }
            }
            if (canManage) {
                TextButton(onClick = onEdit, modifier = Modifier.align(Alignment.End)) {
                    Text("Редактировать", color = IcePrimary, fontSize = 13.sp)
                }
            }
            HorizontalDivider(color = IceCardLight, modifier = Modifier.padding(vertical = 10.dp))
            PollView(ev.poll)
        }
    }
}

@Composable
private fun PollView(poll: Poll) {
    val myVote = poll.votes[MockRepo.currentUserName]
    Text(poll.question, color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Spacer(Modifier.height(8.dp))
    val total = poll.votes.size.coerceAtLeast(1)
    poll.options.forEach { option ->
        val count = poll.count(option)
        val mine = myVote == option
        Surface(
            Modifier.fillMaxWidth().padding(vertical = 3.dp).clickable { poll.vote(MockRepo.currentUserName, option) },
            shape = RoundedCornerShape(10.dp),
            color = if (mine) IcePrimary.copy(alpha = 0.2f) else IceCardLight
        ) {
            Row(Modifier.padding(horizontal = 12.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    option, Modifier.weight(1f),
                    color = if (mine) IcePrimary else IceText,
                    fontWeight = if (mine) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp
                )
                Text("$count (${count * 100 / total}%)", color = if (mine) IcePrimary else IceTextSecondary, fontSize = 13.sp)
                if (mine) { Spacer(Modifier.width(6.dp)); Text("✓", color = IcePrimary, fontWeight = FontWeight.Bold) }
            }
        }
    }
    Spacer(Modifier.height(4.dp))
    Text("Ответили: ${poll.votes.size}", color = IceTextSecondary, fontSize = 11.sp)
}

@Composable
private fun EventEditDialog(ev: TeamEvent, onClose: () -> Unit) {
    var time by remember { mutableStateOf(ev.time) }
    var place by remember { mutableStateOf(ev.place) }
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = IceSurface,
        title = { Text(if (ev.type == EventType.GAME) "Игра: ${ev.title}" else "Тренировка ${ev.date}", color = IceText) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(time, { time = it }, label = { Text("Время") }, singleLine = true, shape = RoundedCornerShape(10.dp))
                OutlinedTextField(place, { place = it }, label = { Text("Место") }, singleLine = true, shape = RoundedCornerShape(10.dp))
                Text("Редактирование доступно владельцу и модератору", color = IceTextSecondary, fontSize = 11.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = { ev.time = time; ev.place = place; onClose() },
                colors = ButtonDefaults.buttonColors(containerColor = IcePrimary, contentColor = IceOnPrimary),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onClose) { Text("Отмена", color = IceTextSecondary) } }
    )
}
