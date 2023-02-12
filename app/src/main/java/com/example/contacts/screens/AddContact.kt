package com.example.contacts

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.tv.TvContract.Channels.CONTENT_URI
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Photo.PHOTO
import android.provider.ContactsContract.Contacts.Photo.PHOTO
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
 import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
 import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
  import androidx.navigation.NavController
import com.example.contacts.navGraph.Screens
import kotlin.math.ln

@Composable
fun AddContact(navController: NavController,contactsViewModel: ContactsViewModel){
    var  image: ImageBitmap = ImageBitmap.imageResource(R.drawable.user)

    val fname = remember{mutableStateOf("")}
    val lname = remember{mutableStateOf("")}
    val number = remember{mutableStateOf("")}
    val email = remember{mutableStateOf("")}
    val photo = remember{mutableStateOf<Uri?>(null)}
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if ( it    ) {
            contactsViewModel.insertContact(fname, lname,email,number,photo)

        }
         else {

        }}
    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp)) {

       photo.value= ChoosePic();

        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(value = fname.value,
        onValueChange = {it->fname.value=it
    },
            label = { Text(text = "First name") },
        modifier = Modifier.fillMaxWidth() ,
        colors = TextFieldDefaults.textFieldColors(
        focusedIndicatorColor = MaterialTheme.colors.primary,
        unfocusedIndicatorColor = Color.Gray,
        focusedLabelColor = MaterialTheme.colors.primary, backgroundColor = Color.White))

Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(value = lname.value,
            onValueChange = {it->lname.value=it
            },
            label = { Text(text = "Last name") },
            modifier = Modifier.fillMaxWidth() ,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = MaterialTheme.colors.primary, backgroundColor = Color.White))
        Spacer(modifier = Modifier.height(5.dp))


        OutlinedTextField(value = number.value,
            onValueChange = {it->number.value=it
            },
            label = { Text(text = "Phone number") },
            modifier = Modifier.fillMaxWidth() ,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = MaterialTheme.colors.primary, backgroundColor = Color.White))
        Spacer(modifier = Modifier.height(10.dp))


        OutlinedTextField(value = email.value,
            onValueChange = {it->email.value=it
            },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth() ,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = MaterialTheme.colors.primary, backgroundColor = Color.White))
        Spacer(modifier = Modifier.height(10.dp))

        val isGranted = permissionResultWrite(LocalContext.current)

Button(onClick = {
    if (isGranted) {
        contactsViewModel.insertContact(fname, lname,email,number,photo)

    }else{
        permissionLauncher.launch(Manifest.permission.WRITE_CONTACTS)
    }
        navController.navigate(Screens.Home.route)
                 }
    ,Modifier.align(Alignment.CenterHorizontally)) {
    Text(text = "Add Contact", color = Color.White, fontWeight = FontWeight.Bold)
}

    }
}

@Composable
fun SaveContact() {

}

@Composable
fun ChoosePic() :Uri{
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val defaultImg:ImageBitmap=ImageBitmap.imageResource(R.drawable.contact)
    val context = LocalContext.current
    val bitmap =  remember {
        mutableStateOf<Bitmap?>(defaultImg.asAndroidBitmap())
    }

    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }
    Column(Modifier.fillMaxWidth()) {


        Spacer(modifier = Modifier.height(12.dp))

        imageUri?.let {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap.value = MediaStore.Images
                    .Media.getBitmap(context.contentResolver,it)

            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver,it)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }

        }
        bitmap.value?.let {
            Image( bitmap= it.asImageBitmap() , contentDescription ="contact photo",
                Modifier
                    .width(150.dp)
                    .height(150.dp)
                    .clickable {
                        launcher.launch("image/*")
                    }
                    .clip(shape = CircleShape).align(Alignment.CenterHorizontally),)

        }
    }
    return if (imageUri!=null){
        imageUri!!

    }else{
        Uri.parse(defaultImg.toString())
    }
}
@Composable
fun permissionResultWrite(context: Context): Boolean {

    val  permissionResult= ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_CONTACTS
    )
    return permissionResult== PackageManager.PERMISSION_GRANTED
}