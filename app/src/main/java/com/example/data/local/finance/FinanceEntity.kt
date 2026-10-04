package com.example.data.local.finance

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.finance.*

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val invoiceId: String,
    val studentId: String,
    val studentName: String,
    val rollNumber: String,
    val department: String,
    val semester: String,
    val academicYear: String,
    val feeItemsJson: String, // Serialized fee items
    val totalAmount: Double,
    val discount: Double,
    val waiver: Double,
    val scholarship: Double,
    val tax: Double,
    val lateFine: Double,
    val grandTotal: Double,
    val paidAmount: Double,
    val dueAmount: Double,
    val dueDate: Long,
    val status: String,
    val generatedAt: Long
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val paymentId: String,
    val studentId: String,
    val studentName: String,
    val invoiceId: String,
    val amount: Double,
    val currency: String,
    val paymentGateway: String,
    val transactionId: String,
    val gatewayReference: String,
    val status: String,
    val paymentDate: Long,
    val paymentMethod: String,
    val receiptUrl: String,
    val remarks: String,
    val verificationHash: String,
    val isSynced: Boolean = true
)

fun PaymentEntity.toDomain(): PaymentModel {
    return PaymentModel(
        paymentId = paymentId,
        studentId = studentId,
        studentName = studentName,
        invoiceId = invoiceId,
        amount = amount,
        currency = currency,
        paymentGateway = try { PaymentGateway.valueOf(paymentGateway) } catch (e: Exception) { PaymentGateway.BKASH },
        transactionId = transactionId,
        gatewayReference = gatewayReference,
        status = try { PaymentStatus.valueOf(status) } catch (e: Exception) { PaymentStatus.SUCCESSFUL },
        paymentDate = paymentDate,
        paymentMethod = paymentMethod,
        receiptUrl = receiptUrl,
        remarks = remarks,
        verificationHash = verificationHash
    )
}

fun PaymentModel.toEntity(isSynced: Boolean = true): PaymentEntity {
    return PaymentEntity(
        paymentId = paymentId,
        studentId = studentId,
        studentName = studentName,
        invoiceId = invoiceId,
        amount = amount,
        currency = currency,
        paymentGateway = paymentGateway.name,
        transactionId = transactionId,
        gatewayReference = gatewayReference,
        status = status.name,
        paymentDate = paymentDate,
        paymentMethod = paymentMethod,
        receiptUrl = receiptUrl,
        remarks = remarks,
        verificationHash = verificationHash,
        isSynced = isSynced
    )
}
