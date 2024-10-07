package com.example.vendingmachineinventorymanagement.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.databinding.ActivityDashboardBinding
import com.example.vendingmachineinventorymanagement.utils.singleClickListener

class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        binding.btnExit.singleClickListener {
            //exit all the activities
            finishAffinity()
        }
        binding.apply {
            itemViewProducts.setOnClickListener {
                onModuleClick(itemViewProducts)
            }
            itemAddProducts.setOnClickListener {
                onModuleClick(itemAddProducts)
            }
            itemViewStandardVM.setOnClickListener {
                onModuleClick(itemViewStandardVM)
            }
            itemViewTestPrinter.setOnClickListener {
                onModuleClick(itemViewTestPrinter)
            }
        }
    }
    private fun onModuleClick(itemView: ConstraintLayout) {
        when (itemView.id) {
            R.id.itemAddProducts -> {
            }

            R.id.itemViewProducts -> {
            }
            else -> {
                null
            }
        }

    }
}