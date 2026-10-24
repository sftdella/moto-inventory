package com.example.motoinventory.ui.aktivitas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.motoinventory.data.entity.Aktivitas
import com.example.motoinventory.databinding.ItemAktivitasBinding
import com.example.motoinventory.utils.FormatUtils

class AktivitasAdapter : ListAdapter<Aktivitas, AktivitasAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Aktivitas>() {
            override fun areItemsTheSame(old: Aktivitas, new: Aktivitas) = old.id == new.id
            override fun areContentsTheSame(old: Aktivitas, new: Aktivitas) = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAktivitasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemAktivitasBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Aktivitas) {
            binding.apply {
                tvAksi.text = item.aksi
                tvDetail.text = item.detail.ifEmpty { "-" }
                tvUser.text = item.username
                tvRole.text = "(${item.role})"
                tvWaktu.text = FormatUtils.formatDateTime(item.timestamp)
            }
        }
    }
}
