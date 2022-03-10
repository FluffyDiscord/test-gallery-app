package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.gallery.GalleryAdapter
import com.example.myapplication.gallery.ItemModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var requestQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        val galleryGrid = findViewById<GridView>(R.id.galleryGrid)
        val galleryAdapter = GalleryAdapter(this, ArrayList())
        galleryGrid.adapter = galleryAdapter

        val request = JsonObjectRequest(
            Request.Method.GET, "${BuildConfig.BASE_API_URL}/galleries", null,
            { response ->
                val jsonGalleries = response.getJSONArray("entries")

                val galleries = ArrayList<ItemModel>()
                for (i in 0 until jsonGalleries.length()) {
                    val galleryJson = jsonGalleries.getJSONObject(i)
                    galleries.add(ItemModel(galleryJson.getString("name")))
                }

                galleryAdapter.clear()
                galleryAdapter.addAll(galleries.shuffled())
                galleryAdapter.notifyDataSetChanged()
            },
            { error -> Log.d("NETWORK ERROR", error.toString()) }
        )
        requestQueue?.add(request)


        val randomGalleryBtn = findViewById<FloatingActionButton>(R.id.randomGallery)
        randomGalleryBtn.setOnClickListener {
            val length = galleryAdapter.count - 1

            val itemModel = galleryAdapter.getItem((0..length).random())
            val intent = Intent(this, GalleryDetailActivity::class.java).apply {
                putExtra("galleryId", itemModel?.name)
            }

            startActivity(intent)
        }
    }
}