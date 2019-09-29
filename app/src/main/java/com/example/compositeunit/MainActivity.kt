package com.example.compositeunit

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.compositeunit2.base.CompositeBuilder
import com.example.compositeunit2.base.SimpleCompositeUnit
import com.example.compositeunit2.base.ViewCompositeUnit
import kotlinx.android.synthetic.main.activity_main.*
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
        val extraCU = object : ViewCompositeUnit {
            override fun getView(parent: ViewGroup): View {
                return View(parent.context).also {
                    it.viewTreeObserver.addOnGlobalLayoutListener {
                        it.background = ColorDrawable(Color.CYAN)
                        it.layoutParams = it.layoutParams.also {
                            it.height = 400
                            it.width = ViewGroup.LayoutParams.MATCH_PARENT
                        }
                    }
                }
            }

            override val clazz: Class<*>
                get() = NumberRed::class.java
        }
        val adapter = CompositeBuilder()
            .add(SimpleCompositeUnit(Number::class.java, R.layout.item_number))
            .add(extraCU)
            .buildPaged(extraCU, NumberRed(""))
        recyclerView.adapter = adapter

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .build()

        val pagedList = PagedList.Builder(DataSource(), config)
            .setNotifyExecutor(MainThreadExecutor())
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()

        adapter.submitList(pagedList)
    }

    class DataSource : PageKeyedDataSource<Int, Any>() {
        override fun loadInitial(
            params: LoadInitialParams<Int>,
            callback: LoadInitialCallback<Int, Any>
        ) {
            callback.onResult(List(params.requestedLoadSize) {
                if (it % 2 == 0) Number(it.toString()) else NumberRed(it.toString())
            }, null, 1)
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Any>) {
            callback.onResult(
                List(params.requestedLoadSize) {
                    if (it % 2 == 0) Number(it.toString()) else NumberRed(
                        it.toString()
                    )
                },
                if (params.key < 5) params.key + 1 else null
            )
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Any>) {
            callback.onResult(List(params.requestedLoadSize) {
                if (it % 2 == 0) Number(it.toString()) else NumberRed(
                    it.toString()
                )
            }, 1)
        }
    }

    open class Number(val number: String)
    class NumberRed(number: String) : Number(number)

    internal inner class MainThreadExecutor : Executor {
        private val mHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }
}
