package com.bcsd.android.lotteryticketapplication.view.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bcsd.android.lotteryticketapplication.R
import com.bcsd.android.lotteryticketapplication.databinding.ActivityMyWinningBinding
import com.bcsd.android.lotteryticketapplication.view.adapter.MyWinningAdapter

class MyWinningActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMyWinningBinding
    private lateinit var adapter:MyWinningAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_winning)


        val winningNumbers = intent.getIntegerArrayListExtra("winningNumbers")
        val winningDate = intent.getStringExtra("winningDate")
        val myLotteryNumbers = intent.getStringExtra("myLotteryNumbers")

        var myrandnumberlist = mutableListOf<MutableList<String>>()
        var count = 0

        if (myLotteryNumbers?.isNotEmpty() == true) {
            val a_list = myLotteryNumbers.split(" ") as MutableList<String>
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

        createAdapter(myrandnumberlist)
        clickView()

        binding.textDate.text = winningDate
        binding.textLotteryNumbers.text = winningNumbers.toString()
    }

    private fun createAdapter(myrandnumberlist:MutableList<MutableList<String>>){
        adapter = MyWinningAdapter(myrandnumberlist)
        binding.myWinningRecyclerView.adapter = adapter
        binding.myWinningRecyclerView.layoutManager = GridLayoutManager(this,3)
    }

    private fun clickView(){
        adapter.setItemClickListener(object : MyWinningAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int, myLotterys: String) {
                binding.textMyLotteryNumbers.text = myLotterys
            }
        })
    }
}