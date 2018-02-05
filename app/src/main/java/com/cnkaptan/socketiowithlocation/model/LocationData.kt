package com.cnkaptan.socketiowithlocation.model

data class LocationData(val latitude: Double, val longitude: Double,val status_car: String = "READY"){
    override fun toString(): String {
        return "{ \"latitude\": $latitude,\n" +
                "\"longitude\": $longitude,\n" +
                "\"status_car\":$status_car,\n" +
                "}"
    }
}