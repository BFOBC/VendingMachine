package com.example.vendingmachineinventorymanagement.ui.admin

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.vendingmachineinventorymanagement.R
import com.example.vendingmachineinventorymanagement.databinding.ActivityProductsDetailsBinding
import com.example.vendingmachineinventorymanagement.extensionfunctions.isNetworkAvailable
import com.example.vendingmachineinventorymanagement.extensionfunctions.loadImageIntoImageView
import com.example.vendingmachineinventorymanagement.extensionfunctions.showCustomErrorDialog
import com.example.vendingmachineinventorymanagement.extensionfunctions.showCustomToast
import com.example.vendingmachineinventorymanagement.models.Product
import com.example.vendingmachineinventorymanagement.utils.constansts.Constants.CURRENCY_SYMBOL
import com.example.vendingmachineinventorymanagement.utils.singleClickListener
import com.example.vendingmachineinventorymanagement.viewmodels.ProductViewModel
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ProductsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductsDetailsBinding
    private val productViewModel: ProductViewModel by viewModels()
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private val STORAGE_PERMISSION_CODE = 101
    private val storageReference = FirebaseStorage.getInstance().reference
    private lateinit var progressDialog: ProgressDialog // Declare ProgressDialog
    private  var isImageSelected:Boolean=false
    private lateinit var product:Product
    private lateinit var selectedCurrency:String
    private lateinit var items: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProductsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }
    private fun checkPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android 12 and below
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    // Open the gallery for image selection
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // Get the selected image URI
            imageUri = data.data!!

            // Set the image to the ImageView
            binding.productImageView.setImageURI(imageUri)
        }
    }

    private fun requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Request READ_MEDIA_IMAGES for Android 13 and above
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                STORAGE_PERMISSION_CODE
            )
        } else {
            // Request READ_EXTERNAL_STORAGE for older Android versions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }
    private fun checkAndRequestManageExternalStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Check if permission is granted
            if (!Environment.isExternalStorageManager()) {
                try {
                    // Open settings to grant access to all files
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    startActivityForResult(intent, STORAGE_PERMISSION_CODE)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivityForResult(intent, STORAGE_PERMISSION_CODE)
                }
            }
        } else {
            // For Android versions below R, request traditional permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setCurrency(){
        // Load currency symbols from strings.xml
        val currencies = resources.getStringArray(R.array.currency_symbols)
        // Create ArrayAdapter and set it on the Spinner
        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, currencies
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.currencySpinner.adapter = adapter

        // Set OnItemSelectedListener for the Spinner
        binding.currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Get the selected item
                CURRENCY_SYMBOL=currencies[position]
                selectedCurrency = currencies[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle case when nothing is selected
            }
        }
    }
    private fun initViews() {
        setCurrency()
        binding.labelToolbar.singleClickListener {
            onBackPressed()
        }
        setupKeyboardDismissListeners()

        binding.frameImage.setOnClickListener {
            if (checkPermission()) {
                openGallery()
            } else {
                requestStoragePermission()
            }
            isImageSelected=true
        }
        if (intent.getParcelableExtra<Product>("product_key")!=null){
            product = intent.getParcelableExtra<Product>("product_key")!!
        }
        // Check if the product is not null and set the data to the layout
        product?.let {
            binding.etProductName.setText(it.productName)
            binding.etSellPrice.setText(it.productSellingPrice.toString())
            binding.etCostPrice.setText(it.productCostPrice.toString())
            binding.etSlotNumber.setText(it.slotNumber.toString())
            binding.etDescription.setText(it.productDescription)
            binding.etMaxQuantity.setText(it.maxQuantity.toString())
            binding.etAvailableQuantity.setText(it.availableQuantity.toString())

            val placeholderImage = R.drawable.baseline_image_24
            loadImageIntoImageView(this@ProductsDetailsActivity, product.productImage.toString(),
                binding.productImageView, placeholderImage, placeholderImage)
        }
        binding.btnDeleteProduct.singleClickListener {
            // Show confirmation dialog
            AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Do you want to delete this product?")
                .setPositiveButton("Yes") { dialog, _ ->
                    // Proceed with deletion if confirmed
                    if (isNetworkAvailable(this@ProductsDetailsActivity)) {
                        productViewModel.deleteDataFromFirebase(product?.productId.toString())
                        Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        val imageResId = resources.getIdentifier(
                            "sad_icon",
                            "drawable",
                            packageName
                        )
                        showCustomErrorDialog("No Internet",
                            "Check your internet",
                            "retry",
                            imageResId
                        ) {
                            productViewModel.deleteDataFromFirebase(product?.productId.toString())
                        }
                    }
                    dialog.dismiss() // Dismiss the dialog after action
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss() // Just dismiss if user clicks "No"
                }
                .setCancelable(false) /// Optional: Prevent dismissal by tapping outside
                .show() // Show the dialog
        }

        binding.btnUpdateProduct.singleClickListener {
            // Collect data from EditTexts into a Map
            val productName = binding.etProductName.text.toString()
            val productSellingPrice = binding.etSellPrice.text.toString().toDoubleOrNull() ?: 0.0
            val productCostPrice = binding.etCostPrice.text.toString().toDoubleOrNull() ?: 0.0
            val slotNumber = binding.etSlotNumber.text.toString().toIntOrNull() ?: 0
            val productDescription = binding.etDescription.text.toString()
            val maxQuantity = binding.etMaxQuantity.text.toString().toIntOrNull() ?: 0
            val availableQuantity = binding.etAvailableQuantity.text.toString().toIntOrNull() ?: 0

            // Create a map with all the product values
            val updatedProductData = mapOf(
                "productName" to productName,
                "productSellingPrice" to productSellingPrice,
                "productCostPrice" to productCostPrice,
                "slotNumber" to slotNumber,
                "productDescription" to productDescription,
                "maxQuantity" to maxQuantity,
                "availableQuantity" to availableQuantity
            )
            //call the update fun to update values in database
            if (isValid()){
                if (isNetworkAvailable(this@ProductsDetailsActivity)) {
                    if (isImageSelected){
                        uploadImageAndSaveProduct()
                    }
                    readAndSaveData(slotNumber,product,updatedProductData)
                }else{
                    val imageResId = resources.getIdentifier(
                        "sad_icon",
                        "drawable",
                        packageName
                    )
                    showCustomErrorDialog("No Internet",
                        "Check your internet",
                        "retry",
                        imageResId
                    ) {
                        if (isImageSelected){
                            uploadImageAndSaveProduct()
                        }
                        readAndSaveData(slotNumber, product,updatedProductData)
                    }
                }
            }

        }
        // Observe the operation status from ViewModel
        productViewModel.operationStatus.observe(this) { result ->
            result.fold(
                onSuccess = { successMessage ->
                    // Show success message
                    val imageResId = resources.getIdentifier(
                        "ic_green_tick_icon",
                        "drawable",
                        packageName
                    )
                    showCustomErrorDialog("Updated",
                        "Product updated successfully",
                        "OK",
                        imageResId
                    ) {
                        finish()
                    }
                },
                onFailure = { exception ->
                    // Show error message
                    val imageResId = resources.getIdentifier(
                        "sad_icon",
                        "drawable",
                        packageName
                    )
                    showCustomErrorDialog("Error",
                        "Error: ${exception.localizedMessage}",
                        "Retry",
                        imageResId
                    ) {
                        productViewModel.deleteDataFromFirebase(product?.productId.toString())
                    }
                    Toast.makeText(this, "Error: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    // Validation function that checks all fields
    private fun isValid(): Boolean {
        var isValid = true
        binding.apply {
            // Check Product Name
            if (etProductName.text.toString().trim().isEmpty()) {
                etProductName.error = "Product name is required"
                isValid = false
            } else {
                etProductName.error = null
            }
            // Check Sell Price
            if (etSellPrice.text.toString().trim().isEmpty()) {
                etSellPrice.error = "Sell price is required"
                isValid = false
            } else {
                etSellPrice.error = null
            }
            // Check Cost Price
            if (etCostPrice.text.toString().trim().isEmpty()) {
                etCostPrice.error = "Cost price is required"
                isValid = false
            } else {
                etCostPrice.error = null
            }
            // Check Slot Number
            if (etSlotNumber.text.toString().trim().isEmpty()) {
                etSlotNumber.error = "Slot number is required"
                isValid = false
            } else {
                etSlotNumber.error = null
            }
            // Check Description
            if (etDescription.text.toString().trim().isEmpty()) {
                etDescription.error = "Description is required"
                isValid = false
            } else {
                etDescription.error = null
            }
            // Check Max Quantity
            if (etMaxQuantity.text.toString().trim().isEmpty()) {
                etMaxQuantity.error = "Max quantity is required"
                isValid = false
            } else {
                etMaxQuantity.error = null
            }
            // Check Available Quantity
            if (etAvailableQuantity.text.toString().trim().isEmpty()) {
                etAvailableQuantity.error = "Available quantity is required"
                isValid = false
            } else {
                etAvailableQuantity.error = null
            }
        }
        return isValid
    }

    private fun uploadImageAndSaveProduct() {
        if (imageUri != null) {
            // Show the progress dialog
            progressDialog = ProgressDialog(this).apply {
                setMessage("Uploading product...")
                setCancelable(false) // Prevent canceling the dialog
                show() // Show the dialog
            }

            // Generate a unique ID for the product image
            val imageId = UUID.randomUUID().toString()
            val imageRef = storageReference.child("product_images/$imageId.jpg")

            // Upload image to Firebase Storage
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Get the download URL for the image and save product data
                        val imageUrl = uri.toString()
                        productViewModel.updateImagePath(product?.productId.toString(),imageUrl)
                    }
                }
                .addOnFailureListener {
                    progressDialog.dismiss() // Dismiss the dialog
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }
    private fun readAndSaveData(slotNumber: Int, product: Product, updatedProductData: Map<String, Any>) {
        productViewModel.items.observeOnce(this) { itemList ->
            items=itemList
        }
        if (items.isNotEmpty()){
            val occupyingProduct = getOccupyingProduct(slotNumber, product.productId.toString(), items)
            if (occupyingProduct!=null) {
                showCustomToast("Slot $slotNumber is already occupied by ${occupyingProduct.productName}")
            } else {
                productViewModel.updateDataInFirebase(product.productId.toString(), updatedProductData)
            }
        }
    }

    // Helper function to observe LiveData only once
    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }
    private fun getOccupyingProduct(slotNumber: Int, productId: String, itemList: List<Product>): Product? {
        // Find the product occupying the slot, excluding the current product
        return itemList.find { it.slotNumber == slotNumber && it.productId != productId }
    }

    private fun setupKeyboardDismissListeners() {
        // List all the EditText fields that need this behavior
        val editTexts = listOf(binding.etProductName, binding.etSellPrice, binding.etCostPrice, binding.etSlotNumber,
            binding.etDescription, binding.etMaxQuantity, binding.etAvailableQuantity)

        // Apply focus change listener to each EditText
        editTexts.forEach { editText ->
            editText.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    hideKeyboard(view)
                }
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}