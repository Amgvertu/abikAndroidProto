package info.prorabka.abik.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import info.prorabka.abik.data.*
import info.prorabka.abik.ui.*
import info.prorabka.abik.ui.theme.*

// ---------- Команда ----------

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TeamInfoScreen(nav: NavController) {
    val team = MockRepo.selectedTeam
    val canManage = MockRepo.canManage(team)
    var contextMember by remember { mutableStateOf<Member?>(null) }
    var numberFor by remember { mutableStateOf<Member?>(null) }
    var showInvite by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Avatar(team.name.replace("ХК ", "").replace("«", "").take(1), size = 56)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(team.name, color = IceText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("${team.city} · ${team.season}", color = IceTextSecondary, fontSize = 13.sp)
                        Text("${team.members.size} участников", color = IceTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
        if (canManage) {
            item {
                Button(
                    onClick = { showInvite = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IcePrimary, contentColor = IceOnPrimary)
                ) {
                    Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Пригласить в команду")
                }
            }
        }
        item { SectionTitle("Состав", Modifier.padding(0.dp)) }
        items(team.members.sortedWith(compareBy({ it.role.ordinal }, { it.number ?: 999 })), key = { it.id }) { m ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { nav.navigate("memberProfile/${m.id}") },
                        onLongClick = { if (canManage) contextMember = m }
                    ),
                shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = IceCard)
            ) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "№${m.number ?: "—"}",
                        color = IcePrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                        modifier = Modifier.width(46.dp)
                    )
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(m.name, color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            if (m.role != Role.PLAYER) { Spacer(Modifier.width(6.dp)); RoleBadge(m.role.label) }
                        }
                    }
                    Text(m.position, color = IceTextSecondary, fontSize = 13.sp)
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
                        onClick = { numberFor = m; contextMember = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = IcePrimary, contentColor = IceOnPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("Назначить игровой номер") }
                    OutlinedButton(
                        onClick = { contextMember = null; nav.navigate("memberProfile/${m.id}") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)
                    ) { Text("Редактировать профиль", color = IcePrimary) }
                    TextButton(onClick = { contextMember = null }, modifier = Modifier.fillMaxWidth()) { Text("Отмена", color = IceTextSecondary) }
                }
            }
        )
    }

    numberFor?.let { m ->
        NumberPickerDialog(team = team, member = m, onClose = { numberFor = null })
    }

    if (showInvite) {
        InviteDialog(team = team, onClose = { showInvite = false })
    }
}

// ---------- Таблица игровых номеров ----------

@Composable
fun NumberPickerDialog(team: Team, member: Member, onClose: () -> Unit) {
    val taken = team.members.mapNotNull { it.number }.filter { it != member.number }.toSet()
    var confirmNumber by remember { mutableStateOf<Int?>(null) }

    Dialog(onDismissRequest = onClose) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = IceSurface)) {
            Column(Modifier.padding(16.dp)) {
                Text("Номер для ${member.name}", color = IceText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Текущий: №${member.number ?: "—"} · зачёркнутые заняты", color = IceTextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items((1..99).toList()) { n ->
                        val isTaken = n in taken
                        val isMine = n == member.number
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                isMine -> IcePrimary.copy(alpha = 0.25f)
                                isTaken -> IceCard
                                else -> IceCardLight
                            },
                            modifier = Modifier.clickable(enabled = !isTaken) { confirmNumber = n }
                        ) {
                            Text(
                                "$n",
                                Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center, fontSize = 13.sp,
                                color = when {
                                    isMine -> IcePrimary
                                    isTaken -> IceTextSecondary
                                    else -> IceText
                                },
                                textDecoration = if (isTaken) TextDecoration.LineThrough else null
                            )
                        }
                    }
                }
                TextButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) { Text("Закрыть", color = IceTextSecondary) }
            }
        }
    }

    confirmNumber?.let { n ->
        AlertDialog(
            onDismissRequest = { confirmNumber = null },
            containerColor = IceSurface,
            title = { Text("Подтверждение", color = IceText) },
            text = { Text("Присвоить номер №$n игроку ${member.name}?", color = IceTextSecondary) },
            confirmButton = {
                Button(
                    onClick = { MockRepo.assignNumber(team, member, n); confirmNumber = null; onClose() },
                    colors = ButtonDefaults.buttonColors(containerColor = IcePrimary, contentColor = IceOnPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Присвоить") }
            },
            dismissButton = { TextButton(onClick = { confirmNumber = null }) { Text("Отмена", color = IceTextSecondary) } }
        )
    }
}

// ---------- Приглашение в команду ----------

@Composable
private fun InviteDialog(team: Team, onClose: () -> Unit) {
    var phone by remember { mutableStateOf("+7 ") }
    var sent by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = IceSurface,
        title = { Text("Приглашение в ${team.name}", color = IceText) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    phone, { phone = it }, label = { Text("Номер телефона") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(10.dp)
                )
                Text("Приглашение действует 7 дней. Можно отправить только зарегистрированному пользователю.", color = IceTextSecondary, fontSize = 12.sp)
                if (sent) Text("✅ Приглашение отправлено (mock)", color = Success, fontSize = 13.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (phone.length > 4 && !sent) {
                        MockRepo.invites.add(0, Invite((MockRepo.invites.maxOfOrNull { it.id } ?: 0) + 1, team.name, phone, "21.10.2026", incoming = false, status = "Ожидает"))
                        sent = true
                    } else onClose()
                },
                colors = ButtonDefaults.buttonColors(containerColor = IcePrimary, contentColor = IceOnPrimary),
                shape = RoundedCornerShape(10.dp)
            ) { Text(if (sent) "Готово" else "Отправить") }
        },
        dismissButton = { TextButton(onClick = onClose) { Text("Отмена", color = IceTextSecondary) } }
    )
}

