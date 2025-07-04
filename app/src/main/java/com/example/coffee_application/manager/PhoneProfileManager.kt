package com.example.coffee_application.manager

import com.example.coffee_application.model.Profile
import com.google.firebase.firestore.FirebaseFirestore

class PhoneProfileManager {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("profiles")


    fun isPhoneTaken(phoneNumber: String, onResult: (Boolean) -> Unit) {
        collection.document(phoneNumber).get()
            .addOnSuccessListener { document ->
                onResult(document.exists())
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getProfileByPhone(phoneNumber: String, onResult: (Profile?) -> Unit) {
        collection.document(phoneNumber).get()
            .addOnSuccessListener { document ->
                val profile = document.toObject(Profile::class.java)
                onResult(profile)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun saveProfile(profile: Profile, onResult: (Boolean) -> Unit) {
        collection.document(profile.phoneNumber).set(profile)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }


    fun deletePhoneProfile(phoneNumber: String, onResult: (Boolean) -> Unit) {
        collection.document(phoneNumber).delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }


    fun getPhoneByEmail(email: String, onResult: (String?) -> Unit) {
        collection.whereEqualTo("email", email).get()
            .addOnSuccessListener { result ->
                val document = result.documents.firstOrNull()
                onResult(document?.id)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}
