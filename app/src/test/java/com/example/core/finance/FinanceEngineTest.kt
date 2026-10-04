package com.example.core.finance

import com.example.domain.model.finance.FeeItem
import com.example.domain.model.finance.FeeType
import com.example.domain.model.finance.ScholarshipModel
import com.example.domain.model.finance.WaiverModel
import org.junit.Assert.*
import org.junit.Test

class FinanceEngineTest {

    @Test
    fun calculateLateFine_returnsZero_whenBeforeDueDate() {
        val dueDate = System.currentTimeMillis() + 100000L
        val fine = FeeCalculationEngine.calculateLateFine(dueDate)
        assertEquals(0.0, fine, 0.01)
    }

    @Test
    fun calculateLateFine_calculatesCorrectPenalty_whenOverdue() {
        val graceMs = FeeCalculationEngine.DEFAULT_GRACE_DAYS * 24 * 60 * 60 * 1000L
        val tenDaysOverdue = System.currentTimeMillis() - graceMs - (10 * 24 * 60 * 60 * 1000L)
        val fine = FeeCalculationEngine.calculateLateFine(tenDaysOverdue)
        assertTrue(fine > 0.0)
        assertTrue(fine <= FeeCalculationEngine.MAX_LATE_FINE)
    }

    @Test
    fun calculateInvoiceGrandTotal_appliesScholarshipAndWaiver() {
        val feeItems = listOf(
            FeeItem("1", FeeType.TUITION_FEE, "Tuition", 10000.0),
            FeeItem("2", FeeType.LAB_FEE, "Lab", 2000.0)
        )
        val scholarship = ScholarshipModel(percentage = 10.0) // 10% of 12000 = 1200
        val waiver = WaiverModel(percentage = 5.0) // 5% of 12000 = 600

        val invoice = FeeCalculationEngine.calculateInvoiceGrandTotal(
            feeItems = feeItems,
            scholarship = scholarship,
            waiver = waiver,
            dueDate = System.currentTimeMillis() + (10 * 24 * 60 * 60 * 1000L)
        )

        assertEquals(12000.0, invoice.totalAmount, 0.01)
        assertEquals(1200.0, invoice.scholarship, 0.01)
        assertEquals(600.0, invoice.waiver, 0.01)
        assertEquals(10200.0, invoice.grandTotal, 0.01)
    }

    @Test
    fun generateInstallments_dividesGrandTotalEqually() {
        val feeItems = listOf(FeeItem("1", FeeType.TUITION_FEE, "Tuition", 30000.0))
        val invoice = FeeCalculationEngine.calculateInvoiceGrandTotal(feeItems = feeItems)

        val installments = FeeCalculationEngine.generateInstallments(invoice, totalInstallments = 3)

        assertEquals(3, installments.size)
        assertEquals(10000.0, installments[0].amountDue, 0.01)
        assertEquals(10000.0, installments[1].amountDue, 0.01)
        assertEquals(10000.0, installments[2].amountDue, 0.01)
    }

    @Test
    fun generateDigitalVerificationHash_createsValidString() {
        val hash = FeeCalculationEngine.generateDigitalVerificationHash("STU1", "INV1", "TX1", 5000.0)
        assertNotNull(hash)
        assertEquals(16, hash.length)
    }
}
