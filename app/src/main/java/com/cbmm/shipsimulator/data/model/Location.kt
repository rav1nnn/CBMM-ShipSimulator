package com.cbmm.shipsimulator.data.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("portId")
    val portId: String? = null,
    @SerializedName("portName")
    val portName: String? = null
) {
    companion object {
        private val gson = Gson()

        fun fromString(json: String): Location {
            return try {
                gson.fromJson(json, Location::class.java) ?: Location(0.0, 0.0)
            } catch (e: Exception) {
                Location(0.0, 0.0)
            }
        }
    }

    override fun toString(): String {
        return gson.toJson(this)
    }
} 