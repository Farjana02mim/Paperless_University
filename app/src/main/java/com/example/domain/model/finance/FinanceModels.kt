package com.example.domain.model.finance

enum class FeeType {
    ADMISSION_FEE,
    SEMESTER_FEE,
    TUITION_FEE,
    REGISTRATION_FEE,
    LAB_FEE,
    LIBRARY_FEE,
    HOSTEL_FEE,
    TRANSPORT_FEE,
    EXAMINATION_FEE,
    DEVELOPMENT_FEE,
    GRADUATION_FEE,
    CONVOCATION_FEE,
    LATE_FINE,
    MISCELLANEOUS
}

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCESSFUL,
    FAILED,
    CANCELLED,
    REFUNDED,
    PARTIALLY_REFUNDED
}

enum class PaymentGateway {
    BKASH,
    NAGAD,
    ROCKET,
    SSLCOMMERZ,
    VISA_MASTER_CARD,
    AMERICAN_EXPRESS,
    GOOGLE_PAY,
    APPLE_PAY,
    STRIPE,
    PAYPAL,
    MANUAL_BANK_TRANSFER
}

enum class RefundStatus {
    REQUESTED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
    DISBURSED
}

data class FeeItem(
    val itemId: String = "",
    val feeType: FeeType = FeeType.TUITION_FEE,
    val title: String = "",
    val amount: Double = 0.0,
    val isMandatory: Boolean = true
)

data class ScholarshipModel(
    val scholarshipId: String = "",
    val studentId: String = "",
    val name: String = "",
    val percentage: Double = 0.0,
    val fixedAmount: Double = 0.0,
    val effectiveSemester: String = "",
    val expirySemester: String = "",
    val conditions: String = ""
)

data class WaiverModel(
    val waiverId: String = "",
    val studentId: String = "",
    val reason: String = "",
    val percentage: Double = 0.0,
    val approvedBy: String = "",
    val approvalDate: Long = System.currentTimeMillis()
)

data class InstallmentPlan(
    val installmentId: String = "",
    val invoiceId: String = "",
    val installmentNumber: Int = 1,
    val totalInstallments: Int = 3,
    val amountDue: Double = 0.0,
    val dueDate: Long = 0L,
    val isPaid: Boolean = false,
    val paidDate: Long? = null
)

data class InvoiceModel(
    val invoiceId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val rollNumber: String = "",
    val department: String = "",
    val semester: String = "",
    val academicYear: String = "2025-2026",
    val feeItems: List<FeeItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val discount: Double = 0.0,
    val waiver: Double = 0.0,
    val scholarship: Double = 0.0,
    val tax: Double = 0.0,
    val lateFine: Double = 0.0,
    val grandTotal: Double = 0.0,
    val paidAmount: Double = 0.0,
    val dueAmount: Double = 0.0,
    val dueDate: Long = 0L,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val generatedAt: Long = System.currentTimeMillis(),
    val installments: List<InstallmentPlan> = emptyList()
)

data class PaymentModel(
    val paymentId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val invoiceId: String = "",
    val amount: Double = 0.0,
    val currency: String = "BDT",
    val paymentGateway: PaymentGateway = PaymentGateway.BKASH,
    val transactionId: String = "",
    val gatewayReference: String = "",
    val status: PaymentStatus = PaymentStatus.SUCCESSFUL,
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentMethod: String = "Mobile Wallet",
    val receiptUrl: String = "",
    val remarks: String = "",
    val verificationHash: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class RefundRequest(
    val refundId: String = "",
    val paymentId: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val amount: Double = 0.0,
    val reason: String = "",
    val status: RefundStatus = RefundStatus.REQUESTED,
    val requestedAt: Long = System.currentTimeMillis(),
    val reviewedBy: String = "",
    val remarks: String = ""
)

data class FinancialAnalytics(
    val totalRevenueCollected: Double = 0.0,
    val totalOutstandingBalance: Double = 0.0,
    val totalScholarshipDisbursed: Double = 0.0,
    val totalRefundsProcessed: Double = 0.0,
    val dailyCollection: Map<String, Double> = emptyMap(),
    val monthlyRevenue: Map<String, Double> = emptyMap(),
    val feeDistribution: Map<String, Double> = emptyMap(),
    val departmentRevenue: Map<String, Double> = emptyMap(),
    val topDefaultersCount: Int = 12
)
