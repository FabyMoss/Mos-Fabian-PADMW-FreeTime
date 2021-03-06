package com.example.lemonade

import com.codepath.asynchttpclient.AsyncHttpClient
import com.example.lemonade.BookClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class BookClient {
    private val client: AsyncHttpClient
    private fun getApiUrl(relativeUrl: String): String {
        return API_BASE_URL + relativeUrl
    }

    // Method for accessing the search API
    fun getBooks(query: String?, handler: JsonHttpResponseHandler?) {
        try {
            val url = getApiUrl("search.json?q=")
            client[url + URLEncoder.encode(query, "utf-8"), handler]
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val API_BASE_URL = "http://openlibrary.org/"
    }

    init {
        client = AsyncHttpClient()
    }
}