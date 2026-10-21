package info.prorabka.abik.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import java.util.Locale

// ---------- Модели ----------

enum class Role(val label: String) { OWNER("Владелец"), MODERATOR("Модератор"), PLAYER("Участник") }

enum class ChargeStatus(val label: String) {
    ACTIVE("Активно"), PARTIALLY_PAID("Частично"), PAID("Оплачено"), CANCELLED("Отменено"), ARCHIVED("Архив")
}

class Member(
    val id: Int,
    val name: String,
    numberInit: Int?,
    val position: String,       // амплуа
    val grip: String,           // хват
    val height: Int, val weight: Int,
    val jerseySize: String, val coverSize: String, val gaiterSize: String,
    roleInit: Role,
    balanceInit: Int            // <0 долг, >0 переплата
) {
    var balance by mutableIntStateOf(balanceInit)
    var number by androidx.compose.runtime.mutableStateOf(numberInit)
    var role by androidx.compose.runtime.mutableStateOf(roleInit)
}

data class Team(
    val id: Int,
    val name: String,
    val city: String,
    val season: String,
    val members: MutableList<Member>
)

class Charge(
    val id: Int,
    val teamId: Int,
    val group: String,
    val subgroup: String,
    val amount: Int,            // сумма на участника
    val date: String,
    val periodic: String,       // "Разовое" / "Ежемесячно"
    paidInit: Int,              // собрано
    val total: Int,             // всего к сбору
    statusInit: ChargeStatus
) {
    var paid by mutableIntStateOf(paidInit)
    var status by androidx.compose.runtime.mutableStateOf(statusInit)
}

data class Payment(
    val id: Int,
    val teamId: Int,
    val memberName: String,
    val amount: Int,
    val date: String,
    val acceptedBy: String
)

data class ChargeTemplate(
    val name: String, val group: String, val subgroup: String,
    val amount: Int, val periodic: String
)

data class Invite(
    val id: Int, val teamName: String, val person: String,
    val date: String, val incoming: Boolean, var status: String
)

data class AppNotification(
    val id: Int, val icon: String, val title: String, val body: String, val time: String, val unread: Boolean
)

// ---------- События (тренировки и игры) ----------

enum class EventType(val label: String) { TRAINING("Тренировка"), GAME("Игра") }

class Poll(
    val question: String,
    val options: List<String>,
    votesInit: Map<String, String>     // имя участника -> вариант
) {
    val votes = androidx.compose.runtime.mutableStateMapOf<String, String>().apply { putAll(votesInit) }
    fun count(option: String) = votes.values.count { it == option }
    fun vote(user: String, option: String) { votes[user] = option }
}

class TeamEvent(
    val id: Int,
    val teamId: Int,
    val type: EventType,
    val day: Int,                   // день октября 2026
    val monthYear: String,          // "10.2026"
    timeInit: String,
    placeInit: String,
    val title: String,              // для игры — соперник
    val poll: Poll
) {
    var time by androidx.compose.runtime.mutableStateOf(timeInit)
    var place by androidx.compose.runtime.mutableStateOf(placeInit)
    val date: String get() = "%02d.%s".format(day, monthYear)
}

// ---------- Форматирование ----------

fun rub(v: Int): String =
    String.format(Locale("ru"), "%,d", kotlin.math.abs(v)).replace(',', ' ') + " ₽"

fun signedRub(v: Int): String = when {
    v < 0 -> "−" + rub(v)
    v > 0 -> "+" + rub(v)
    else -> "0 ₽"
}

// ---------- Mock-репозиторий (данные «живые»: платежи меняют балансы) ----------

object MockRepo {

    const val currentUserName = "Александр Прохоров"
    const val currentUserPhone = "+7 916 345-67-89"

