package com.example

import com.example.core.upload.FileUploadEngine
import com.example.domain.model.Assignment
import com.example.domain.model.AssignmentStatus
import com.example.domain.model.SubmissionStatus
import org.junit.Assert.*
import org.junit.Test

class AssignmentSystemTest {

    @Test
    fun testFileValidation_ValidPdf() {
        val result = FileUploadEngine.validateFile(
            fileName = "computer_networks_solution.pdf",
            sizeBytes = 5 * 1024 * 1024L,
            maxMb = 25
        )
        assertTrue("Valid 5MB PDF file should pass validation", result.isSuccess)
    }

    @Test
    fun testFileValidation_ExceedsSizeLimit() {
        val result = FileUploadEngine.validateFile(
            fileName = "heavy_video_project.mp4",
            sizeBytes = 50 * 1024 * 1024L,
            maxMb = 25
        )
        assertTrue("File exceeding 25MB limit should fail", result.isFailure)
    }

    @Test
    fun testFileValidation_InvalidExtension() {
        val result = FileUploadEngine.validateFile(
            fileName = "malicious_script.exe",
            sizeBytes = 1 * 1024 * 1024L,
            maxMb = 25
        )
        assertTrue("Disallowed file extension .exe should fail", result.isFailure)
    }

    @Test
    fun testAssignmentDeadlineStatus() {
        val futureDeadline = System.currentTimeMillis() + 86400000L
        val assignment = Assignment(
            assignmentId = "ASSIGN_101",
            title = "Algorithms Benchmark",
            deadline = futureDeadline,
            status = AssignmentStatus.PUBLISHED
        )

        assertTrue("Assignment deadline is in future", assignment.deadline > System.currentTimeMillis())
        assertEquals("Assignment status is PUBLISHED", AssignmentStatus.PUBLISHED, assignment.status)
    }
}
