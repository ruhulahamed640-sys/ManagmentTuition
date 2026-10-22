package com.managementtution.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.managementtution.app.databinding.ItemStudentBinding
import com.managementtution.app.model.StudentRecord

class StudentAdapter(
    private var items: List<StudentRecord> = emptyList()
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    fun submitList(newItems: List<StudentRecord>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentRecord) {
            binding.tvStudentName.text = student.fullName
            binding.tvAdmissionNo.text = student.admissionNo
            binding.tvStudentBatchGrade.text = "${student.gradeLevel} · Batch: ${student.batchCode}"
            binding.tvGuardianPhone.text = "Guardian: ${student.guardianContact}"
        }
    }
}
