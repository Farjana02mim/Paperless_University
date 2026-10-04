package com.example.domain.usecase.finance

import com.example.domain.model.finance.*
import com.example.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow

class GetStudentInvoicesUseCase(
    private val repository: FinanceRepository
) {
    operator fun invoke(studentId: String): Flow<List<InvoiceModel>> {
        return repository.getInvoicesForStudent(studentId)
    }
}

class GetStudentPaymentsUseCase(
    private val repository: FinanceRepository
) {
    operator fun invoke(studentId: String): Flow<List<PaymentModel>> {
        return repository.getPaymentsForStudent(studentId)
    }
}

class ProcessOnlinePaymentUseCase(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(payment: PaymentModel): Result<PaymentModel> {
        return repository.processPayment(payment)
    }
}

class GetFinancialAnalyticsUseCase(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(department: String = "ALL"): Result<FinancialAnalytics> {
        return repository.getFinancialAnalytics(department)
    }
}
