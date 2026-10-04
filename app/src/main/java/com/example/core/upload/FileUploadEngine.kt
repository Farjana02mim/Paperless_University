package com.example.core.upload

import android.content.Context
import android.net.Uri
import com.example.domain.model.AssignmentAttachment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.UUID

sealed class UploadState {
    object Idle : UploadState()
    data class Progress(val percent: Int, val bytesTransferred: Long, val totalBytes: Long) : UploadState()
    data class Success(val attachment: AssignmentAttachment) : UploadState()
    data class Error(val message: String) : UploadState()
}

object FileUploadEngine {

    val SUPPORTED_TYPES = listOf("PDF", "DOC", "DOCX", "PPT", "PPTX", "XLS", "XLSX", "ZIP", "RAR", "TXT", "JPG", "PNG", "MP4")

    fun validateFile(fileName: String, sizeBytes: Long, maxMb: Int): Result<Unit> {
        val ext = fileName.substringAfterLast('.', "").uppercase()
        if (ext.isNotBlank() && !SUPPORTED_TYPES.contains(ext)) {
            return Result.failure(IllegalArgumentException("Unsupported file type: .$ext. Allowed: ${SUPPORTED_TYPES.joinToString()}"))
        }

        val maxBytes = maxMb * 1024 * 1024L
        if (sizeBytes > maxBytes) {
            return Result.failure(IllegalArgumentException("File size (${sizeBytes / (1024 * 1024)}MB) exceeds limit of $maxMb MB."))
        }

        return Result.success(Unit)
    }

    fun uploadFileWithProgress(
        context: Context,
        uri: Uri,
        fileName: String,
        fileType: String,
        fileSizeBytes: Long
    ): Flow<UploadState> = flow {
        emit(UploadState.Progress(percent = 0, bytesTransferred = 0, totalBytes = fileSizeBytes))

        // Simulate chunked upload with progress events
        val totalSteps = 10
        val chunkSize = fileSizeBytes / totalSteps

        for (i in 1..totalSteps) {
            delay(150)
            val currentBytes = (chunkSize * i).coerceAtMost(fileSizeBytes)
            val pct = ((currentBytes.toDouble() / fileSizeBytes) * 100).toInt()
            emit(UploadState.Progress(percent = pct, bytesTransferred = currentBytes, totalBytes = fileSizeBytes))
        }

        val fileId = UUID.randomUUID().toString()
        val formattedSize = "%.1f MB".format(fileSizeBytes.toDouble() / (1024 * 1024))
        val attachment = AssignmentAttachment(
            fileId = fileId,
            fileName = fileName,
            fileUrl = uri.toString(),
            fileType = fileType.uppercase(),
            fileSizeFormatted = if (fileSizeBytes > 0) formattedSize else "1.5 MB"
        )

        emit(UploadState.Success(attachment))
    }
}
