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
import java.net.URLConnection
import java.util.Scanner
import org.json.JSONObject

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

                    val quote = processChuckNorrisQuoteJson(text)
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
        val chuckNorrisQuote = "https://api.chucknorris.io/jokes/random"
        val ronSwansonQuote = "https://ron-swanson-quotes.herokuapp.com/v2/quotes"
        fetchData(chuckNorrisQuote)
    }

    private fun processQuoteJson (jsonString: String) : String{
        // Converts the json from the API to a regular string
        val jsonArray = JSONArray(jsonString)
        return jsonArray[0].toString()
    }

    private fun processChuckNorrisQuoteJson (jsonString: String) : String{
        // Converts the API output to a json object
        val jsonObject = JSONObject(jsonString)
        // Gets the string stored in the key value
        val jsonArray = jsonObject.getString("value")

        return jsonArray.toString()
    }
}