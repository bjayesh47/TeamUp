package `in`.walnutlabs.teamup.firebase

import android.util.Log
import android.widget.Toast
import androidx.browser.browseractions.BrowserActionsIntent.BrowserActionsItemId
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import `in`.walnutlabs.teamup.activities.BaseActivity
import `in`.walnutlabs.teamup.activities.CreateBoardActivity
import `in`.walnutlabs.teamup.activities.MainActivity
import `in`.walnutlabs.teamup.activities.ProfileActivity
import `in`.walnutlabs.teamup.activities.SignInActivity
import `in`.walnutlabs.teamup.activities.SignUpActivity
import `in`.walnutlabs.teamup.activities.TaskListActivity
import `in`.walnutlabs.teamup.models.Board
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

    fun registerBoard(activity: CreateBoardActivity, board: Board) {
        fireStoreInstance.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(
                    activity,
                    "Board Created Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    activity,
                    exception.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun getBoardList(activity: MainActivity) {
        fireStoreInstance
            .collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()

                for (i in document.documents) {
                    val board: Board = i.toObject(Board::class.java)!!
                    board.documentID = i.id
                    boardList.add(board)
                }

                activity.populateListToUI(boardList)
            }
            .addOnFailureListener {
                it.message?.let {
                    activity.showErrorSnackBar(it)
                }
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

    fun loadUser(activity: BaseActivity, readBoardList: Boolean = false) {
        fireStoreInstance.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { it ->
                val loggedInUser: User? = it.toObject(User::class.java)
                loggedInUser?.let {
                    when (activity) {
                        is SignInActivity -> activity.signInSuccess(loggedInUser)
                        is MainActivity -> activity.updateUserDetails(loggedInUser, readBoardList)
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

    fun getBoardDetails(activity: TaskListActivity, boardDocumentID: String) {
        fireStoreInstance.collection(Constants.BOARDS)
            .document(boardDocumentID)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                activity.boardDetails(document.toObject(Board::class.java)!!)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "ERROR RETRIEVING FROM FIRESTORE")
            }
    }
}