    val teams = mutableStateListOf(
        Team(
            1, "ХК «Сокол»", "Москва", "Сезон 2026/27",
            mutableListOf(
                Member(1, "Александр Прохоров", 77, "Нападающий", "Левый", 183, 86, "L", "M", "L", Role.OWNER, 0),
                Member(2, "Дмитрий Ковалёв", 30, "Вратарь", "Левый", 188, 92, "XL", "L", "XL", Role.MODERATOR, 0),
                Member(3, "Игорь Сафонов", 14, "Защитник", "Правый", 185, 90, "L", "M", "L", Role.PLAYER, -2500),
                Member(4, "Максим Орлов", 91, "Нападающий", "Правый", 178, 79, "M", "M", "M", Role.PLAYER, -2500),
                Member(5, "Сергей Быков", 5, "Защитник", "Левый", 190, 95, "XL", "L", "XL", Role.PLAYER, 1200),
                Member(6, "Антон Гусев", 21, "Нападающий", "Левый", 181, 82, "L", "M", "L", Role.PLAYER, 0),
                Member(7, "Павел Лебедев", 8, "Защитник", "Правый", 186, 88, "L", "L", "L", Role.PLAYER, -5000),
                Member(8, "Кирилл Морозов", 63, "Нападающий", "Левый", 176, 74, "M", "S", "M", Role.PLAYER, 0),
                Member(9, "Владимир Титов", 1, "Вратарь", "Правый", 184, 89, "XL", "L", "XL", Role.PLAYER, -1300),
                Member(10, "Егор Смирнов", 44, "Нападающий", "Левый", 179, 80, "M", "M", "M", Role.PLAYER, 0),
                Member(11, "Олег Жуков", 17, "Защитник", "Правый", 187, 93, "XL", "L", "XL", Role.PLAYER, -2500),
                Member(12, "Артём Волков", 99, "Нападающий", "Левый", 182, 84, "L", "M", "L", Role.PLAYER, 700)
            )
        ),
        Team(
            2, "«Медведи»", "Химки", "Сезон 2026/27",
            mutableListOf(
                Member(20, "Александр Прохоров", 77, "Нападающий", "Левый", 183, 86, "L", "M", "L", Role.PLAYER, -1800),
                Member(21, "Никита Фролов", 10, "Нападающий", "Правый", 180, 81, "M", "M", "M", Role.OWNER, 0),
                Member(22, "Глеб Романов", 4, "Защитник", "Левый", 189, 94, "XL", "L", "XL", Role.PLAYER, -3600),
                Member(23, "Тимур Алиев", 72, "Нападающий", "Левый", 177, 78, "M", "M", "M", Role.PLAYER, 0)
            )
        )
    )

    val charges = mutableStateListOf(
        Charge(1, 1, "Лёд", "Тренировки, октябрь", 2500, "05.10.2026", "Ежемесячно", 22500, 30000, ChargeStatus.PARTIALLY_PAID),
        Charge(2, 1, "Турниры", "Кубок «Золотая шайба»", 4000, "12.10.2026", "Разовое", 32000, 48000, ChargeStatus.PARTIALLY_PAID),
        Charge(3, 1, "Форма", "Джерси домашние", 6500, "01.09.2026", "Разовое", 78000, 78000, ChargeStatus.PAID),
        Charge(4, 1, "Лёд", "Тренировки, сентябрь", 2500, "05.09.2026", "Ежемесячно", 30000, 30000, ChargeStatus.PAID),
        Charge(5, 1, "Штрафы", "Опоздания, сентябрь", 500, "30.09.2026", "Разовое", 1500, 2000, ChargeStatus.PARTIALLY_PAID),
        Charge(6, 2, "Лёд", "Тренировки, октябрь", 1800, "05.10.2026", "Ежемесячно", 5400, 7200, ChargeStatus.PARTIALLY_PAID)
    )

    val payments = mutableStateListOf(
        Payment(1, 1, "Игорь Сафонов", 2500, "18.10.2026", "Дмитрий Ковалёв"),
        Payment(2, 1, "Максим Орлов", 2500, "17.10.2026", "Александр Прохоров"),
        Payment(3, 1, "Сергей Быков", 5000, "15.10.2026", "Александр Прохоров"),
        Payment(4, 1, "Павел Лебедев", 1000, "12.10.2026", "Дмитрий Ковалёв"),
        Payment(5, 1, "Антон Гусев", 2500, "10.10.2026", "Александр Прохоров"),
        Payment(6, 1, "Владимир Титов", 1200, "08.10.2026", "Александр Прохоров"),
        Payment(7, 2, "Глеб Романов", 1800, "16.10.2026", "Никита Фролов")
    )

