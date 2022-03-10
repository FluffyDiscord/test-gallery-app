package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso

class GalleryDetailActivity : Activity() {
    private var requestQueue: RequestQueue? = null
    private var currentPage = 1
    private var maxPages: Int? = null
    private var galleryId: String? = null
    private var pagesView: TextView? = null
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_detail)

        requestQueue = Volley.newRequestQueue(this)

        galleryId = intent.getStringExtra("galleryId")

        val galleryNameView = findViewById<TextView>(R.id.name)
        galleryNameView.apply {
            isVisible = false
        }

        pagesView = findViewById(R.id.pages)
        pagesView?.apply {
            isVisible = false
        }

        imageView = findViewById(R.id.image)

        updateImage()

        val leftArrowView = findViewById<ImageView>(R.id.leftArrow)
        leftArrowView.setOnClickListener {
            currentPage--
            if (currentPage < 1) {
                currentPage = maxPages!!
            }

            updatePageCounter()
            updateImage(false)
        }

        val rightArrowView = findViewById<ImageView>(R.id.rightArrow)
        rightArrowView.setOnClickListener {
            currentPage++
            if (currentPage > maxPages!!) {
                currentPage = 1
            }

            updatePageCounter()
            updateImage(backward = false)
        }

        val rightAreaView = findViewById<Button>(R.id.rightArea)
        rightAreaView.setOnClickListener {
            currentPage++
            if (currentPage > maxPages!!) {
                currentPage = 1
            }

            updatePageCounter()
            updateImage(backward = false)
        }

        requestQueue?.add(JsonObjectRequest(
            Request.Method.GET,
            "${BuildConfig.BASE_API_URL}/galleries/$galleryId/metadata.json",
            null,
            { response ->
                val name = response.getString("name_pretty")

                galleryNameView.apply {
                    isVisible = true
                    text = name
                }

                maxPages = response.getInt("images")
                updatePageCounter()
                if (currentPage == 1) {
                    updateImage(backward = false)
                }
            },
            { error -> Log.d("NETWORK ERROR", error.toString()) }
        ))
    }

    override fun onStart() {
        super.onStart()

        val sp = getSharedPreferences("visited-galleries-$galleryId", Context.MODE_PRIVATE)

        var refresh = false
        val lastCurrentPage = sp.getInt("current-page", 0)
        if (lastCurrentPage > 0) {
            refresh = true
            currentPage = lastCurrentPage
        }

        val lastMaxPages = sp.getInt("max-page", 0)
        if (lastMaxPages > 0 && maxPages == null) {
            refresh = true
            maxPages = lastMaxPages
        }

        if (refresh) {
            updatePageCounter()
            updateImage()
        }
    }

    override fun onStop() {
        val sp = getSharedPreferences("visited-galleries-$galleryId", Context.MODE_PRIVATE).edit()

        sp.putInt("current-page", currentPage)
        if (maxPages != null) {
            sp.putInt("max-page", maxPages!!)
        }

        sp.apply()
        super.onStop()
    }

    private fun updateImage(forward: Boolean = true, backward: Boolean = true) {
        Picasso.get().load("${BuildConfig.BASE_API_URL}/galleries/$galleryId/$currentPage.jpg")
            .into(imageView)

        if (maxPages == null) {
            return
        }

        // preload next few images
        if (forward) {
            for (nextImage in currentPage + 1 until currentPage + 3) {
                if (nextImage > maxPages!!) {
                    return
                }

                Picasso.get()
                    .load("${BuildConfig.BASE_API_URL}/galleries/$galleryId/$nextImage.jpg")
                    .fetch()
            }
        }

        if (backward) {
            for (nextImage in currentPage - 1 downTo currentPage - 3) {
                if (nextImage < 1) {
                    return
                }

                Picasso.get()
                    .load("${BuildConfig.BASE_API_URL}/galleries/$galleryId/$nextImage.jpg")
                    .fetch()
            }
        }
    }

    private fun updatePageCounter() {
        pagesView?.apply {
            text = "$currentPage / $maxPages"
            isVisible = true
        }
    }
}