package com.managementtution.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.managementtution.app.databinding.ItemAttendanceBinding
import com.managementtution.app.model.StudentRecord

class AttendanceAdapter(
    private var items: List<StudentRecord> = emptyList(),
    private val onToggleClick: (StudentRecord) -> Unit
) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    fun submitList(newItems: List<StudentRecord>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(items[position], onToggleClick)
    }

    override fun getItemCount(): Int = items.size

    class AttendanceViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentRecord, onToggleClick: (StudentRecord) -> Unit) {
            binding.tvAttStudentName.text = student.fullName
            binding.tvAttBatchInfo.text = "${student.admissionNo} · ${student.batchCode}"
            binding.btnToggleAttendance.text = student.attendanceToday.displayName
            binding.btnToggleAttendance.setOnClickListener {
                onToggleClick(student)
            }
        }
    }
}
