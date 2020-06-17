package com.zc.nettystu.kt

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


/**
 * @author coderzc
 * Created on 2020-06-15
 */

fun main() {
    //    println("Start")
//
//    // 启动一个协程
//    GlobalScope.launch {
//        delay(1000)
//        println("Hello")
//    }
//
//    Thread.sleep(2000) // 等待 2 秒钟
//    println("Stop")
//
//    test1()
//    fun postItem(item: Item): PostResult? {
//        val token = requestToken()
//        val post = createPost(token, item)
//    return processPost(post)
//    }

    suspend fun await() {
        for (index in 1..100) {
            Thread.sleep(1000)
            println(index)
        }
    }

    println(123)
    Thread.sleep(3000)

}


fun requestToken(): String {
    return UUID.randomUUID().toString()
}

fun createPost(token: String, item: Item): Any? {
    return null;
}

fun processPost(post: Any?): PostResult? {
    return null
}

class Item {

}

class PostResult {

}

fun test1() = runBlocking {
    val job = launch {
        repeat(10) {
            delay(500L)
            println(it)
        }
    }
    delay(1000L)
    job.cancelAndJoin()
}