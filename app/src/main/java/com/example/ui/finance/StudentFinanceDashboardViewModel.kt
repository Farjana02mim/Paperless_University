package com.example.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.finance.*
import com.example.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StudentFinanceUiState(
    val isLoading: Boolean = true,
    val invoices: List<InvoiceModel> = emptyList(),
    val payments: List<PaymentModel> = emptyList(),
    val totalOutstanding: Double = 0.0,
    val upcomingDue: Double = 0.0,
    val paidThisSemester: Double = 0.0,
    val totalScholarship: Double = 0.0,
    val selectedInvoice: InvoiceModel? = null,
    val errorMessage: String? = null
)

class StudentFinanceViewModel(
    private val repository: FinanceRepository,
    private val studentId: String = "STU-1001"
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentFinanceUiState())
    val uiState: StateFlow<StudentFinanceUiState> = _uiState.asStateFlow()

    init {
        loadStudentFinanceData()
    }

    fun loadStudentFinanceData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                repository.getInvoicesForStudent(studentId),
                repository.getPaymentsForStudent(studentId)
            ) { invoices, payments ->
                val outstanding = invoices.filter { it.status != PaymentStatus.SUCCESSFUL }.sumOf { it.dueAmount }
                val paid = payments.filter { it.status == PaymentStatus.SUCCESSFUL }.sumOf { it.amount }
                val scholarship = invoices.sumOf { it.scholarship }
                val upcoming = invoices.firstOrNull { it.status == PaymentStatus.PENDING }?.dueAmount ?: 0.0

                StudentFinanceUiState(
                    isLoading = false,
                    invoices = invoices,
                    payments = payments,
                    totalOutstanding = outstanding,
                    upcomingDue = upcoming,
                    paidThisSemester = paid,
                    totalScholarship = scholarship,
                    selectedInvoice = invoices.firstOrNull { it.status == PaymentStatus.PENDING }
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun selectInvoiceForCheckout(invoice: InvoiceModel) {
        _uiState.update { it.copy(selectedInvoice = invoice) }
    }
}

data class PaymentCheckoutUiState(
    val isProcessing: Boolean = false,
    val selectedGateway: PaymentGateway = PaymentGateway.BKASH,
    val completedPayment: PaymentModel? = null,
    val errorMessage: String? = null
)

class PaymentCheckoutViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _checkoutState = MutableStateFlow(PaymentCheckoutUiState())
    val checkoutState: StateFlow<PaymentCheckoutUiState> = _checkoutState.asStateFlow()

    fun selectGateway(gateway: PaymentGateway) {
        _checkoutState.update { it.copy(selectedGateway = gateway) }
    }

    fun executePayment(invoice: InvoiceModel, studentId: String = "STU-1001", studentName: String = "Alex Rivera") {
        viewModelScope.launch {
            _checkoutState.update { it.copy(isProcessing = true, errorMessage = null) }
            val request = PaymentModel(
                paymentId = "PAY-${System.currentTimeMillis().toString().takeLast(8)}",
                studentId = studentId,
                studentName = studentName,
                invoiceId = invoice.invoiceId,
                amount = invoice.dueAmount,
                currency = "BDT",
                paymentGateway = _checkoutState.value.selectedGateway,
                transactionId = "${_checkoutState.value.selectedGateway.name}_TX_${System.currentTimeMillis().toString().takeLast(6)}",
                gatewayReference = "REF_${System.currentTimeMillis().toString().takeLast(6)}",
                status = PaymentStatus.SUCCESSFUL,
                paymentDate = System.currentTimeMillis()
            )

            val result = repository.processPayment(request)
            result.onSuccess { payment ->
                _checkoutState.update { it.copy(isProcessing = false, completedPayment = payment) }
            }.onFailure { err ->
                _checkoutState.update { it.copy(isProcessing = false, errorMessage = err.message) }
            }
        }
    }
}

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val analytics: FinancialAnalytics? = null,
    val errorMessage: String? = null
)

class AdminFinanceAnalyticsViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _analyticsState = MutableStateFlow(AnalyticsUiState())
    val analyticsState: StateFlow<AnalyticsUiState> = _analyticsState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _analyticsState.update { it.copy(isLoading = true) }
            val res = repository.getFinancialAnalytics("ALL")
            res.onSuccess { data ->
                _analyticsState.update { it.copy(isLoading = false, analytics = data) }
            }.onFailure { err ->
                _analyticsState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }
}
