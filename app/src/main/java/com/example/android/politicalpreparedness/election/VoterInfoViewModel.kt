package com.example.android.politicalpreparedness.election

import android.graphics.Color
import android.graphics.Color.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.representative.adapter.toTypedAdapter
import kotlinx.coroutines.launch
import retrofit2.await

class VoterInfoViewModel(private val dataSource: ElectionDao, private val electionId: Int, private val division: Division) : ViewModel() {

    private val _voterInfo = MutableLiveData<VoterInfoResponse?>()
    val voterInfo : LiveData<VoterInfoResponse?>
        get() = _voterInfo

    private val _url = MutableLiveData<String>()
    val url : LiveData<String>
        get() = _url

    init {
         getVoterInfo()
    }

    private fun getVoterInfo() {
        viewModelScope.launch {
            try {
                _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(division.state, electionId)
            } catch (e: Exception) {
                _voterInfo.value = null
            }
        }
    }

    fun onFollowButtonClick() {
        viewModelScope.launch {
            val savedElection = dataSource.getElection(electionId)
            if (savedElection != null) {
                dataSource.deleteByElectionId(electionId)
            } else {
                voterInfo.value?.let { dataSource.insertElection(it.election) }
            }
        }
    }

}
