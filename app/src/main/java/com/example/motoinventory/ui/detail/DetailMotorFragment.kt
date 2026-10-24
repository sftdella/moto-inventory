package com.example.motoinventory.ui.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motoinventory.R
import com.example.motoinventory.data.entity.Aktivitas
import com.example.motoinventory.data.entity.BiayaTambahan
import com.example.motoinventory.data.entity.Motor
import com.example.motoinventory.databinding.FragmentDetailMotorBinding
import com.example.motoinventory.utils.FormatUtils
import com.example.motoinventory.utils.SessionManager
import com.example.motoinventory.viewmodel.AktivitasViewModel
import com.example.motoinventory.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

class DetailMotorFragment : Fragment() {

    private var _binding: FragmentDetailMotorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels()
    private val aktivitasViewModel: AktivitasViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager
    private var motorId: Int = -1
    private var currentMotor: Motor? = null
    private lateinit var biayaAdapter: BiayaAdapter
    private var totalBiaya = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailMotorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        motorId = arguments?.getInt("motorId", -1) ?: -1
        if (motorId == -1) { findNavController().popBackStack(); return }

        setupBiayaRecyclerView()
        loadMotorData()
        setupButtons()
        applyRolePermissions()
    }

    private fun applyRolePermissions() {
        if (!sessionManager.canDeleteMotor()) binding.btnHapus.visibility = View.GONE
        if (!sessionManager.canEditMotor()) binding.btnEdit.visibility = View.GONE
        if (!sessionManager.canApproveJual()) binding.btnJual.visibility = View.GONE
        if (!sessionManager.canAddBiaya()) binding.btnTambahBiaya.visibility = View.GONE
    }

    private fun setupBiayaRecyclerView() {
        biayaAdapter = BiayaAdapter { biaya ->
            if (!sessionManager.canAddBiaya()) return@BiayaAdapter
            AlertDialog.Builder(requireContext())
                .setTitle("Hapus Biaya")
                .setMessage("Hapus biaya \"${biaya.keterangan}\"?")
                .setPositiveButton("Hapus") { _, _ ->
                    viewModel.deleteBiaya(biaya)
                    logActivity(Aktivitas.AKSI_HAPUS_BIAYA, "Hapus biaya: ${biaya.keterangan}")
                }
                .setNegativeButton("Batal", null)
                .show()
        }
        binding.rvBiaya.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = biayaAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun loadMotorData() {
        lifecycleScope.launch {
            currentMotor = viewModel.getMotorById(motorId)
            currentMotor?.let { bindMotorData(it) }
        }

        viewModel.getBiayaByMotor(motorId).observe(viewLifecycleOwner) {
            biayaAdapter.submitList(it)
        }

        viewModel.totalBiayaByMotor(motorId).observe(viewLifecycleOwner) { total ->
            totalBiaya = total ?: 0L
            binding.tvTotalBiayaTambahan.text = FormatUtils.formatRupiah(totalBiaya)
            updateKeuangan()
        }
    }

    private fun bindMotorData(motor: Motor) {
        binding.apply {
            tvNamaMotor.text = motor.namMotor
            tvMerk.text = "${motor.merk} • ${motor.tahun}"
            tvTahun.text = motor.tahun.toString()
            tvWarna.text = motor.warna
            tvHargaBeli.text = FormatUtils.formatRupiah(motor.hargaBeli)
            tvHargaJual.text = FormatUtils.formatRupiah(motor.hargaJual)
            tvNomorPolisi.text = motor.nomorPolisi.ifEmpty { "-" }
            tvNomorRangka.text = motor.nomorRangka.ifEmpty { "-" }
            tvNomorMesin.text = motor.nomorMesin.ifEmpty { "-" }
            tvCatatan.text = motor.catatan.ifEmpty { "-" }
            tvTanggalMasuk.text = "Masuk: ${FormatUtils.formatDate(motor.tanggalMasuk)}"

            // Status
            val (statusText, statusColor, statusBg) = when (motor.status) {
                Motor.STATUS_TERSEDIA -> Triple("Tersedia",
                    requireContext().getColor(R.color.status_tersedia),
                    requireContext().getColor(R.color.status_tersedia_bg))
                Motor.STATUS_SERVIS -> Triple("Proses Servis",
                    requireContext().getColor(R.color.status_servis),
                    requireContext().getColor(R.color.status_servis_bg))
                Motor.STATUS_APPROVAL -> Triple("Menunggu Approval",
                    requireContext().getColor(R.color.status_approval),
                    requireContext().getColor(R.color.status_approval_bg))
                else -> Triple("Terjual",
                    requireContext().getColor(R.color.status_terjual),
                    requireContext().getColor(R.color.status_terjual_bg))
            }
            tvStatus.text = statusText
            tvStatus.setTextColor(statusColor)
            tvStatus.setBackgroundColor(statusBg)

            if (motor.status == Motor.STATUS_TERJUAL) {
                tvTanggalTerjual.text = motor.tanggalTerjual?.let { FormatUtils.formatDate(it) } ?: "-"
                btnJual.visibility = View.GONE
                cardTerjualInfo.visibility = View.VISIBLE
            } else {
                btnJual.visibility = if (sessionManager.canApproveJual()) View.VISIBLE else View.GONE
                cardTerjualInfo.visibility = View.GONE
            }
            updateKeuangan()
        }
    }

    private fun updateKeuangan() {
        val motor = currentMotor ?: return
        val totalModal = motor.hargaBeli + totalBiaya
        binding.tvTotalPengeluaran.text = FormatUtils.formatRupiah(totalModal)
        val profit = motor.hargaJual - totalModal
        binding.tvProfit.text = FormatUtils.formatRupiah(profit)
        binding.tvProfit.setTextColor(
            if (profit >= 0) requireContext().getColor(R.color.profit_positive)
            else requireContext().getColor(R.color.profit_negative)
        )
    }

    private fun setupButtons() {
        binding.btnEdit.setOnClickListener {
            val bundle = Bundle().apply { putInt("motorId", motorId) }
            findNavController().navigate(R.id.action_detail_to_addMotor, bundle)
        }

        binding.btnHapus.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Hapus Motor")
                .setMessage("Data motor akan dihapus permanen. Yakin?")
                .setPositiveButton("Hapus") { _, _ ->
                    currentMotor?.let {
                        viewModel.deleteMotor(it)
                        logActivity(Aktivitas.AKSI_HAPUS_MOTOR, "Hapus motor: ${it.namMotor}")
                    }
                    findNavController().popBackStack()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        binding.btnJual.setOnClickListener { showJualDialog() }
        binding.btnTambahBiaya.setOnClickListener { showKategoriDialog() }
    }

    private fun showJualDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_jual_motor, null)
        val etHargaJual = dialogView.findViewById<EditText>(R.id.et_harga_jual_dialog)
        etHargaJual.setText(currentMotor?.hargaJual?.toString() ?: "")

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Penjualan")
            .setView(dialogView)
            .setPositiveButton("Konfirmasi") { _, _ ->
                val harga = FormatUtils.parseRupiah(etHargaJual.text.toString())
                currentMotor?.let {
                    viewModel.jualMotor(it, harga)
                    logActivity(Aktivitas.AKSI_JUAL_MOTOR, "Jual motor: ${it.namMotor} - ${FormatUtils.formatRupiah(harga)}")
                    Toast.makeText(requireContext(), "Motor berhasil terjual", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showKategoriDialog() {
        val items = arrayOf("Perbaikan", "Spare Part", "Administrasi", "Ganti Oli", "Cat Body", "Ban Baru", "Servis Mesin", "Lainnya")
        AlertDialog.Builder(requireContext())
            .setTitle("Kategori Pengeluaran")
            .setItems(items) { _, which -> showBiayaInputDialog(items[which]) }
            .show()
    }

    private fun showBiayaInputDialog(kategori: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_tambah_biaya, null)
        val etKeterangan = dialogView.findViewById<EditText>(R.id.et_keterangan)
        val etJumlah = dialogView.findViewById<EditText>(R.id.et_jumlah)

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Pengeluaran: $kategori")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val keterangan = etKeterangan.text.toString().trim()
                val jumlah = FormatUtils.parseRupiah(etJumlah.text.toString())
                if (keterangan.isNotEmpty() && jumlah > 0) {
                    viewModel.insertBiaya(BiayaTambahan(motorId = motorId, kategori = kategori, keterangan = keterangan, jumlah = jumlah))
                    logActivity(Aktivitas.AKSI_TAMBAH_BIAYA, "$kategori: $keterangan - ${FormatUtils.formatRupiah(jumlah)}")
                    Toast.makeText(requireContext(), "Pengeluaran ditambahkan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Lengkapi keterangan dan jumlah", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun logActivity(aksi: String, detail: String) {
        aktivitasViewModel.logAktivitas(
            sessionManager.getUserId(), sessionManager.getUsername(),
            sessionManager.getRole(), aksi, detail
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
