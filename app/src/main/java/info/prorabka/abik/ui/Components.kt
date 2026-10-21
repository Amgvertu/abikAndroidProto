package info.prorabka.abik.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.prorabka.abik.data.rub
import info.prorabka.abik.ui.theme.*

@Composable
fun Avatar(name: String, modifier: Modifier = Modifier, size: Int = 44) {
    val initials = name.split(" ").take(2).mapNotNull { it.firstOrNull() }.joinToString("")
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(IceCardLight),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = IcePrimary, fontWeight = FontWeight.Bold, fontSize = (size / 2.6).sp)
    }
}

@Composable
fun BalanceBadge(balance: Int) {
    val (text, color) = when {
        balance < 0 -> "Долг " + rub(balance) to Danger
        balance > 0 -> "+" + rub(balance) to Success
        else -> "0 ₽" to IceTextSecondary
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.14f)
    ) {
        Text(
            text, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun RoleBadge(label: String) {
    Surface(shape = RoundedCornerShape(6.dp), color = IcePrimary.copy(alpha = 0.15f)) {
        Text(label, color = IcePrimary, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text, modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        color = IceTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun StatCard(title: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = IceCard)) {
        Column(Modifier.padding(14.dp)) {
            Text(title, color = IceTextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EmptyState(text: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text, color = IceTextSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
fun AbikTopBar(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit = {}) {
    Surface(color = IceSurface) {
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Text("←", color = IceText, fontSize = 22.sp)
                }
            } else {
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, color = IceText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                if (subtitle != null) Text(subtitle, color = IceTextSecondary, fontSize = 13.sp)
            }
            actions()
        }
    }
}
