package com.example.motoinventory.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motoinventory.R
import com.example.motoinventory.databinding.FragmentDashboardBinding
import com.example.motoinventory.ui.inventory.MotorAdapter
import com.example.motoinventory.utils.FormatUtils
import com.example.motoinventory.utils.NotificationHelper
import com.example.motoinventory.utils.SessionManager
import com.example.motoinventory.viewmodel.InventoryViewModel
import com.example.motoinventory.viewmodel.UserViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var recentAdapter: MotorAdapter
    private lateinit var sessionManager: SessionManager

    private var latestPemasukan = 0L
    private var latestPengeluaran = 0L
    private var latestBiaya = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupHeader()
        setupRecyclerView()
        observeData()
        setupButtons()

        binding.tvBulanIni.text = FormatUtils.bulanIni()

        // Role-based visibility
        if (!sessionManager.canAddMotor()) {
            binding.btnTambahMotor.visibility = View.GONE
        }
    }

    private fun setupHeader() {
        binding.tvGreeting.text = FormatUtils.getGreeting()
        binding.tvUserName.text = sessionManager.getNama()
    }

    private fun setupRecyclerView() {
        recentAdapter = MotorAdapter { motor ->
            val bundle = android.os.Bundle().apply { putInt("motorId", motor.id) }
            findNavController().navigate(R.id.action_dashboard_to_detail, bundle)
        }
        binding.rvRecentMotors.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun updateProfit() {
        val total = latestPengeluaran + latestBiaya
        binding.tvPengeluaran.text = FormatUtils.formatRupiah(total)
        val profit = latestPemasukan - total
        binding.tvProfit.text = FormatUtils.formatRupiah(profit)
        binding.tvProfit.setTextColor(
            if (profit >= 0) requireContext().getColor(R.color.profit_positive)
            else requireContext().getColor(R.color.profit_negative)
        )
        binding.tvProfitCard.text = FormatUtils.formatRupiahShort(profit)
        binding.tvProfitCard.setTextColor(
            if (profit >= 0) requireContext().getColor(R.color.profit_positive)
            else requireContext().getColor(R.color.profit_negative)
        )
    }

    private fun observeData() {
        viewModel.countStok.observe(viewLifecycleOwner) {
            binding.tvStokTersedia.text = (it ?: 0).toString()
        }
        viewModel.countTerjual.observe(viewLifecycleOwner) {
            binding.tvTotalTerjual.text = (it ?: 0).toString()
        }
        viewModel.countApproval.observe(viewLifecycleOwner) { count ->
            binding.tvApproval.text = (count ?: 0).toString()
            if ((count ?: 0) > 0) {
                NotificationHelper.showApprovalNotification(requireContext(), count ?: 0)
            }
        }
        viewModel.pemasukanBulanIni.observe(viewLifecycleOwner) {
            latestPemasukan = it ?: 0L
            binding.tvPemasukan.text = FormatUtils.formatRupiah(latestPemasukan)
            updateProfit()
        }
        viewModel.pengeluaranBulanIni.observe(viewLifecycleOwner) {
            latestPengeluaran = it ?: 0L
            updateProfit()
        }
        viewModel.biayaTambahanBulanIni.observe(viewLifecycleOwner) {
            latestBiaya = it ?: 0L
            updateProfit()
        }
        viewModel.recentMotors.observe(viewLifecycleOwner) {
            recentAdapter.submitList(it)
        }
    }

    private fun setupButtons() {
        binding.btnTambahMotor.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_addMotor)
        }
        binding.btnLihatSemua.setOnClickListener {
            findNavController().navigate(R.id.inventoryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
