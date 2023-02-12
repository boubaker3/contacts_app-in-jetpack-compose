package com.example.contacts.data

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.runtime.MutableState
import com.example.contacts.pojo.Contact
import com.example.contacts.pojo.ContactDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class ContactsSrc(private val context: Context){
 @SuppressLint("Range")
 suspend   fun getContacts( ):List<Contact>{

            val contacts= mutableListOf<Contact>()
            val contentResolver:ContentResolver=context.contentResolver
            val cursor=contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                ,null
                ,null
                ,null
                ,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            if(cursor!=null&&cursor.count>0){
                while (cursor.moveToNext()) {
                    val id=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    val number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val photo=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))

                    contacts.add(Contact(id,number,name,photo))
                }
                cursor.close()
            }
         return  contacts


    }
    @SuppressLint("Range")
    fun getContactById(contactId: String?): ContactDetails? {
        val contentResolver: ContentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId ),
            null
        )

        var contact: ContactDetails? = null
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            val number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val photo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
            val email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))

            contact = ContactDetails(contactId,number, name, photo,email)
            cursor.close()
        }
        return contact
    }

    fun saveContacts(fname: MutableState<String>, lname: MutableState<String>, number: MutableState<String>,
                     email: MutableState<String>, photo: MutableState<Uri?>){

        val values = ContentValues().apply {
            put(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.android.contacts")
            put(ContactsContract.RawContacts.ACCOUNT_NAME, "${fname}@gmail.com")
        }

        val rawContactUri = context.contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, values)

        val contactId: Long = ContentUris.parseId(rawContactUri!!)



        val inputStream: InputStream? = context.contentResolver.openInputStream(photo.value!!)

        if (inputStream != null) {
            val photoBytes = inputStream.readBytes()

            val values = ContentValues().apply {
                put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                put(ContactsContract.CommonDataKinds.Photo.PHOTO, photoBytes)
            }

            context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)

            inputStream.close()
        }

        val phoneValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, number.value)
            put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
        }

        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)

        val emailValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.Email.ADDRESS, email.value)
            put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
        }

        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, emailValues)

        val nameValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, contactId)

            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "${fname.value} ${lname.value}")
         }

        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, nameValues)


    }

}