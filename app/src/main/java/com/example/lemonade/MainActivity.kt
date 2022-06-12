/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lemonade

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.net.Authenticator
import java.net.PasswordAuthentication


class MainActivity : AppCompatActivity() {
    var btnLogOut: Button? = null
    var mAuth: FirebaseAuth? = null
    /**
     * DO NOT ALTER ANY VARIABLE OR VALUE NAMES OR THEIR INITIAL VALUES.
     *
     * Anything labeled var instead of val is expected to be changed in the functions but DO NOT
     * alter their initial values declared here, this could cause the app to not function properly.
     */
    private val LEMONADE_STATE = "LEMONADE_STATE"
    private val LEMON_SIZE = "LEMON_SIZE"
    private val SQUEEZE_COUNT = "SQUEEZE_COUNT"
    // SELECT represents the "pick lemon" state
    private val SELECT = "select"
    // SQUEEZE represents the "squeeze lemon" state
    private val SQUEEZE = "squeeze"
    // DRINK represents the "drink lemonade" state
    private val DRINK = "drink"
    // RESTART represents the state where the lemonade has been drunk and the glass is empty
    private val RESTART = "restart"
    // Default the state to select
    private var lemonadeState = "select"
    // Default lemonSize to -1
    private var lemonSize = -1
    // Default the squeezeCount to -1
    private var squeezeCount = -1

//    private var lemonTree = LemonTree()
    private var lemonImage: ImageView? = null
    var listView: ListView? = null;

    private class MyCustomAdapter(context: Context, array: Array<Pair<String, String>>): BaseAdapter() {

        private val mContext: Context = context
        private val books: Array<Pair<String, String>> = array

        private val names = arrayListOf<String>(
            "Donald Trump", "Steve Jobs", "Tim Cook", "Mark Zuckerberg", "Barack Obama"
        )

        // responsible for how many rows in my list
        override fun getCount(): Int {
            return books.size
        }

        // you can also ignore this
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        // you can ignore this for now
        override fun getItem(position: Int): Any {
            return "TEST STRING"
        }

        // responsible for rendering out each row
        @SuppressLint("ViewHolder", "SetTextI18n")
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.row_main, viewGroup, false)

            val nameTextView = rowMain.findViewById<TextView>(R.id.name_textView)
//            books.reversed()
            for(item in books.reversed()){

//                val ceva = books.elementAt(i)
                val (a, b) = item
//                    nameTextView.setSingleLine(false);
                    nameTextView.append("$a: $b\n")
            }



            val positionTextView = rowMain.findViewById<TextView>(R.id.position_textview)
            positionTextView.text = "Row number: $position"


            return rowMain
//            val textView = TextView(mContext)
//            textView.text = "HERE is my ROW for my LISTVIEW"
//            return textView
        }

    }

    var booksIds = arrayOf(9780679407584, 9781400079148, 9781415940365, 9781400097746, 9780385494762, 9780345447982, 9780739340653)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        listView=findViewById(R.id.listView)

//        ArrayList<String>()() arrayList = new ArrayList<>()



        btnLogOut = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();

        val queue = Volley.newRequestQueue(this)
        val url = "https://reststop.randomhouse.com/resources/titles/9781400079148"
//        val textAction: TextView = findViewById(R.id.text_action)

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                    try {
                        val factory = XmlPullParserFactory.newInstance()
                        val numbersMap: MutableMap<String, String> = HashMap()

                        factory.isNamespaceAware = true
                        val xpp = factory.newPullParser()
                        xpp.setInput(StringReader(response)) // pass input whatever xml you have
                        var eventType = xpp.eventType
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_DOCUMENT) {
//                                Log.d(ContentValues.TAG, "Start document")
                            } else if (eventType == XmlPullParser.START_TAG) {

                                when(xpp.name) {
                                    "titleweb" -> {
                                        val name = xpp.nextText()

                                        numbersMap["Title"] = name
                                    }
                                    "subjectcategorydescription1" -> {
                                        val name = xpp.nextText()

                                        numbersMap["Category"] = name
                                    }
                                    "pages" -> {
                                        val name = xpp.nextText()

                                        numbersMap["Pages"] = name
                                    }
                                    "isbn" -> {
                                        val name = xpp.nextText()

                                        numbersMap["ISBN"] = name
                                    }
                                    "author" -> {
                                        val name = xpp.nextText()

                                        numbersMap["Author"] = name
                                    }
                                }
//                                if(xpp.name == "titleweb" || xpp.name == "subjectcategorydescription1" || xpp.name == "pages"
//                                    || xpp.name == "isbn" || xpp.name == "author") {
//                                    val numbersMap = xpp.nextText()
//                                    numbersMap[xpp.name] = name
                                println(numbersMap)

//                                Log.d(ContentValues.TAG, xpp.name)
//                                Log.d(ContentValues.TAG, name)


                            } else if (eventType == XmlPullParser.END_TAG) {
//                                Log.d(ContentValues.TAG, "End tag " + xpp.name)
                            } else if (eventType == XmlPullParser.TEXT) {
//                                Log.d(ContentValues.TAG, "Text " + xpp.text) // here you get the text from xml
                            }
//                            if(numbersMap != "ceva"){
//                            }
                            println()
//                            Log.d("surprza", numbersMap)
                            eventType = xpp.next()
                        }
                        val array: Array<Array<Pair<String, String>>> = numbersMap.toList();
                        numbersMap.forEach { entry ->

//                            textAction.append("\n${entry.key}: ${entry.value}")
                        }
                        val listView:ListView = findViewById(R.id.listview_1)
                        listView.adapter = MyCustomAdapter(this, array) // this needs to be my custom adapter telling my list what to render
