package info.prorabka.abik.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import info.prorabka.abik.data.*
import info.prorabka.abik.ui.*
import info.prorabka.abik.ui.theme.*

// ---------- Детальные финансы участника (короткий тап из Абонемента) ----------

@Composable
fun MemberFinanceScreen(nav: NavController, memberId: Int) {
    val team = MockRepo.selectedTeam
    val m = team.members.find { it.id == memberId } ?: return
    val teamCharges = MockRepo.chargesOf(team.id).filter { it.status != ChargeStatus.ARCHIVED && it.status != ChargeStatus.CANCELLED }
    val memberPayments = MockRepo.paymentsOf(team.id).filter { it.memberName == m.name }
    val canManage = MockRepo.canManage(team)

    Scaffold(containerColor = IceBg, topBar = { AbikTopBar(m.name, "Начисления и платежи", onBack = { nav.popBackStack() }) }) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        "Баланс",
                        if (m.balance < 0) "−" + rub(m.balance) else rub(m.balance),
                        if (m.balance < 0) Danger else Success, Modifier.weight(1f)
                    )
                    StatCard("Всего внесено", rub(memberPayments.sumOf { it.amount }), IceText, Modifier.weight(1f))
                }
            }
            item { SectionTitle("Начисления (${teamCharges.size})", Modifier.padding(0.dp)) }
            items(teamCharges, key = { "ch${it.id}" }) { ch ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${ch.group} · ${ch.subgroup}", color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${ch.periodic} · от ${ch.date}", color = IceTextSecondary, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(rub(ch.amount), color = IceText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            StatusChip(ch.status)
                        }
                    }
                }
            }
            item { SectionTitle("Платежи (${memberPayments.size})", Modifier.padding(0.dp)) }
            if (memberPayments.isEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                        Text("Платежей пока нет", Modifier.padding(14.dp), color = IceTextSecondary, fontSize = 13.sp)
                    }
                }
            }
            items(memberPayments, key = { "p${it.id}" }) { p ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(p.date, color = IceText, fontSize = 14.sp)
                            Text("внёс ${p.acceptedBy}", color = IceTextSecondary, fontSize = 12.sp)
                        }
                        Text("+" + rub(p.amount), color = Success, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (canManage) {
                item {
                    Button(
                        onClick = { nav.navigate("paymentAdd?memberId=${m.id}") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Success, contentColor = IceOnPrimary)
                    ) { Text("Внести платёж", fontSize = 15.sp) }
                }
            }
        }
    }
}

// ---------- Создание начисления ----------

