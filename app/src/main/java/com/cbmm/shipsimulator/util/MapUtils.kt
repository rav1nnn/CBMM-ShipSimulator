package com.cbmm.shipsimulator.util

import kotlin.math.*

object MapUtils {
    private const val EARTH_RADIUS = 6371000.0 // Raio da Terra em metros

    /**
     * Calcula a distância entre duas coordenadas geográficas usando a fórmula de Haversine
     * @return Distância em metros
     */
    fun calculateDistance(
        lat1: Double, 
        lon1: Double, 
        lat2: Double, 
        lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + 
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * 
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS * c
    }

    /**
     * Calcula o ângulo de direção (bearing) entre dois pontos geográficos
     * @return Ângulo em graus (0-360) do norte verdadeiro
     */
    fun calculateBearing(
        lat1: Double, 
        lon1: Double, 
        lat2: Double, 
        lon2: Double
    ): Float {
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val dLonRad = Math.toRadians(lon2 - lon1)

        val y = sin(dLonRad) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - 
                sin(lat1Rad) * cos(lat2Rad) * cos(dLonRad)
        
        var bearing = Math.toDegrees(atan2(y, x)).toFloat()
        return (bearing + 360) % 360 // Normaliza para 0-360
    }

    /**
     * Calcula um ponto intermediário entre duas coordenadas baseado em um fator de progresso
     * @param progress Valor entre 0.0 e 1.0 representando o progresso entre os pontos
     */
    fun interpolate(
        startLat: Double, 
        startLon: Double, 
        endLat: Double, 
        endLon: Double, 
        progress: Double
    ): Pair<Double, Double> {
        val lat = startLat + (endLat - startLat) * progress
        val lon = startLon + (endLon - startLon) * progress
        return lat to lon
    }
}
