package com.example.birdview

class SpecieViewModel {
    var uid:String =""
    var id:String =""
    var Name : String =""
    var Description:String =""
    var categoryId : String =""
    var Date : String =""
    var Location : String =""
    var TimeImage = ""

    constructor()
    constructor(
        uid: String,
        id: String,
        Name: String,
        Description: String,
        categoryId: String,
        Date: String,
        Location: String,
        TimeImage: String,
    ) {
        this.uid = uid
        this.id = id
        this.Name = Name
        this.Description = Description
        this.categoryId = categoryId
        this.Date = Date
        this.Location = Location
        this.TimeImage = TimeImage
    }
}