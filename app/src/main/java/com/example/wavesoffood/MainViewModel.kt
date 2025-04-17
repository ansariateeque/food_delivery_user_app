package com.example.wavesoffood

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.wavesoffood.Models.RetriveAddedItem
import com.example.wavesoffood.Models.Users
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: MyApplication, private val repository: MainRepository) :
    AndroidViewModel(application) {

    val loading = repository.loading

    val addtocartresult = repository.addtocartresult

    val saveUserDetails = repository.saveUserDetails

    val getuserDetails: LiveData<Users?> = repository.userdetails

    val logoutState: StateFlow<Boolean> = repository.logoutState // ✅ Observing in UI

    val historyList = repository.historylist

    val bottommenulist: LiveData<List<RetriveAddedItem>> = repository.bottommenulist

    val addtocartlist: LiveData<List<RetrieveAddToCart>> = repository.addtocartlist


    val totalPriceLiveData = repository.totalPriceLiveData


    fun addtocart(retriveAddedItem: RetriveAddedItem) {
        viewModelScope.launch {
            repository.addtocart(retriveAddedItem) // ✅ Call suspend function in coroutine
        }
    }


    suspend fun deleteaddtocartitem(keyfordeleteionitem: String?): Boolean {
        return repository.deleteaddtocartitem(keyfordeleteionitem)
    }

    fun updateUserValue(name: String, address: String, email: String, phone: String) {

        viewModelScope.launch {
            repository.updateUserValue(name, address, email, phone)

        }
    }

    fun updateQuantityOfItem(pushkey: String?, value: String) {
        repository.updateQuantityOfItem(pushkey, value)
    }

    fun calculateTotalPrice() {
        repository.calculateTotalPrice()
    }

    fun placedOrder(hotelname: String) {
        viewModelScope.launch {
            repository.placedOrder(hotelname)
        }
    }


    fun logout(context: Context) {
        viewModelScope.launch {
            repository.logout(context)
        }
    }

    fun fetchaddtocart() {
        repository.fetchAddToCart()
    }

    fun history() {
        repository.history()
    }

    fun getuserDetail() {
        repository.getUserDetails()
    }

    suspend fun gethotelname(): String? {

        return repository.getHotelName()
    }

    suspend fun gethotelownertoken(hoteluserid: String): String {
        return repository.getHotelOwnerToken(hoteluserid)
    }

    suspend fun gethotelUserId(): String {
        return repository.getHotelUserId()
    }


}