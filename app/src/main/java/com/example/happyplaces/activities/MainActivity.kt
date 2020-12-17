package com.example.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.R
import com.example.happyplaces.adapters.itemPlacesAdapter
import com.example.happyplaces.database.DataBaseHandler
import com.example.happyplaces.models.HappyPlaceModel
import com.example.happyplaces.utils.SwipeToDeleteCallback
import com.example.happyplaces.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fabAdd.setOnClickListener {
            val intent = Intent(this, AddHappyPlaces::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        //viewhappyPlaces()
        setupListofDataIntoRecyclerView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE ){
            if (resultCode == Activity.RESULT_OK){
                setupListofDataIntoRecyclerView()
            }
            else Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListofDataIntoRecyclerView() {

        if (getItemsList().size > 0) {
            rvMainActivity.layoutManager = LinearLayoutManager(this)

            // Adapter class is initialized and list is passed in the param.
            itemAdapter = itemPlacesAdapter(this, getItemsList())

            // adapter instance is set to the recyclerview to inflate the items.
            rvMainActivity.adapter = itemAdapter

            itemAdapter!!.setOnclickListener(object : itemPlacesAdapter.OnClickListener{
                override fun onClick(model: HappyPlaceModel, position: Int) {
                    val intent = Intent(this@MainActivity , HappyPlacesDetail::class.java)
                    intent.putExtra(EXTRA_PLACE_DETAILS , model)
                    startActivity(intent)
                }
            })

            val editToSwipe = object : SwipeToEditCallback(this){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter  =rvMainActivity.adapter as itemPlacesAdapter
                    adapter.notifyeditItem(this@MainActivity , viewHolder.position , ADD_PLACE_ACTIVITY_REQUEST_CODE)
                }
            }
            val editItemTouchHelper =ItemTouchHelper(editToSwipe)
            editItemTouchHelper.attachToRecyclerView(rvMainActivity)


            val deleteToSwipe = object : SwipeToDeleteCallback(this){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter  =rvMainActivity.adapter as itemPlacesAdapter
                   adapter.removeAt(viewHolder.adapterPosition)

                }
            }
            val deleteItemTouchHelper =ItemTouchHelper(deleteToSwipe)
            deleteItemTouchHelper.attachToRecyclerView(rvMainActivity)
        }
    }

    private fun getItemsList(): ArrayList<HappyPlaceModel> {
        val databaseHandler = DataBaseHandler(this)
        return databaseHandler.viewhappyPlaceloyee()
    }

    //ADDING TO CATLOG
  /*  private fun viewhappyPlaces() {
        val dataBaseHandler = DataBaseHandler(this)
        val happyPlaces = dataBaseHandler.viewhappyPlaceloyee()

        if (happyPlaces.size > 0) {
            for (i in happyPlaces) {
                Log.i("MODEL", i.date)
                Log.i("MODEL", i.title)
                Log.i("MODEL", i.description)
            }
        }
    }*/

    companion object {
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var itemAdapter: itemPlacesAdapter? = null
        val EXTRA_PLACE_DETAILS = "extra_place_detail"
    }
}