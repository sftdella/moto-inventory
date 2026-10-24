package com.example.motoinventory.ui.laporan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.motoinventory.R
import com.example.motoinventory.data.entity.Aktivitas
import com.example.motoinventory.databinding.FragmentLaporanBinding
import com.example.motoinventory.utils.FormatUtils
import com.example.motoinventory.utils.PdfExporter
import com.example.motoinventory.utils.SessionManager
import com.example.motoinventory.viewmodel.AktivitasViewModel
import com.example.motoinventory.viewmodel.InventoryViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LaporanFragment : Fragment() {

    private var _binding: FragmentLaporanBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels()
    private val aktivitasViewModel: AktivitasViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager

    private var currentPeriod = "bulan"
    private var lastPdfFile: File? = null
    private var allMotorsList = listOf<com.example.motoinventory.data.entity.Motor>()
    private var currentPemasukan = 0L
    private var currentPengeluaran = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLaporanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        updatePeriodUI()
        observeData()
        setupPeriodFilter()
        setupExportButtons()

        // Role check
        if (!sessionManager.canExportPdf()) {
            binding.cardExport.visibility = View.GONE
        }
    }

    private fun setupPeriodFilter() {
        binding.chipGroupPeriod.setOnCheckedStateChangeListener { _, checkedIds ->
            currentPeriod = when {
                R.id.chip_tahun_ini in checkedIds -> "tahun"
                R.id.chip_semua_waktu in checkedIds -> "semua"
                else -> "bulan"
            }
            updatePeriodUI()
            observeData()
        }
    }

    private fun updatePeriodUI() {
        val title = when (currentPeriod) {
            "tahun" -> "Laporan Tahun ${Calendar.getInstance().get(Calendar.YEAR)}"
            "semua" -> "Laporan Semua Waktu"
            else -> "Laporan ${FormatUtils.bulanIni()}"
        }
        binding.tvBulanIni.text = title
        binding.tvPeriodSubtitle.text = when (currentPeriod) {
            "tahun" -> "Data seluruh tahun ini"
            "semua" -> "Data keseluruhan"
            else -> "Data bulan ${FormatUtils.bulanIni()}"
        }
    }

    private fun observeData() {
        var pemasukan = 0L
        var pengeluaran = 0L
        var biayaTambahan = 0L

        if (currentPeriod == "bulan") {
            viewModel.pemasukanBulanIni.observe(viewLifecycleOwner) { p ->
                pemasukan = p ?: 0L; updateUI(pemasukan, pengeluaran, biayaTambahan)
            }
            viewModel.pengeluaranBulanIni.observe(viewLifecycleOwner) { b ->
                pengeluaran = b ?: 0L; updateUI(pemasukan, pengeluaran, biayaTambahan)
            }
            viewModel.biayaTambahanBulanIni.observe(viewLifecycleOwner) { bt ->
                biayaTambahan = bt ?: 0L; updateUI(pemasukan, pengeluaran, biayaTambahan)
            }
        } else {
            // For year/all, use total
            viewModel.totalPemasukan.observe(viewLifecycleOwner) { p ->
                pemasukan = p ?: 0L; updateUI(pemasukan, pengeluaran, biayaTambahan)
            }
            viewModel.totalHargaBeli.observe(viewLifecycleOwner) { b ->
                pengeluaran = b ?: 0L; updateUI(pemasukan, pengeluaran, biayaTambahan)
            }
        }

        viewModel.countTersedia.observe(viewLifecycleOwner) {
            binding.tvStokSaatIni.text = "${it ?: 0} unit"
        }
        viewModel.countTerjual.observe(viewLifecycleOwner) {
            binding.tvTotalTerjualAll.text = "${it ?: 0} unit"
        }
        viewModel.totalPemasukan.observe(viewLifecycleOwner) {
            binding.tvTotalPemasukan.text = FormatUtils.formatRupiah(it ?: 0L)
        }
        viewModel.allMotors.observe(viewLifecycleOwner) {
            allMotorsList = it
        }
    }

    private fun updateUI(pemasukan: Long, pengeluaran: Long, biayaTambahan: Long) {
        currentPemasukan = pemasukan
        val totalPengeluaran = pengeluaran + biayaTambahan
        currentPengeluaran = totalPengeluaran
        val profit = pemasukan - totalPengeluaran

        binding.tvPemasukan.text = FormatUtils.formatRupiah(pemasukan)
        binding.tvPengeluaran.text = FormatUtils.formatRupiah(totalPengeluaran)
        binding.tvProfit.text = FormatUtils.formatRupiah(profit)
        binding.tvProfit.setTextColor(
            if (profit >= 0) requireContext().getColor(R.color.profit_positive)
            else requireContext().getColor(R.color.profit_negative)
        )

        if (pemasukan > 0 || totalPengeluaran > 0) setupPieChart(pemasukan, totalPengeluaran)
    }

    private fun setupPieChart(pemasukan: Long, pengeluaran: Long) {
        val entries = mutableListOf<PieEntry>()
        if (pemasukan > 0) entries.add(PieEntry(pemasukan.toFloat(), "Pemasukan"))
        if (pengeluaran > 0) entries.add(PieEntry(pengeluaran.toFloat(), "Pengeluaran"))

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#F44336")
            )
            valueTextSize = 13f
            valueTextColor = Color.WHITE
            sliceSpace = 3f
        }

        binding.pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 55f
            setHoleColor(Color.TRANSPARENT)
            setCenterText("PCM")
            setCenterTextSize(14f)
            setCenterTextColor(Color.parseColor("#1565C0"))
            legend.isEnabled = true
            animateY(1000, Easing.EaseInOutQuad)
            invalidate()
        }
    }

    private fun setupExportButtons() {
        binding.btnExportPdf.setOnClickListener {
            exportPdf()
        }
        binding.btnSharePdf.setOnClickListener {
            lastPdfFile?.let { sharePdf(it) }
        }
    }

    private fun exportPdf() {
        val periodLabel = when (currentPeriod) {
            "tahun" -> SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
            "semua" -> "Semua-Waktu"
            else -> FormatUtils.bulanIni().replace(" ", "-")
        }

        val data = PdfExporter.LaporanData(
            motors = allMotorsList,
            totalPemasukan = currentPemasukan,
            totalPengeluaran = currentPengeluaran,
            profit = currentPemasukan - currentPengeluaran,
            period = periodLabel
        )

        binding.btnExportPdf.isEnabled = false
        binding.btnExportPdf.text = "Mengekspor..."

        Thread {
            val file = PdfExporter.exportLaporan(requireContext(), data)
            requireActivity().runOnUiThread {
                binding.btnExportPdf.isEnabled = true
                binding.btnExportPdf.text = "Export PDF"

                if (file != null) {
                    lastPdfFile = file
                    binding.btnSharePdf.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "PDF disimpan: ${file.name}", Toast.LENGTH_LONG).show()
                    aktivitasViewModel.logAktivitas(
                        sessionManager.getUserId(), sessionManager.getUsername(),
                        sessionManager.getRole(), Aktivitas.AKSI_EXPORT_PDF,
                        "Export PDF: $periodLabel"
                    )
                } else {
                    Toast.makeText(requireContext(), "Gagal mengekspor PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun sharePdf(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Bagikan Laporan PDF"))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tidak dapat berbagi file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
