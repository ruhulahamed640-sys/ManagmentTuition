package com.managementtution.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.managementtution.app.data.TuitionRepository
import com.managementtution.app.model.DashboardMetrics
import com.managementtution.app.model.FeeInvoice
import com.managementtution.app.model.StudentRecord
import com.managementtution.app.model.TuitionBatch
import com.managementtution.app.network.ApiResult
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: TuitionRepository
) : ViewModel() {

    private val _metrics = MutableLiveData<DashboardMetrics>()
    val metrics: LiveData<DashboardMetrics> = _metrics

    private val _batches = MutableLiveData<List<TuitionBatch>>()
    val batches: LiveData<List<TuitionBatch>> = _batches

    private val _students = MutableLiveData<List<StudentRecord>>()
    val students: LiveData<List<StudentRecord>> = _students

    private val _invoices = MutableLiveData<List<FeeInvoice>>()
    val invoices: LiveData<List<FeeInvoice>> = _invoices

    private val _isSyncing = MutableLiveData(false)
    val isSyncing: LiveData<Boolean> = _isSyncing

    private val _syncMessage = MutableLiveData<String>()
    val syncMessage: LiveData<String> = _syncMessage

    private var currentSearchQuery: String = ""

    init {
        refreshAllState()
    }

    fun refreshAllState() {
        _batches.value = repository.getBatches()
        _students.value = repository.getStudents(currentSearchQuery)
        _invoices.value = repository.getInvoices()
        _metrics.value = repository.calculateDashboardMetrics()
    }

    fun searchStudents(query: String) {
        currentSearchQuery = query
        _students.value = repository.getStudents(currentSearchQuery)
    }

    fun addNewStudent(
        fullName: String,
        gradeLevel: String,
        batchCode: String,
        guardianContact: String,
        monthlyFeeInr: Int
    ) {
        repository.addStudent(fullName, gradeLevel, batchCode, guardianContact, monthlyFeeInr)
        refreshAllState()
    }

    fun addNewBatch(
        batchCode: String,
        title: String,
        subjectAndTeacher: String,
        schedule: String
    ) {
        repository.addBatch(batchCode, title, subjectAndTeacher, schedule)
        refreshAllState()
    }

    fun toggleStudentAttendance(studentId: Int) {
        repository.cycleAttendanceStatus(studentId)
        refreshAllState()
    }

    fun markAllStudentsPresent() {
        repository.markAllPresent()
        refreshAllState()
    }

    fun collectFeePayment(invoiceId: Int, amountInr: Int) {
        repository.recordFeePayment(invoiceId, amountInr)
        refreshAllState()
    }

    fun syncWithRemoteApi() {
        _isSyncing.value = true
        viewModelScope.launch {
            when (val result = repository.syncWithBackend()) {
                is ApiResult.Success -> _syncMessage.value = result.data
                is ApiResult.Error -> _syncMessage.value = result.message
            }
            _isSyncing.value = false
            refreshAllState()
        }
    }
}

class MainViewModelFactory(
    private val repository: TuitionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
