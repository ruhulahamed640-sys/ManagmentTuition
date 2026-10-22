package com.managementtution.app

import com.managementtution.app.data.TuitionRepository
import com.managementtution.app.model.AttendanceStatus
import com.managementtution.app.model.InvoiceStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TuitionRepositoryTest {

    @Test
    fun `initial dashboard metrics match seeded batches students and invoices`() {
        val repo = TuitionRepository()
        val metrics = repo.calculateDashboardMetrics()

        assertEquals(4, metrics.totalStudents)
        assertEquals(3, metrics.totalBatches)
        assertEquals(3, metrics.presentCountToday)
        assertEquals(75, metrics.attendancePercentage)
        assertEquals(3800, metrics.totalCollectedInr)
        assertEquals(2600, metrics.totalPendingInr)
    }

    @Test
    fun `adding student creates corresponding monthly invoice and updates metrics`() {
        val repo = TuitionRepository()
        val created = repo.addStudent(
            fullName = "Anirban Ghosh",
            gradeLevel = "Class XI Science",
            batchCode = "BAT-PHY-01",
            guardianContact = "Subrata Ghosh · +91 98300 12345",
            monthlyFeeInr = 2000
        )

        assertEquals("ADM-2026-5", created.admissionNo)
        val metrics = repo.calculateDashboardMetrics()
        assertEquals(5, metrics.totalStudents)
        assertEquals(4600, metrics.totalPendingInr)
    }

    @Test
    fun `recording fee payment updates invoice status to PAID`() {
        val repo = TuitionRepository()
        val updated = repo.recordFeePayment(invoiceId = 502, amountInr = 800)

        assertNotNull(updated)
        assertEquals(0, updated?.dueAmountInr)
        assertEquals(InvoiceStatus.PAID, updated?.status)
    }

    @Test
    fun `cycling attendance transitions from PRESENT to LATE to ABSENT`() {
        val repo = TuitionRepository()
        val s1 = repo.cycleAttendanceStatus(101)
        assertEquals(AttendanceStatus.LATE, s1)
        val s2 = repo.cycleAttendanceStatus(101)
        assertEquals(AttendanceStatus.ABSENT, s2)
        val s3 = repo.cycleAttendanceStatus(101)
        assertEquals(AttendanceStatus.PRESENT, s3)
    }
}
