package com.managementtution.app.model

enum class AttendanceStatus(val displayName: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
    LATE("Late")
}

enum class InvoiceStatus(val displayName: String) {
    PAID("PAID"),
    PARTIAL("PARTIAL"),
    DUE("DUE")
}

data class TuitionBatch(
    val id: Int,
    val batchCode: String,
    val title: String,
    val subjectAndTeacher: String,
    val schedule: String,
    val monthlyFeeInr: Int
)

data class StudentRecord(
    val id: Int,
    val admissionNo: String,
    val fullName: String,
    val gradeLevel: String,
    val batchCode: String,
    val guardianContact: String,
    val attendanceToday: AttendanceStatus = AttendanceStatus.PRESENT
)

data class FeeInvoice(
    val id: Int,
    val invoiceNo: String,
    val studentId: Int,
    val studentName: String,
    val billingMonth: String,
    val totalFeeInr: Int,
    val paidAmountInr: Int
) {
    val dueAmountInr: Int
        get() = (totalFeeInr - paidAmountInr).coerceAtLeast(0)

    val status: InvoiceStatus
        get() = when {
            dueAmountInr == 0 -> InvoiceStatus.PAID
            paidAmountInr > 0 -> InvoiceStatus.PARTIAL
            else -> InvoiceStatus.DUE
        }
}

data class DashboardMetrics(
    val totalStudents: Int,
    val totalBatches: Int,
    val presentCountToday: Int,
    val attendancePercentage: Int,
    val totalCollectedInr: Int,
    val totalPendingInr: Int
)

data class ApiHealthResponse(
    val status: String,
    val version: String,
    val timestamp: String
)
