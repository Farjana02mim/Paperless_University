package com.example.core.result

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import com.example.domain.model.result.TranscriptModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfTranscriptExporter {

    fun generateTranscriptPdf(context: Context, transcript: TranscriptModel): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 page size at 72 dpi
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.parseColor("#1A237E")
            textSize = 20f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val subTitlePaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
            textAlign = Paint.Align.CENTER
        }

        val headerPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            isFakeBoldText = true
        }

        val bodyPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
        }

        val watermarkPaint = Paint().apply {
            color = Color.parseColor("#15000000") // Very faint
            textSize = 42f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        // Draw Watermark
        canvas.drawText("OFFICIAL UNIVERSITY TRANSCRIPT", 297f, 421f, watermarkPaint)

        // Title Header
        canvas.drawText("SMART PAPERLESS UNIVERSITY", 297f, 40f, titlePaint)
        canvas.drawText("OFFICIAL ACADEMIC TRANSCRIPT OF RECORD", 297f, 58f, subTitlePaint)

        // Draw Top Divider Line
        val linePaint = Paint().apply {
            color = Color.parseColor("#1A237E")
            strokeWidth = 2f
        }
        canvas.drawLine(40f, 70f, 555f, 70f, linePaint)

        // Student Info Box
        var y = 90f
        canvas.drawText("Student Name: ${transcript.studentName}", 40f, y, headerPaint)
        canvas.drawText("Roll Number: ${transcript.rollNumber}", 350f, y, headerPaint)
        y += 18f
        canvas.drawText("Department: ${transcript.department}", 40f, y, bodyPaint)
        canvas.drawText("Degree: ${transcript.degreeTitle}", 40f, y + 16f, bodyPaint)
        canvas.drawText("Overall CGPA: ${"%.2f".format(transcript.overallCGPA)} / 4.00", 350f, y + 16f, headerPaint)
        y += 40f

        // Table Header
        val bgPaint = Paint().apply { color = Color.parseColor("#E8EAF6") }
        canvas.drawRect(40f, y - 12f, 555f, y + 6f, bgPaint)
        canvas.drawText("Course Code", 45f, y, headerPaint)
        canvas.drawText("Course Title", 130f, y, headerPaint)
        canvas.drawText("Credits", 370f, y, headerPaint)
        canvas.drawText("Grade", 440f, y, headerPaint)
        canvas.drawText("Points", 500f, y, headerPaint)
        y += 16f

        // Iteration over semester results
        for (sem in transcript.allSemesterResults) {
            canvas.drawText("--- ${sem.semester} (${sem.academicYear}) • GPA: ${"%.2f".format(sem.semesterGPA)} ---", 45f, y, headerPaint)
            y += 16f
            for (course in sem.courseResults) {
                if (y > 780f) break // Simple pagination clip guard
                canvas.drawText(course.courseCode, 45f, y, bodyPaint)
                canvas.drawText(course.courseTitle.take(35), 130f, y, bodyPaint)
                canvas.drawText("${"%.1f".format(course.creditHours)}", 375f, y, bodyPaint)
                canvas.drawText(course.letterGrade, 445f, y, headerPaint)
                canvas.drawText("${"%.2f".format(course.gradePoint)}", 505f, y, bodyPaint)
                y += 15f
            }
            y += 6f
        }

        // Footer & Digital Verification Signature
        y = 800f
        canvas.drawLine(40f, y - 15f, 555f, y - 15f, linePaint)
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(transcript.generatedAt))
        canvas.drawText("Generated On: $dateStr", 40f, y, bodyPaint)
        canvas.drawText("Verification Code: ${transcript.verificationCode}", 250f, y, headerPaint)
        canvas.drawText("Signature: ${transcript.digitalSignature.take(16)}...", 430f, y, subTitlePaint)

        pdfDocument.finishPage(page)

        val outputFile = File(context.cacheDir, "Transcript_${transcript.rollNumber}.pdf")
        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return outputFile
    }

    fun printTranscript(context: Context, pdfFile: File) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "Transcript_${pdfFile.name}"
        printManager.print(
            jobName,
            object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: CancellationSignal?,
                    callback: LayoutResultCallback?,
                    extras: Bundle?
                ) {
                    if (cancellationSignal?.isCanceled == true) {
                        callback?.onLayoutCancelled()
                        return
                    }
                    val info = PrintDocumentInfo.Builder(pdfFile.name)
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(1)
                        .build()
                    callback?.onLayoutFinished(info, true)
                }

                override fun onWrite(
                    pages: Array<out PageRange>?,
                    destination: ParcelFileDescriptor?,
                    cancellationSignal: CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    try {
                        pdfFile.inputStream().use { input ->
                            FileOutputStream(destination?.fileDescriptor).use { output ->
                                input.copyTo(output)
                            }
                        }
                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                    } catch (e: Exception) {
                        callback?.onWriteFailed(e.message)
                    }
                }
            },
            null
        )
    }
}