    val templates = mutableStateListOf(
        ChargeTemplate("Тренировка (месяц)", "Лёд", "Тренировки", 2500, "Ежемесячно, 5-го числа"),
        ChargeTemplate("Игра чемпионата", "Лёд", "Игры", 1200, "Разовое"),
        ChargeTemplate("Джерси комплект", "Форма", "Джерси", 6500, "Разовое"),
        ChargeTemplate("Взнос за турнир", "Турниры", "—", 4000, "Разовое"),
        ChargeTemplate("Штраф за опоздание", "Штрафы", "Опоздания", 500, "Разовое")
    )

    val invites = mutableStateListOf(
        Invite(1, "ХК «Вымпел» (Долгопрудный)", "Вас приглашает Станислав Ким", "19.10.2026", incoming = true, status = "Ожидает"),
        Invite(2, "ХК «Сокол»", "Роман Зайцев, +7 903 •••-••-12", "14.10.2026", incoming = false, status = "Ожидает"),
        Invite(3, "ХК «Сокол»", "Илья Громов, +7 925 •••-••-48", "03.10.2026", incoming = false, status = "Принято")
    )

    val notifications = mutableStateListOf(
        AppNotification(1, "💰", "Новое начисление", "Лёд / Тренировки, октябрь — 2 500 ₽. Оплатите до 05.11.2026.", "сегодня, 09:12", true),
        AppNotification(2, "✅", "Платёж принят", "Ваш платёж 2 500 ₽ внесён модератором Дмитрий Ковалёв.", "вчера, 18:40", true),
        AppNotification(3, "📩", "Приглашение в команду", "Станислав Ким приглашает вас в ХК «Вымпел» (Долгопрудный).", "вчера, 12:03", true),
        AppNotification(4, "⚠️", "Напоминание о долге", "По команде «Медведи» у вас долг 1 800 ₽.", "17.10.2026", false),
        AppNotification(5, "🏒", "Изменение в расписании", "Тренировка 22.10 перенесена на 21:30 — mock-пример.", "10.10.2026", false)
    )

    // --- выбранная команда (переключатель в тулбаре) ---

    var selectedTeamId by mutableIntStateOf(1)
    val selectedTeam: Team get() = teams.first { it.id == selectedTeamId }
    fun myRole(team: Team): Role = team.members.find { it.name == currentUserName }?.role ?: Role.PLAYER
    fun canManage(team: Team): Boolean = myRole(team) != Role.PLAYER

    // --- события: тренировки и игры, октябрь 2026 ---

    private fun training(id: Int, teamId: Int, day: Int, time: String, place: String, votes: Map<String, String>) =
        TeamEvent(id, teamId, EventType.TRAINING, day, "10.2026", time, place, "Тренировка", Poll("Пойдёшь на тренировку?", listOf("Иду", "Под вопросом", "Не иду"), votes))

    private fun game(id: Int, teamId: Int, day: Int, time: String, place: String, rival: String, votes: Map<String, String>) =
        TeamEvent(id, teamId, EventType.GAME, day, "10.2026", time, place, rival, Poll("Пойдёшь на игру?", listOf("Иду", "Под вопросом", "Не иду"), votes))

