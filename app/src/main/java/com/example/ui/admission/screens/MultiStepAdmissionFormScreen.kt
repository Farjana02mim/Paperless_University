package com.example.ui.admission.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.admission.*
import com.example.ui.admission.AdmissionFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiStepAdmissionFormScreen(
    viewModel: AdmissionFormViewModel,
    onBackClick: () -> Unit,
    onSubmitSuccess: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val app = uiState.application
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.submittedApplicationId) {
        uiState.submittedApplicationId?.let { id ->
            onSubmitSuccess(id)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Online Admission Form", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isSavingDraft) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Text("Draft Saved", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(end = 12.dp))
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.currentStep > 1) {
                        OutlinedButton(
                            onClick = { viewModel.previousStep() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (uiState.currentStep < 5) {
                        Button(
                            onClick = { viewModel.nextStep() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Continue")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Animated Stepper Header
            FormStepperHeader(currentStep = uiState.currentStep)

            // Step Content Box
            AnimatedContent(
                targetState = uiState.currentStep,
                label = "FormStepTransition"
            ) { step ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (step) {
                        1 -> item { Step1PersonalInfo(viewModel = viewModel, app = app, errors = uiState.validationErrors) }
                        2 -> item { Step2AcademicInfo(viewModel = viewModel, app = app, errors = uiState.validationErrors) }
                        3 -> item { Step3ProgramAndQuota(viewModel = viewModel, app = app, session = uiState.activeSession, errors = uiState.validationErrors) }
                        4 -> item { Step4DocumentUpload(viewModel = viewModel, app = app, isUploading = uiState.isUploadingDoc, errors = uiState.validationErrors) }
                        5 -> item { Step5PaymentAndSubmit(viewModel = viewModel, app = app, session = uiState.activeSession, isSubmitting = uiState.isSubmitting) }
                    }
                }
            }
        }
    }
}

@Composable
fun FormStepperHeader(currentStep: Int) {
    val stepTitles = listOf("Personal", "Academic", "Programs", "Documents", "Payment")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stepTitles.forEachIndexed { idx, title ->
            val stepNum = idx + 1
            val isCurrent = stepNum == currentStep
            val isDone = stepNum < currentStep

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isDone -> Color(0xFF388E3C)
                                isCurrent -> MaterialTheme.colorScheme.primary
                                else -> Color.LightGray
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text("$stepNum", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    title,
                    fontSize = 10.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}

// Step 1: Personal Info
@Composable
fun Step1PersonalInfo(
    viewModel: AdmissionFormViewModel,
    app: AdmissionApplication,
    errors: Map<String, String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Personal Information", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Enter your full legal name as per SSC / HSC certificate.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        OutlinedTextField(
            value = app.fullName,
            onValueChange = { viewModel.updatePersonalInfo(it, app.fatherName, app.motherName, app.dateOfBirth, app.gender, app.nationality, app.religion, app.bloodGroup, app.email, app.phone, app.address) },
            label = { Text("Applicant Full Name *") },
            isError = errors.containsKey("fullName"),
            supportingText = { errors["fullName"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = app.fatherName,
                onValueChange = { viewModel.updatePersonalInfo(app.fullName, it, app.motherName, app.dateOfBirth, app.gender, app.nationality, app.religion, app.bloodGroup, app.email, app.phone, app.address) },
                label = { Text("Father's Name") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = app.motherName,
                onValueChange = { viewModel.updatePersonalInfo(app.fullName, app.fatherName, it, app.dateOfBirth, app.gender, app.nationality, app.religion, app.bloodGroup, app.email, app.phone, app.address) },
                label = { Text("Mother's Name") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = app.dateOfBirth,
                onValueChange = { viewModel.updatePersonalInfo(app.fullName, app.fatherName, app.motherName, it, app.gender, app.nationality, app.religion, app.bloodGroup, app.email, app.phone, app.address) },
                label = { Text("Date of Birth (YYYY-MM-DD) *") },
                isError = errors.containsKey("dateOfBirth"),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = app.gender,
                onValueChange = { viewModel.updatePersonalInfo(app.fullName, app.fatherName, app.motherName, app.dateOfBirth, it, app.nationality, app.religion, app.bloodGroup, app.email, app.phone, app.address) },
                label = { Text("Gender") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = app.email,
                onValueChange = { viewModel.updatePersonalInfo(app.fullName, app.fatherName, app.motherName, app.dateOfBirth, app.gender, app.nationality, app.religion, app.bloodGroup, it, app.phone, app.address) },
                label = { Text("Email Address *") },
                isError = errors.containsKey("email"),
                supportingText = { errors["email"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = app.phone,
                onValueChange = { viewModel.updatePersonalInfo(app.fullName, app.fatherName, app.motherName, app.dateOfBirth, app.gender, app.nationality, app.religion, app.bloodGroup, app.email, it, app.address) },
                label = { Text("Phone Number *") },
                isError = errors.containsKey("phone"),
                supportingText = { errors["phone"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = app.address,
            onValueChange = { viewModel.updatePersonalInfo(app.fullName, app.fatherName, app.motherName, app.dateOfBirth, app.gender, app.nationality, app.religion, app.bloodGroup, app.email, app.phone, it) },
            label = { Text("Present / Permanent Address") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
    }
}

// Step 2: Academic Info
@Composable
fun Step2AcademicInfo(
    viewModel: AdmissionFormViewModel,
    app: AdmissionApplication,
    errors: Map<String, String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Academic Qualifications", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        // SSC Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("SSC / Equivalent Info", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = app.sscInfo.board, onValueChange = { viewModel.updateSscInfo(it, app.sscInfo.rollNumber, app.sscInfo.registrationNumber, app.sscInfo.passingYear, app.sscInfo.gpa, app.sscInfo.group, app.sscInfo.instituteName) }, label = { Text("Board") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = app.sscInfo.passingYear, onValueChange = { viewModel.updateSscInfo(app.sscInfo.board, app.sscInfo.rollNumber, app.sscInfo.registrationNumber, it, app.sscInfo.gpa, app.sscInfo.group, app.sscInfo.instituteName) }, label = { Text("Year") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = app.sscInfo.rollNumber, onValueChange = { viewModel.updateSscInfo(app.sscInfo.board, it, app.sscInfo.registrationNumber, app.sscInfo.passingYear, app.sscInfo.gpa, app.sscInfo.group, app.sscInfo.instituteName) }, label = { Text("Roll No") }, isError = errors.containsKey("sscRoll"), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = app.sscInfo.gpa.toString(), onValueChange = { viewModel.updateSscInfo(app.sscInfo.board, app.sscInfo.rollNumber, app.sscInfo.registrationNumber, app.sscInfo.passingYear, it.toDoubleOrNull() ?: 0.0, app.sscInfo.group, app.sscInfo.instituteName) }, label = { Text("GPA (out of 5)") }, isError = errors.containsKey("sscGpa"), modifier = Modifier.weight(1f))
                }
            }
        }

        // HSC Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("HSC / Equivalent Info", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = app.hscInfo.board, onValueChange = { viewModel.updateHscInfo(it, app.hscInfo.rollNumber, app.hscInfo.registrationNumber, app.hscInfo.passingYear, app.hscInfo.gpa, app.hscInfo.group, app.hscInfo.instituteName) }, label = { Text("Board") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = app.hscInfo.passingYear, onValueChange = { viewModel.updateHscInfo(app.hscInfo.board, app.hscInfo.rollNumber, app.hscInfo.registrationNumber, it, app.hscInfo.gpa, app.hscInfo.group, app.hscInfo.instituteName) }, label = { Text("Year") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = app.hscInfo.rollNumber, onValueChange = { viewModel.updateHscInfo(app.hscInfo.board, it, app.hscInfo.registrationNumber, app.hscInfo.passingYear, app.hscInfo.gpa, app.hscInfo.group, app.hscInfo.instituteName) }, label = { Text("Roll No") }, isError = errors.containsKey("hscRoll"), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = app.hscInfo.gpa.toString(), onValueChange = { viewModel.updateHscInfo(app.hscInfo.board, app.hscInfo.rollNumber, app.hscInfo.registrationNumber, app.hscInfo.passingYear, it.toDoubleOrNull() ?: 0.0, app.hscInfo.group, app.hscInfo.instituteName) }, label = { Text("GPA (out of 5)") }, isError = errors.containsKey("hscGpa"), modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// Step 3: Program & Quota Choice
@Composable
fun Step3ProgramAndQuota(
    viewModel: AdmissionFormViewModel,
    app: AdmissionApplication,
    session: AdmissionSession?,
    errors: Map<String, String>
) {
    val depts = session?.departments ?: listOf("Computer Science & Engineering", "Electrical Engineering", "Business Administration", "Law & Justice", "Pharmacy")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Department Choices & Quota", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        Text("First Preference Department *", fontWeight = FontWeight.Bold)
        depts.forEach { dept ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { viewModel.updateProgramAndQuota(dept, app.departmentChoice2, app.quotaType) }
                    .background(if (app.departmentChoice1 == dept) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                    .border(1.dp, if (app.departmentChoice1 == dept) MaterialTheme.colorScheme.primary else Color.LightGray, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = app.departmentChoice1 == dept, onClick = { viewModel.updateProgramAndQuota(dept, app.departmentChoice2, app.quotaType) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(dept, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Quota Reservation Category", fontWeight = FontWeight.Bold)
        QuotaType.entries.forEach { q ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { viewModel.updateProgramAndQuota(app.departmentChoice1, app.departmentChoice2, q) }
                    .background(if (app.quotaType == q) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
                    .border(1.dp, if (app.quotaType == q) MaterialTheme.colorScheme.secondary else Color.LightGray, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = app.quotaType == q, onClick = { viewModel.updateProgramAndQuota(app.departmentChoice1, app.departmentChoice2, q) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(q.name.replace("_", " "), fontWeight = FontWeight.Medium)
            }
        }
    }
}

// Step 4: Document Upload
@Composable
fun Step4DocumentUpload(
    viewModel: AdmissionFormViewModel,
    app: AdmissionApplication,
    isUploading: Boolean,
    errors: Map<String, String>
) {
    val requiredDocs = listOf(
        DocumentType.PASSPORT_PHOTO to "Passport Photo (JPEG / PNG)",
        DocumentType.SSC_MARKSHEET to "SSC Marksheet / Transcript (PDF)",
        DocumentType.HSC_MARKSHEET to "HSC Marksheet / Transcript (PDF)"
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Document Upload Portal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Upload clear scanned copies or original images.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        if (errors.containsKey("docs")) {
            Text(errors["docs"] ?: "", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }

        requiredDocs.forEach { (type, label) ->
            val uploadedDoc = app.documents.find { it.docType == type }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        if (uploadedDoc != null) {
                            Text("File: ${uploadedDoc.fileName} (${uploadedDoc.fileSizeKb} KB)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF388E3C))
                        } else {
                            Text("Status: Pending Upload", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }

                    if (uploadedDoc != null) {
                        IconButton(onClick = { viewModel.uploadDocument(type, "reupload_${type.name}.pdf", ByteArray(1024)) }) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF388E3C))
                        }
                    } else {
                        Button(
                            onClick = { viewModel.uploadDocument(type, "scan_${type.name}.pdf", ByteArray(2048)) },
                            enabled = !isUploading,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// Step 5: Payment & Submit
@Composable
fun Step5PaymentAndSubmit(
    viewModel: AdmissionFormViewModel,
    app: AdmissionApplication,
    session: AdmissionSession?,
    isSubmitting: Boolean
) {
    var selectedMethod by remember { mutableStateOf("bKash") }
    val fee = session?.admissionFee ?: 1500.0

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Admission Fee & Submission", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Application Summary", fontWeight = FontWeight.Bold)
                Text("Applicant: ${app.fullName}")
                Text("Primary Program: ${app.departmentChoice1}")
                Text("Total Admission Fee: ৳${fee.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        Text("Select Payment Gateway", fontWeight = FontWeight.Bold)

        listOf("bKash", "Nagad", "Rocket", "SSLCommerz / Card").forEach { method ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { selectedMethod = method }
                    .background(if (selectedMethod == method) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
                    .border(1.dp, if (selectedMethod == method) MaterialTheme.colorScheme.secondary else Color.LightGray, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selectedMethod == method, onClick = { selectedMethod = method })
                Spacer(modifier = Modifier.width(8.dp))
                Text(method, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { viewModel.processPaymentAndSubmit(selectedMethod) },
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Icon(Icons.Default.Payment, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pay ৳${fee.toInt()} & Submit Application", fontWeight = FontWeight.Bold)
            }
        }
    }
}