@Composable
fun CreateChargeScreen(nav: NavController) {
    val team = MockRepo.selectedTeam
    var type by remember { mutableIntStateOf(0) } // 0 разовое, 1 систематическое
    var group by remember { mutableStateOf("") }
    var subgroup by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dayOfMonth by remember { mutableStateOf("5") }
    var distribution by remember { mutableIntStateOf(0) } // 0 поровну, 1 индивидуально
    val selected = remember { mutableStateMapOf<Int, Boolean>().apply { team.members.forEach { put(it.id, true) } } }
    val groupSuggestions = MockRepo.charges.map { it.group }.distinct()
    val amountInt = amount.toIntOrNull() ?: 0
    val selectedCount = selected.count { it.value }

    Scaffold(containerColor = IceBg, topBar = { AbikTopBar("Новое начисление", team.name, onBack = { nav.popBackStack() }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Разовое", "Систематическое").forEachIndexed { i, t ->
                    FilterChip(selected = type == i, onClick = { type = i }, label = { Text(t) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IcePrimary.copy(alpha = 0.2f), selectedLabelColor = IcePrimary, containerColor = IceCard, labelColor = IceTextSecondary))
                }
            }
            OutlinedTextField(group, { group = it }, label = { Text("Группа (напр. Лёд)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            if (group.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    groupSuggestions.filter { it.startsWith(group, true) && it != group }.take(3).forEach {
                        AssistChip(onClick = { group = it }, label = { Text(it, fontSize = 12.sp) })
                    }
                }
            }
            OutlinedTextField(subgroup, { subgroup = it }, label = { Text("Подгруппа (напр. Тренировки, октябрь)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(
                amount, { amount = it.filter(Char::isDigit) }, label = { Text("Сумма, ₽") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )
            if (type == 1) {
                OutlinedTextField(
                    dayOfMonth, { dayOfMonth = it.filter(Char::isDigit).take(2) }, label = { Text("День месяца (1–28)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
            }
            SectionTitle("Распределение", Modifier.padding(0.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Поровну между выбранными", "Индивидуально").forEachIndexed { i, t ->
                    FilterChip(selected = distribution == i, onClick = { distribution = i }, label = { Text(t, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IcePrimary.copy(alpha = 0.2f), selectedLabelColor = IcePrimary, containerColor = IceCard, labelColor = IceTextSecondary))
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                SectionTitle("Участники ($selectedCount)", Modifier.padding(0.dp))
                TextButton(onClick = {
                    val all = selectedCount < team.members.size
                    team.members.forEach { selected[it.id] = all }
                }) { Text(if (selectedCount < team.members.size) "Выбрать всех" else "Снять выбор", color = IcePrimary, fontSize = 13.sp) }
            }
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                Column {
                    team.members.forEachIndexed { idx, m ->
                        Row(
                            Modifier.fillMaxWidth().clickable { selected[m.id] = !(selected[m.id] ?: true) }.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selected[m.id] ?: true, onCheckedChange = { selected[m.id] = it },
                                colors = CheckboxDefaults.colors(checkedColor = IcePrimary, uncheckedColor = IceTextSecondary)
                            )
                            Text("№${m.number ?: "—"} ${m.name}", color = IceText, fontSize = 14.sp)
                        }
                        if (idx < team.members.size - 1) HorizontalDivider(color = IceCardLight, thickness = 0.5.dp)
                    }
                }
            }
            if (amountInt > 0 && selectedCount > 0) {
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = IceCardLight)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Итого: ", color = IceTextSecondary)
                        Text("${rub(amountInt)} с человека × $selectedCount = ${rub(amountInt * selectedCount)}", color = IceText, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Button(
                onClick = {
                    if (group.isNotBlank() && amountInt > 0 && selectedCount > 0) {
                        MockRepo.addCharge(team.id, group, subgroup.ifBlank { "—" }, amountInt, if (type == 1) "Ежемесячно" else "Разовое", selectedCount)
                        nav.popBackStack()
                    }
                },
                enabled = group.isNotBlank() && amountInt > 0 && selectedCount > 0,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IcePrimary, contentColor = IceOnPrimary)
            ) { Text("Создать начисление", fontSize = 16.sp) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ---------- Шаблоны ----------

@Composable
fun TemplatesScreen(nav: NavController) {
    Scaffold(containerColor = IceBg, topBar = { AbikTopBar("Шаблоны начислений", onBack = { nav.popBackStack() }) }) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(MockRepo.templates) { t ->
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(t.name, color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text("${t.group} · ${t.subgroup} · ${t.periodic}", color = IceTextSecondary, fontSize = 12.sp)
                        }
                        Text(rub(t.amount), color = IceText, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(10.dp))
                        OutlinedButton(onClick = { nav.navigate("chargeCreate") }, shape = RoundedCornerShape(10.dp)) { Text("Применить", color = IcePrimary, fontSize = 12.sp) }
                    }
                }
            }
        }
    }
}

// ---------- Внесение платежа ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentScreen(nav: NavController, preselectedMemberId: Int) {
    val team = MockRepo.selectedTeam
    var memberIdx by remember { mutableIntStateOf(team.members.indexOfFirst { it.id == preselectedMemberId }) }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val amountInt = amount.toIntOrNull() ?: 0
    val member = team.members.getOrNull(memberIdx)

    Scaffold(containerColor = IceBg, topBar = { AbikTopBar("Внесение платежа", team.name, onBack = { nav.popBackStack() }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Платёж вносит владелец или модератор. Распределение автоматическое: сначала старые долги, потом новые.", color = IceTextSecondary, fontSize = 13.sp)

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = member?.name ?: "", onValueChange = {}, readOnly = true,
                    label = { Text("Участник") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable), shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    team.members.forEachIndexed { i, m ->
                        DropdownMenuItem(text = { Text("${m.name}  (${if (m.balance < 0) "долг " + rub(m.balance) else "без долга"})") }, onClick = { memberIdx = i; expanded = false })
                    }
                }
            }

            OutlinedTextField(
                amount, { amount = it.filter(Char::isDigit) }, label = { Text("Сумма, ₽") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )
            if (member != null && member.balance < 0) {
                OutlinedButton(onClick = { amount = (-member.balance).toString() }, shape = RoundedCornerShape(10.dp)) {
                    Text("Закрыть долг (${rub(member.balance)})", color = IcePrimary, fontSize = 13.sp)
                }
            }

            if (member != null && amountInt > 0) {
                val newBalance = member.balance + amountInt
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = IceCardLight)) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Предпросмотр автораспределения", color = IceText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        if (member.balance < 0) {
                            val covered = minOf(amountInt, -member.balance)
                            Text("• закроет долг: ${rub(covered)}", color = IceTextSecondary, fontSize = 13.sp)
                        }
                        if (newBalance > 0) Text("• переплата (баланс): ${rub(newBalance)}", color = Success, fontSize = 13.sp)
                        if (newBalance <= 0 && member.balance >= 0) Text("• уйдёт в баланс (аванс): ${rub(amountInt)}", color = IcePrimary, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = { if (member != null && amountInt > 0) { MockRepo.addPayment(team.id, member, amountInt); nav.popBackStack() } },
                enabled = member != null && amountInt > 0,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Success, contentColor = IceOnPrimary)
            ) { Text("Внести платёж", fontSize = 16.sp) }
        }
    }
}

// ---------- Отчёты ----------

@Composable
fun ReportsScreen(nav: NavController) {
    val team = MockRepo.selectedTeam
    var period by remember { mutableIntStateOf(0) }
    var groupFilter by remember { mutableStateOf("Все группы") }
    var exported by remember { mutableStateOf(false) }
    Scaffold(containerColor = IceBg, topBar = { AbikTopBar("Отчёты и экспорт", team.name, onBack = { nav.popBackStack() }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle("Период", Modifier.padding(0.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Месяц", "Сезон", "Всё время").forEachIndexed { i, t ->
                    FilterChip(selected = period == i, onClick = { period = i }, label = { Text(t) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IcePrimary.copy(alpha = 0.2f), selectedLabelColor = IcePrimary, containerColor = IceCard, labelColor = IceTextSecondary))
                }
            }
            SectionTitle("Фильтры", Modifier.padding(0.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Все группы", "Лёд", "Форма", "Турниры", "Штрафы").forEach {
                    FilterChip(selected = groupFilter == it, onClick = { groupFilter = it }, label = { Text(it, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IcePrimary.copy(alpha = 0.2f), selectedLabelColor = IcePrimary, containerColor = IceCard, labelColor = IceTextSecondary))
                }
            }
            val debt = MockRepo.teamDebt(team)
            val paid = MockRepo.paymentsOf(team.id).sumOf { it.amount }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Начислено", rub(MockRepo.chargesOf(team.id).sumOf { it.total }), IceText, Modifier.weight(1f))
                StatCard("Оплачено", rub(paid), Success, Modifier.weight(1f))
                StatCard("Остаток", rub(debt), Danger, Modifier.weight(1f))
            }
            Spacer(Modifier.weight(1f))
            if (exported) Text("✅ Отчёт «abik_${team.name.replace(" ", "_")}.xlsx» сформирован (mock)", color = Success, fontSize = 13.sp)
            Button(
                onClick = { exported = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IcePrimary, contentColor = IceOnPrimary)
            ) { Text("Экспортировать в Excel", fontSize = 16.sp) }
            Text("Доступно владельцу и модератору. Архив виден всем участникам.", color = IceTextSecondary, fontSize = 12.sp)
        }
    }
}
