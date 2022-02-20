package com.bcsd.android.lotteryticketapplication.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeScreenViewModel:ViewModel() {
    val past_allUserLotteryNumbers = MutableLiveData<MutableList<MutableList<Int>>>()
    val past_lotteryNumbers = MutableLiveData<ArrayList<Int>>()

    fun todayWinningNumber(){

    }

}