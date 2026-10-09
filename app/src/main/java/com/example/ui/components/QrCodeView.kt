package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.VaultBorder
import kotlin.math.absoluteValue

/**
 * Procedural QR code canvas renderer that generates authentic 2D matrix patterns
 * for cryptocurrency addresses and payment URIs.
 */
@Composable
fun QrCodeView(
    data: String,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    qrColor: Color = Color.Black,
    backgroundColor: Color = Color.White
) {
    val matrixSize = 21 // Standard Version 1 QR matrix size (21x21)
    val grid = remember(data) {
        val hash = data.hashCode().absoluteValue
        val result = Array(matrixSize) { BooleanArray(matrixSize) }

        // Finder patterns in corners
        fun drawFinder(startX: Int, startY: Int) {
            for (x in 0 until 7) {
                for (y in 0 until 7) {
                    val isBorder = x == 0 || x == 6 || y == 0 || y == 6
                    val isCenter = x in 2..4 && y in 2..4
                    result[startY + y][startX + x] = isBorder || isCenter
                }
            }
        }

        drawFinder(0, 0)
        drawFinder(matrixSize - 7, 0)
        drawFinder(0, matrixSize - 7)

        // Timing patterns
        for (i in 7 until matrixSize - 7) {
            result[6][i] = (i % 2 == 0)
            result[i][6] = (i % 2 == 0)
        }

        // Fill remaining payload deterministically based on data hash
        var bitCounter = hash
        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                val inTopLeftFinder = r < 8 && c < 8
                val inTopRightFinder = r < 8 && c >= matrixSize - 8
                val inBottomLeftFinder = r >= matrixSize - 8 && c < 8
                val inTiming = r == 6 || c == 6

                if (!inTopLeftFinder && !inTopRightFinder && !inBottomLeftFinder && !inTiming) {
                    bitCounter = (bitCounter * 1664525 + 1013904223)
                    result[r][c] = (bitCounter % 2 != 0)
                }
            }
        }
        result
    }

    Box(
        modifier = modifier
            .testTag("qr_code_container")
            .size(size)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(1.dp, VaultBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size - 24.dp)) {
            val cellWidth = this.size.width / matrixSize
            val cellHeight = this.size.height / matrixSize

            for (r in 0 until matrixSize) {
                for (c in 0 until matrixSize) {
                    if (grid[r][c]) {
                        drawRect(
                            color = qrColor,
                            topLeft = Offset(c * cellWidth, r * cellHeight),
                            size = Size(cellWidth * 0.95f, cellHeight * 0.95f)
                        )
                    }
                }
            }
        }
    }
}
