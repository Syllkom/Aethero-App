package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.*
import compose.icons.TablerIcons
import compose.icons.tablericons.Check

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricingScreen(navController: NavController) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 1)
    
    val plans = listOf(
        PricingPlan(
            name = "Starter",
            price = "Gratis",
            subtitle = "Para empezar a interactuar",
            buttonText = "Comenzar gratis",
            features = listOf("Uso de bot en grupos (públicos/donde esté)", "Descargas ilimitadas", "Acceso a juegos"),
            isHighlighted = false,
            glowColor = Color(0xFF9CA3AF)
        ),
        PricingPlan(
            name = "PROFESSIONAL",
            price = "$10",
            subtitle = "Para aprovechar todo el potencial",
            buttonText = "Adquirir Professional",
            features = listOf("Chat privado con owner", "Añade al bot a 5 grupos", "Sugerencias tomadas en cuenta", "Comandos especiales"),
            isHighlighted = true,
            glowColor = Color.White
        ),
        PricingPlan(
            name = "ENTERPRISE",
            price = "Custom",
            subtitle = "Experiencia a tu medida",
            buttonText = "Contactar Ventas",
            features = listOf("Todo como el usuario quiera personalizar"),
            isHighlighted = false,
            glowColor = Color(0xFFA78BFA)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgrounDark)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Planes de Precios",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Elige el plan adecuado para tus necesidades.",
            fontSize = 16.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(48.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 48.dp),
            pageSpacing = 16.dp
        ) { page ->
            val plan = plans[page]
            PricingCard(plan = plan, onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/573148017567?text=Me%20interesa%20el%20plan%20${plan.name}"))
                context.startActivity(intent)
            })
        }
    }
}

data class PricingPlan(
    val name: String,
    val price: String,
    val subtitle: String,
    val buttonText: String,
    val features: List<String>,
    val isHighlighted: Boolean,
    val glowColor: Color
)

@Composable
fun PricingCard(plan: PricingPlan, onClick: () -> Unit) {
    val cardBg = if (plan.name == "ENTERPRISE") Color(0xFF111111) else SurfaceDark
    val textColor = TextPrimary
    
    val headerModifier = when {
        plan.isHighlighted -> Modifier.background(
            Brush.linearGradient(listOf(Color(0xFF3F3F46), Color(0xFF18181B)))
        )
        plan.name == "ENTERPRISE" -> Modifier.background(Color(0xFF27272A))
        else -> Modifier.background(SurfaceVariantDark)
    }

    val borderModifier = when {
        plan.isHighlighted -> Modifier.border(
            width = 2.dp,
            brush = Brush.linearGradient(listOf(Color.White, Color(0xFFD1D5DB), Color(0xFF6B7280))),
            shape = RoundedCornerShape(24.dp)
        )
        plan.name == "ENTERPRISE" -> Modifier.border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(24.dp))
        else -> Modifier.border(1.dp, DividerColor, RoundedCornerShape(24.dp))
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (plan.isHighlighted) 24.dp else 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = if (plan.isHighlighted) Color.White.copy(alpha = 0.5f) else Color.Black
            )
            .background(cardBg, RoundedCornerShape(24.dp))
            .then(borderModifier)
            .clip(RoundedCornerShape(24.dp))
    ) {
        // Glow effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.width * 0.9f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(plan.glowColor.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.9f),
                    radius = radius
                ),
                center = Offset(size.width * 0.9f, size.height * 0.9f),
                radius = radius
            )
            
            if (plan.isHighlighted) {
                fun createSparklePath(centerX: Float, centerY: Float, sz: Float): Path {
                    val half = sz / 2
                    return Path().apply {
                        moveTo(centerX, centerY - half)
                        quadraticBezierTo(centerX, centerY, centerX + half, centerY)
                        quadraticBezierTo(centerX, centerY, centerX, centerY + half)
                        quadraticBezierTo(centerX, centerY, centerX - half, centerY)
                        quadraticBezierTo(centerX, centerY, centerX, centerY - half)
                        close()
                    }
                }
                
                val sparkle1 = createSparklePath(size.width * 0.8f, size.height * 0.85f, 40f)
                val sparkle2 = createSparklePath(size.width * 0.95f, size.height * 0.7f, 25f)
                val sparkle3 = createSparklePath(size.width * 0.7f, size.height * 0.95f, 20f)
                
                drawPath(sparkle1, plan.glowColor.copy(alpha = 0.8f))
                drawPath(sparkle2, plan.glowColor.copy(alpha = 0.6f))
                drawPath(sparkle3, plan.glowColor.copy(alpha = 0.5f))
            } else if (plan.name == "ENTERPRISE") {
                // Abstract geometric decoration for Custom plan
                fun createHexagonPath(centerX: Float, centerY: Float, sz: Float): Path {
                    val sides = 6
                    val angle = 2.0 * Math.PI / sides
                    return Path().apply {
                        for (i in 0 until sides) {
                            val x = centerX + sz * kotlin.math.cos(i * angle).toFloat()
                            val y = centerY + sz * kotlin.math.sin(i * angle).toFloat()
                            if (i == 0) moveTo(x, y) else lineTo(x, y)
                        }
                        close()
                    }
                }

                // Wavy abstract line
                val abstractWave = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.95f)
                    cubicTo(
                        size.width * 0.6f, size.height * 0.8f,
                        size.width * 0.8f, size.height * 1.0f,
                        size.width * 1f, size.height * 0.75f
                    )
                }

                drawPath(
                    path = abstractWave,
                    color = plan.glowColor.copy(alpha = 0.3f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                )

                val hex1 = createHexagonPath(size.width * 0.85f, size.height * 0.85f, 30f)
                val hex2 = createHexagonPath(size.width * 0.75f, size.height * 0.75f, 15f)
                val hex3 = createHexagonPath(size.width * 0.95f, size.height * 0.6f, 20f)

                drawPath(hex1, plan.glowColor.copy(alpha = 0.4f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                drawPath(hex2, plan.glowColor.copy(alpha = 0.5f))
                drawPath(hex3, plan.glowColor.copy(alpha = 0.3f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
            }
        }
        
        Column(modifier = Modifier.padding(24.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .then(headerModifier)
                    .padding(20.dp)
            ) {
                Column {
                    Surface(
                        color = if (plan.isHighlighted) Accent else Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = plan.name.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (plan.isHighlighted) Color.Black else TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = plan.price,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )
                        if (plan.price != "Custom") {
                            Text(
                                text = "/mes",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = plan.subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val buttonColors = when {
                plan.isHighlighted -> ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.Black)
                plan.name == "ENTERPRISE" -> ButtonDefaults.buttonColors(containerColor = Color(0xFF3F3F46), contentColor = TextPrimary)
                else -> ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = TextPrimary)
            }
            
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = buttonColors,
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(if (plan.isHighlighted) 8.dp else 4.dp)
            ) {
                Text(text = plan.buttonText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            plan.features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                    Icon(
                        imageVector = TablerIcons.Check,
                        contentDescription = "Incluido",
                        tint = if (plan.isHighlighted) Color.White else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = feature,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
