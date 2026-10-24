package com.example.motoinventory.ui.inventory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motoinventory.R
import com.example.motoinventory.databinding.FragmentInventoryBinding
import com.example.motoinventory.utils.SessionManager
import com.example.motoinventory.viewmodel.InventoryViewModel

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels()
    private lateinit var motorAdapter: MotorAdapter
    private lateinit var sessionManager: SessionManager
    private var currentFilter = "Semua"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupRecyclerView()
        setupSearch()
        setupFilter()
        observeData()

        // Hide FAB if no permission
        binding.fabTambah.visibility =
            if (sessionManager.canAddMotor()) View.VISIBLE else View.GONE

        binding.fabTambah.setOnClickListener {
            findNavController().navigate(R.id.action_inventory_to_addMotor)
        }
    }

    private fun setupRecyclerView() {
        motorAdapter = MotorAdapter { motor ->
            val bundle = Bundle().apply { putInt("motorId", motor.id) }
            findNavController().navigate(R.id.action_inventory_to_detail, bundle)
        }
        binding.rvMotors.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = motorAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchMotors(query).observe(viewLifecycleOwner) {
                        motorAdapter.submitList(it)
                    }
                } else {
                    loadByFilter()
                }
            }
        })
    }

    private fun setupFilter() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilter = when {
                R.id.chip_tersedia in checkedIds -> "Tersedia"
                R.id.chip_servis in checkedIds -> "Proses Servis"
                R.id.chip_approval in checkedIds -> "Menunggu Approval"
                R.id.chip_terjual in checkedIds -> "Terjual"
                else -> "Semua"
            }
            loadByFilter()
        }
    }

    private fun loadByFilter() {
        val liveData = when (currentFilter) {
            "Tersedia" -> viewModel.motorTersedia
            "Proses Servis" -> viewModel.motorServis
            "Menunggu Approval" -> viewModel.motorApproval
            "Terjual" -> viewModel.motorTerjual
            else -> viewModel.allMotors
        }
        liveData.observe(viewLifecycleOwner) { motorAdapter.submitList(it) }
    }

    private fun observeData() {
        loadByFilter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
