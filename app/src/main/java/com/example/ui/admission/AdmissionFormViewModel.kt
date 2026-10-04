package com.example.ui.admission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.admission.*
import com.example.domain.repository.admission.AdmissionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdmissionFormUiState(
    val currentStep: Int = 1, // 1: Personal, 2: Academic, 3: Program & Quota, 4: Documents, 5: Payment & Review
    val activeSession: AdmissionSession? = null,
    val application: AdmissionApplication = AdmissionApplication(),
    val isSavingDraft: Boolean = false,
    val isSubmitting: Boolean = false,
    val isUploadingDoc: Boolean = false,
    val isProcessingPayment: Boolean = false,
    val validationErrors: Map<String, String> = emptyMap(),
    val userMessage: String? = null,
    val submittedApplicationId: String? = null
)

class AdmissionFormViewModel(
    private val repository: AdmissionRepository,
    private val applicantId: String = "user_applicant_01"
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdmissionFormUiState())
    val uiState: StateFlow<AdmissionFormUiState> = _uiState.asStateFlow()

    init {
        loadActiveSessionAndDraft()
    }

    private fun loadActiveSessionAndDraft() {
        viewModelScope.launch {
            repository.getActiveSession().collect { session ->
                _uiState.update { it.copy(activeSession = session) }
                if (session != null) {
                    loadDraft(session.sessionId)
                }
            }
        }
    }

    private fun loadDraft(sessionId: String) {
        viewModelScope.launch {
            repository.getDraftApplication(applicantId, sessionId).collect { draft ->
                if (draft != null) {
                    _uiState.update { state ->
                        state.copy(
                            application = draft.copy(
                                applicantId = applicantId,
                                sessionId = sessionId,
                                sessionName = state.activeSession?.name ?: ""
                            )
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            application = AdmissionApplication(
                                applicantId = applicantId,
                                sessionId = sessionId,
                                sessionName = state.activeSession?.name ?: "",
                                departmentChoice1 = state.activeSession?.departments?.firstOrNull() ?: "Computer Science & Engineering"
                            )
                        )
                    }
                }
            }
        }
    }

    fun updatePersonalInfo(
        fullName: String,
        fatherName: String,
        motherName: String,
        dateOfBirth: String,
        gender: String,
        nationality: String,
        religion: String,
        bloodGroup: String,
        email: String,
        phone: String,
        address: String
    ) {
        _uiState.update { state ->
            state.copy(
                application = state.application.copy(
                    fullName = fullName,
                    fatherName = fatherName,
                    motherName = motherName,
                    dateOfBirth = dateOfBirth,
                    gender = gender,
                    nationality = nationality,
                    religion = religion,
                    bloodGroup = bloodGroup,
                    email = email,
                    phone = phone,
                    address = address
                )
            )
        }
        autoSaveDraft()
    }

    fun updateSscInfo(board: String, roll: String, reg: String, year: String, gpa: Double, group: String, institute: String) {
        _uiState.update { state ->
            val updatedSsc = AcademicInfo(board, roll, reg, year, gpa, group, institute)
            state.copy(application = state.application.copy(sscInfo = updatedSsc))
        }
        autoSaveDraft()
    }

    fun updateHscInfo(board: String, roll: String, reg: String, year: String, gpa: Double, group: String, institute: String) {
        _uiState.update { state ->
            val updatedHsc = AcademicInfo(board, roll, reg, year, gpa, group, institute)
            state.copy(application = state.application.copy(hscInfo = updatedHsc))
        }
        autoSaveDraft()
    }

    fun updateProgramAndQuota(choice1: String, choice2: String, quotaType: QuotaType) {
        _uiState.update { state ->
            state.copy(
                application = state.application.copy(
                    departmentChoice1 = choice1,
                    departmentChoice2 = choice2,
                    quotaType = quotaType
                )
            )
        }
        autoSaveDraft()
    }

    private fun autoSaveDraft() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingDraft = true) }
            repository.saveApplicationDraft(_uiState.value.application)
            _uiState.update { it.copy(isSavingDraft = false) }
        }
    }

    fun validateStep(step: Int): Boolean {
        val app = _uiState.value.application
        val errors = mutableMapOf<String, String>()

        when (step) {
            1 -> {
                if (app.fullName.isBlank()) errors["fullName"] = "Full Name is required"
                if (app.email.isBlank() || !app.email.contains("@")) errors["email"] = "Valid Email is required"
                if (app.phone.isBlank() || app.phone.length < 11) errors["phone"] = "Valid 11-digit Phone is required"
                if (app.dateOfBirth.isBlank()) errors["dateOfBirth"] = "Date of Birth is required"
            }
            2 -> {
                if (app.sscInfo.gpa <= 0.0) errors["sscGpa"] = "Valid SSC GPA is required"
                if (app.hscInfo.gpa <= 0.0) errors["hscGpa"] = "Valid HSC GPA is required"
                if (app.sscInfo.rollNumber.isBlank()) errors["sscRoll"] = "SSC Roll is required"
                if (app.hscInfo.rollNumber.isBlank()) errors["hscRoll"] = "HSC Roll is required"
                val minSsc = _uiState.value.activeSession?.sscMinGpa ?: 3.5
                val minHsc = _uiState.value.activeSession?.hscMinGpa ?: 3.5
                if (app.sscInfo.gpa < minSsc) errors["sscGpa"] = "SSC GPA must be at least $minSsc"
                if (app.hscInfo.gpa < minHsc) errors["hscGpa"] = "HSC GPA must be at least $minHsc"
            }
            3 -> {
                if (app.departmentChoice1.isBlank()) errors["dept1"] = "Department Choice 1 is required"
            }
            4 -> {
                val hasPhoto = app.documents.any { it.docType == DocumentType.PASSPORT_PHOTO }
                val hasSsc = app.documents.any { it.docType == DocumentType.SSC_MARKSHEET }
                val hasHsc = app.documents.any { it.docType == DocumentType.HSC_MARKSHEET }
                if (!hasPhoto) errors["docs"] = "Passport Photo upload is mandatory"
                if (!hasSsc) errors["docs"] = "SSC Marksheet upload is mandatory"
                if (!hasHsc) errors["docs"] = "HSC Marksheet upload is mandatory"
            }
        }

        _uiState.update { it.copy(validationErrors = errors) }
        return errors.isEmpty()
    }

    fun nextStep() {
        if (validateStep(_uiState.value.currentStep)) {
            _uiState.update { it.copy(currentStep = (it.currentStep + 1).coerceAtMost(5)) }
        } else {
            _uiState.update { it.copy(userMessage = "Please fix errors before proceeding") }
        }
    }

    fun previousStep() {
        _uiState.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(1)) }
    }

    fun uploadDocument(docType: DocumentType, fileName: String, fileBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingDoc = true) }
            val appId = _uiState.value.application.applicationId.ifBlank { "DRAFT_${System.currentTimeMillis()}" }
            val result = repository.uploadDocument(appId, docType, fileName, fileBytes)
            result.onSuccess { doc ->
                _uiState.update { state ->
                    val updatedDocs = state.application.documents.filterNot { it.docType == docType } + doc
                    state.copy(
                        isUploadingDoc = false,
                        application = state.application.copy(documents = updatedDocs),
                        userMessage = "${docType.name.replace("_", " ")} uploaded successfully"
                    )
                }
                autoSaveDraft()
            }.onFailure { err ->
                _uiState.update { it.copy(isUploadingDoc = false, userMessage = "Upload failed: ${err.message}") }
            }
        }
    }

    fun processPaymentAndSubmit(paymentMethod: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPayment = true, isSubmitting = true) }

            val trxId = "${paymentMethod.uppercase()}-TRX-${(100000..999999).random()}"
            val fee = _uiState.value.activeSession?.admissionFee ?: 1500.0

            val submitRes = repository.submitApplication(_uiState.value.application)
            submitRes.onSuccess { finalAppId ->
                repository.processAdmissionPayment(finalAppId, paymentMethod, fee, trxId)
                _uiState.update {
                    it.copy(
                        isProcessingPayment = false,
                        isSubmitting = false,
                        submittedApplicationId = finalAppId,
                        userMessage = "Application & Fee Payment Submitted Successfully!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isProcessingPayment = false,
                        isSubmitting = false,
                        userMessage = "Submission error: ${err.message}"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
