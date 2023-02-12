package com.example.contacts

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.contacts.navGraph.Screens

import com.example.contacts.pojo.Contact

@Composable
fun Home(contacts: List<Contact>,  contactsViewModel: ContactsViewModel,navController: NavHostController){

    var searchFor= remember {
        mutableStateOf("")  }
    Column(
        Modifier
            .fillMaxSize()
            .padding(10.dp)  ) {

        OutlinedTextField(value = searchFor.value, onValueChange = {it->searchFor.value=it
            contactsViewModel.setSearch(searchFor)

        },
            label = { Text(text = "search for contacts...") },
            modifier = Modifier.fillMaxWidth() ,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colors.primary,
                unfocusedIndicatorColor = Color.Gray,
                focusedLabelColor = MaterialTheme.colors.primary, backgroundColor = Color.White))

        Text(text = "search for: "+searchFor.value)
        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(modifier = Modifier.fillMaxHeight()){

                items(items = contacts, itemContent ={ contact->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            navController.navigate(Screens.ContactDetails.route + "/${contact.id}")

                        }){

                        Row(modifier = Modifier.fillMaxWidth()) {

                            if (contact.photoUri != null) {
                                setImage(contact.photoUri, LocalContext.current,"home")
                            }else{
                                Card(modifier = Modifier
                                    .width(45.dp)
                                    .height(45.dp) , shape = RoundedCornerShape(8.dp), backgroundColor = MaterialTheme.colors.primary){
                                    Column( verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                                        Text(text = contact.name.substring(0,1),  fontSize = 16.sp, color = Color.White )

                                    }
                                }
                            }
                            Column( modifier = Modifier.padding(5.dp)) {
                                Text(text = contact.name,  )
                                Text(text = contact.number , color = Color.Gray)
                            }
                        }
                    }

                }

                )


            }
            Row(modifier = Modifier.align(Alignment.BottomEnd)){
                FloatingActionButton(onClick = { navController.navigate(Screens.AddContact.route) }, backgroundColor =MaterialTheme.colors.primary) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Button", tint = Color.White)
                }
            }

        }

    }
}


@Composable
fun setImage(photoUrl:String,context: Context,screen:String){
    var  image: ImageBitmap = ImageBitmap.imageResource(R.drawable.user)

    val imageBitmap= try{

        MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(photoUrl))

    }catch (e:Exception){
        null
    }
    if (imageBitmap!=null){
        image=imageBitmap.asImageBitmap()
    }
    if (screen=="home"){

    Card(shape = RoundedCornerShape(8.dp)) {

        Image(bitmap = image, contentDescription = "this is contact photo", modifier = Modifier
            .width(45.dp)
            .height(45.dp), contentScale = ContentScale.Crop)
    }
    }

else{
        Image(bitmap = image, contentDescription = "this is contact photo", modifier = Modifier
            .fillMaxWidth()
            .height(300.dp), contentScale = ContentScale.Crop)
    }

}