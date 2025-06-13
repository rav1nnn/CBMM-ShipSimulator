package com.cbmm.shipsimulator.ui.navigation

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object Fleet : Screen("fleet")
    object Ports : Screen("ports")
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
    object History : Screen("history")
    
    companion object {
        const val SHIP_ID_KEY = "shipId"
        const val PORT_ID_KEY = "portId"
        
        fun getRouteWithArgs(route: String, vararg args: Pair<String, String>): String {
            return buildString {
                append(route)
                args.takeIf { it.isNotEmpty() }?.let {
                    append("?")
                    it.forEachIndexed { index, (key, value) ->
                        if (index > 0) append("&")
                        append("$key=$value")
                    }
                }
            }
        }
    }
}
