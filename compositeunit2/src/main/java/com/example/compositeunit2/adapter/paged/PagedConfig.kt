package com.example.compositeunit2.adapter.paged

class PagedConfig(
    val offset: Int,
    val onBottomReached: (Int) -> Unit
)