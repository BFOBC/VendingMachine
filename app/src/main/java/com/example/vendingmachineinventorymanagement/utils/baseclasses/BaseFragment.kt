package com.example.vendingmachineinventorymanagement.utils.baseclasses

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.vendingmachineinventorymanagement.R

abstract class BaseFragment<DB : ViewDataBinding>() : Fragment() {
    lateinit var binding : DB
    lateinit var acty : Activity
    lateinit var ctx: Context
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,getResLayout(),container,false)
        acty = requireActivity()
        ctx = requireContext()

        onCreateView(inflater,savedInstanceState)
        return binding.root
    }


    abstract fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?)

    @LayoutRes
    abstract fun getResLayout() : Int
    fun applyNavigationOptions(): NavOptions {
        return navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }
    }


}
