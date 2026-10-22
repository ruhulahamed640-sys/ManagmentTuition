package com.managementtution.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.managementtution.app.databinding.ItemFeeInvoiceBinding
import com.managementtution.app.model.FeeInvoice
import com.managementtution.app.model.InvoiceStatus

class FeeInvoiceAdapter(
    private var items: List<FeeInvoice> = emptyList(),
    private val onCollectPaymentClick: (FeeInvoice) -> Unit
) : RecyclerView.Adapter<FeeInvoiceAdapter.FeeInvoiceViewHolder>() {

    fun submitList(newItems: List<FeeInvoice>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeeInvoiceViewHolder {
        val binding = ItemFeeInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeeInvoiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeeInvoiceViewHolder, position: Int) {
        holder.bind(items[position], onCollectPaymentClick)
    }

    override fun getItemCount(): Int = items.size

    class FeeInvoiceViewHolder(private val binding: ItemFeeInvoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(invoice: FeeInvoice, onCollectPaymentClick: (FeeInvoice) -> Unit) {
            binding.tvInvoiceStudent.text = invoice.studentName
            binding.tvInvoiceStatus.text = invoice.status.displayName
            binding.tvInvoiceNumberMonth.text = "${invoice.invoiceNo} · ${invoice.billingMonth}"
            binding.tvInvoiceAmounts.text =
                "Total: ₹${invoice.totalFeeInr} · Paid: ₹${invoice.paidAmountInr} · Due: ₹${invoice.dueAmountInr}"

            if (invoice.status == InvoiceStatus.PAID) {
                binding.btnPayInvoice.visibility = View.GONE
            } else {
                binding.btnPayInvoice.visibility = View.VISIBLE
                binding.btnPayInvoice.setOnClickListener {
                    onCollectPaymentClick(invoice)
                }
            }
        }
    }
}
