package net.micode.listviewcatogory

import Item2
import MyCustomAdapter2
import Section2
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val contextTAG: String = "MainActivity"
    private lateinit var adapter: ListAdapter
    private lateinit var listView: ListView
    private lateinit var totalAmount: TextView

    private val fileName = "getData.json"
    private val directoryName = "RoomBudget"
    private val groupedItemsJson = JSONObject()
    private lateinit var months: List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.lv_items)
        totalAmount = findViewById(R.id.total_Amount_id)

        totalAmount.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        Log.i(contextTAG, "in onCreate Function started")

        getItems()

        Log.i(contextTAG, "in onCreate Function finished")

        // Create JSON data
        //val jsonData = createJsonData()

        // Save JSON data to a file
        //createAndWriteToFile(jsonData)

        //Log.d("JSONObjectContent", jsonData.toString())

    }

    private fun getItems() {

        Log.i(contextTAG, "in getItems Function started")

        val userId = "H242a1410I4294g"
        val roomId = "1524R232y1558P"

        val param = "?action=getItem&userId=$userId&roomId=$roomId"
        val url = resources.getString(R.string.spreadsheet_url)
        val stringRequest = StringRequest(
            Request.Method.GET, url + param,
            { response ->
                Log.i(contextTAG, "response = $response")
                parseItems(response)
            }
        ) { error ->
            Log.i(contextTAG, "error = $error")
        }


        val socketTimeOut = 50000
        val policy: RetryPolicy =
            DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        stringRequest.setRetryPolicy(policy)
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }


    private fun parseItems(jsonResponse: String) {


        try {
            val jsonObj = JSONObject(jsonResponse)
            val jsonArray = jsonObj.getJSONArray("items")

            for (i in 0 until jsonArray.length()) {
                val jo = jsonArray.getJSONObject(i)
                val dataId = jo.getString("dataId")

                val dateFormats = convertDateFormat(jo.getString("date"))
                val date = dateFormats.format1

                val amount = jo.getString("amount").toInt()
                var description = jo.getString("description")

                if (description.length >= 20) {
                    description = description.substring(0, 20) + ".."
                }

                // Grouping by month
                val monthKey = date.substring(3, 11) // Extracting MMM yyyy
                if (groupedItemsJson.has(monthKey)) {
                    val monthData =
                        groupedItemsJson.getJSONObject(monthKey).getJSONArray("MonthData")
                    val monthTotal =
                        groupedItemsJson.getJSONObject(monthKey).getDouble("MonthTotal")

                    // Add data to MonthData
                    val newData = JSONObject()
                    newData.put("position1", "Nithish Gajula")
                    newData.put("position2", description)
                    newData.put("position3", date)
                    newData.put("position4", "₹ $amount")
                    newData.put("position5", dataId)
                    monthData.put(newData)

                    // Update total amount for the month
                    groupedItemsJson.getJSONObject(monthKey).put("MonthTotal", monthTotal + amount)
                } else {
                    val newDataArray = JSONArray()
                    val newData = JSONObject()
                    newData.put("position1", "Nithish Gajula")
                    newData.put("position2", description)
                    newData.put("position3", date)
                    newData.put("position4", "₹ $amount")
                    newData.put("position5", dataId)
                    newDataArray.put(newData)

                    val monthObject = JSONObject()
                    monthObject.put("MonthName", monthKey)
                    monthObject.put("MonthData", newDataArray)
                    monthObject.put("MonthTotal", amount.toDouble())

                    groupedItemsJson.put(monthKey, monthObject)
                }
            }

            // Output the JSON object for further use
            Log.d("GroupedItemsJSON", groupedItemsJson.toString())
            months = groupedItemsJson.keys().asSequence().toList()
            Log.d("months", months.toString())
            val dateFormat = SimpleDateFormat("MMM yyyy", Locale.ENGLISH)
            val dateList = months.map { dateFormat.parse(it) }
            val sortedDescending = dateList.sortedDescending()
            months = sortedDescending.map { dateFormat.format(it) }
            Log.d(contextTAG, "Descending order: $months")


        } catch (e: JSONException) {
            e.printStackTrace()
        }

        categorizeItems()
    }

    private fun categorizeItems() {

        Log.d(
            contextTAG,
            "================================Started===================================="
        )

        val dataList = mutableListOf<Any>()


        try {

            for (i in months.indices) {
                val monthJsonObject = groupedItemsJson.getJSONObject(months[i])
                Log.d(contextTAG, "monthJsonObject :  $monthJsonObject")

                val monthHeader = monthJsonObject.getString("MonthName")
                Log.d(contextTAG, "monthHeader :  $monthHeader")

                val monthTotal = monthJsonObject.getString("MonthTotal")
                Log.d(contextTAG, "monthTotal :  $monthTotal")

                dataList.add(Section2(monthHeader, monthTotal))

                val monthData = monthJsonObject.getJSONArray("MonthData")
                Log.d(contextTAG, "monthData :  $monthData")

                for (i in 0 until monthData.length()) {
                    val userName = monthData.getJSONObject(i).getString("position1")
                    val description = monthData.getJSONObject(i).getString("position2")
                    val date = monthData.getJSONObject(i).getString("position3")
                    val amount = monthData.getJSONObject(i).getString("position4")
                    val id = monthData.getJSONObject(i).getString("position5")
                    dataList.add(Item2(userName, description, date, amount, id))
                }

            }


        } catch (e: JSONException) {
            e.printStackTrace()
        }

        adapter = MyCustomAdapter2(this, dataList)
        listView.adapter = adapter

        Log.d(
            contextTAG,
            "================================Completed===================================="
        )


    }


    private data class DateFormats(val format1: String, val format2: String)

    private fun convertDateFormat(dateString: String): DateFormats {
        // Parsing the input date string
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val date: Date = dateFormat.parse(dateString) ?: return DateFormats("", "")

        // Formatting the date to "dd MMM yyyy" format
        val outputDateFormat1 = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val outputDateFormat2 = SimpleDateFormat("MMM yyyy", Locale.getDefault())

        val formattedDate1 = outputDateFormat1.format(date)
        val formattedDate2 = outputDateFormat2.format(date)

        return DateFormats(formattedDate1, formattedDate2)
    }

    private fun createAndWriteToFile(userData: JSONObject) {
        Log.i(contextTAG, "Entered in createAndWriteToFile Function")
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            directoryName
        )
        val file = File(directory, fileName)

        if (!directory.exists()) {
            Log.i(contextTAG, "Directory not exists, Creating Directory")
            directory.mkdirs()
        }
        if (!file.exists()) {
            Log.i(contextTAG, "File not exists, Creating File")
            try {
                var fileBool = file.createNewFile()
                Log.i(contextTAG, "fileBool = $fileBool")
                FileWriter(file).use { it.write(userData.toString()) }
            } catch (e: IOException) {
                Log.e(contextTAG, "Error creating or writing to file: $e")
            }
        } else {
            Log.i(contextTAG, "File already exists, overwriting")
            try {
                FileWriter(file).use { it.write(userData.toString()) }
            } catch (e: IOException) {
                Log.e(contextTAG, "Error overwriting file: $e")
            }
        }
    }


}