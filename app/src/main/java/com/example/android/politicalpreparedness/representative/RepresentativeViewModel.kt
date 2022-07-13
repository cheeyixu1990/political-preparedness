package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel: ViewModel() {

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    var addressLine1 = MutableLiveData<String>()
    var addressLine2 = MutableLiveData<String>()
    var city = MutableLiveData<String>()
    var state = MutableLiveData<String>()
    var zip = MutableLiveData<String>()

    fun findRepresentatives() {
        viewModelScope.launch {
            try {
                val addressString = address.value?.toFormattedString()
                if (addressString != null) {
                    val representativesResponse = CivicsApi.retrofitService.getRepresentativesByAddr(addressString)
                    _representatives.value = representativesResponse.offices.flatMap {
                        it.getRepresentatives(representativesResponse.officials)
                    }
                }

            } catch (e: Exception) {
                _representatives.value = listOf()
            }
        }
    }

    fun clearAllField(){
        addressLine1.value = ""
        addressLine2.value = ""
        city.value = ""
        zip.value = ""
        state.value = "Alabama"
    }

    fun setAddress(address: Address){
        _address.value = address
        addressLine1.value = address.line1
        addressLine2.value = address.line2.toString()
        city.value = address.city
        zip.value = address.zip
        state.value = address.state.toString()
    }

    fun restoreRepList(repList: List<Representative>?){
        if (repList != null) {
            _representatives.value = repList!!
        }
    }

}
