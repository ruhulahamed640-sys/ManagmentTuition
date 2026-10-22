package com.managementtution.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.managementtution.app.databinding.ItemBatchBinding
import com.managementtution.app.model.TuitionBatch

class BatchAdapter(
    private var items: List<TuitionBatch> = emptyList()
) : RecyclerView.Adapter<BatchAdapter.BatchViewHolder>() {

    fun submitList(newItems: List<TuitionBatch>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchViewHolder {
        val binding = ItemBatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BatchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class BatchViewHolder(private val binding: ItemBatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(batch: TuitionBatch) {
            binding.tvBatchName.text = batch.title
            binding.tvBatchCode.text = batch.batchCode
            binding.tvBatchSubjectTeacher.text = batch.subjectAndTeacher
            binding.tvBatchScheduleFee.text = "${batch.schedule} · ₹${batch.monthlyFeeInr}/mo"
        }
    }
}
