package com.example.birdview

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.birdview.databinding.ActivityBirdCategoryEntryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BirdCategoryEntry : AppCompatActivity() {
    private lateinit var binding: ActivityBirdCategoryEntryBinding
    private lateinit var progressDialog: ProgressDialog
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBirdCategoryEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        binding.AddSpecie.setOnClickListener {
            addDataValidate()
        }

        toggle = ActionBarDrawerToggle(this@BirdCategoryEntry, binding.drawerLayouts, 0, 0)
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

                R.id.Category -> {
                    val category = Intent(this, SpecieCatgeory::class.java)
                    startActivity(category)
                }

                R.id.logout -> Toast.makeText(applicationContext, "cghj", Toast.LENGTH_SHORT).show()
            }
            true
        }


    }
    private  var specie =""
    private var specieNumber = ""
    private fun addDataValidate() {
        specie = binding.SpecieCategory.text.toString().trim()

        if (specie.isEmpty()){
            Toast.makeText(this,"Enter a Specie", Toast.LENGTH_SHORT).show()
        }else{
            addSpecietoFirebase()
        }
    }

    private fun addSpecietoFirebase() {
        progressDialog.show()
        var userID = "1"
        val timestamp = System.currentTimeMillis()
        val hashMap  = HashMap<String,Any>()
        hashMap["id"] = "$timestamp"
        hashMap["specie"]= specie
        hashMap["specieNumber"] = specieNumber
        hashMap["uid"] = "$userID"

        val reference = FirebaseDatabase.getInstance().getReference("Species")
        reference.child("$timestamp").setValue(hashMap).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(this,"Specie Added", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { n->
            progressDialog.dismiss()
            Toast.makeText(this,n.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)

    }
}