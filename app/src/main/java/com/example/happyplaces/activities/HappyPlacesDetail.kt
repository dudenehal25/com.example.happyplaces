package com.example.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.R
import com.example.happyplaces.activities.MainActivity.Companion.EXTRA_PLACE_DETAILS
import com.example.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_add_happy_places.*
import kotlinx.android.synthetic.main.activity_add_happy_places.toolbar_add_place
import kotlinx.android.synthetic.main.activity_happy_places_detail.*

class HappyPlacesDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_places_detail)

        setSupportActionBar(toolbar_add_place)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // This is to use the home back button.
        // Setting the click event to the back button
        toolbar_add_place.setNavigationOnClickListener {
            onBackPressed()
        }

        var happyPlaceModel: HappyPlaceModel? = null

        if (intent.hasExtra(EXTRA_PLACE_DETAILS)) {
            happyPlaceModel = intent.getParcelableExtra(EXTRA_PLACE_DETAILS) as HappyPlaceModel?
        }

        if (happyPlaceModel != null) {
            toolbar_add_place.setTitle(happyPlaceModel.title)

            val uri = Uri.parse(happyPlaceModel.image)
            ivDetail.setImageURI(uri)

            tvDetailName.text = happyPlaceModel.title
            tvDetailDesc.text = happyPlaceModel.description
        }
    }
}