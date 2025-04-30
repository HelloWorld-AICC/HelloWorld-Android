package com.example.helloworld.ui.consultation_center

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.helloworld.databinding.FragmentConsultationCenterBinding

class ConsultationCenterFragment : Fragment() {

    private var _binding: FragmentConsultationCenterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val consultationCenterViewModel =
            ViewModelProvider(this).get(ConsultationCenterViewModel::class.java)

        _binding = FragmentConsultationCenterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        consultationCenterViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}