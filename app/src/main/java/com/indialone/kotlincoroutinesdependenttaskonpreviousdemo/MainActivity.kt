package com.indialone.kotlincoroutinesdependenttaskonpreviousdemo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
        result = findViewById(R.id.text)

        button.setOnClickListener {
            setNewText("Click!")

            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }

    }

    private fun setNewText(text: String) {
        result.text = "${result.text}\n$text"
    }

    private suspend fun setNewTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }


    private suspend fun fakeApiRequest() {
        withContext(IO) {
            val executionTime = measureTimeMillis {
                var result1 = ""

                val job1 = launch {
                    print("debug : thread name : ${Thread.currentThread().name}")
                    result1 = getResult1FromApi()
                }
                job1.join()

                var result2 = ""
                val job2 = launch {
                    result2 = withContext(Dispatchers.Default) {
                        print("debug : thread name: ${Thread.currentThread().name}")
                        getResult2FromApi(result1)
                    }
                }
                job2.join()

                print("Got Result2 : $result2")

                var result3 = ""

                val job3 = launch {
                    result3 = withContext(Dispatchers.Default) {
                        print("debug : Thread name : ${Thread.currentThread().name}")
                        getResult3FromApi(result2)
                    }
                }
                job3.join()

                var result4 = ""
                val job4 = launch {
                    result4 = withContext(Dispatchers.Default) {
                        print("debug : Thread name : ${Thread.currentThread().name}")
                        getResult4FromApi(result3)
                    }
                }
                job4.join()

                val result5 = withContext(Dispatchers.Default) {
                    print("debug : thread name : ${Thread.currentThread().name}")
                    getResult5FromApi(result4)
                }
                print("Result 5 : $result5")

            }
            print("debug: job1 and job2 are complete. Execution Time : $executionTime")
        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(1000)
        return "Result #1"
    }

    private suspend fun getResult2FromApi(result1: String): String {
        delay(1700)
        return "Result #2"
    }

    private suspend fun getResult3FromApi(result2: String): String {
        delay(2000)
        return "Result #3"
    }

    private suspend fun getResult4FromApi(result3: String): String {
        delay(1000)
        return "Result #4"
    }

    private suspend fun getResult5FromApi(result4: String): String {
        delay(1000)
        return "Result #5"
    }

}