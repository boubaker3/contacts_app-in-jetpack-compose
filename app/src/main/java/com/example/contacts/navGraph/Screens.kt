package com.example.contacts.navGraph

sealed class Screens(val route:String){
    object Home:Screens("Home")
    object AddContact:Screens("AddContact")
    object ContactDetails:Screens("ContactDetails")
}
