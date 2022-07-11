package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert
    suspend fun insertElection(election: Election)

    @Query("select * from election_table")
    fun getAllElections(): LiveData<List<Election>>

    @Query("select * from election_table where id = :electionId")
    fun getElection(electionId: Int): Election?

    @Query("delete from election_table where id = :electionId")
    suspend fun deleteByElectionId(electionId: Int)

    @Query("delete from election_table")
    suspend fun clearAllElections()

}