package com.example.vendingmachineinventorymanagement.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vendingmachineinventorymanagement.models.Product
import com.example.vendingmachineinventorymanagement.utils.constansts.Constants.TBL_PRODUCTS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProductViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().getReference(TBL_PRODUCTS)
    private val storageReference = FirebaseStorage.getInstance().reference
    // LiveData for observing the list of items
    private val _items = MutableLiveData<List<Product>>()
    val items: LiveData<List<Product>> get() = _items
    // LiveData to track operation status and error messages
    private val _operationStatus = MutableLiveData<Result<String>>()
    val operationStatus: LiveData<Result<String>> = _operationStatus

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
                    try {
                        // Try to convert the snapshot into a Product object
                        val product = productSnapshot.getValue(Product::class.java)
                        product?.let { productList.add(it) }
                    } catch (e: DatabaseException) {
                        // Handle the conversion error (e.g., log the error or notify the user)
                        Log.e("FirebaseError", "Data conversion error: ${e.message}")
                    }
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
    // Update function with Firebase Realtime Database
    fun updateDataInFirebase(referenceKey: String, newData: Map<String, Any>) {
        val ref = database.child(referenceKey)
        ref.updateChildren(newData)
            .addOnSuccessListener {
                // Notify success with a success message
                _operationStatus.value = Result.success("Product updated successfully")
            }
            .addOnFailureListener { e ->
                // Notify failure with an error message
                _operationStatus.value = Result.failure(e)
            }
    }

    // Delete function with Firebase Realtime Database
    fun deleteDataFromFirebase(referenceKey: String) {
        val ref = database.child(referenceKey)
        ref.removeValue()
            .addOnSuccessListener {
                // Notify success with a success message
                _operationStatus.value = Result.success("Product deleted successfully")
            }
            .addOnFailureListener { e ->
                // Notify failure with an error message
                _operationStatus.value = Result.failure(e)
            }
    }
    fun updateImagePath(referenceKey: String, imageURL:String) {
        val ref = database.child(referenceKey) // Reference to the desired node
        // Update the "imagePath" field with the new imageURL
        ref.child("productImage").setValue(imageURL)
    }
}
