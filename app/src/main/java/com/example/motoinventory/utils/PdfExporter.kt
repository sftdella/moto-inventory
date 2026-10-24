package com.example.motoinventory.utils

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.motoinventory.data.entity.Motor
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExporter {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f

    data class LaporanData(
        val motors: List<Motor>,
        val totalPemasukan: Long,
        val totalPengeluaran: Long,
        val profit: Long,
        val period: String
    )

    fun exportLaporan(context: Context, data: LaporanData): File? {
        return try {
            val document = PdfDocument()
            var pageNumber = 1
            var y = MARGIN + 20f

            fun newPage(): PdfDocument.Page {
                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber++).create()
                return document.startPage(pageInfo)
            }

            var page = newPage()
            var canvas = page.canvas

            // Header
            y = drawHeader(canvas, data.period, y)
            y += 20f

            // Financial Summary
            y = drawFinancialSummary(canvas, data, y)
            y += 20f

            // Motor List title
            val titlePaint = Paint().apply {
                color = Color.parseColor("#1565C0")
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
            }
            canvas.drawText("Daftar Motor", MARGIN, y, titlePaint)
            y += 20f

            // Motor list
            val headerPaint = Paint().apply {
                color = Color.parseColor("#546E8A")
                textSize = 10f
                typeface = Typeface.DEFAULT_BOLD
            }
            val bodyPaint = Paint().apply {
                color = Color.parseColor("#1A2341")
                textSize = 9f
            }
            val linePaint = Paint().apply {
                color = Color.parseColor("#E0E6F0")
                strokeWidth = 0.5f
            }

            // Table header
            canvas.drawText("No", MARGIN, y, headerPaint)
            canvas.drawText("Nama Motor", MARGIN + 25f, y, headerPaint)
            canvas.drawText("Status", MARGIN + 200f, y, headerPaint)
            canvas.drawText("Harga Beli", MARGIN + 290f, y, headerPaint)
            canvas.drawText("Harga Jual", MARGIN + 390f, y, headerPaint)
            y += 5f
            canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
            y += 12f

            data.motors.forEachIndexed { index, motor ->
                if (y > PAGE_HEIGHT - 60f) {
                    document.finishPage(page)
                    page = newPage()
                    canvas = page.canvas
                    y = MARGIN + 20f
                }
                canvas.drawText("${index + 1}", MARGIN, y, bodyPaint)
                canvas.drawText(
                    if (motor.namMotor.length > 22) motor.namMotor.take(22) + "…" else motor.namMotor,
                    MARGIN + 25f, y, bodyPaint
                )
                canvas.drawText(motor.status, MARGIN + 200f, y, bodyPaint)
                canvas.drawText(FormatUtils.formatRupiah(motor.hargaBeli), MARGIN + 290f, y, bodyPaint)
                canvas.drawText(FormatUtils.formatRupiah(motor.hargaJual), MARGIN + 390f, y, bodyPaint)
                y += 4f
                canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
                y += 12f
            }

            // Footer
            drawFooter(canvas)
            document.finishPage(page)

            // Save to Downloads
            val fileName = "PCM_Laporan_${data.period.replace(" ", "_")}_${
                SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
            }.pdf"

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)

            FileOutputStream(file).use { document.writeTo(it) }
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun drawHeader(canvas: Canvas, period: String, startY: Float): Float {
        var y = startY

        val bgPaint = Paint().apply { color = Color.parseColor("#1565C0") }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 80f, bgPaint)

        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
        }
        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#90CAF9")
            textSize = 11f
        }
        canvas.drawText("PCM INVENTORY", MARGIN, 35f, titlePaint)
        canvas.drawText("Pardi Ciken Motor", MARGIN, 52f, subtitlePaint)
        canvas.drawText("Laporan: $period", MARGIN, 68f, subtitlePaint)

        y = 100f
        return y
    }

    private fun drawFinancialSummary(canvas: Canvas, data: LaporanData, startY: Float): Float {
        var y = startY
        val sectionPaint = Paint().apply {
            color = Color.parseColor("#1565C0")
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
        }
        val labelPaint = Paint().apply {
            color = Color.parseColor("#546E8A")
            textSize = 11f
        }
        val valuePaint = Paint().apply {
            color = Color.parseColor("#1A2341")
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
        }
        val profitColor = if (data.profit >= 0) Color.parseColor("#4CAF50") else Color.parseColor("#F44336")
        val profitPaint = Paint().apply {
            color = profitColor
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
        }

        canvas.drawText("Ringkasan Keuangan", MARGIN, y, sectionPaint)
        y += 20f

        canvas.drawText("Total Pemasukan", MARGIN, y, labelPaint)
        canvas.drawText(FormatUtils.formatRupiah(data.totalPemasukan), MARGIN + 200f, y, valuePaint)
        y += 18f

        canvas.drawText("Total Pengeluaran", MARGIN, y, labelPaint)
        canvas.drawText(FormatUtils.formatRupiah(data.totalPengeluaran), MARGIN + 200f, y, valuePaint)
        y += 18f

        val linePaint = Paint().apply { color = Color.parseColor("#E0E6F0"); strokeWidth = 1f }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, linePaint)
        y += 15f

        canvas.drawText("Profit / Rugi", MARGIN, y, sectionPaint)
        canvas.drawText(FormatUtils.formatRupiah(data.profit), MARGIN + 200f, y, profitPaint)
        y += 20f

        return y
    }

    private fun drawFooter(canvas: Canvas) {
        val footerPaint = Paint().apply {
            color = Color.parseColor("#9EB3C8")
            textSize = 9f
        }
        val linePaint = Paint().apply {
            color = Color.parseColor("#E0E6F0")
            strokeWidth = 0.5f
        }
        canvas.drawLine(MARGIN, PAGE_HEIGHT - 30f, PAGE_WIDTH - MARGIN.toFloat(), PAGE_HEIGHT - 30f, linePaint)
        canvas.drawText("Dicetak oleh PCM Inventory - ${
            SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID")).format(Date())
        }", MARGIN, PAGE_HEIGHT - 15f, footerPaint)
    }
}
