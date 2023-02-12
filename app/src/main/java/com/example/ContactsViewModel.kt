package com.example.contacts

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.icu.text.StringSearch
import android.media.tv.TvContract.Channels.CONTENT_URI
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contacts.data.ContactsSrc
import com.example.contacts.pojo.Contact
import com.example.contacts.pojo.ContactDetails
import kotlinx.coroutines.launch

class ContactsViewModel(private val contactsSrc: ContactsSrc,private val context: Context):ViewModel() {
private val _contacts= MutableLiveData<List<Contact>>()
val contacts:LiveData<List<Contact>>get()=_contacts
    init {
        viewModelScope.launch {
            val contacts=contactsSrc.getContacts()
            _contacts.value=contacts
        }
    }
    fun getContactById(contactId:String?):ContactDetails?{
      return  contactsSrc.getContactById(contactId)
    }
    fun setSearch(search:MutableState<String>){

        val filteredContacts=contacts.value?.filter { it.name.contains(search.value)||it.number.contains(search.value) }
        if (search.value.isNotEmpty()){
            _contacts.value=filteredContacts!!

        }else{
            viewModelScope.launch {
                val contacts=contactsSrc.getContacts()
                _contacts.value=contacts
            }
        }
    }
    fun insertContact(fname:MutableState<String>,lname:MutableState<String>,email:MutableState<String>,number:MutableState<String>,photo:MutableState<Uri?>){

        contactsSrc.saveContacts(fname,lname,number,email,photo)
    }

}