package com.example.motoinventory.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.motoinventory.data.entity.BiayaTambahan
import com.example.motoinventory.databinding.ItemBiayaBinding
import com.example.motoinventory.utils.FormatUtils

class BiayaAdapter(
    private val onDeleteClick: (BiayaTambahan) -> Unit
) : ListAdapter<BiayaTambahan, BiayaAdapter.BiayaViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BiayaTambahan>() {
            override fun areItemsTheSame(old: BiayaTambahan, new: BiayaTambahan) = old.id == new.id
            override fun areContentsTheSame(old: BiayaTambahan, new: BiayaTambahan) = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiayaViewHolder {
        val binding = ItemBiayaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BiayaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BiayaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BiayaViewHolder(private val binding: ItemBiayaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(biaya: BiayaTambahan) {
            binding.tvKategori.text = biaya.kategori
            binding.tvKeterangan.text = biaya.keterangan
            binding.tvJumlah.text = FormatUtils.formatRupiah(biaya.jumlah)
            binding.tvTanggal.text = FormatUtils.formatDate(biaya.tanggal)
            binding.btnDelete.setOnClickListener { onDeleteClick(biaya) }
        }
    }
}
