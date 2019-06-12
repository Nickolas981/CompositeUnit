package com.example.compositeunit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.compositeunit2.adapter.CompositeBuilder
import com.example.compositeunit2.adapter.SimpleCompositeUnit
import kotlinx.android.synthetic.main.activity_main.*
import androidx.paging.PagedList
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
    }

    fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CompositeBuilder().add(SimpleCompositeUnit(Number::class.java, R.layout.item_number))
            .buildPaged(SimpleCompositeUnit(NumberRed::class.java, R.layout.item_number_red), NumberRed(""))
        recyclerView.adapter = adapter

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .build()

        val pagedList = PagedList.Builder(DataSourse(), config)
            .setNotifyExecutor(MainThreadExecutor())
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()

        adapter.submitList(pagedList)
    }

    class DataSourse() : PageKeyedDataSource<Int, Any>() {
        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Any>) {
            callback.onResult(List(params.requestedLoadSize) { Number(it.toString()) }, null, 1)
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Any>) {
            callback.onResult(List(params.requestedLoadSize) { Number(it.toString()) }, if (params.key < 5) params.key + 1 else null)
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Any>) {
            callback.onResult(List(params.requestedLoadSize) { Number(it.toString()) }, 1)
        }
    }

    class Number(val number: String)
    class NumberRed(val number: String)

    internal inner class MainThreadExecutor : Executor {
        private val mHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }
}
