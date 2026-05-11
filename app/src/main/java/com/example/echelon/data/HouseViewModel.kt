package com.example.echelon.data

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.echelon.models.HouseModel
import com.example.echelon.navigation.ROUTE_DASHBOARD1
import com.example.echelon.navigation.ROUTE_VIEWHOUSE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class HouseViewModel : ViewModel() {
    private val uploadPreset = "pic_folder" 
    private val auth = FirebaseAuth.getInstance()

    private val _houses = mutableStateListOf<HouseModel>()
    val houses: List<HouseModel> = _houses

    fun uploadHouse(
        imageUri: Uri?,
        houseLocation: String,
        uploaderName: String,
        phoneNumber: String,
        housePrice: String,
        context: Context,
        navController: NavController
    ) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null || houseLocation.isBlank() || uploaderName.isBlank() || phoneNumber.isBlank() || housePrice.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imageUrl = uploadToCloudinary(imageUri)
                val ref = FirebaseDatabase.getInstance().getReference("Houses").push()
                val houseData = HouseModel(
                    id = ref.key,
                    houseLocation = houseLocation,
                    uploaderName = uploaderName,
                    phoneNumber = phoneNumber,
                    housePrice = housePrice,
                    imageUrl = imageUrl,
                    uploaderId = currentUserId
                )
                ref.setValue(houseData).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "House saved Successfully", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_DASHBOARD1)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("HouseViewModel", "Upload failed: ${e.message}")
                    Toast.makeText(context, "Upload Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun uploadToCloudinary(uri: Uri): String = suspendCancellableCoroutine { continuation ->
        MediaManager.get().upload(uri)
            .unsigned(uploadPreset) 
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val url = resultData?.get("secure_url") as? String
                    if (url != null) {
                        continuation.resume(url)
                    } else {
                        continuation.resumeWithException(Exception("Failed to get image URL from Cloudinary"))
                    }
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    continuation.resumeWithException(Exception(error?.description ?: "Upload failed"))
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    fun fetchHouses(context: Context) {
        val ref = FirebaseDatabase.getInstance().getReference("Houses")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _houses.clear()
                for (child in snapshot.children) {
                    try {
                        val house = child.getValue(HouseModel::class.java)
                        house?.let {
                            it.id = child.key
                            _houses.add(it)
                        }
                    } catch (e: Exception) {
                        Log.e("HouseViewModel", "Data mismatch for house ${child.key}: ${e.message}")
                        // Fallback: Manually parse fields to handle type mismatches (e.g., Long instead of String)
                        val house = HouseModel(
                            id = child.key,
                            houseLocation = child.child("houseLocation").getValue(String::class.java) ?: "",
                            uploaderName = child.child("uploaderName").getValue(String::class.java) ?: "",
                            phoneNumber = child.child("phoneNumber").value?.toString() ?: "",
                            housePrice = child.child("housePrice").value?.toString() ?: "",
                            imageUrl = child.child("imageUrl").getValue(String::class.java),
                            uploaderId = child.child("uploaderId").getValue(String::class.java)
                        )
                        _houses.add(house)
                    }
                }
                if (_houses.isEmpty()) {
                    Log.d("HouseViewModel", "No houses found in database")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HouseViewModel", "Database error: ${error.message}")
                Toast.makeText(context, "Database Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun fetchMyHouses(context: Context) {
        val currentUserId = auth.currentUser?.uid ?: ""
        if (currentUserId.isEmpty()) {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ref = FirebaseDatabase.getInstance().getReference("Houses")
                val snapshot = ref.orderByChild("uploaderId").equalTo(currentUserId).get().await()
                
                withContext(Dispatchers.Main) {
                    _houses.clear()
                    if (snapshot.exists()) {
                        for (child in snapshot.children) {
                            try {
                                val house = child.getValue(HouseModel::class.java)
                                house?.let {
                                    it.id = child.key
                                    _houses.add(it)
                                }
                            } catch (e: Exception) {
                                // Fallback for MyHouses as well
                                val house = HouseModel(
                                    id = child.key,
                                    houseLocation = child.child("houseLocation").getValue(String::class.java) ?: "",
                                    uploaderName = child.child("uploaderName").getValue(String::class.java) ?: "",
                                    phoneNumber = child.child("phoneNumber").value?.toString() ?: "",
                                    housePrice = child.child("housePrice").value?.toString() ?: "",
                                    imageUrl = child.child("imageUrl").getValue(String::class.java),
                                    uploaderId = child.child("uploaderId").getValue(String::class.java)
                                )
                                _houses.add(house)
                            }
                        }
                    } else {
                        Log.d("HouseViewModel", "No houses found for user: $currentUserId")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("HouseViewModel", "Fetch my houses failed", e)
                    Toast.makeText(context, "Fetch Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun updateHouse(
        houseId: String,
        imageUri: Uri?,
        houseLocation: String,
        uploaderName: String,
        housePrice: String,
        phoneNumber: String,
        context: Context,
        navController: NavController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imageUrl = if (imageUri != null) uploadToCloudinary(imageUri) else null
                val ref = FirebaseDatabase.getInstance().getReference("Houses").child(houseId)
                
                val updateData = mutableMapOf<String, Any>()
                updateData["houseLocation"] = houseLocation
                updateData["uploaderName"] = uploaderName
                updateData["phoneNumber"] = phoneNumber
                updateData["housePrice"] = housePrice
                
                if (imageUrl != null) {
                    updateData["imageUrl"] = imageUrl
                }

                ref.updateChildren(updateData).await()
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "House updated Successfully", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_VIEWHOUSE) {
                        popUpTo(ROUTE_DASHBOARD1) { inclusive = false }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("HouseViewModel", "Update failed: ${e.message}")
                    Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun deleteHouse(houseId: String, context: Context) {
        val ref = FirebaseDatabase.getInstance().getReference("Houses").child(houseId)
        ref.removeValue().addOnSuccessListener {
            _houses.removeAll { it.id == houseId }
            Toast.makeText(context, "House deleted successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.e("HouseViewModel", "Delete failed: ${it.message}")
            Toast.makeText(context, "Delete Error: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }
}
