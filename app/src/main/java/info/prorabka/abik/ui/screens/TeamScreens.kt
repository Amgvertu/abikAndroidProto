package info.prorabka.abik.ui.screens

import info.prorabka.abik.data.ChargeStatus
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.prorabka.abik.ui.theme.*

@Composable
fun StatusChip(status: ChargeStatus) {
    val color = when (status) {
        ChargeStatus.PAID -> Success
        ChargeStatus.PARTIALLY_PAID -> Warning
        ChargeStatus.ACTIVE -> IcePrimary
        else -> IceTextSecondary
    }
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.14f)) {
        Text(status.label, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, Modifier.weight(1f), color = IceTextSecondary, fontSize = 14.sp)
        Text(value, color = IceText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
