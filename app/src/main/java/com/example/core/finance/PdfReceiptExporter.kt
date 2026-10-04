package com.example.core.finance

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import com.example.domain.model.finance.InvoiceModel
import com.example.domain.model.finance.PaymentModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReceiptExporter {

    fun generatePaymentReceiptPdf(context: Context, payment: PaymentModel, invoice: InvoiceModel): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 at 72dpi
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.parseColor("#0D47A1")
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
            color = Color.parseColor("#15000000")
            textSize = 40f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val successBadgePaint = Paint().apply {
            color = Color.parseColor("#2E7D32")
            textSize = 14f
            isFakeBoldText = true
        }

        // Watermark
        canvas.drawText("OFFICIAL PAYMENT RECEIPT", 297f, 421f, watermarkPaint)

        // Title Header
        canvas.drawText("SMART PAPERLESS UNIVERSITY", 297f, 45f, titlePaint)
        canvas.drawText("ELECTRONIC FINANCIAL PAYMENT RECEIPT", 297f, 63f, subTitlePaint)

        // Divider
        val linePaint = Paint().apply {
            color = Color.parseColor("#0D47A1")
            strokeWidth = 2f
        }
        canvas.drawLine(40f, 75f, 555f, 75f, linePaint)

        // Status Badge
        canvas.drawText("STATUS: PAID & VERIFIED", 40f, 95f, successBadgePaint)

        // Student Info
        var y = 120f
        canvas.drawText("Student Name: ${payment.studentName}", 40f, y, headerPaint)
        canvas.drawText("Invoice ID: ${payment.invoiceId}", 350f, y, headerPaint)
        y += 18f
        canvas.drawText("Payment ID: ${payment.paymentId}", 40f, y, bodyPaint)
        canvas.drawText("Transaction ID: ${payment.transactionId}", 350f, y, bodyPaint)
        y += 18f
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(payment.paymentDate))
        canvas.drawText("Payment Date: $dateStr", 40f, y, bodyPaint)
        canvas.drawText("Gateway: ${payment.paymentGateway.name}", 350f, y, bodyPaint)
        y += 35f

        // Table Header
        val bgPaint = Paint().apply { color = Color.parseColor("#E3F2FD") }
        canvas.drawRect(40f, y - 12f, 555f, y + 6f, bgPaint)
        canvas.drawText("Item Description", 45f, y, headerPaint)
        canvas.drawText("Type", 300f, y, headerPaint)
        canvas.drawText("Amount (${payment.currency})", 450f, y, headerPaint)
        y += 20f

        for (item in invoice.feeItems) {
            canvas.drawText(item.title, 45f, y, bodyPaint)
            canvas.drawText(item.feeType.name, 300f, y, bodyPaint)
            canvas.drawText("%.2f".format(item.amount), 450f, y, bodyPaint)
            y += 16f
        }

        y += 10f
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 18f

        // Financial Calculations Breakdown
        canvas.drawText("Sub Total:", 350f, y, bodyPaint)
        canvas.drawText("%.2f BDT".format(invoice.totalAmount), 450f, y, bodyPaint)
        y += 16f
        if (invoice.scholarship > 0) {
            canvas.drawText("Scholarship Discount:", 350f, y, bodyPaint)
            canvas.drawText("- %.2f BDT".format(invoice.scholarship), 450f, y, bodyPaint)
            y += 16f
        }
        if (invoice.waiver > 0) {
            canvas.drawText("Special Waiver:", 350f, y, bodyPaint)
            canvas.drawText("- %.2f BDT".format(invoice.waiver), 450f, y, bodyPaint)
            y += 16f
        }
        if (invoice.lateFine > 0) {
            canvas.drawText("Late Penalty:", 350f, y, bodyPaint)
            canvas.drawText("+ %.2f BDT".format(invoice.lateFine), 450f, y, bodyPaint)
            y += 16f
        }

        canvas.drawText("Total Paid Amount:", 350f, y, headerPaint)
        canvas.drawText("%.2f BDT".format(payment.amount), 450f, y, headerPaint)

        // Verification & Footer
        y = 780f
        canvas.drawLine(40f, y - 15f, 555f, y - 15f, linePaint)
        canvas.drawText("Digital Verification Hash: ${payment.verificationHash}", 40f, y, headerPaint)
        canvas.drawText("Generated automatically by Smart University Financial Engine. No seal required.", 40f, y + 15f, subTitlePaint)

        pdfDocument.finishPage(page)

        val outputFile = File(context.cacheDir, "Receipt_${payment.paymentId}.pdf")
        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return outputFile
    }

    fun printReceipt(context: Context, pdfFile: File) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "Receipt_${pdfFile.name}"
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
