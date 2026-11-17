package com.lokin.app.presentation.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Biểu đồ đường: mỗi điểm là (thời điểm ms, giá trị). */
@Composable
fun LineChart(
    points: List<Pair<Long, Float>>,
    from: Long,
    to: Long,
    modifier: Modifier = Modifier.fillMaxWidth().height(160.dp)
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    Canvas(modifier) {
        if (points.isEmpty()) return@Canvas
        val minV = points.minOf { it.second }
        val maxV = points.maxOf { it.second }
        val pad = (maxV - minV).coerceAtLeast(1f) * 0.25f
        val lo = minV - pad
        val hi = maxV + pad
        val margin = 12f
        fun x(t: Long): Float =
            if (to > from) margin + ((t - from).toFloat() / (to - from)) * (size.width - 2 * margin)
            else size.width / 2
        fun y(v: Float): Float = size.height - ((v - lo) / (hi - lo)) * size.height

        drawLine(axisColor, Offset(0f, size.height), Offset(size.width, size.height), 2f)

        val path = Path()
        points.forEachIndexed { i, p ->
            if (i == 0) path.moveTo(x(p.first), y(p.second)) else path.lineTo(x(p.first), y(p.second))
        }
        drawPath(path, lineColor, style = Stroke(width = 4f))
        points.forEach { drawCircle(lineColor, 6f, Offset(x(it.first), y(it.second))) }
    }
}

/** Biểu đồ cột theo ngày, có đường đứt nét cho mục tiêu. */
@Composable
fun BarChart(
    values: List<Float>,
    goal: Float?,
    modifier: Modifier = Modifier.fillMaxWidth().height(160.dp)
) {
    val barColor = MaterialTheme.colorScheme.primary
    val goalColor = MaterialTheme.colorScheme.tertiary
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    Canvas(modifier) {
        if (values.isEmpty()) return@Canvas
        val maxV = maxOf(values.max(), goal ?: 0f, 1f) * 1.1f
        val slot = size.width / values.size
        val barW = slot * 0.6f
        values.forEachIndexed { i, v ->
            val h = v / maxV * size.height
            drawRect(
                barColor,
                topLeft = Offset(i * slot + (slot - barW) / 2f, size.height - h),
                size = Size(barW, h)
            )
        }
        goal?.let {
            val gy = size.height - it / maxV * size.height
            drawLine(
                goalColor, Offset(0f, gy), Offset(size.width, gy), 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
            )
        }
        drawLine(axisColor, Offset(0f, size.height), Offset(size.width, size.height), 2f)
    }
}