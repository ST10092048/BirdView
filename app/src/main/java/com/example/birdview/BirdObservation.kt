package com.example.birdview

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.birdview.databinding.ActivityBirdObservationBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BirdObservation : AppCompatActivity() {
    private lateinit var binding :ActivityBirdObservationBinding
    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var progressDialog: ProgressDialog
    private lateinit var birdlist : ArrayList<SpecieViewModel>
    private lateinit var birdAdapter: SpecieViewAdapter
    var categoryId = ""
    var category = ""
    var TAG ="Bird"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirdObservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Bird Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!
        loadBird()

        //This code is for the side bar
        toggle = ActionBarDrawerToggle(this@BirdObservation, binding.drawerLayouts, 0, 0)
        binding.drawerLayouts.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navViews.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.Map -> {
                    val map = Intent(this, FullMap::class.java)
                    startActivity(map)
                }
                R.id.Entry -> {
                    val entry = Intent(this, BirdEntry::class.java)
                    startActivity(entry)
                }
                R.id.Category ->{
                    val category = Intent(this, SpecieCatgeory::class.java)
                    startActivity(category)
                }
                R.id.logout -> Toast.makeText(applicationContext, "cghj", Toast.LENGTH_SHORT).show()
            }
            true
            // The code ends here
        }
    }

    private fun loadBird() {
        progressDialog.show()
        birdlist = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Bird")
        reference.orderByChild("categoryId").equalTo(categoryId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()
                birdlist.clear()
                for (i in snapshot.children){
                    val model = i.getValue(SpecieViewModel::class.java)
                    if(model != null){
                        birdlist.add(model)
                        Log.d(TAG,"onDataChange: ${model.Name} ${model.categoryId}")
                    }
                }
                birdAdapter = SpecieViewAdapter(this@BirdObservation,birdlist)
                binding.SpecieList.adapter = birdAdapter

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)

    }
}