package com.example.vendingmachineinventorymanagement.ui.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.databinding.FragmentHomeBinding
import com.example.vendingmachineinventorymanagement.utils.baseclasses.BaseFragment
import com.example.vendingmachineinventorymanagement.utils.singleClickListener


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?) {
        initViews()
    }
    private fun initViews() {
        binding.redeemButton.singleClickListener {
            applyNavigationOptions()
            findNavController().navigate(R.id.action_homeFragment_to_redeemFragment)
        }
        binding.checkBalanceButton.singleClickListener {
            applyNavigationOptions()
            findNavController().navigate(R.id.action_homeFragment_to_checkBalanceFragment)

        }
    }

    override fun getResLayout(): Int {
       return R.layout.fragment_home
    }

}