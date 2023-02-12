package com.example.contacts

import android.content.Context
import android.content.pm.PackageManager

import android.os.Bundle
import android.util.Log

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.*

import androidx.compose.material.*

import androidx.compose.runtime.*
 import androidx.compose.ui.Modifier

import androidx.core.content.ContextCompat
 import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compose.AppTheme

import com.example.contacts.data.ContactsSrc
import com.example.contacts.navGraph.Screens
import com.example.contacts.pojo.Contact
import java.util.jar.Manifest


class MainActivity : ComponentActivity() {

    val contactsSrc= ContactsSrc(this)
    lateinit var contactsViewModel: ContactsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme() {
                val navController= rememberNavController()
                val contacts = remember {
                    mutableListOf<Contact>()

                }


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) {
                        if ( it    ) {
                        setContent {
                            contactsViewModel= ViewModelProvider(this,
                                ContactsViewModelFactory(contactsSrc,this))
                                .get(ContactsViewModel::class.java)
                            contactsViewModel.contacts.observe(this, Observer {data->
                                contacts.clear()
                                contacts+=data
                            })
                            NavGraph(contacts ,contactsViewModel,navController)
                        }
                    } else {
Toast.makeText(this,"Permission denied",Toast.LENGTH_LONG).show()
                    }

                }
                if(permissionResult(context = this)){
                    contactsViewModel= ViewModelProvider(this,
                        ContactsViewModelFactory(contactsSrc,this))
                        .get(ContactsViewModel::class.java)
                    contactsViewModel.contacts.observe(this, Observer {data->
                        contacts.clear()
                        contacts+=data
                    })
                    NavGraph(contacts, contactsViewModel,navController)
                }else{
                    SideEffect {
                        permissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)


                    }
                }

            }
            }

        }
    }
}
@Composable
fun NavGraph(contacts: List<Contact>,  contactsViewModel: ContactsViewModel,navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route)
    {
        composable(route = Screens.Home.route){
            Home(contacts = contacts, contactsViewModel,navController)

        }
        composable(route = Screens.AddContact.route){
            AddContact(navController = navController,contactsViewModel)
        }
        composable(route = Screens.ContactDetails.route+"/{contactId}", arguments = listOf(
            navArgument("contactId"){
                type= NavType.StringType
                defaultValue="not found"
            }
        )){

            ContactDetails(navController = navController,contactsViewModel,it.arguments?.getString("contactId"))
        }
    }
}
@Composable
fun permissionResult(context: Context): Boolean {
    val  permissionResult= ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.READ_CONTACTS
    )
    return permissionResult==PackageManager.PERMISSION_GRANTED
}

