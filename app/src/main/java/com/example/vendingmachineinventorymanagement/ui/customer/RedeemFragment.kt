package com.example.vendingmachineinventorymanagement.ui.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.viewmodels.ProductViewModel


class RedeemFragment : Fragment() {
    private val productViewModel: ProductViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_redeem, container, false)
    }

}