// ---------- Карточка участника (детальная информация) ----------

@Composable
fun MemberProfileScreen(nav: NavController, memberId: Int) {
    val team = MockRepo.selectedTeam
    val m = team.members.find { it.id == memberId } ?: return
    val canManage = MockRepo.canManage(team)
    var editMode by remember { mutableStateOf(false) }

    Scaffold(containerColor = IceBg, topBar = { AbikTopBar("Участник", team.name, onBack = { nav.popBackStack() }) }) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Avatar(m.name, size = 72)
                        Spacer(Modifier.height(8.dp))
                        Text(m.name, color = IceText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("№${m.number ?: "—"} · ${m.position}", color = IceTextSecondary)
                            if (m.role != Role.PLAYER) { Spacer(Modifier.width(6.dp)); RoleBadge(m.role.label) }
                        }
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoRow("Хват", m.grip)
                        InfoRow("Рост / вес", "${m.height} см / ${m.weight} кг")
                        InfoRow("Джерси", m.jerseySize)
                        InfoRow("Чехлы", m.coverSize)
                        InfoRow("Гамаши", m.gaiterSize)
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Баланс", Modifier.weight(1f), color = IceTextSecondary)
                        BalanceBadge(m.balance)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { nav.navigate("memberFinance/${m.id}") },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                    ) { Text("Финансы", color = IcePrimary, fontSize = 13.sp) }
                    if (canManage) {
                        OutlinedButton(
                            onClick = { editMode = true },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                        ) { Text("Редактировать", color = IcePrimary, fontSize = 13.sp) }
                    }
                }
            }
        }
    }

    if (editMode) {
        MemberEditDialog(m, onClose = { editMode = false })
    }
}

@Composable
private fun MemberEditDialog(m: Member, onClose: () -> Unit) {
    var height by remember { mutableStateOf(m.height.toString()) }
    var weight by remember { mutableStateOf(m.weight.toString()) }
    var position by remember { mutableStateOf(m.position) }
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = IceSurface,
        title = { Text("Редактирование: ${m.name}", color = IceText, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(position, { position = it }, label = { Text("Амплуа") }, singleLine = true, shape = RoundedCornerShape(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        height, { height = it.filter(Char::isDigit) }, label = { Text("Рост, см") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        weight, { weight = it.filter(Char::isDigit) }, label = { Text("Вес, кг") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)
                    )
                }
                Text("Изменения сохраняются локально (mock)", color = IceTextSecondary, fontSize = 11.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = IcePrimary, contentColor = IceOnPrimary),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onClose) { Text("Отмена", color = IceTextSecondary) } }
    )
}
