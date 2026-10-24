package com.example.motoinventory.ui.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.motoinventory.R
import com.example.motoinventory.data.entity.Aktivitas
import com.example.motoinventory.data.entity.Motor
import com.example.motoinventory.databinding.FragmentAddMotorBinding
import com.example.motoinventory.utils.FormatUtils
import com.example.motoinventory.utils.SessionManager
import com.example.motoinventory.viewmodel.AktivitasViewModel
import com.example.motoinventory.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

class AddMotorFragment : Fragment() {

    private var _binding: FragmentAddMotorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels()
    private val aktivitasViewModel: AktivitasViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager
    private var editMotorId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddMotorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        editMotorId = arguments?.getInt("motorId", -1)?.takeIf { it != -1 }

        if (editMotorId != null) {
            binding.tvTitle.text = "Edit Motor"
            loadMotorData(editMotorId!!)
        }

        // Role-based: limit status changes for staff
        if (sessionManager.isStaff()) {
            binding.chipStatusTerjual.isEnabled = false
        }

        setupRupiahWatcher(binding.etHargaBeli)
        setupRupiahWatcher(binding.etHargaJual)

        binding.btnSimpan.setOnClickListener { saveMotor() }
        binding.btnBatal.setOnClickListener { findNavController().popBackStack() }
    }

    private fun loadMotorData(id: Int) {
        lifecycleScope.launch {
            val motor = viewModel.getMotorById(id)
            motor?.let {
                binding.etNamMotor.setText(it.namMotor)
                binding.etMerk.setText(it.merk)
                binding.etTahun.setText(it.tahun.toString())
                binding.etWarna.setText(it.warna)
                binding.etHargaBeli.setText(FormatUtils.formatRupiahInput(it.hargaBeli))
                binding.etHargaJual.setText(FormatUtils.formatRupiahInput(it.hargaJual))
                binding.etNomorPolisi.setText(it.nomorPolisi)
                binding.etNomorRangka.setText(it.nomorRangka)
                binding.etNomorMesin.setText(it.nomorMesin)
                binding.etCatatan.setText(it.catatan)

                // Set status chip
                when (it.status) {
                    Motor.STATUS_TERSEDIA -> binding.chipStatusTersedia.isChecked = true
                    Motor.STATUS_SERVIS -> binding.chipStatusServis.isChecked = true
                    Motor.STATUS_APPROVAL -> binding.chipStatusApproval.isChecked = true
                    Motor.STATUS_TERJUAL -> binding.chipStatusTerjual.isChecked = true
                }
                updateSelisih()
            }
        }
    }

    private fun setupRupiahWatcher(editText: android.widget.EditText) {
        var isEditing = false
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true
                val clean = s.toString().replace("[^0-9]".toRegex(), "")
                val amount = clean.toLongOrNull() ?: 0L
                if (amount > 0) {
                    val formatted = String.format("%,d", amount).replace(",", ".")
                    editText.setText(formatted)
                    editText.setSelection(editText.text.length)
                }
                isEditing = false
                updateSelisih()
            }
        })
    }

    private fun updateSelisih() {
        val beli = FormatUtils.parseRupiah(binding.etHargaBeli.text.toString())
        val jual = FormatUtils.parseRupiah(binding.etHargaJual.text.toString())
        val selisih = jual - beli
        binding.tvSelisihValue.text = FormatUtils.formatRupiah(selisih)
        binding.tvSelisihValue.setTextColor(
            if (selisih >= 0) requireContext().getColor(R.color.profit_positive)
            else requireContext().getColor(R.color.profit_negative)
        )
    }

    private fun getSelectedStatus(): String {
        return when (binding.chipGroupStatus.checkedChipId) {
            R.id.chip_status_servis -> Motor.STATUS_SERVIS
            R.id.chip_status_approval -> Motor.STATUS_APPROVAL
            R.id.chip_status_terjual -> Motor.STATUS_TERJUAL
            else -> Motor.STATUS_TERSEDIA
        }
    }

    private fun saveMotor() {
        val nama = binding.etNamMotor.text.toString().trim()
        val merk = binding.etMerk.text.toString().trim()
        val tahunStr = binding.etTahun.text.toString().trim()
        val warna = binding.etWarna.text.toString().trim()
        val hargaBeli = FormatUtils.parseRupiah(binding.etHargaBeli.text.toString())
        val hargaJual = FormatUtils.parseRupiah(binding.etHargaJual.text.toString())

        if (nama.isEmpty() || merk.isEmpty() || tahunStr.isEmpty() || warna.isEmpty()) {
            Toast.makeText(requireContext(), "Mohon lengkapi data wajib", Toast.LENGTH_SHORT).show()
            return
        }
        val tahun = tahunStr.toIntOrNull() ?: 0
        if (tahun < 1990 || tahun > 2030) {
            Toast.makeText(requireContext(), "Tahun tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val status = getSelectedStatus()
        val motor = Motor(
            id = editMotorId ?: 0,
            namMotor = nama,
            merk = merk,
            tahun = tahun,
            warna = warna,
            hargaBeli = hargaBeli,
            hargaJual = hargaJual,
            status = status,
            nomorPolisi = binding.etNomorPolisi.text.toString().trim(),
            nomorRangka = binding.etNomorRangka.text.toString().trim(),
            nomorMesin = binding.etNomorMesin.text.toString().trim(),
            catatan = binding.etCatatan.text.toString().trim()
        )

        if (editMotorId != null) {
            viewModel.updateMotor(motor)
            logActivity(Aktivitas.AKSI_EDIT_MOTOR, "Edit motor: $nama")
            Toast.makeText(requireContext(), "Motor berhasil diupdate", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.insertMotor(motor)
            logActivity(Aktivitas.AKSI_TAMBAH_MOTOR, "Tambah motor: $nama ($merk $tahun)")
            Toast.makeText(requireContext(), "Motor berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        }
        findNavController().popBackStack()
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
