package `in`.walnutlabs.teamup.firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
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
    }

    private fun getCurrentUserID(): String {
        return Firebase.auth.currentUser!!.uid
    }
}