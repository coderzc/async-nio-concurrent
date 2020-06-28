package com.zc.async.nio.concurrent.kt

/**
 * @author coderzc
 * Created on 2020-06-23
 */

fun main() {
    /**
     * val 只读变量(常量)
     * var 可读写变量
     */
    var a = 10
    val b = "Hello Kotlin"
    var c = 19008L

//    val e:Long = a
    val e: Long = a.toLong()
    val f: Int = e.toInt()

    var j = "我♥️China"
    var str = "Value of string is :$j"
    println(str)
    println("Value of string length is :${j.length}")
//    j = "我♥️中国"
//    println(str)

    var text = """
        <html>
            <body>
                <h1>多行字符串</h1>
                <span>${j}</span>
            </body>
        </html>
    """.trimIndent()
    println(text)

    var jsonText = """
{
    "people": [{
        "firstName": "Brett",
        "lastName": "McLaughlin"
    }, {
        "firstName": "Jason",
        "lastName": "Hunter"
    }]
}
    """
    println(jsonText)
    print(jsonText.trim())
}