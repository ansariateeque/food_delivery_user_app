package com.example.wavesoffood

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wavesoffood.Models.RetriveAddedItem
import com.example.wavesoffood.Models.Users
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume


class MainRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().getReference()
    private val _bottommenulist = MutableLiveData<List<RetriveAddedItem>>()
    val bottommenulist: LiveData<List<RetriveAddedItem>> = _bottommenulist

    private val _logoutState = MutableStateFlow(false) // ✅ Persisted state
    val logoutState: StateFlow<Boolean> = _logoutState.asStateFlow()

    private val _userdetails = MutableLiveData<Users?>()
    val userdetails: LiveData<Users?> = _userdetails

    private val _addtocartlist = MutableLiveData<List<RetrieveAddToCart>>() // ✅ Use List
    val addtocartlist: LiveData<List<RetrieveAddToCart>> = _addtocartlist

    private val _addtocartresult =
        MutableSharedFlow<Pair<Boolean, String>>(replay = 0, extraBufferCapacity = 1)
    val addtocartresult = _addtocartresult.asSharedFlow()


    private val _loading = MutableSharedFlow<Boolean>(replay = 0, extraBufferCapacity = 1)
    val loading = _loading.asSharedFlow()

    private val _saveUserDetails =
        MutableSharedFlow<Pair<Boolean, String>>(replay = 0, extraBufferCapacity = 1)
    val saveUserDetails = _saveUserDetails.asSharedFlow()


    private val _totalPriceLiveData = MutableLiveData<Double>()
    val totalPriceLiveData: LiveData<Double> get() = _totalPriceLiveData


    private val _historylisty = MutableLiveData<List<Orders>>()
    val historylist: LiveData<List<Orders>> get() = _historylisty


    init {
        fetchAddedItem()
    }

    fun generateCustomKey(item: RetriveAddedItem): String {
        return "${item.name}_${item.price}"
    }

    private fun fetchAddedItem() {
        databaseReference.child("AddItemsByAdmin")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val retriveAddedItemlist = mutableListOf<RetriveAddedItem>()
                    _bottommenulist.value = emptyList()

                    for (adminSnapshot in snapshot.children) { // 🔥 Har admin ke liye loop
                        for (itemSnapshot in adminSnapshot.children) { // 🔥 Uske items loop
                            val item = itemSnapshot.getValue(RetriveAddedItem::class.java)
                            item?.let { retriveAddedItemlist.add(it) }
                        }
                    }

                    _bottommenulist.value = retriveAddedItemlist

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error: ${error.message}")
                }
            })

    }


    fun fetchAddToCart() {


        databaseReference.child("AddToCart").child("${auth.currentUser?.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val retriveAddTOCartlist = mutableListOf<RetrieveAddToCart>()

                    _addtocartlist.value = emptyList()

                    for (data in snapshot.children) {
                        if (data.key != "totalAmount") {
                            val item = data.getValue(RetrieveAddToCart::class.java)

                            item?.let { it: RetrieveAddToCart ->
                                retriveAddTOCartlist.add(it)
                            }
                        }
                    }
                    _addtocartlist.value = retriveAddTOCartlist

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error: ${error.message}")
                }
            })

    }


    suspend fun addtocart(addtocartitem: RetriveAddedItem) {
        _loading.emit(true)
        val userCartRef = databaseReference.child("AddToCart").child("${auth.currentUser?.uid}")

        try {
            val snapshot = userCartRef.get().await() // ✅ Firebase se synchronously data fetch karo

            if (snapshot.exists()) {
                var existingRestaurant: String? = null // ✅ Pehle restaurant ka naam nikalo

                for (childSnapshot in snapshot.children) {
                    Log.d("FirebaseDebug", "Snapshot Data: ${childSnapshot.value}") // 🔥 Print data

                    val cartItem = childSnapshot.getValue(RetriveAddedItem::class.java)
                    existingRestaurant = cartItem?.nameofrestuarant
                    break// ✅ Return first restaurant name
                }
                if (existingRestaurant != null && existingRestaurant != addtocartitem.nameofrestuarant) {
                    // ❌ Different restaurant item exists, reject add to cart
                    _addtocartresult.tryEmit(
                        Pair(false, "You can only add items from the same restaurant!")
                    )
                    _loading.emit(false)
                    return
                }
            }

            // ✅ Agar cart empty hai ya same restaurant ka item hai toh item add karo
            proceedToAddItem(userCartRef, addtocartitem)

        } catch (e: Exception) {
            Log.e("FirebaseError", "Database error: ${e.message}") // Debugging error

            _addtocartresult.tryEmit(Pair(false, "Database error: ${e.message}"))
            _loading.emit(false)

        }
    }


    suspend fun getHotelName(): String? {
        _loading.emit(true)
        return suspendCancellableCoroutine { continuation ->
            val userCartRef = databaseReference.child("AddToCart").child("${auth.currentUser?.uid}")

            userCartRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var existingRestaurant: String? = null

                    for (childSnapshot in snapshot.children) {
                        val cartItem = childSnapshot.getValue(RetrieveAddToCart::class.java)
                        existingRestaurant = cartItem?.nameofrestuarant
                        break // ✅ Take only the first restaurant name
                    }
                    _loading.tryEmit(false)

                    continuation.resume(existingRestaurant) // ✅ Resume with the fetched value
                }

                override fun onCancelled(error: DatabaseError) {
                    _loading.tryEmit(false)
                    continuation.resume(null) // ✅ Return null in case of failure
                }
            })
        }
    }

    private fun proceedToAddItem(userCartRef: DatabaseReference, addtocartitem: RetriveAddedItem) {

        val newItemKey = generateCustomKey(addtocartitem)


        userCartRef.orderByChild("namepricekey").equalTo(newItemKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // ✅ Item already exists
                        Log.d("yes firebase", "Item already in cart: ${addtocartitem.name}")
                        _addtocartresult.tryEmit(Pair(false, "Item already present in the cart"))
                        _loading.tryEmit(false)

                    } else {
                        Log.d("Firebase no", "Item already in cart: ${addtocartitem.name}")

                        val newItemRef = userCartRef.push() // 🔥 Firebase Push Key
                        val firebasepushKey = newItemRef.key // Firebase generated key

                        val newItem = RetrieveAddToCart(
                            pushkey = firebasepushKey,
                            namepricekey = newItemKey, // ✅ Store Firebase generated key
                            name = addtocartitem.name ?: "Unknown",
                            price = addtocartitem.price ?: 0.0,
                            uri = addtocartitem.uri ?: "",
                            description = addtocartitem.description ?: "",
                            ingredeinets = addtocartitem.ingredeinets ?: emptyList(),
                            quantity = 1,
                            nameofrestuarant = addtocartitem.nameofrestuarant,
                            hotelUserId = addtocartitem.userId// Default quantity
                        )


                        if (firebasepushKey != null) {

                            newItemRef.setValue(newItem) // 🔥 Save the updated object
                                .addOnCompleteListener { task ->
                                    _loading.tryEmit(false)
                                    if (task.isSuccessful) {
                                        _addtocartresult.tryEmit(
                                            Pair(
                                                true, "Item Added successfully"
                                            )
                                        )
                                    } else {
                                        _addtocartresult.tryEmit(
                                            Pair(
                                                false, "${task.exception?.message}"
                                            )
                                        )
                                    }
                                }
                        } else {
                            _loading.tryEmit(false)

                            _addtocartresult.tryEmit(Pair(false, "Error generating key"))
                        }


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _loading.tryEmit(false)

                    _addtocartresult.tryEmit(Pair(false, "Error generating key"))
                }
            })


    }


    suspend fun deleteaddtocartitem(keyfordeleteionitem: String?): Boolean {
        var isDeleted = false // ✅ Pehle default false rakho

        try {
            if (keyfordeleteionitem != null) {
                databaseReference.child("AddToCart").child(auth.currentUser?.uid ?: "")
                    .child(keyfordeleteionitem).removeValue().await()

                isDeleted = true

                val currentList = _addtocartlist.value?.toMutableList() ?: mutableListOf()

                val updatedList = currentList.filterNot { it.pushkey == keyfordeleteionitem }

                _addtocartlist.postValue(updatedList) // ✅ LiveData update

                Log.d("CartViewModel", "Item deleted and list updated successfully")
            }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Failed to delete item: ${e.message}")
        }

        return isDeleted //
    }

    fun getUserDetails() {
        if (auth.currentUser == null) {
            return
        }

        databaseReference.child("USERS").child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _userdetails.postValue(null)
                    if (snapshot.exists()) {  // ✅ Check if data exists
                        val user = snapshot.getValue(Users::class.java)
                        _userdetails.postValue(user)  // ✅ Use postValue for background thread safety
                    } else {
                        Log.e("getUserDetails", "User data does not exist")
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                    Log.e("userdetails", "failed to load the userdetails")
                }
            })

    }

    suspend fun updateUserValue(name: String, address: String, email: String, phone: String) {
        _loading.emit(true)

        val userref = databaseReference.child("USERS").child(auth.currentUser!!.uid)

        val updatevalue = mapOf<String, String>(
            "name" to name, "address" to address, "email" to email, "phone" to phone
        )

        userref.updateChildren(updatevalue).addOnCompleteListener {
            _loading.tryEmit(false)
            if (it.isSuccessful) {
                _saveUserDetails.tryEmit(Pair(true, "successfully Updated"))
            } else {
                _saveUserDetails.tryEmit(Pair(false, "${it.exception?.message}"))
            }
        }

    }

    fun updateQuantityOfItem(pushkey: String?, value: String) {

        val userCartRef =
            databaseReference.child("AddToCart").child("${auth.currentUser?.uid}").child(pushkey!!)

        val updateQuantitiy = mapOf("quantity" to value.toString().toInt())

        userCartRef.updateChildren(updateQuantitiy).addOnSuccessListener {
            Log.d("QUANTITY", value.toString())
        }.addOnFailureListener {
            Log.d("QUANTITY", value.toString())
        }
    }

    fun calculateTotalPrice() {
        val userCartRef = databaseReference.child("AddToCart").child(auth.currentUser?.uid!!)

        userCartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalPrice = 0.0  // 🔥 Total price initialize

                for (cartItem in snapshot.children) {
                    if (cartItem.key != "totalAmount") {
                        val item = cartItem.getValue(RetrieveAddToCart::class.java)

                        if (item != null) {
                            val price = item.price
                            val quantity = item.quantity
                            totalPrice += (price!! * quantity!!)  // ✅ Price * Quantity ka sum
                        }
                    }

                }
                _totalPriceLiveData.postValue(totalPrice)  // 🔥 LiveData me update karo
            }


            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to calculate total: ${error.message}")
            }
        })

        Log.e("Firebase log", "Failed to calculate total: ${totalPriceLiveData.value}")

    }

    suspend fun placedOrder(hotelname: String) {
        _loading.emit(true)
        val userorderRef = databaseReference.child("Orders").child(auth.currentUser!!.uid)

        val orderref = userorderRef.push()

        val orderkey = orderref.key

        val itemlistcart = addtocartlist.value
        val totalAMount = totalPriceLiveData.value

        try {
            // ✅ Fetch user details properly
            val snapshot =
                databaseReference.child("USERS").child(auth.currentUser!!.uid).get().await()
            val users = snapshot.getValue(Users::class.java)

            if (users == null) {
                _loading.tryEmit(false) // ❌ Stop loading if user data is null
                return
            }

            val userData = mapOf(
                "userId" to auth.currentUser!!.uid,
                "name" to users.name,
                "address" to users.address,
                "phone" to users.phone,
                "email" to users.email
            )

            val order = orderkey?.let { key ->
                itemlistcart?.let { items ->
                    totalAMount?.let { amount ->
                        Orders(
                            orderID = key,
                            items = items,
                            totalAmount = amount,
                            userDetails = userData,
                            paymentMethod = "Cash On Delivery",
                            hotelname = hotelname
                        )
                    }
                }
            }

            if (order != null) {
                orderref.setValue(order).await()

                val totalAmountRef =
                    databaseReference.child("AddToCart").child(auth.currentUser!!.uid)
                        .child("totalAmount")

                totalAmountRef.get().await()?.let {
                    totalAmountRef.removeValue().await() // ✅ Pehle check karo, fir delete karo
                }

                databaseReference.child("AddToCart").child(auth.currentUser!!.uid)
                    .removeValue().await()
            }

        } catch (e: Exception) {
            Log.d("FirebaseError", "Error placing order: ${e.message}")
        } finally {
            _loading.emit(false) // ✅ Stop loading in every case
        }
    }


    fun history() {

        if (auth.currentUser == null) {
            Log.e("history", "User not logged in")
            return
        }
        val userorderRef = databaseReference.child("Orders").child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = mutableListOf<Orders>()
                    _historylisty.value = emptyList()

                    for (data in snapshot.children) {
                        val ordermodel = data.getValue(Orders::class.java)
                        ordermodel?.let { orders.add(ordermodel) }
                    }
                    _historylisty.value = orders
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("history", "failed to load it")
                }
            })

    }

    suspend fun logout(context: Context) {
        auth.signOut() // Firebase logout

        try {
            val googleSignInClient =
                GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
            googleSignInClient.signOut().await() // Google Sign-Out
        } catch (e: Exception) {
            e.printStackTrace()
        }

        _logoutState.value = true // ✅ Update state
    }

    suspend fun getHotelOwnerToken(hotelUserId: String): String {
        return try {
            val snapshot = databaseReference.child("USERS")
                .child(hotelUserId)
                .child("fcmToken")
                .get()
                .await()

            snapshot.getValue(String::class.java) ?: "" // Ensure type safety and handle null values
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching FCM token: ${e.message}", e)
            ""
        }
    }

    suspend fun getHotelUserId(): String {
        return try {
            val snapshot = databaseReference
                .child("AddToCart")
                .child(auth.currentUser!!.uid)
                .get()
                .await()

            if (snapshot.exists() && snapshot.children.iterator().hasNext()) {
                val firstChild = snapshot.children.iterator().next() // Get the first child
                val hotelUserId = firstChild.child("hotelUserId").getValue(String::class.java) ?: ""

                Log.d("FirebaseDebug", "Fetched first hotelUserId: $hotelUserId")
                hotelUserId
            } else {
                Log.e("FirebaseError", "No items found in AddToCart for user: ${auth.currentUser!!.uid}")
                ""
            }
        } catch (e: Exception) {
            Log.e("FirebaseError", "Error fetching first hotelUserId: ${e.message}", e)
            ""
        }
    }

}