//                        val adapter = ArrayAdapter(this,
//                            R.layout.listview_item, array)


//                            listView.adapter = adapter

                        println(numbersMap.count())
                        Log.d(ContentValues.TAG, "End document")
                    } catch (e: XmlPullParserException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
//                    val ceva = response.getJSONArray("results")
//                    textAction.text = eventType.toString()

                    println("just worked")


                    // Do something with result


                // Display the first 500 characters of the response string.

//                val mJsonArray = JSONArray(response)
//                val mJsonObject: JSONObject = mJsonArray.getJSONObject(0)
//
//                val results = mJsonObject.getString("results")



//                val arrayList = ArrayList<String>()
//                val jsonObject = JSONObject(response)
//                val ceva = jsonObject.optJSONArray(("results"))
//                ceva.forEach {
//
//                }
//                val books = response['response']
//                textAction.setMovementMethod(ScrollingMovementMethod())
//                val xmlToJson = XmlToJson.Builder(response).build()

//                PrintWriter(FileWriter("json.json"))
//                    .use { it.write(response.toString()) }


//                textView.text = "Response is: ${response.substring(0, 500)}"
            },
            {  error -> println(error)})

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
//        val url = "http://openlibrary.org/search.json?q=dictionary"
//        doAsync {
//            Request(url).run()
//            uiThread { toast("Request performed") }
//        }



        // Method for accessing the search API
//        fun getBooks(query: String?, handler: JsonHttpResponseHandler?) {
//            try {
//                val url = getApiUrl("search.json?q=")
//                client[url + URLEncoder.encode(query, "utf-8"), handler]
//            } catch (e: UnsupportedEncodingException) {
//                e.printStackTrace()
//            }
//        }


        btnLogOut!!.setOnClickListener{
            mAuth!!.signOut();
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
        // === DO NOT ALTER THE CODE IN THE FOLLOWING IF STATEMENT ===
        if (savedInstanceState != null) {
            lemonadeState = savedInstanceState.getString(LEMONADE_STATE, "select")
            lemonSize = savedInstanceState.getInt(LEMON_SIZE, -1)
            squeezeCount = savedInstanceState.getInt(SQUEEZE_COUNT, -1)
        }
        // === END IF STATEMENT ===

//        lemonImage = findViewById(R.id.image_lemon_state)
//        setViewElements()
//        lemonImage!!.setOnClickListener {
//            Toast.makeText(this, "Lucky Number!", Toast.LENGTH_SHORT).show()
            // TODO: call the method that handles the state when the image is clicked
//             clickLemonImage()
//        }
//        lemonImage!!.setOnLongClickListener {
//            showSnackbar();
            // TODO: replace 'false' with a call to the function that shows the squeeze count
//             false
//        }
    }

    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }
    /**
     * === DO NOT ALTER THIS METHOD ===
     *
     * This method saves the state of the app if it is put in the background.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(LEMONADE_STATE, lemonadeState)
        outState.putInt(LEMON_SIZE, lemonSize)
        outState.putInt(SQUEEZE_COUNT, squeezeCount)
        super.onSaveInstanceState(outState)
    }

    /**
     * Clicking will elicit a different response depending on the state.
     * This method determines the state and proceeds with the correct action.
     */
//    private fun clickLemonImage() {
//
//        when (lemonadeState){
//
//            SELECT-> {
//
//                lemonadeState=SQUEEZE
//
//                lemonSize=2
//
//                squeezeCount=0
//
//            }
//
//            SQUEEZE->{
//
//                squeezeCount +=1
//
//                lemonSize -= 1
//
//                lemonadeState = if(lemonSize==0){
//
//                    DRINK
//
//                } else SQUEEZE
//
//            }
//
//            DRINK->{
//
//                lemonadeState=RESTART
//
//                lemonSize=-1
//
//            }
//
//            RESTART->lemonadeState=SELECT
//
//        }
//
////        setViewElements()
//
//    }



//    private fun setViewElements() {
//
//        val textAction: TextView = findViewById(R.id.text_action)
//
//        val lemonImage: ImageView = findViewById(R.id.image_lemon_state)
//
//        when (lemonadeState) {
//
//            SELECT -> {
//
//                textAction.text = getString(R.string.lemon_select)
//
//                lemonImage.setImageResource(R.drawable.lemon_tree)
//
//            }
//
//            SQUEEZE -> {
//
//                textAction.text = getString(R.string.lemon_squeeze)
//
//                lemonImage.setImageResource(R.drawable.lemon_squeeze)
//
//            }
//
//            DRINK -> {
//
//                textAction.text = getString(R.string.lemon_drink)
//
//                lemonImage.setImageResource(R.drawable.lemon_drink)
//
//            }
//
//            RESTART -> {
//
//                textAction.text = getString(R.string.lemon_empty_glass)
//
//                lemonImage.setImageResource(R.drawable.lemon_restart)
//
//            }
//
//        }
//    }
//
//    /**
//     * === DO NOT ALTER THIS METHOD ===
//     *
//     * Long clicking the lemon image will show how many times the lemon has been squeezed.
//     */
//    private fun showSnackbar(): Boolean {
//        if (lemonadeState != SQUEEZE) {
//            return false
//        }
//        val squeezeText = getString(R.string.squeeze_count, squeezeCount)
//        Snackbar.make(
//            findViewById(R.id.constraint_Layout),
//            squeezeText,
//            Snackbar.LENGTH_SHORT
//        ).show()
//        return true
//    }
//}
//
///**
// * A Lemon tree class with a method to "pick" a lemon. The "size" of the lemon is randomized
// * and determines how many times a lemon needs to be squeezed before you get lemonade.
// */
//class LemonTree {
//    fun pick(): Int {
//        return (2..4).random()
//    }
}
