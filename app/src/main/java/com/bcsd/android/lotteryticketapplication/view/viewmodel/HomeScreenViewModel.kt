package com.bcsd.android.lotteryticketapplication.view.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class HomeScreenViewModel : ViewModel() {
    private lateinit var databaseReference: DatabaseReference

    val currentAllUserLotteryNumbers = MutableLiveData<String>()
    private val _pastAllUserLotteryNumbers = MutableLiveData<String>()
    val pastAllUserLotteryNumbers:LiveData<String> get() = _pastAllUserLotteryNumbers
    private val _pastWinningNumbers = MutableLiveData<MutableList<Int?>>()
    val pastWinningNumbers : LiveData<MutableList<Int?>> = _pastWinningNumbers
    private val _pastDate = MutableLiveData<String>()
    val pastDate : LiveData<String> = _pastDate

    var pastWinningItems = mutableListOf<Int?>()

    // 데이터베이스에 저장 된 현재 날짜의 모든 회원들의 로또 번호를 불러오는 함수
    fun setCurrentUserLotteryNumbers(date: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.child("LotteryNumbers")
                    .child(date)
                if (value.child("customer").exists()) {
                    currentAllUserLotteryNumbers.postValue(value.child("customer").value.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // 과거 유저들의 모든 로또 번호를 삭제해주는 함수
    fun updatePastAllUserLotteryNumbers(_value : String){
        _pastAllUserLotteryNumbers.postValue(_value)
    }
    fun updatePastDate(_value:String){
        _pastDate.postValue(_value)
    }

    // 과거 당첨 번호를 업데이트하는 함수
    fun updatePastWinningNumbers(pastlist: List<Int?>) {
        pastWinningItems.clear()
        pastWinningItems.addAll(pastlist)
        _pastWinningNumbers.postValue(pastWinningItems)
    }

    // 과거 로또 번호가 존재하는 지 확인하는 함수
    fun findPastLotteryNumbers(editTextDate: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.child("LotteryNumbers")
                        .child(editTextDate)
                    val valueCustomer = snapshot.child("LotteryNumbers")
                        .child(editTextDate).child("customer")
                    if (valueCustomer.exists()){
                        val masterNumber = value.child("master").value.toString()
                        val customerNumber = value.child("customer").value.toString()
                        _pastAllUserLotteryNumbers.postValue(customerNumber)
                        _pastDate.postValue(editTextDate)

                        val listInt = createStringToList(masterNumber)

                        updatePastWinningNumbers(listInt)
                    } else {
                        _pastAllUserLotteryNumbers.postValue(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    // 문자열을 리스트로 변환하는 함수
    fun createStringToList(it:String) : List<Int>{
        var listInt = mutableListOf<Int>()
        val listStr = it.split(" ") as MutableList<String>
        listStr.removeAt(listStr.size - 1)
        listStr.forEach {
            listInt.add(it.toInt())
        }
        listInt.sort()
        return listInt
    }
}