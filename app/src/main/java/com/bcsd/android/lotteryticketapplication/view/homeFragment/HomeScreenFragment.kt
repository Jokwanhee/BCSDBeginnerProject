package com.bcsd.android.lotteryticketapplication.view.view.homeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.FragmentHomescreenMainBinding
import com.bcsd.android.lotteryticketapplication.view.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeScreenFragment : Fragment() {
    private lateinit var binding: FragmentHomescreenMainBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mainViewModel: MainViewModel

    var winningList = arrayListOf<Int>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())
            .get(MainViewModel::class.java)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_homescreen_main, container, false)
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayWinningNumber()

    }

    private fun todayWinningNumber(){
        val lotteryNumbersObserver = Observer<ArrayList<Int>>{
            winningList = it
            winningList.sort()
            winningList.forEach {
                when (winningList.indexOf(it)) {
                    0 -> binding.todayCircleBall1.text = it.toString()
                    1 -> binding.todayCircleBall2.text = it.toString()
                    2 -> binding.todayCircleBall3.text = it.toString()
                    3 -> binding.todayCircleBall4.text = it.toString()
                    4 -> binding.todayCircleBall5.text = it.toString()
                    5 -> binding.todayCircleBall6.text = it.toString()
                    6 -> binding.todayCircleBall7.text = it.toString()
                }
            }
        }

        val winningDateObserver = Observer<String>{
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.child("LotteryNumbers")
                        .child(it)
                    if (value.child("customer").exists()){
                        var myrandnumberlist = mutableListOf<MutableList<Int>>()
                        var count = 0
                        var userList = value.child("customer").value.toString()
                        Log.d("testing","user list -> ${userList}")
                        // =====
                        var int_list = mutableListOf<Int>()
                        val str_list = userList.split(" ") as MutableList<String>
                        str_list.removeAt(str_list.size - 1)
                        str_list.forEach {
                            int_list.add(it.toInt())
                        }
                        // =====
                        while (count != int_list.size) {
                            count += 1 // count 1씩 증가
                            if ((count + 1) % 7 == 0) { // count(6,13...) + 1 => 7의 배수일 때
                                val innerList = int_list.slice((count - 6)..count)
                                myrandnumberlist.add(innerList as MutableList<Int>)
                            }
                        }
                        for (i in 0..myrandnumberlist.size - 1) {
                            myrandnumberlist[i].sort()
                        }

                        var winningHistory = mutableListOf<Int>(0,0,0,0,0,0,0)
                        var winningCount = 0
                        
                        myrandnumberlist.forEach { listIt ->
                            Log.d("testing","list -> ${listIt}")
                            winningCount = 0
                            listIt.forEach {
                                if (it in winningList){
                                    winningCount ++
                                }
                            }
                            when (winningCount){
                                0 -> winningHistory[0] += 1
                                1 -> winningHistory[1] += 1
                                2 -> winningHistory[2] += 1
                                3 -> winningHistory[3] += 1
                                4 -> winningHistory[4] += 1
                                5 -> winningHistory[5] += 1
                                6 -> winningHistory[6] += 1
                            }
                        }
                        binding.winnerText.text = winningHistory.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        mainViewModel.date.observe(viewLifecycleOwner, winningDateObserver)
        mainViewModel.lotteryNumbers.observe(viewLifecycleOwner, lotteryNumbersObserver)
    }

    private fun setNumberBackground(number: Int, textView: TextView) {
        when (number) {
            in 1..10 -> textView.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.circle_yellow) }
            in 11..20 -> textView.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.circle_blue) }
            in 21..30 -> textView.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.circle_red) }
            in 31..40 -> textView.background =
                context?.let { ContextCompat.getDrawable(it, R.drawable.circle_purple) }
        }
        true
    }

}