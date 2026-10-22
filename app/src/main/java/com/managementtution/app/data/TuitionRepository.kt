package com.managementtution.app.data

import com.managementtution.app.model.AttendanceStatus
import com.managementtution.app.model.DashboardMetrics
import com.managementtution.app.model.FeeInvoice
import com.managementtution.app.model.StudentRecord
import com.managementtution.app.model.TuitionBatch
import com.managementtution.app.network.ApiResult
import com.managementtution.app.network.TuitionApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TuitionRepository(
    private val apiService: TuitionApiService? = null
) {
    private val batches = mutableListOf(
        TuitionBatch(
            id = 1,
            batchCode = "BAT-PHY-01",
            title = "JEE & WBJEE Physics Mastery",
            subjectAndTeacher = "Physics (Class XII) · Dr. Ananya Bose",
            schedule = "Mon, Wed, Fri · 07:30 AM - 09:30 AM",
            monthlyFeeInr = 1800
        ),
        TuitionBatch(
            id = 2,
            batchCode = "BAT-MATH-02",
            title = "Higher Secondary Calculus Intensive",
            subjectAndTeacher = "Mathematics (Class XII) · Prof. Subir Chatterjee",
            schedule = "Tue, Thu, Sat · 05:00 PM - 07:00 PM",
            monthlyFeeInr = 1600
        ),
        TuitionBatch(
            id = 3,
            batchCode = "BAT-SCI-03",
            title = "Madhyamik Physical Science Foundation",
            subjectAndTeacher = "Physical Science (Class X) · Debashis Roy",
            schedule = "Sat, Sun · 09:00 AM - 11:30 AM",
            monthlyFeeInr = 1200
        )
    )

    private val students = mutableListOf(
        StudentRecord(
            id = 101,
            admissionNo = "ADM-2026-01",
            fullName = "Ishaan Mukherjee",
            gradeLevel = "Class XII Science",
            batchCode = "BAT-PHY-01",
            guardianContact = "Debashis Mukherjee · +91 98302 66771",
            attendanceToday = AttendanceStatus.PRESENT
        ),
        StudentRecord(
            id = 102,
            admissionNo = "ADM-2026-02",
            fullName = "Riya Banerjee",
            gradeLevel = "Class XII Science",
            batchCode = "BAT-MATH-02",
            guardianContact = "Sutapa Banerjee · +91 98302 66772",
            attendanceToday = AttendanceStatus.PRESENT
        ),
        StudentRecord(
            id = 103,
            admissionNo = "ADM-2026-03",
            fullName = "Arjun Sen",
            gradeLevel = "Class X",
            batchCode = "BAT-SCI-03",
            guardianContact = "Tapas Sen · +91 98305 11229",
            attendanceToday = AttendanceStatus.LATE
        ),
        StudentRecord(
            id = 104,
            admissionNo = "ADM-2026-04",
            fullName = "Sana Rahman",
            gradeLevel = "Class XII Science",
            batchCode = "BAT-PHY-01",
            guardianContact = "Farhan Rahman · +91 98311 44880",
            attendanceToday = AttendanceStatus.ABSENT
        )
    )

    private val invoices = mutableListOf(
        FeeInvoice(
            id = 501,
            invoiceNo = "INV-2026-101",
            studentId = 101,
            studentName = "Ishaan Mukherjee",
            billingMonth = "October 2026",
            totalFeeInr = 1800,
            paidAmountInr = 1800
        ),
        FeeInvoice(
            id = 502,
            invoiceNo = "INV-2026-102",
            studentId = 102,
            studentName = "Riya Banerjee",
            billingMonth = "October 2026",
            totalFeeInr = 1600,
            paidAmountInr = 800
        ),
        FeeInvoice(
            id = 503,
            invoiceNo = "INV-2026-103",
            studentId = 103,
            studentName = "Arjun Sen",
            billingMonth = "October 2026",
            totalFeeInr = 1200,
            paidAmountInr = 1200
        ),
        FeeInvoice(
            id = 504,
            invoiceNo = "INV-2026-104",
            studentId = 104,
            studentName = "Sana Rahman",
            billingMonth = "October 2026",
            totalFeeInr = 1800,
            paidAmountInr = 0
        )
    )

    fun getBatches(): List<TuitionBatch> = batches.toList()

    fun getStudents(query: String = ""): List<StudentRecord> {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) return students.toList()
        return students.filter {
            it.fullName.lowercase().contains(trimmed) ||
                it.admissionNo.lowercase().contains(trimmed) ||
                it.batchCode.lowercase().contains(trimmed) ||
                it.gradeLevel.lowercase().contains(trimmed)
        }
    }

    fun getInvoices(): List<FeeInvoice> = invoices.toList()

    fun calculateDashboardMetrics(): DashboardMetrics {
        val totalStu = students.size
        val present = students.count {
            it.attendanceToday == AttendanceStatus.PRESENT || it.attendanceToday == AttendanceStatus.LATE
        }
        val pct = if (totalStu > 0) (present * 100) / totalStu else 0
        val collected = invoices.sumOf { it.paidAmountInr }
        val pending = invoices.sumOf { it.dueAmountInr }

        return DashboardMetrics(
            totalStudents = totalStu,
            totalBatches = batches.size,
            presentCountToday = present,
            attendancePercentage = pct,
            totalCollectedInr = collected,
            totalPendingInr = pending
        )
    }

    fun addStudent(
        fullName: String,
        gradeLevel: String,
        batchCode: String,
        guardianContact: String,
        monthlyFeeInr: Int
    ): StudentRecord {
        val nextId = (students.maxOfOrNull { it.id } ?: 100) + 1
        val admNo = "ADM-2026-${nextId - 100}"
        val record = StudentRecord(
            id = nextId,
            admissionNo = admNo,
            fullName = fullName.trim(),
            gradeLevel = gradeLevel.trim(),
            batchCode = batchCode.trim().uppercase(),
            guardianContact = guardianContact.trim(),
            attendanceToday = AttendanceStatus.PRESENT
        )
        students.add(0, record)

        val nextInvId = (invoices.maxOfOrNull { it.id } ?: 500) + 1
        invoices.add(
            0,
            FeeInvoice(
                id = nextInvId,
                invoiceNo = "INV-2026-$nextId",
                studentId = record.id,
                studentName = record.fullName,
                billingMonth = "October 2026",
                totalFeeInr = monthlyFeeInr.coerceAtLeast(100),
                paidAmountInr = 0
            )
        )
        return record
    }

    fun addBatch(
        batchCode: String,
        title: String,
        subjectAndTeacher: String,
        schedule: String
    ): TuitionBatch {
        val nextId = (batches.maxOfOrNull { it.id } ?: 0) + 1
        val batch = TuitionBatch(
            id = nextId,
            batchCode = batchCode.trim().uppercase(),
            title = title.trim(),
            subjectAndTeacher = subjectAndTeacher.trim(),
            schedule = schedule.trim(),
            monthlyFeeInr = 1500
        )
        batches.add(0, batch)
        return batch
    }

    fun cycleAttendanceStatus(studentId: Int): AttendanceStatus? {
        val index = students.indexOfFirst { it.id == studentId }
        if (index == -1) return null
        val current = students[index]
        val nextStatus = when (current.attendanceToday) {
            AttendanceStatus.PRESENT -> AttendanceStatus.LATE
            AttendanceStatus.LATE -> AttendanceStatus.ABSENT
            AttendanceStatus.ABSENT -> AttendanceStatus.PRESENT
        }
        students[index] = current.copy(attendanceToday = nextStatus)
        return nextStatus
    }

    fun markAllPresent() {
        for (i in students.indices) {
            students[i] = students[i].copy(attendanceToday = AttendanceStatus.PRESENT)
        }
    }

    fun recordFeePayment(invoiceId: Int, amountInr: Int): FeeInvoice? {
        if (amountInr <= 0) return null
        val index = invoices.indexOfFirst { it.id == invoiceId }
        if (index == -1) return null
        val current = invoices[index]
        val updatedPaid = (current.paidAmountInr + amountInr).coerceAtMost(current.totalFeeInr)
        val updated = current.copy(paidAmountInr = updatedPaid)
        invoices[index] = updated
        return updated
    }

    suspend fun syncWithBackend(): ApiResult<String> = withContext(Dispatchers.IO) {
        val service = apiService ?: return@withContext ApiResult.Error("API service not initialized")
        try {
            val response = service.checkHealth()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                ApiResult.Success("Connected (${body.status}, v${body.version})")
            } else {
                ApiResult.Error("Server returned HTTP ${response.code()}", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error("Offline local mode active (${e.javaClass.simpleName})")
        }
    }
}
