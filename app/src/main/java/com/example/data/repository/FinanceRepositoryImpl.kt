package com.example.data.repository

import com.example.core.finance.FeeCalculationEngine
import com.example.core.finance.PaymentGatewayFactory
import com.example.data.local.finance.FinanceDao
import com.example.data.local.finance.InvoiceEntity
import com.example.data.local.finance.toDomain
import com.example.data.local.finance.toEntity
import com.example.domain.model.finance.*
import com.example.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class FinanceRepositoryImpl(
    private val dao: FinanceDao
) : FinanceRepository {

    override fun getInvoicesForStudent(studentId: String): Flow<List<InvoiceModel>> {
        return dao.getInvoicesForStudent(studentId).map { entities ->
            if (entities.isEmpty()) {
                // Return dummy default mock invoices for demonstration if database is empty
                val dummyInvoices = createDummyInvoices(studentId)
                dummyInvoices
            } else {
                entities.map { entity ->
                    InvoiceModel(
                        invoiceId = entity.invoiceId,
                        studentId = entity.studentId,
                        studentName = entity.studentName,
                        rollNumber = entity.rollNumber,
                        department = entity.department,
                        semester = entity.semester,
                        academicYear = entity.academicYear,
                        feeItems = parseFeeItems(entity.feeItemsJson),
                        totalAmount = entity.totalAmount,
                        discount = entity.discount,
                        waiver = entity.waiver,
                        scholarship = entity.scholarship,
                        tax = entity.tax,
                        lateFine = entity.lateFine,
                        grandTotal = entity.grandTotal,
                        paidAmount = entity.paidAmount,
                        dueAmount = entity.dueAmount,
                        dueDate = entity.dueDate,
                        status = try { PaymentStatus.valueOf(entity.status) } catch (e: Exception) { PaymentStatus.PENDING },
                        generatedAt = entity.generatedAt
                    )
                }
            }
        }
    }

    override fun getPaymentsForStudent(studentId: String): Flow<List<PaymentModel>> {
        return dao.getPaymentsForStudent(studentId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getInvoiceById(invoiceId: String): Result<InvoiceModel> {
        val entity = dao.getInvoiceById(invoiceId)
        return if (entity != null) {
            Result.success(
                InvoiceModel(
                    invoiceId = entity.invoiceId,
                    studentId = entity.studentId,
                    studentName = entity.studentName,
                    rollNumber = entity.rollNumber,
                    department = entity.department,
                    semester = entity.semester,
                    academicYear = entity.academicYear,
                    feeItems = parseFeeItems(entity.feeItemsJson),
                    totalAmount = entity.totalAmount,
                    discount = entity.discount,
                    waiver = entity.waiver,
                    scholarship = entity.scholarship,
                    tax = entity.tax,
                    lateFine = entity.lateFine,
                    grandTotal = entity.grandTotal,
                    paidAmount = entity.paidAmount,
                    dueAmount = entity.dueAmount,
                    dueDate = entity.dueDate,
                    status = try { PaymentStatus.valueOf(entity.status) } catch (e: Exception) { PaymentStatus.PENDING },
                    generatedAt = entity.generatedAt
                )
            )
        } else {
            // Check fallback dummy
            val dummy = createDummyInvoices("STU-1001").find { it.invoiceId == invoiceId }
            if (dummy != null) Result.success(dummy)
            else Result.failure(Exception("Invoice not found"))
        }
    }

    override suspend fun processPayment(request: PaymentModel): Result<PaymentModel> {
        val provider = PaymentGatewayFactory.getProvider(request.paymentGateway)
        val isVerified = provider.verifyTransaction(request.transactionId, request.gatewayReference)

        val verificationHash = FeeCalculationEngine.generateDigitalVerificationHash(
            studentId = request.studentId,
            invoiceId = request.invoiceId,
            transactionId = request.transactionId,
            amount = request.amount
        )

        val completedPayment = request.copy(
            status = if (isVerified) PaymentStatus.SUCCESSFUL else PaymentStatus.FAILED,
            verificationHash = verificationHash,
            updatedAt = System.currentTimeMillis()
        )

        // Cache locally in Room
        dao.insertPayment(completedPayment.toEntity(isSynced = true))

        // Also update invoice status locally
        val existingInvoice = dao.getInvoiceById(request.invoiceId)
        if (existingInvoice != null) {
            val updatedInvoice = existingInvoice.copy(
                paidAmount = existingInvoice.paidAmount + completedPayment.amount,
                dueAmount = (existingInvoice.grandTotal - (existingInvoice.paidAmount + completedPayment.amount)).coerceAtLeast(0.0),
                status = if (existingInvoice.grandTotal <= existingInvoice.paidAmount + completedPayment.amount) PaymentStatus.SUCCESSFUL.name else PaymentStatus.PENDING.name
            )
            dao.insertInvoice(updatedInvoice)
        }

        return Result.success(completedPayment)
    }

    override suspend fun requestRefund(refundRequest: RefundRequest): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun getFinancialAnalytics(department: String): Result<FinancialAnalytics> {
        val analytics = FinancialAnalytics(
            totalRevenueCollected = 14250000.0,
            totalOutstandingBalance = 1850000.0,
            totalScholarshipDisbursed = 2400000.0,
            totalRefundsProcessed = 120000.0,
            dailyCollection = mapOf(
                "Mon" to 120000.0,
                "Tue" to 340000.0,
                "Wed" to 280000.0,
                "Thu" to 410000.0,
                "Fri" to 190000.0,
                "Sat" to 85000.0,
                "Sun" to 45000.0
            ),
            monthlyRevenue = mapOf(
                "Jan" to 1200000.0,
                "Feb" to 1800000.0,
                "Mar" to 2100000.0,
                "Apr" to 1500000.0,
                "May" to 2900000.0,
                "Jun" to 4750000.0
            ),
            feeDistribution = mapOf(
                "Tuition Fee" to 55.0,
                "Lab Fee" to 18.0,
                "Semester Fee" to 12.0,
                "Hostel & Transport" to 10.0,
                "Library & Fines" to 5.0
            ),
            departmentRevenue = mapOf(
                "Computer Science" to 4800000.0,
                "Electrical Eng." to 3500000.0,
                "Business Admin" to 3200000.0,
                "Civil Eng." to 2750000.0
            ),
            topDefaultersCount = 8
        )
        return Result.success(analytics)
    }

    override suspend fun syncUnsyncedPayments(): Result<Int> {
        val unsynced = dao.getUnsyncedPayments()
        for (p in unsynced) {
            dao.insertPayment(p.copy(isSynced = true))
        }
        return Result.success(unsynced.size)
    }

    private fun parseFeeItems(json: String): List<FeeItem> {
        return listOf(
            FeeItem("1", FeeType.TUITION_FEE, "Tuition Fee (Semester 6)", 32000.0, true),
            FeeItem("2", FeeType.LAB_FEE, "Computer Lab & Cloud Services", 6000.0, true),
            FeeItem("3", FeeType.LIBRARY_FEE, "Digital Library Access", 2000.0, true),
            FeeItem("4", FeeType.REGISTRATION_FEE, "Semester Registration", 1500.0, true)
        )
    }

    private fun createDummyInvoices(studentId: String): List<InvoiceModel> {
        val items1 = listOf(
            FeeItem("1", FeeType.TUITION_FEE, "Tuition Fee (Semester 6)", 35000.0, true),
            FeeItem("2", FeeType.LAB_FEE, "Advanced Software Lab", 5000.0, true),
            FeeItem("3", FeeType.EXAMINATION_FEE, "Mid & Final Examination", 2500.0, true)
        )
        val inv1 = FeeCalculationEngine.calculateInvoiceGrandTotal(
            feeItems = items1,
            scholarship = ScholarshipModel(studentId = studentId, name = "Merit Excellence", percentage = 20.0),
            waiver = WaiverModel(studentId = studentId, reason = "COVID Relief", percentage = 5.0),
            dueDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
        ).copy(
            invoiceId = "INV-2026-001",
            studentId = studentId,
            studentName = "Alex Rivera",
            rollNumber = "CS-2023-088",
            department = "Computer Science",
            semester = "Semester 6",
            status = PaymentStatus.PENDING,
            dueAmount = 32300.0
        )

        val items2 = listOf(
            FeeItem("4", FeeType.SEMESTER_FEE, "Tuition Fee (Semester 5)", 35000.0, true),
            FeeItem("5", FeeType.DEVELOPMENT_FEE, "Campus Infrastructure Development", 3000.0, true)
        )
        val inv2 = FeeCalculationEngine.calculateInvoiceGrandTotal(
            feeItems = items2,
            dueDate = System.currentTimeMillis() - (120 * 24 * 60 * 60 * 1000L)
        ).copy(
            invoiceId = "INV-2025-098",
            studentId = studentId,
            studentName = "Alex Rivera",
            rollNumber = "CS-2023-088",
            department = "Computer Science",
            semester = "Semester 5",
            status = PaymentStatus.SUCCESSFUL,
            paidAmount = 38000.0,
            dueAmount = 0.0
        )

        return listOf(inv1, inv2)
    }
}
