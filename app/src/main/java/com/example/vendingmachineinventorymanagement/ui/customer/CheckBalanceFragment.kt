package com.example.vendingmachineinventorymanagement.ui.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.databinding.FragmentCheckBalanceBinding
import com.example.vendingmachineinventorymanagement.databinding.FragmentHomeBinding
import com.example.vendingmachineinventorymanagement.utils.baseclasses.BaseFragment


class CheckBalanceFragment : BaseFragment<FragmentCheckBalanceBinding>()  {

    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?) {

    }

    override fun getResLayout(): Int {
        return R.layout.fragment_check_balance
    }

}