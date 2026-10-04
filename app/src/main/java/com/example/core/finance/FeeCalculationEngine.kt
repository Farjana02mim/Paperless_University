package com.example.core.finance

import com.example.domain.model.finance.*
import java.security.MessageDigest
import kotlin.math.max
import kotlin.math.roundToInt

object FeeCalculationEngine {

    const val DEFAULT_GRACE_DAYS = 5
    const val DAILY_LATE_FINE_RATE = 100.0 // 100 per day late
    const val MAX_LATE_FINE = 2000.0

    fun calculateLateFine(dueDate: Long, currentTime: Long = System.currentTimeMillis()): Double {
        val gracePeriodMs = DEFAULT_GRACE_DAYS * 24 * 60 * 60 * 1000L
        val effectiveDeadline = dueDate + gracePeriodMs

        if (currentTime <= effectiveDeadline) return 0.0

        val daysOverdue = max(1, ((currentTime - effectiveDeadline) / (24 * 60 * 60 * 1000L)).toInt())
        val fine = daysOverdue * DAILY_LATE_FINE_RATE
        return fine.coerceAtMost(MAX_LATE_FINE)
    }

    fun calculateInvoiceGrandTotal(
        feeItems: List<FeeItem>,
        scholarship: ScholarshipModel? = null,
        waiver: WaiverModel? = null,
        dueDate: Long = System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000L,
        currentTime: Long = System.currentTimeMillis()
    ): InvoiceModel {
        val totalAmount = feeItems.sumOf { it.amount }

        var scholarshipDiscount = 0.0
        scholarship?.let {
            if (it.percentage > 0.0) {
                scholarshipDiscount += (totalAmount * (it.percentage / 100.0))
            }
            scholarshipDiscount += it.fixedAmount
        }

        var waiverDiscount = 0.0
        waiver?.let {
            if (it.percentage > 0.0) {
                waiverDiscount += (totalAmount * (it.percentage / 100.0))
            }
        }

        val totalDiscounts = (scholarshipDiscount + waiverDiscount).coerceAtMost(totalAmount)
        val lateFine = calculateLateFine(dueDate, currentTime)
        val grandTotal = (totalAmount - totalDiscounts + lateFine).coerceAtLeast(0.0)
        val roundedGrandTotal = (grandTotal * 100.0).roundToInt() / 100.0

        return InvoiceModel(
            feeItems = feeItems,
            totalAmount = totalAmount,
            scholarship = scholarshipDiscount,
            waiver = waiverDiscount,
            discount = totalDiscounts,
            lateFine = lateFine,
            grandTotal = roundedGrandTotal,
            dueAmount = roundedGrandTotal,
            dueDate = dueDate,
            status = if (roundedGrandTotal == 0.0) PaymentStatus.SUCCESSFUL else PaymentStatus.PENDING
        )
    }

    fun generateInstallments(invoice: InvoiceModel, totalInstallments: Int = 3): List<InstallmentPlan> {
        if (totalInstallments <= 1 || invoice.grandTotal <= 0.0) {
            return listOf(
                InstallmentPlan(
                    installmentId = "INS_${invoice.invoiceId}_1",
                    invoiceId = invoice.invoiceId,
                    installmentNumber = 1,
                    totalInstallments = 1,
                    amountDue = invoice.grandTotal,
                    dueDate = invoice.dueDate,
                    isPaid = invoice.status == PaymentStatus.SUCCESSFUL
                )
            )
        }

        val perInstallment = (invoice.grandTotal / totalInstallments * 100.0).roundToInt() / 100.0
        val monthMs = 30 * 24 * 60 * 60 * 1000L

        return (1..totalInstallments).map { num ->
            val due = invoice.dueDate + ((num - 1) * monthMs)
            InstallmentPlan(
                installmentId = "INS_${invoice.invoiceId}_$num",
                invoiceId = invoice.invoiceId,
                installmentNumber = num,
                totalInstallments = totalInstallments,
                amountDue = perInstallment,
                dueDate = due,
                isPaid = false
            )
        }
    }

    fun generateDigitalVerificationHash(studentId: String, invoiceId: String, transactionId: String, amount: Double): String {
        val raw = "UNI-FIN-$studentId-$invoiceId-$transactionId-$amount-${System.currentTimeMillis()}"
        val bytes = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }.take(16).uppercase()
    }
}
