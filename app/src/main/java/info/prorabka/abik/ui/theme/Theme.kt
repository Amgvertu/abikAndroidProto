package info.prorabka.abik.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// «Ледовая» тёмная палитра
val IceBg = Color(0xFF0B1622)
val IceSurface = Color(0xFF122334)
val IceCard = Color(0xFF182C40)
val IceCardLight = Color(0xFF1F3A52)
val IcePrimary = Color(0xFF4CC9F0)
val IceOnPrimary = Color(0xFF06222E)
val IceText = Color(0xFFE8F1F8)
val IceTextSecondary = Color(0xFF93A9BD)
val Success = Color(0xFF3DDC97)
val Danger = Color(0xFFFF6B6B)
val Warning = Color(0xFFFFD166)

private val AbikColors = darkColorScheme(
    primary = IcePrimary,
    onPrimary = IceOnPrimary,
    primaryContainer = IceCardLight,
    onPrimaryContainer = IceText,
    secondary = Warning,
    background = IceBg,
    onBackground = IceText,
    surface = IceSurface,
    onSurface = IceText,
    surfaceVariant = IceCard,
    onSurfaceVariant = IceTextSecondary,
    outline = Color(0xFF2E4A63),
    error = Danger
)

@Composable
fun AbikTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AbikColors, content = content)
}

val MoneyLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold)
val MoneyMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
