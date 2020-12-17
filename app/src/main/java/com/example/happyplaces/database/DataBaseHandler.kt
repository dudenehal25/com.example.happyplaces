package com.example.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.happyplaces.models.HappyPlaceModel

class DataBaseHandler(context: Context) :
    SQLiteOpenHelper(context ,DATABASE_NAME, null, DATABASE_VERSION ) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "HappyPlacesDB"
        private val TABLE_PLACES = "Placestable"

        private val KEY_ID = "_id"
        private val KEY_TITLE = "title"
        private val KEY_IMAGE = "image"
        private val KEY_DESCRIPTION = "description"
        private val KEY_DATE = "date"
        private val KEY_LOCATION = "location"
        private val KEY_LONGITUDE = "longitude"
        private val KEY_LATITUDE = "latitude"
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL("CREATE TABLE $TABLE_PLACES ($KEY_ID  INTEGER PRIMARY KEY , $KEY_TITLE TEXT , $KEY_IMAGE TEXT,  $KEY_DESCRIPTION TEXT , $KEY_DATE TEXT , $KEY_LOCATION TEXT ,$KEY_LONGITUDE TEXT , $KEY_LATITUDE TEXT ) ")

        //alternate way to add
        /*val CREATE_HAPPY_PLACE_TABLE = ("CREATE TABLE " + TABLE_PLACES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        p0?.execSQL(CREATE_HAPPY_PLACE_TABLE)*/
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXISTS $TABLE_PLACES")
        onCreate(p0)
    }

    /**
     * Function to insert data
     */
    fun addhappyPlaceloyee(happyPlace: HappyPlaceModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title)
        contentValues.put(KEY_IMAGE, happyPlace.image)
        contentValues.put(KEY_DESCRIPTION, happyPlace.description)
        contentValues.put(KEY_DATE, happyPlace.date)
        contentValues.put(KEY_LOCATION, happyPlace.location)
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)
        contentValues.put(KEY_LATITUDE, happyPlace.latitude)

        // Inserting Row
        val success = db.insert(TABLE_PLACES, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return success
    }

    /**
     * Function to insert data
     */
    //method to read data
    fun viewhappyPlaceloyee(): ArrayList<HappyPlaceModel> {

        val happyPlaceList: ArrayList<HappyPlaceModel> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_PLACES"

        val db = this.readableDatabase
        var cursor: Cursor

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val place = HappyPlaceModel(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                    cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                    cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
                )
                happyPlaceList.add(place)
            } while (cursor.moveToNext())
        }
        return happyPlaceList
    }

    /**
     * Function to update record
     */
    fun updatehappyPlaceloyee(happyPlace: HappyPlaceModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title) // HappyPlaceModelClass TITLE
        contentValues.put(KEY_IMAGE, happyPlace.image) // HappyPlaceModelClass IMAGE
        contentValues.put(KEY_DESCRIPTION, happyPlace.description) 
        contentValues.put(KEY_DATE, happyPlace.date) // HappyPlaceModelClass DATE
        contentValues.put(KEY_LOCATION, happyPlace.location) // HappyPlaceModelClass LOCATION
        contentValues.put(KEY_LATITUDE, happyPlace.latitude) // HappyPlaceModelClass LATITUDE
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude) // HappyPlaceModelClass LONGITUDE

        // Updating Row
        val success = db.update(TABLE_PLACES, contentValues, KEY_ID + "=" + happyPlace.id, null)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return success
    }

    /**
     * Function to delete record
     */
    fun deletehappyPlaceloyee(happyPlace: HappyPlaceModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, happyPlace.id) // HappyPlaceModel id
        // Deleting Row
        val success = db.delete(TABLE_PLACES, KEY_ID + "=" + happyPlace.id, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}