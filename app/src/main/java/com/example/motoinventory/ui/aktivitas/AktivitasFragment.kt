package com.example.motoinventory.ui.aktivitas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motoinventory.databinding.FragmentAktivitasBinding
import com.example.motoinventory.viewmodel.AktivitasViewModel

class AktivitasFragment : Fragment() {

    private var _binding: FragmentAktivitasBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AktivitasViewModel by activityViewModels()
    private lateinit var adapter: AktivitasAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAktivitasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AktivitasAdapter()
        binding.rvAktivitas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AktivitasFragment.adapter
        }

        viewModel.allAktivitas.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
