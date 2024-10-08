package com.example.vendingmachineinventorymanagement.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vendingmachineinventorymanagement.models.Product
import com.example.vendingmachineinventorymanagement.utils.constansts.Constants.TBL_PRODUCTS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference(TBL_PRODUCTS)
    // LiveData for observing the list of items
    private val _items = MutableLiveData<List<Product>>()
    val items: LiveData<List<Product>> get() = _items
    private var listener: ValueEventListener? = null  // Listener variable

    init {
        fetchItems()
    }

    // Function to fetch items from Firebase Realtime Database
    // Public function to fetch data
    fun fetchItems() {
        // Remove existing listener if it exists
        listener?.let {
            database.removeEventListener(it)
        }
        // Create a new listener
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                _items.postValue(productList)
                // Remove the listener after fetching data
                database.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                _items.postValue(emptyList())  // Optionally post an empty list or error
            }
        }
        // Add the new listener
        database.addValueEventListener(listener!!)
    }
}
