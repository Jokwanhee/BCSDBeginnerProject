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
        var randnumber = String()
        var myrandnumberlist = mutableListOf<MutableList<String>>()
        var count = 0

        val myLotteryNumbersObserver = Observer<String>{
            binding.text1.text = it
            randnumber = it
            if (!it.isEmpty()){
                val a_list = it.split(" ") as MutableList<String>
                a_list.removeAt(a_list.size - 1)

                while (count != a_list.size){
                    count += 1
                    if ((count + 1) % 6 == 0){
                        val b_list = a_list.slice((count-5)..count) as MutableList<String>
                        myrandnumberlist.add(b_list)
                    }
                }
                for (i in 0..myrandnumberlist.size-1){
                    val comparator : Comparator<String> = compareBy { it.toInt() }
                    myrandnumberlist[i].sortWith(comparator)
                }
            }
            binding.text2.text = myrandnumberlist.toString()
        }
        mainViewModel.myLotteryNumbers.observe(viewLifecycleOwner, myLotteryNumbersObserver)

        binding.randomButton.setOnClickListener {
            for (i in 1..6) {
                val value = (1..45).random()
                randnumber += value.toString()
                randnumber += " "
            }
            mainViewModel.myLotteryNumbers.postValue(randnumber)
            updataData(randnumber)
        }
    }

    private fun updataData(rechargeMyLotteryNumbers: String) {
        var map = mutableMapOf<String, Any>()
        map["userLotteryNumbers"] = rechargeMyLotteryNumbers
        databaseReference.child("UserAccount").child(firebaseAuth.currentUser?.uid.toString())
            .updateChildren(map)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "데이터 업데이트 성공", Toast.LENGTH_SHORT).show()
                }
            }
    }
}