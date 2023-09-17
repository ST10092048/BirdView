package com.example.birdview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.birdview.databinding.BirdRowBinding

class SpecieViewAdapter:RecyclerView.Adapter<SpecieViewAdapter.HoldPDF> {
    private var context: Context
    public var speciesArraylist: ArrayList<SpecieViewModel>

    private lateinit var binding: BirdRowBinding


    constructor(context: Context, speciesArraylist: ArrayList<SpecieViewModel>) : super() {
        this.context = context
        this.speciesArraylist = speciesArraylist

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldPDF {
        binding = BirdRowBinding.inflate(LayoutInflater.from(context),parent ,false)
        return HoldPDF(binding.root)
    }

    override fun getItemCount(): Int {
        return speciesArraylist.size
    }


    override fun onBindViewHolder(holder: HoldPDF, position: Int) {
        val  model = speciesArraylist[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val Name = model.Name
        val Description = model.Description
        val Date = model.Date

        val Location = model.Location
        val TimeImage = model.TimeImage

        holder.birdNameTV.text = Name
        holder.birdDescriptionTV.text = Description
        holder.birdDateTV.text = Date
        holder.TimesheetLocationTV.text = Location

    }

    inner  class HoldPDF(itemView: View):RecyclerView.ViewHolder(itemView){
        val birdNameTV : TextView = binding.BirdName
        val birdDescriptionTV = binding.BirdDescription
        val birdCategoryTV = binding.BirdCategory
        val birdDateTV = binding.BirdDate
        val birdImageTV = binding.BirdImage
        val TimesheetLocationTV = binding.BirdLocation
    }
}