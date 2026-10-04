package com.example.domain.repository

import com.example.domain.model.finance.*
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getInvoicesForStudent(studentId: String): Flow<List<InvoiceModel>>
    fun getPaymentsForStudent(studentId: String): Flow<List<PaymentModel>>
    
    suspend fun getInvoiceById(invoiceId: String): Result<InvoiceModel>
    suspend fun processPayment(request: PaymentModel): Result<PaymentModel>
    suspend fun requestRefund(refundRequest: RefundRequest): Result<Unit>
    suspend fun getFinancialAnalytics(department: String): Result<FinancialAnalytics>
    suspend fun syncUnsyncedPayments(): Result<Int>
}
