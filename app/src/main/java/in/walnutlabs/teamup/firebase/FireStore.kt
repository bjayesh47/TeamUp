package `in`.walnutlabs.teamup.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import `in`.walnutlabs.teamup.activities.SignInActivity
import `in`.walnutlabs.teamup.activities.SignUpActivity
import `in`.walnutlabs.teamup.models.User
import `in`.walnutlabs.teamup.utils.Constants

class FireStore {
    private val fireStoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        fireStoreInstance.collection(Constants.USERS)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.registerUserSuccess()
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName, "ERROR WRITING IN FIRESTORE")
            }
    }

    fun signInUser(activity: SignInActivity) {
        fireStoreInstance.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { it ->
                val loggedInUser: User? = it.toObject(User::class.java)
                loggedInUser?.let { activity.signInSuccess(loggedInUser) }
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName, "ERROR RETRIEVING FROM FIRESTORE")
            }
    }

    fun getCurrentUserID(): String {
        val currentUser: FirebaseUser? = Firebase.auth.currentUser
        var currentUserID: String = ""
        currentUser?.let {
            currentUserID = it.uid
        }
        return currentUserID
    }
}