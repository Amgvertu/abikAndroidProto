package info.prorabka.abik.ui.screens

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import info.prorabka.abik.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(nav: NavController) {
    LaunchedEffect(Unit) {
        delay(900)
        nav.navigate("login") { popUpTo("splash") { inclusive = true } }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🏒", fontSize = 64.sp)
            Spacer(Modifier.height(12.dp))
            Text("Абик", color = IceText, fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Text("учёт взносов команды", color = IceTextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
private fun AuthField(value: String, onChange: (String) -> Unit, label: String, password: Boolean = false, phone: Boolean = false) {
    OutlinedTextField(
        value = value, onValueChange = onChange, label = { Text(label) },
        singleLine = true,
        visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (phone) KeyboardType.Phone else KeyboardType.Text),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = authFieldColors()
    )
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = IcePrimary, unfocusedBorderColor = IceCardLight,
    focusedLabelColor = IcePrimary, unfocusedLabelColor = IceTextSecondary,
    cursorColor = IcePrimary, focusedTextColor = IceText, unfocusedTextColor = IceText
)

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = IcePrimary, contentColor = IceOnPrimary)
    ) { Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
}

@Composable
fun LoginScreen(nav: NavController) {
    var phone by remember { mutableStateOf("+7 ") }
    var password by remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(72.dp))
        Text("🏒", fontSize = 48.sp)
        Text("Абик", color = IceText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))
        AuthField(phone, { phone = it }, "Номер телефона", phone = true)
        Spacer(Modifier.height(12.dp))
        AuthField(password, { password = it }, "Пароль", password = true)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Войти") { nav.navigate("home") { popUpTo("login") { inclusive = true } } }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { nav.navigate("forgot") }) { Text("Забыли пароль?", color = IcePrimary) }
        Spacer(Modifier.weight(1f))
        TextButton(onClick = { nav.navigate("register") }) { Text("Регистрация", color = IcePrimary) }
        Spacer(Modifier.height(16.dp))
        Text("Прототип: вход по кнопке, данные mock", color = IceTextSecondary, fontSize = 12.sp)
    }
}

@Composable
fun RegisterScreen(nav: NavController) {
    var phone by remember { mutableStateOf("+7 ") }
    var password by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var consent by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        Spacer(Modifier.height(40.dp))
        Text("Регистрация", color = IceText, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        AuthField(phone, { phone = it }, "Номер телефона", phone = true)
        Spacer(Modifier.height(12.dp))
        AuthField(password, { password = it }, "Пароль", password = true)
        Spacer(Modifier.height(12.dp))
        AuthField(code, { code = it }, "Код из SMS", phone = true)
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = consent, onCheckedChange = { consent = it },
                colors = CheckboxDefaults.colors(checkedColor = IcePrimary, uncheckedColor = IceTextSecondary)
            )
            Text("Согласен на обработку персональных данных (политика v1.2)", color = IceTextSecondary, fontSize = 13.sp)
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Зарегистрироваться") { nav.navigate("home") { popUpTo("login") { inclusive = true } } }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { nav.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Уже есть аккаунт", color = IcePrimary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ForgotPasswordScreen(nav: NavController) {
    var phone by remember { mutableStateOf("+7 ") }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(Modifier.height(40.dp))
        Text("Восстановление пароля", color = IceText, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("На текущий номер придёт SMS с кодом", color = IceTextSecondary, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        AuthField(phone, { phone = it }, "Номер телефона", phone = true)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Отправить код") { nav.popBackStack() }
    }
}
