package com.example.birdview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.birdview.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this@Settings, binding.drawerLayouts, 0, 0)
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)

    }
}