package com.bcsd.android.lotteryticketapplication.view.view.purchaseFragment

import android.os.Bundle
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
import com.google.firebase.database.*

// 회원 로또 구매 프래그먼트 (로또 구매(자동))
class PurchaseFragment : Fragment() {
    private lateinit var binding: FragmentPurchaseBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    var varMoney = 0
    var varMyLotteryNumbersStr = String()

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

        updateObserverData()

        binding.deleteButton.setOnClickListener {
            mainViewModel.myLotteryNumbersStr.postValue("")
            mainViewModel.deleteMyLotteryNumbers()
            mainViewModel.updateData("userLotteryNumbers", "", requireContext())
        }

        binding.randomButton.setOnClickListener {
            if (varMoney <= 0) {
                Toast.makeText(context, "No Money!", Toast.LENGTH_SHORT).show()
            } else {
                varMoney -= 5000
                mainViewModel.money.postValue(varMoney)
                mainViewModel.updateData("money", varMoney, view.context)

                var oneNumberStr = String()
                var myRandomNumber = mainViewModel.createRandomNumber()
                myRandomNumber.forEach {
                    oneNumberStr += it.toString() + " "
                    varMyLotteryNumbersStr += it.toString() + " "
                }

                createMyLotteryNumbers(varMyLotteryNumbersStr)

                mainViewModel.myLotteryNumbersStr.postValue(varMyLotteryNumbersStr)
                mainViewModel.updateData("userLotteryNumbers", varMyLotteryNumbersStr, requireContext())

                mainViewModel.updateCurrentTimeLotteryNumbers(oneNumberStr)

            }
        }
    }

    private fun updateObserverData() {
        val moneyObserver = Observer<Int> {
            varMoney = it
        }
        val myLotteryNumbersStrObserver = Observer<String> {
            varMyLotteryNumbersStr = it
        }
        mainViewModel.money.observe(viewLifecycleOwner, moneyObserver)
        mainViewModel.myLotteryNumbersStr.observe(viewLifecycleOwner, myLotteryNumbersStrObserver)
    }

    private fun createMyLotteryNumbers(it: String) {
        binding.text1.text = it
        val myLotteryNumbers = mainViewModel.createTwoDimensionalList(it)
        mainViewModel.updateMyLotteryNumbers(myLotteryNumbers)
    }

}