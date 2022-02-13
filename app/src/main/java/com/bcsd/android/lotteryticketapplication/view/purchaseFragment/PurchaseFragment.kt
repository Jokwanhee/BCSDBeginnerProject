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

class PurchaseFragment : Fragment() {
    private lateinit var binding: FragmentPurchaseBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    var deductionMoney = 0

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

        val myLotteryNumbersObserver = Observer<String> {
            binding.text1.text = it
            randnumber = it
            if (it.isNotEmpty()) {
                val a_list = it.split(" ") as MutableList<String>
                a_list.removeAt(a_list.size - 1)

                while (count != a_list.size) {
                    count += 1
                    if ((count + 1) % 6 == 0) {
                        val b_list = a_list.slice((count - 5)..count) as MutableList<String>
                        myrandnumberlist.add(b_list)
                    }
                }
                for (i in 0..myrandnumberlist.size - 1) {
                    val comparator: Comparator<String> = compareBy { it.toInt() }
                    myrandnumberlist[i].sortWith(comparator)
                }
            }
            binding.text2.text = myrandnumberlist.toString()
            mainViewModel.updateMyLotteryNumbers(myrandnumberlist)
        }
        mainViewModel.myLotteryNumbers.observe(viewLifecycleOwner, myLotteryNumbersObserver)

        val myLotteryNumbersListObserver = Observer<MutableList<MutableList<String>>> {
            binding.text3.text = it.toString()
        }
        mainViewModel.myLotteryNumbersList.observe(viewLifecycleOwner, myLotteryNumbersListObserver)

        val moneyObserver = Observer<Int>{
            deductionMoney = it
        }
        mainViewModel.money.observe(viewLifecycleOwner, moneyObserver)

        binding.randomButton.setOnClickListener {
            for (i in 1..6) {
                val value = (1..45).random()
                randnumber += value.toString()
                randnumber += " "
            }

            deductionMoney = deductionMoney - 5000

            mainViewModel.myLotteryNumbers.postValue(randnumber)
            mainViewModel.updateData("userLotteryNumbers", randnumber, view.context)
            mainViewModel.updateData("money", deductionMoney, view.context)
        }
    }
}