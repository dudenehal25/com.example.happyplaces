package com.example.happyplaces.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.provider.VoicemailContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplaces.R
import com.example.happyplaces.database.DataBaseHandler
import com.example.happyplaces.models.HappyPlaceModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happy_places.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddHappyPlaces : AppCompatActivity() {

    companion object {
        const val GALLERY = 1
        const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
        const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }

    val myCalendar = Calendar.getInstance()
    var imageUriPath: Uri? = null
    var mLatitude: Double = 0.0
    var mLongitude: Double = 0.0

    //For Edit Functionality
    var mHappyPlacesModel: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_places)

        setSupportActionBar(toolbar_add_place)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // This is to use the home back button.
        // Setting the click event to the back button
        toolbar_add_place.setNavigationOnClickListener {
            onBackPressed()
        }

        if (!Places.isInitialized()) {
            Places.initialize(this@AddHappyPlaces, resources.getString(R.string.api_maps))
        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            mHappyPlacesModel = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }


        //SelectDate
        updateLabel()//SET DEFAULT DATE
        etdate.setOnClickListener {
            DatePickerDialog(
                this@AddHappyPlaces, date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        if (mHappyPlacesModel != null) {
            supportActionBar?.title = "Edit Happy Place"

            etName.setText(mHappyPlacesModel!!.title)
            etDescription.setText(mHappyPlacesModel!!.description)
            etdate.setText(mHappyPlacesModel!!.date)
            etLocation.setText(mHappyPlacesModel!!.location)
            mLatitude = mHappyPlacesModel!!.latitude
            mLongitude = mHappyPlacesModel!!.longitude

            imageUriPath = Uri.parse(mHappyPlacesModel!!.image)
            iv_place_image.setImageURI(imageUriPath)

            btnSave.text = "UPDATE"
        }


        btnTvAddimage.setOnClickListener {
            val picturedialog = AlertDialog.Builder(this)

            picturedialog.setTitle("SELECT ACTION")
            val action = arrayOf("GALLERY", "CAMERA")
            picturedialog.setItems(action) { dialog, i ->

                when (i) {
                    0 -> choosePhotoFromGallery()

                    1 -> choosePhotoFromCamera()
                }

            }.show()
        }

        btnSave.setOnClickListener {
            if (etName.text.isNullOrEmpty() || etDescription.text.isNullOrEmpty() || etLocation.text.isNullOrEmpty() || imageUriPath == null) {
                Toast.makeText(this, "FILL PROPERLY", Toast.LENGTH_SHORT).show()
            } else {
                addhappyPlace()
            }
        }

        etLocation.setOnClickListener {
            try {
                // These are the list of fields which we required is passed
                val fields = listOf(
                    Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                    Place.Field.ADDRESS
                )
                // Start the autocomplete intent with a unique request code.
                val intent =
                    Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this@AddHappyPlaces)
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun addhappyPlace() {
        //Initialise happy place model
        val happyPlaceItems = HappyPlaceModel(
            mHappyPlacesModel?.id ?: 0,
            etName.text.toString(),
            imageUriPath.toString(),
            etDescription.text.toString(),
            etdate.text.toString(),
            etLocation.text.toString(),
            mLatitude,
            mLongitude
        )
        val dbhandler = DataBaseHandler(this@AddHappyPlaces)
        var sucess: Long
        if (btnSave.text == "UPDATE") {
            sucess = dbhandler.updatehappyPlaceloyee(happyPlaceItems).toLong()
            Toast.makeText(this, sucess.toString(), Toast.LENGTH_SHORT).show()
        } else {
            sucess = dbhandler.addhappyPlaceloyee(happyPlaceItems)
        }

        if (sucess > 0) {
            setResult(Activity.RESULT_OK)
            Toast.makeText(this@AddHappyPlaces, "ADDED IN SQL", Toast.LENGTH_SHORT).show()
            // MainActivity.itemAdapter?.notifyDataSetChanged()
            finish()
        } else if (sucess <= 0)
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        // Here this is used to get an bitmap from URI
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                        imageUriPath = saveImagetoInternalStorage(selectedImageBitmap)
                        Log.e("SAVED", imageUriPath.toString())

                        iv_place_image!!.setImageBitmap(selectedImageBitmap) // Set the selected image from GALLERY to imageView.
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            else if (requestCode == CAMERA) {
                if (data != null) {

                    val thumbNail = data.extras!!.get("data") as Bitmap

                    imageUriPath = saveImagetoInternalStorage(thumbNail)
                    Log.e("SAVED", imageUriPath.toString())

                    iv_place_image.setImageBitmap(thumbNail)
                }
            }
            else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
                val place = Autocomplete.getPlaceFromIntent(data!!)
                etLocation.setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
            }
        }

    }

    //Ask permisiion and start Acticity for result(FOR CAMERA)
    private fun choosePhotoFromCamera() {
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val cameraIntent = Intent("android.media.action.IMAGE_CAPTURE")
                    //cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    startActivityForResult(
                        cameraIntent,
                        CAMERA
                    )
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }
        ).onSameThread().check()

    }

    //TO store in ingternal storage
    fun saveImagetoInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)

        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException) { // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return Uri.parse(file.absolutePath)
    }


    //Ask permisiion and start Acticity for result(FOR GALLERY)
    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }
        ).onSameThread().check()
    }

    //To ask permission from settings(CAMERA AND GALLERY)
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    //ForCalendar
    private var date = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        myCalendar[Calendar.YEAR] = year
        myCalendar[Calendar.MONTH] = monthOfYear
        myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        updateLabel()

    }

    private fun updateLabel() {
        val myFormat = "dd/MM/YYYY" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etdate.setText(sdf.format(myCalendar.time))
    }

}