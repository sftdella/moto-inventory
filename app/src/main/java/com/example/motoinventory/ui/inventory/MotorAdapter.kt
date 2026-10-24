package com.example.motoinventory.ui.inventory

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.motoinventory.R
import com.example.motoinventory.data.entity.Motor
import com.example.motoinventory.databinding.ItemMotorBinding
import com.example.motoinventory.utils.FormatUtils

class MotorAdapter(
    private val onItemClick: (Motor) -> Unit
) : ListAdapter<Motor, MotorAdapter.MotorViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Motor>() {
            override fun areItemsTheSame(old: Motor, new: Motor) = old.id == new.id
            override fun areContentsTheSame(old: Motor, new: Motor) = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotorViewHolder {
        val binding = ItemMotorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MotorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MotorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MotorViewHolder(private val binding: ItemMotorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(motor: Motor) {
            val ctx = binding.root.context
            binding.apply {
                tvNamMotor.text = motor.namMotor
                tvMerk.text = "${motor.merk} • ${motor.tahun}"
                tvWarna.text = motor.warna
                tvHargaBeli.text = FormatUtils.formatRupiah(motor.hargaBeli)
                tvHargaJual.text = FormatUtils.formatRupiah(motor.hargaJual)
                tvTanggal.text = FormatUtils.formatDate(motor.tanggalMasuk)

                val selisih = motor.hargaJual - motor.hargaBeli
                tvSelisih.text = FormatUtils.formatRupiah(selisih)
                tvSelisih.setTextColor(
                    if (selisih >= 0) ContextCompat.getColor(ctx, R.color.success)
                    else ContextCompat.getColor(ctx, R.color.danger)
                )

                // Status badge
                val (statusText, statusColor, statusBg) = when (motor.status) {
                    Motor.STATUS_TERSEDIA -> Triple(
                        "Tersedia",
                        ctx.getColor(R.color.status_tersedia),
                        ctx.getColor(R.color.status_tersedia_bg)
                    )
                    Motor.STATUS_SERVIS -> Triple(
                        "Servis",
                        ctx.getColor(R.color.status_servis),
                        ctx.getColor(R.color.status_servis_bg)
                    )
                    Motor.STATUS_APPROVAL -> Triple(
                        "Approval",
                        ctx.getColor(R.color.status_approval),
                        ctx.getColor(R.color.status_approval_bg)
                    )
                    else -> Triple(
                        "Terjual",
                        ctx.getColor(R.color.status_terjual),
                        ctx.getColor(R.color.status_terjual_bg)
                    )
                }
                tvStatus.text = statusText
                tvStatus.setTextColor(statusColor)
                tvStatus.setBackgroundColor(statusBg)

                root.setOnClickListener { onItemClick(motor) }
            }
        }
    }
}
