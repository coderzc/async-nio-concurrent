package com.zc.async.nio.concurrent.kt

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread


/**
 * @author coderzc
 * Created on 2020-06-15
 */

fun coroutineLaunch() {
    GlobalScope.launch { // 在后台启动一个新的协程并继续
        delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
        println("World!") // 在延迟后打印输出
    }
    thread {
        Thread.sleep(1000L)
        println("thread World!")
    }
    println("Hello,") // 协程已在等待时主线程还在继续
//    Thread.sleep(2000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
    runBlocking {     // 但是这个表达式阻塞了主线程
        delay(2000L)  // ……我们延迟 2 秒来保证 JVM 的存活
    }
}

fun coroutineRunBlocking() = runBlocking {
    launch { // 在后台启动一个新的协程并继续
        delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
        println("World!") // 在延迟后打印输出
    }
    delay(2000L)
    println("Hello,") // 协程已在等待时主线程还在继续
}

suspend fun coroutineJoinSuspend() {
    val job = GlobalScope.launch { // 在后台启动一个新的协程并继续
        delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
        println("World!") // 在延迟后打印输出
    }
    println("Hello,") // 协程已在等待时主线程还在继续
    job.join()
}

fun main() = runBlocking {
//    coroutineLaunch()

    coroutineRunBlocking()

//    coroutineJoinSuspend()
}