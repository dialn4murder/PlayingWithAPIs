package com.example.playingwithapis

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playingwithapis.databinding.ActivityMainBinding
import org.json.JSONArray
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.button.setOnClickListener(){
            getQuote()
        }

    }

    private fun fetchData(urlString: String){
        val thread = Thread{
            try{
                val url = URL(urlString)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK){
                    val scanner = Scanner(connection.inputStream).useDelimiter("\\A")
                    val text = if (scanner.hasNext()) scanner.next() else ""

                    val quote = processQuoteJson(text)
                    updateTextView(quote)
                }else{
                    updateTextView("The server has returned an error: $responseCode")
                }

            } catch (e: IOException) {
                updateTextView("An error has occurred from the server")
            }

        }
        thread.start()
    }

    private fun updateTextView(text: String){
        // Updates the UI as you cant update via threads
        runOnUiThread{
            binding.textView.text = text
        }
    }

    private fun getQuote(){
        // Calls fetchData with the API
        fetchData("https://ron-swanson-quotes.herokuapp.com/v2/quotes")
    }

    private fun processQuoteJson (jsonString: String) : String{
        // Converts the json from the API to a regular string
        val jsonArray = JSONArray(jsonString)
        return jsonArray[0].toString()
    }
}