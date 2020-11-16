package com.inplace


object DataSource {
    private var list: MutableList<Int>? = null

    fun create(size: Int) {
        if (list == null) {
            list = MutableList(size) { i -> i + 1 }
        }
    }

    fun add() {
        list?.apply {
            add(size + 1)
        }
    }

    fun size() = list?.size ?: 0

    fun get() = list ?: MutableList(0) { i -> i }

}