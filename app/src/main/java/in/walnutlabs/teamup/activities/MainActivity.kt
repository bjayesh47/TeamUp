package `in`.walnutlabs.teamup.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import `in`.walnutlabs.teamup.R
import `in`.walnutlabs.teamup.adapters.BoardsItemAdapter
import `in`.walnutlabs.teamup.firebase.FireStore
import `in`.walnutlabs.teamup.models.Board
import `in`.walnutlabs.teamup.models.User
import `in`.walnutlabs.teamup.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var username: String
    companion object {
        const val PROFILE_REQUEST_CODE: Int = 1
        const val CREATE_BOARD_REQUEST: Int = 2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
        FireStore().loadUser(this@MainActivity)

        findViewById<NavigationView>(R.id.nv).setNavigationItemSelectedListener(this)

        findViewById<FloatingActionButton>(R.id.fabCreateBoard).setOnClickListener {
            if (this::username.isInitialized) {
                val intent: Intent = Intent(this@MainActivity, CreateBoardActivity::class.java)
                intent.putExtra(Constants.NAME, username)
                startActivityForResult(intent, CREATE_BOARD_REQUEST)
            }
            else {
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.progress_dialog_wait),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    fun populateListToUI(list: ArrayList<Board>) {
        hideProgressDialog()
        if (list.size > 0) {
            findViewById<RecyclerView>(R.id.tvNoBoardsAvailable).visibility = View.GONE

            with (findViewById<RecyclerView>(R.id.rvBoardsList)) {
                visibility = View.VISIBLE
                layoutManager = LinearLayoutManager(this@MainActivity)
                setHasFixedSize(true)
                adapter = BoardsItemAdapter(this@MainActivity, list)
            }
        }
        else {
            findViewById<RecyclerView>(R.id.rvBoardsList).visibility = View.GONE
            findViewById<RecyclerView>(R.id.tvNoBoardsAvailable).visibility = View.VISIBLE
        }
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.tbMainActivity)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_list)
        toolbar.setNavigationOnClickListener { toggleDrawer() }
    }

    private fun toggleDrawer() {
        val drawer: DrawerLayout = findViewById<DrawerLayout>(R.id.dl)
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START)
        else
            drawer.openDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        val drawer: DrawerLayout = findViewById<DrawerLayout>(R.id.dl)
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START)
        else
            doubleBackToExit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && resultCode == PROFILE_REQUEST_CODE) {
            FireStore().loadUser(this@MainActivity)
        }
        else {
            Log.e("Cancelled", "Cancelled")
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuMyProfile -> {
                startActivityForResult(
                    Intent(this@MainActivity, ProfileActivity::class.java),
                    PROFILE_REQUEST_CODE)
            }
            R.id.menuSignOut -> {
                FirebaseAuth.getInstance().signOut()
                val intent: Intent = Intent(this@MainActivity, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        findViewById<DrawerLayout>(R.id.dl).closeDrawer(GravityCompat.START)
        return true
    }

    fun updateUserDetails(loggedInUser: User) {
        username = loggedInUser.name
        Glide
            .with(this@MainActivity)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById<CircleImageView>(R.id.civProfilePic))
        findViewById<TextView>(R.id.tvProfileName).text = loggedInUser.name
    }
}