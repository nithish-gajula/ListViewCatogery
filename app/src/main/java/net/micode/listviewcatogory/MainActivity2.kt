package net.micode.listviewcatogory


import MyCustomAdapter
import Section
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView


class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val listView = findViewById<ListView>(R.id.listView)

        // Sample data
        val dataList = mutableListOf<Any>()
        dataList.add(Section("Section 1", "Amount 1"))
        dataList.add("Item 1.1")
        dataList.add("Item 1.2")
        dataList.add(Section("Section 2", "Amount 2"))
        dataList.add("Item 2.1")
        dataList.add("Item 2.2")

        val adapter = MyCustomAdapter(this, dataList)
        listView.adapter = adapter
    }


}

