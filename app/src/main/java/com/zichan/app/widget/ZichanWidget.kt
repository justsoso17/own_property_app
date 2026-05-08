package com.zichan.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.room.Room
import com.zichan.app.data.database.AppDatabase
import kotlinx.coroutines.flow.first
import java.text.NumberFormat
import java.util.Locale

class ZichanWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "zichan.db").build()
        val assets = db.assetDao().getAll().first()
        val active = assets.filter { it.status !in listOf("已出售", "已丢弃") }
        val totalValue = active.sumOf { it.price }
        val inUse = active.count { it.status == "使用中" }
        val idle = active.count { it.status == "闲置" }
        val lent = active.count { it.status == "已借出" }
        db.close()

        val fmt = NumberFormat.getCurrencyInstance(Locale.CHINA)
        val openIntent = Intent(context, com.zichan.app.MainActivity::class.java)

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .background(ColorProvider(android.graphics.Color.parseColor("#F8F6F2")))
                        .cornerRadius(radius = 20.dp)
                        .padding(all = 16.dp)
                        .clickable(onClick = actionStartActivity(openIntent)),
                ) {
                    Text(
                        text = fmt.format(totalValue),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = ColorProvider(android.graphics.Color.parseColor("#C49A3C")),
                        ),
                    )
                    Text(
                        text = "总资产",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = ColorProvider(android.graphics.Color.parseColor("#777777")),
                        ),
                    )
                    Column(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                    ) {
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                        ) {
                            WidgetChip("$inUse 使用中", "#5C9E6D", 0x195C9E6D.toInt(), start = false)
                            WidgetChip("$idle 闲置", "#C4A24C", 0x19C4A24C.toInt(), start = true)
                            WidgetChip("$lent 借出", "#B85C5C", 0x19B85C5C.toInt(), start = true)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun WidgetChip(text: String, colorHex: String, bgColor: Int, start: Boolean) {
        Column(
            modifier = GlanceModifier
                .let { if (start) it.padding(start = 8.dp) else it }
                .background(ColorProvider(bgColor))
                .cornerRadius(radius = 8.dp)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 11.sp,
                    color = ColorProvider(android.graphics.Color.parseColor(colorHex)),
                ),
            )
        }
    }
}

class ZichanWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ZichanWidget()
}
