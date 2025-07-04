package com.example.coffee_application.manager

import com.example.coffee_application.model.Profile
import com.google.firebase.firestore.FirebaseFirestore

class PhoneProfileManager {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("profiles")

    /**
     * Kiểm tra số điện thoại đã tồn tại chưa
     */
    fun isPhoneTaken(phoneNumber: String, onResult: (Boolean) -> Unit) {
        collection.document(phoneNumber).get()
            .addOnSuccessListener { document ->
                onResult(document.exists())
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    /**
     * Lấy thông tin người dùng theo số điện thoại
     */
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

    /**
     * Lưu (hoặc cập nhật) thông tin người dùng
     */
    fun saveProfile(profile: Profile, onResult: (Boolean) -> Unit) {
        collection.document(profile.phoneNumber).set(profile)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    /**
     * Xoá người dùng theo số điện thoại
     */
    fun deletePhoneProfile(phoneNumber: String, onResult: (Boolean) -> Unit) {
        collection.document(phoneNumber).delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    /**
     * Lấy số điện thoại từ email (nếu cần tìm ngược lại)
     */
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
