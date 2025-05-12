package com.leo.okoaleo.navigation

//package com.leo.okoleo.navigation

object Routes {
    val Login = Route("login")
    val Register = Route("register")
    val Home = Route("home")
    val Profile = Route("profile")
    val Rewards = Route("rewards")
    val Search = Route("search")
    val Settings = Route("settings")
    val Scan = Route("scan")

    data class Route(val route: String)
}
