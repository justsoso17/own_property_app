package com.zichan.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val VaultTypography = Typography(
    headlineLarge = TextStyle(
        fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontSize = 24.sp, fontWeight = FontWeight.SemiBold, lineHeight = 30.sp,
        letterSpacing = (-0.3).sp
    ),
    headlineSmall = TextStyle(
        fontSize = 18.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp, fontWeight = FontWeight.Medium, lineHeight = 26.sp,
        letterSpacing = (-0.2).sp
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp, fontWeight = FontWeight.Medium, lineHeight = 22.sp
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(
        fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp
    ),
)

val AppTypography = VaultTypography
