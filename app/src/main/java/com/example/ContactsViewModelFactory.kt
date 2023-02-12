package com.example.contacts;


import android.content.Context;
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contacts.data.ContactsSrc


class ContactsViewModelFactory(private val contactsSrc: ContactsSrc,private val context:Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            return ContactsViewModel(contactsSrc,context  ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}