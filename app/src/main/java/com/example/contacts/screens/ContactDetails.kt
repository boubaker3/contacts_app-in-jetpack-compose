package com.example.contacts

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController


@Composable
fun ContactDetails( navController: NavHostController,contactsViewModel: ContactsViewModel,contactId:String?){
    val contact=contactsViewModel.getContactById(contactId)!!
    Column(Modifier.fillMaxSize()) {
Card(
    Modifier
        .fillMaxWidth()
        .height(300.dp)  , elevation = 0.dp) {

    setImage(photoUrl = contact.photoUri!!, context = LocalContext.current , screen ="details" )
  Box(modifier = Modifier

      .width(60.dp)
      .height(45.dp)){
      Icon(modifier= Modifier
          .width(60.dp)
          .height(45.dp)
          .clickable {
              navController.popBackStack()
          },
          imageVector =  Icons.Filled.ArrowBack ,
          contentDescription = "back icon",
          tint = Color.White
      )
  }
        Box(modifier = Modifier.fillMaxSize(), Alignment.BottomEnd){

        Text(
            modifier = Modifier.padding(10.dp),
            text = contact.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
        )
    }

}

        Spacer(modifier = Modifier.height(10.dp))
        ContactInfo(contact.name,Icons.Outlined.AccountCircle,false, LocalContext.current)
        Spacer(modifier = Modifier.height(10.dp))
        ContactInfo(contact.number,Icons.Outlined.Phone,true, LocalContext.current)
        Spacer(modifier = Modifier.height(10.dp))
        ContactInfo(contact.email,Icons.Outlined.Email,false, LocalContext.current)

    }
}
@Composable
fun ContactInfo(contact:String,vector: ImageVector,call:Boolean,context: Context){
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if ( it    ) {

            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$contact")
            context.startActivity(intent)
        } else {
            Toast.makeText(context,"Permission denied", Toast.LENGTH_LONG).show()
        }

    }
    val isGranted = permissionResultCall(context = context)

    Card(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp)
        , elevation = 5.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    if (isGranted) {

                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = Uri.parse("tel:$contact")
                        context.startActivity(intent)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CALL_PHONE)

                    }
                }) {
            Box(modifier = Modifier

                .width(45.dp)
                .height(45.dp)){
                Icon(modifier=Modifier.padding(5.dp),
                    imageVector = vector ,
                    contentDescription = "back icon",
                    tint = MaterialTheme.colors.primary
                )
            }
            Text(text = contact,Modifier.padding(horizontal = 16.dp, vertical = 10.dp))

        }
    }

}
@Composable
fun permissionResultCall(context: Context): Boolean {

  val  permissionResult= ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CALL_PHONE
    )
    return permissionResult==PackageManager.PERMISSION_GRANTED
}