    val events = mutableStateListOf(
        training(1, 1, 6, "21:00–22:30", "ЛД «Авангард», Москва", mapOf("Александр Прохоров" to "Иду", "Дмитрий Ковалёв" to "Иду", "Игорь Сафонов" to "Под вопросом", "Максим Орлов" to "Иду", "Сергей Быков" to "Не иду")),
        training(2, 1, 8, "21:00–22:30", "ЛД «Авангард», Москва", mapOf("Александр Прохоров" to "Иду", "Дмитрий Ковалёв" to "Иду", "Павел Лебедев" to "Иду")),
        game(3, 1, 10, "18:00", "ЛД «Авангард», Москва", "ХК «Медведи»", mapOf("Александр Прохоров" to "Иду", "Дмитрий Ковалёв" to "Иду", "Игорь Сафонов" to "Иду", "Антон Гусев" to "Под вопросом", "Владимир Титов" to "Иду")),
        training(4, 1, 13, "21:00–22:30", "ЛД «Авангард», Москва", mapOf("Александр Прохоров" to "Иду", "Дмитрий Ковалёв" to "Под вопросом")),
        training(5, 1, 15, "21:00–22:30", "ЛД «Авангард», Москва", mapOf("Александр Прохоров" to "Иду")),
        game(6, 1, 17, "19:30", "ЛД «Вымпел», Долгопрудный", "ХК «Вымпел»", mapOf("Александр Прохоров" to "Под вопросом", "Дмитрий Ковалёв" to "Иду", "Олег Жуков" to "Иду")),
        training(7, 1, 20, "21:00–22:30", "ЛД «Авангард», Москва", mapOf("Александр Прохоров" to "Иду", "Дмитрий Ковалёв" to "Иду", "Максим Орлов" to "Иду", "Егор Смирнов" to "Под вопросом")),
        training(8, 1, 22, "21:00–22:30", "ЛД «Авангард», Москва", emptyMap()),
        game(9, 1, 24, "17:00", "ЛД «Авангард», Москва", "ХК «Атлант»", mapOf("Дмитрий Ковалёв" to "Иду")),
        training(10, 1, 27, "21:00–22:30", "ЛД «Авангард», Москва", emptyMap()),
        training(11, 1, 29, "21:00–22:30", "ЛД «Авангард», Москва", emptyMap()),
        training(12, 2, 7, "20:00–21:30", "Арена Химки", mapOf("Никита Фролов" to "Иду", "Александр Прохоров" to "Иду")),
        training(13, 2, 14, "20:00–21:30", "Арена Химки", mapOf("Никита Фролов" to "Иду")),
        training(14, 2, 21, "20:00–21:30", "Арена Химки", emptyMap()),
        game(15, 2, 24, "16:00", "Арена Химки", "ХК «Легион»", mapOf("Никита Фролов" to "Иду", "Александр Прохоров" to "Под вопросом")),
        training(16, 2, 28, "20:00–21:30", "Арена Химки", emptyMap())
    )

    fun eventsOf(teamId: Int, type: EventType) = events.filter { it.teamId == teamId && it.type == type }.sortedBy { it.day }

    fun nextEvent(teamId: Int): TeamEvent? = events.filter { it.teamId == teamId && it.day >= 20 }.minByOrNull { it.day }

    fun assignNumber(team: Team, member: Member, number: Int?) {
        member.number = number
    }

    // --- вычисляемые сводки ---

    fun teamDebt(team: Team): Int = team.members.filter { it.balance < 0 }.sumOf { -it.balance }

    fun teamOverpay(team: Team): Int = team.members.filter { it.balance > 0 }.sumOf { it.balance }

    fun myBalance(team: Team): Int = team.members.find { it.name == currentUserName }?.balance ?: 0

    fun chargesOf(teamId: Int) = charges.filter { it.teamId == teamId }

    fun paymentsOf(teamId: Int) = payments.filter { it.teamId == teamId }

    // Внесение платежа: уменьшает долг участника и добавляет запись в историю
    fun addPayment(teamId: Int, member: Member, amount: Int) {
        member.balance += amount
        payments.add(0, Payment((payments.maxOfOrNull { it.id } ?: 0) + 1, teamId, member.name, amount, "20.10.2026", currentUserName))
        // обновляем прогресс первого неоплаченного начисления команды (автораспределение: сначала старые)
        charges.filter { it.teamId == teamId && (it.status == ChargeStatus.ACTIVE || it.status == ChargeStatus.PARTIALLY_PAID) }
            .minByOrNull { it.id }?.let { ch ->
                ch.paid = (ch.paid + amount).coerceAtMost(ch.total)
                if (ch.paid >= ch.total) ch.status = ChargeStatus.PAID
                else ch.status = ChargeStatus.PARTIALLY_PAID
            }
    }

    fun addCharge(teamId: Int, group: String, subgroup: String, amount: Int, periodic: String, memberCount: Int) {
        charges.add(
            0, Charge(
                (charges.maxOfOrNull { it.id } ?: 0) + 1, teamId, group, subgroup,
                amount, "20.10.2026", periodic, 0, amount * memberCount, ChargeStatus.ACTIVE
            )
        )
    }
}
