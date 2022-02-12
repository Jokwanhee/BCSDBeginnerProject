package com.bcsd.android.lotteryticketapplication.view.view.purchaseFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.FragmentPurchaseBinding
import com.bcsd.android.lotteryticketapplication.view.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PurchaseFragment:Fragment() {
    private lateinit var binding:FragmentPurchaseBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())
            .get(MainViewModel::class.java)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_purchase, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var randnumber = arrayListOf<Int>()

        val myLotteryNumbersObserver = Observer<String>{
            binding.text1.text = it.toString()
        }
        mainViewModel.myLotteryNumbers.observe(viewLifecycleOwner, myLotteryNumbersObserver)

        binding.randomButton.setOnClickListener {
            randnumber.clear()
            for (i in 1..6) {
                val value = (1..45).random()
                randnumber.add(value)
            }
            randnumber.sort()
            mainViewModel.myLotteryNumbers.postValue(randnumber.toString())
            updataData(randnumber)
        }
    }

    private fun updataData(rechargeMyLotteryNumbers: ArrayList<Int>) {
        var map = mutableMapOf<String, Any>()
        map["userLotteryNumbers"] = rechargeMyLotteryNumbers.toString()
        databaseReference.child("UserAccount").child(firebaseAuth.currentUser?.uid.toString())
            .updateChildren(map)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "데이터 업데이트 성공", Toast.LENGTH_SHORT).show()
                }
            }
    }
}