package com.example.lemonade

import android.os.Parcelable
import android.os.Parcel
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONException
import android.text.TextUtils
import android.os.Parcelable.Creator
import java.lang.Exception
import java.util.ArrayList

class Book : Parcelable {
    var openLibraryId: String? = null
        private set
    var author: String? = null
        private set
    var title: String? = null
        private set
    private var publisher: String? = null
    fun getPublisher(): String? {
        return publisher
    }

    fun setPublisher(publisher: String?) {
        this.publisher = publisher
    }

    // Get book cover from covers API
    val coverUrl: String
        get() = "http://covers.openlibrary.org/b/olid/$openLibraryId-L.jpg?default=false"

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(openLibraryId)
        dest.writeString(author)
        dest.writeString(title)
        dest.writeString(publisher)
    }

    private constructor(`in`: Parcel) {
        openLibraryId = `in`.readString()
        author = `in`.readString()
        title = `in`.readString()
        publisher = `in`.readString()
    }

    companion object {
        // Returns a Book given the expected JSON
        fun fromJson(jsonObject: JSONObject?): Book? {
            val book = Book()
            try {
                // Deserialize json into object fields
                // Check if a cover edition is available
                if (jsonObject!!.has("cover_edition_key")) {
                    book.openLibraryId = jsonObject.getString("cover_edition_key")
                } else if (jsonObject.has("edition_key")) {
                    val ids = jsonObject.getJSONArray("edition_key")
                    book.openLibraryId = ids.getString(0)
                }
                book.title =
                    if (jsonObject.has("title_suggest")) jsonObject.getString("title_suggest") else ""
                book.author = getAuthor(jsonObject)
                book.publisher = getPublisher(jsonObject)
            } catch (e: JSONException) {
                e.printStackTrace()
                return null
            }
            // Return new object
            return book
        }

        // Return comma separated author list when there is more than one author
        private fun getAuthor(jsonObject: JSONObject?): String {
            return try {
                val authors = jsonObject!!.getJSONArray("author_name")
                val numAuthors = authors.length()
                val authorStrings = arrayOfNulls<String>(numAuthors)
                for (i in 0 until numAuthors) {
                    authorStrings[i] = authors.getString(i)
                }
                TextUtils.join(", ", authorStrings)
            } catch (e: JSONException) {
                ""
            }
        }

        private fun getPublisher(jsonObject: JSONObject?): String {
            return try {
                val pubs = jsonObject!!.getJSONArray("publisher")
                val numPubs = pubs.length()
                val pubStrings = arrayOfNulls<String>(numPubs)
                for (i in 0 until numPubs) {
                    pubStrings[i] = pubs.getString(i)
                }
                TextUtils.join(", ", pubStrings)
            } catch (e: JSONException) {
                ""
            }
        }

        // Decodes array of book json results into business model objects
        fun fromJson(jsonArray: JSONArray): ArrayList<Book> {
            val books = ArrayList<Book>(jsonArray.length())
            // Process each result in json array, decode and convert to business
            // object
            for (i in 0 until jsonArray.length()) {
                var bookJson: JSONObject? = null
                bookJson = try {
                    jsonArray.getJSONObject(i)
                } catch (e: Exception) {
                    e.printStackTrace()
                    continue
                }
                val book = fromJson(bookJson)
                if (book != null) {
                    books.add(book)
                }
            }
            return books
        }

        @JvmField
        val CREATOR: Creator<Book?> = object : Creator<Book?> {
            override fun createFromParcel(source: Parcel): Book? {
                return Book(source)
            }

            override fun newArray(size: Int): Array<Book?> {
                return arrayOfNulls(size)
            }
        }
    }

//    private companion object CREATOR : Creator<Book> {
//        override fun createFromParcel(parcel: Parcel): Book {
//            return Book(parcel)
//        }
}