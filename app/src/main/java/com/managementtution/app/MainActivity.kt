package com.managementtution.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.managementtution.app.data.TuitionRepository
import com.managementtution.app.databinding.ActivityMainBinding
import com.managementtution.app.databinding.DialogAddBatchBinding
import com.managementtution.app.databinding.DialogAddStudentBinding
import com.managementtution.app.databinding.DialogRecordPaymentBinding
import com.managementtution.app.model.FeeInvoice
import com.managementtution.app.network.RetrofitClient
import com.managementtution.app.ui.MainViewModel
import com.managementtution.app.ui.MainViewModelFactory
import com.managementtution.app.ui.adapter.AttendanceAdapter
import com.managementtution.app.ui.adapter.BatchAdapter
import com.managementtution.app.ui.adapter.FeeInvoiceAdapter
import com.managementtution.app.ui.adapter.StudentAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(TuitionRepository(RetrofitClient.apiService))
    }

    private lateinit var batchAdapter: BatchAdapter
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var attendanceAdapter: AttendanceAdapter
    private lateinit var feeInvoiceAdapter: FeeInvoiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        setupNavigationAndActions()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        batchAdapter = BatchAdapter()
        binding.rvDashboardBatches.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = batchAdapter
        }

        studentAdapter = StudentAdapter()
        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = studentAdapter
        }

        attendanceAdapter = AttendanceAdapter { student ->
            viewModel.toggleStudentAttendance(student.id)
        }
        binding.rvAttendance.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = attendanceAdapter
        }

        feeInvoiceAdapter = FeeInvoiceAdapter { invoice ->
            showRecordPaymentDialog(invoice)
        }
        binding.rvFeeInvoices.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = feeInvoiceAdapter
        }
    }

    private fun setupNavigationAndActions() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            binding.sectionDashboard.visibility =
                if (item.itemId == R.id.nav_dashboard) View.VISIBLE else View.GONE
            binding.sectionStudents.visibility =
                if (item.itemId == R.id.nav_students) View.VISIBLE else View.GONE
            binding.sectionAttendance.visibility =
                if (item.itemId == R.id.nav_attendance) View.VISIBLE else View.GONE
            binding.sectionFees.visibility =
                if (item.itemId == R.id.nav_fees) View.VISIBLE else View.GONE
            true
        }

        binding.btnSyncServer.setOnClickListener {
            viewModel.syncWithRemoteApi()
        }

        binding.btnQuickAddStudent.setOnClickListener { showAddStudentDialog() }
        binding.btnAddStudent.setOnClickListener { showAddStudentDialog() }
        binding.btnQuickAddBatch.setOnClickListener { showAddBatchDialog() }

        binding.btnQuickRecordFee.setOnClickListener {
            val firstUnpaid = viewModel.invoices.value?.firstOrNull { it.dueAmountInr > 0 }
            if (firstUnpaid != null) {
                showRecordPaymentDialog(firstUnpaid)
            } else {
                Toast.makeText(this, "All current invoices are settled!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRecordNewPayment.setOnClickListener {
            val firstUnpaid = viewModel.invoices.value?.firstOrNull { it.dueAmountInr > 0 }
            if (firstUnpaid != null) {
                showRecordPaymentDialog(firstUnpaid)
            } else {
                Toast.makeText(this, "All current invoices are settled!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnMarkAllPresent.setOnClickListener {
            viewModel.markAllStudentsPresent()
            Toast.makeText(this, "Marked all students Present for today.", Toast.LENGTH_SHORT).show()
        }

        binding.etSearchStudents.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchStudents(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun observeViewModel() {
        viewModel.metrics.observe(this) { m ->
            binding.tvKpiStudents.text = m.totalStudents.toString()
            binding.tvKpiBatches.text = "Across ${m.totalBatches} Active Batches"
            binding.tvKpiAttendanceToday.text = "${m.attendancePercentage}% (${m.presentCountToday}/${m.totalStudents})"
            binding.tvKpiCollectedFees.text = "₹${m.totalCollectedInr}"
            binding.tvKpiPendingDues.text = "₹${m.totalPendingInr}"
            binding.tvFeeSummaryBanner.text =
                "Collected: ₹${m.totalCollectedInr} · Due: ₹${m.totalPendingInr}"
        }

        viewModel.batches.observe(this) { list ->
            batchAdapter.submitList(list)
        }

        viewModel.students.observe(this) { list ->
            studentAdapter.submitList(list)
            attendanceAdapter.submitList(list)
            binding.tvEmptyStudents.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.invoices.observe(this) { list ->
            feeInvoiceAdapter.submitList(list)
        }

        viewModel.isSyncing.observe(this) { syncing ->
            binding.progressBarSync.visibility = if (syncing) View.VISIBLE else View.GONE
            binding.btnSyncServer.isEnabled = !syncing
        }

        viewModel.syncMessage.observe(this) { msg ->
            if (!msg.isNullOrBlank()) {
                binding.tvSyncStatus.text = msg
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddStudentDialog() {
        val dialogBinding = DialogAddStudentBinding.inflate(LayoutInflater.from(this))
        dialogBinding.etDialogStudentBatch.setText("BAT-PHY-01")
        dialogBinding.etDialogMonthlyFee.setText("1800")

        MaterialAlertDialogBuilder(this)
            .setTitle("Enroll New Student")
            .setView(dialogBinding.root)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Enroll") { _, _ ->
                val name = dialogBinding.etDialogStudentName.text?.toString().orEmpty().trim()
                val grade = dialogBinding.etDialogStudentGrade.text?.toString().orEmpty().trim()
                val batch = dialogBinding.etDialogStudentBatch.text?.toString().orEmpty().trim()
                val guardian = dialogBinding.etDialogGuardianPhone.text?.toString().orEmpty().trim()
                val fee = dialogBinding.etDialogMonthlyFee.text?.toString()?.toIntOrNull() ?: 1500

                if (name.isEmpty() || grade.isEmpty() || batch.isEmpty()) {
                    Toast.makeText(this, "Please fill in Name, Grade, and Batch Code.", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addNewStudent(
                        fullName = name,
                        gradeLevel = grade,
                        batchCode = batch,
                        guardianContact = if (guardian.isEmpty()) "Not Provided" else guardian,
                        monthlyFeeInr = fee
                    )
                    Toast.makeText(this, "Enrolled $name & generated monthly invoice.", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun showAddBatchDialog() {
        val dialogBinding = DialogAddBatchBinding.inflate(LayoutInflater.from(this))
        MaterialAlertDialogBuilder(this)
            .setTitle("Create Tuition Batch")
            .setView(dialogBinding.root)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Create") { _, _ ->
                val code = dialogBinding.etDialogBatchCode.text?.toString().orEmpty().trim()
                val title = dialogBinding.etDialogBatchTitle.text?.toString().orEmpty().trim()
                val teacher = dialogBinding.etDialogBatchTeacher.text?.toString().orEmpty().trim()
                val schedule = dialogBinding.etDialogBatchSchedule.text?.toString().orEmpty().trim()

                if (code.isEmpty() || title.isEmpty()) {
                    Toast.makeText(this, "Batch code and title are required.", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addNewBatch(
                        batchCode = code,
                        title = title,
                        subjectAndTeacher = if (teacher.isEmpty()) "General Faculty" else teacher,
                        schedule = if (schedule.isEmpty()) "Mon, Wed, Fri · 18:00 IST" else schedule
                    )
                    Toast.makeText(this, "Created batch $code.", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun showRecordPaymentDialog(invoice: FeeInvoice) {
        val dialogBinding = DialogRecordPaymentBinding.inflate(LayoutInflater.from(this))
        dialogBinding.tvDialogInvoiceDetails.text =
            "${invoice.studentName} (${invoice.invoiceNo}) · Due: ₹${invoice.dueAmountInr}"
        dialogBinding.etDialogPaymentAmount.setText(invoice.dueAmountInr.toString())

        MaterialAlertDialogBuilder(this)
            .setTitle("Record Fee Receipt")
            .setView(dialogBinding.root)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Record Receipt") { _, _ ->
                val amount = dialogBinding.etDialogPaymentAmount.text?.toString()?.toIntOrNull() ?: 0
                if (amount <= 0 || amount > invoice.dueAmountInr) {
                    Toast.makeText(
                        this,
                        "Enter a valid amount up to ₹${invoice.dueAmountInr}.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.collectFeePayment(invoice.id, amount)
                    Toast.makeText(
                        this,
                        "Recorded ₹$amount for ${invoice.studentName}.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .show()
    }
}
