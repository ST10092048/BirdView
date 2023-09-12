package com.example.birdview

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.birdview.databinding.ActivityBirdEntryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

class BirdEntry : AppCompatActivity() {
    private lateinit var binding: ActivityBirdEntryBinding
    lateinit var toggle: ActionBarDrawerToggle
    private val TAG = "Bird Entry"
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var specieArrayList: ArrayList<BirdSpecieCategoryModel>
    private var ImageUri: Uri? = null
    val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirdEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading..")
        progressDialog.setCanceledOnTouchOutside(false)

        loadCategories()
        binding.etCategory.setOnClickListener {
            categoryPick()
        }
        binding.Save.setOnClickListener {
            ValidateandSave()
        }
        //This code is for the side bar
        toggle = ActionBarDrawerToggle(this@BirdEntry, binding.drawerLayouts, 0, 0)
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
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val Myformat = "YYYY-MM-dd"
                val sdf = SimpleDateFormat(Myformat, Locale.US)
                binding.etDate.setText(sdf.format(calendar.time))
            }
        }
        binding.etDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@BirdEntry,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })

    }

    private var Name =""
    private var Description =""
    private var categories =""
    private var Date =""
    private var Location =""
    private fun ValidateandSave() {
        Log.d(TAG,"Validate and Save: validating data")

        Name = binding.etBirdName.text.toString().trim()
        Description = binding.etDescription.text.toString().trim()
        categories = binding.etCategory.text.toString().trim()
        Date = binding.etDate.text.toString().trim()
        Location = binding.etLoaction.text.toString().trim()

        if (Name.isEmpty()){
            Toast.makeText(this,"Enter Bird Name",Toast.LENGTH_SHORT).show()
        }else if(Description.isEmpty()){
            Toast.makeText(this,"Enter Bird Description",Toast.LENGTH_SHORT).show()
        }else if(categories.isEmpty()){
            Toast.makeText(this,"Select a Bird Specie",Toast.LENGTH_SHORT).show()
        }else if(Date.isEmpty()){
            Toast.makeText(this,"Enter Date of Site",Toast.LENGTH_SHORT).show()
        }else if(Location.isEmpty()){
            Toast.makeText(this,"Enter Bird Location",Toast.LENGTH_SHORT).show()
        }else{
            uploadBirdEntry()
        }
    }

    private fun uploadBirdEntry() {
        progressDialog.setMessage("Uploading Bird Entry")
        progressDialog.show()
        Log.d(TAG,"uploadToDatabase: Uploading to database ")
        val timestamp = System.currentTimeMillis()
        var userID = FirebaseAuth.getInstance().currentUser?.uid
        val hashMap: HashMap<String,Any> = HashMap()
        hashMap["uid"]= "1"
        hashMap["id"]= "$timestamp"
        hashMap["Name"]= Name
        hashMap["Description"]= Description
        hashMap["categoryId"]= selectedCategoryID
        hashMap["Date"]= Date
        hashMap["Location"]= Location
        hashMap["TimeImage"]= "$ImageUri"

        val reference = FirebaseDatabase.getInstance().getReference("Bird")
        reference.child("$timestamp").setValue(hashMap).addOnSuccessListener {
            Log.d(TAG,"uploadToDatabase: Uploaded to database ")
            Toast.makeText(this, "Uploaded",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {N->
            Log.d(TAG,"uploadToDatabase: Uploading to database failed ${N.message} ")
            Toast.makeText(this,"Failed to upload ${N.message}",Toast.LENGTH_SHORT).show()
        }

    }
    private fun loadCategories(){
        Log.d(TAG,"loadCategories : Loading Specie categories")
        var userID = FirebaseAuth.getInstance().currentUser?.uid
        specieArrayList = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("Species")
        reference.orderByChild("uid").equalTo("1").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                specieArrayList.clear()
                for (N in snapshot.children){
                    val model = N.getValue(BirdSpecieCategoryModel::class.java)
                    specieArrayList.add(model!!)
                    Log.d(TAG,"onDataChange: ${model.specie}")
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private var selectedCategoryID = ""
    private var selectedCategoryTitle = ""
    private fun categoryPick(){
        Log.d(TAG,"categoryPick: Showing Species category")
        progressDialog.show()
        val categoryArray = arrayOfNulls<String>(specieArrayList.size)
        for (N in specieArrayList.indices){
            categoryArray[N] = specieArrayList[N].specie
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick a Specie").setItems(categoryArray){ dialog, which ->
            selectedCategoryTitle = specieArrayList[which].specie
            selectedCategoryID = specieArrayList[which].id
            binding.etCategory.text = selectedCategoryTitle
            Log.d(TAG,"categoryPick: Selected Category ID: $selectedCategoryID")
            Log.d(TAG,"categoryPick: Selected Category Title $selectedCategoryTitle")
        }.show()
        progressDialog.dismiss()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)

    }
}