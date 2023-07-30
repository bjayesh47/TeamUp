package `in`.walnutlabs.teamup.firebase

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import `in`.walnutlabs.teamup.activities.BaseActivity
import `in`.walnutlabs.teamup.activities.MainActivity
import `in`.walnutlabs.teamup.activities.ProfileActivity
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

    fun updateUserProfileData(
        activity: ProfileActivity,
        userHashMap: HashMap<String, Any>
    ) {
        fireStoreInstance
            .collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "PROFILE DATA UPDATED")
                Toast.makeText(
                    activity,
                    "PROFILE UPDATED SUCCESSFULLY!",
                    Toast.LENGTH_SHORT
                ).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    activity,
                    exception.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun loadUser(activity: BaseActivity) {
        fireStoreInstance.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { it ->
                val loggedInUser: User? = it.toObject(User::class.java)
                loggedInUser?.let {
                    when (activity) {
                        is SignInActivity -> activity.signInSuccess(loggedInUser)
                        is MainActivity -> activity.updateUserDetails(loggedInUser)
                        is ProfileActivity -> activity.loadUser(loggedInUser)
                    }
                }
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
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