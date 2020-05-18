package com.example.compositeunit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.compositeunit2.base.CompositeBuilder
import com.example.compositeunit2.base.SimpleCompositeUnit
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import com.example.compositeunit2.adapter.paged.PagedConfig


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
    }

    fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CompositeBuilder().add(
            object : SimpleCompositeUnit(
                Number::class.java,
                R.layout.item_number
            ) {
                override val preloadedViewHoldersSize: Int
                    get() = 20
            }
        ).buildPagedAdapter(PagedConfig(10) { Log.d("MainActivity", "REACHED_BOTTOM")})
        recyclerView.adapter = adapter

        adapter.items = List(100) { Number(it.toString()) }
    }

    class Number(val number: String)
}
