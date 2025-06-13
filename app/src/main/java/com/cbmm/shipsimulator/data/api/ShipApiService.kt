package com.cbmm.shipsimulator.data.api

import com.cbmm.shipsimulator.data.model.Ship
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ShipApiService {
    @GET("ships")
    suspend fun getAllShips(): Response<List<Ship>>
    
    @GET("ships/{id}")
    suspend fun getShipById(@Path("id") id: String): Response<Ship>
    
    @GET("ships/{id}/route")
    suspend fun getShipRoute(@Path("id") id: String): Response<List<Location>>
}
