package com.example.myapplication.gallery

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.BuildConfig
import com.example.myapplication.GalleryDetailActivity
import com.example.myapplication.R
import com.squareup.picasso.Picasso

class GalleryAdapter(context: Context, arrayList: ArrayList<ItemModel>) :
    ArrayAdapter<ItemModel>(context, 0, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView =
                LayoutInflater.from(context).inflate(R.layout.gallery_list_item, parent, false)
        }

        val itemModel = getItem(position)!!
        val textView = listItemView?.findViewById<TextView>(R.id.giName)
        textView?.setText(itemModel.name)

        val imageView = listItemView?.findViewById<ImageView>(R.id.giCover)
        Picasso.get().load("${BuildConfig.BASE_API_URL}/galleries/" + itemModel.name + "/cover.jpg")
            .into(imageView)

        listItemView?.layoutParams?.height = 650
        listItemView?.setOnClickListener {
            val intent = Intent(context, GalleryDetailActivity::class.java).apply {
                putExtra("galleryId", itemModel.name)
            }

            context.startActivity(intent)
        }

        return listItemView!!
    }
}