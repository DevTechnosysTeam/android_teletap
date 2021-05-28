package com.teletap.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import be.appfoundry.nfclibrary.activities.NfcActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.Session
import com.teletap.dialogFragment.LogoutDialog
import com.teletap.fragment.EarningsFragment
import com.teletap.fragment.MyProfileFragment
import com.teletap.fragment.PayOutFragment
import com.teletap.fragment.PaymentFragment
import com.teletap.model.ProfileModel
import com.teletap.model.SignupModel
import com.teletap.model.SimpleModel
import com.teletap.presenterClasses.HomePresenter
import com.teletap.utilities.LanguagePreference
import com.teletap.utilities.Utility
import com.teletap.view.IHomeView
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class HomeActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, IHomeView {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    private lateinit var headingTitle: TextView
    private lateinit var switchCurrency : SwitchCompat
    private lateinit var currencyText:TextView

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    lateinit var presenterHome : HomePresenter
    lateinit var userInfo: SignupModel.DataBean

    private lateinit var imageViewNav : CircleImageView
    private lateinit var usernameNav : TextView
    private lateinit var emailNav : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        presenterHome = HomePresenter()
        presenterHome.view = this

         toolbar = findViewById(R.id.toolbar)
        headingTitle = toolbar.findViewById(R.id.headingTitle)
        switchCurrency = toolbar.findViewById(R.id.switchCurrency)
        currencyText = toolbar.findViewById(R.id.aed)

        setSupportActionBar(toolbar)
       drawerLayout = findViewById(R.id.drawer_layout)

        val actionBar: androidx.appcompat.app.ActionBar? = supportActionBar
        //actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_nav_icon) // This is the line where you set the drawable

        actionBar?.setDisplayShowTitleEnabled(false)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navigationView: View = navView.inflateHeaderView(R.layout.nav_header_main)
        imageViewNav = navigationView.findViewById(R.id.imageView)
        usernameNav = navigationView.findViewById(R.id.username)
        emailNav = navigationView.findViewById(R.id.email)
        navView.setNavigationItemSelectedListener(this)
        //val navController = findNavController(R.id.nav_host_fragment)

        val navBottomView : BottomNavigationView = findViewById(R.id.bottomNav)
        navBottomView.setOnNavigationItemSelectedListener(this)

        toolbar.background = ContextCompat.getDrawable(this, R.drawable.toolbar_img)
        headingTitle.text = "Home"
        switchCurrency.visibility = View.GONE
        currencyText.visibility = View.GONE
        val earningsFragment = EarningsFragment()
        loadFragment(earningsFragment)

        try {
            userInfo = AppPreference.getUserInfo(this@HomeActivity)
            Log.e("userInfo", userInfo.id.toString())
        }
        catch (e: NullPointerException){
            e.printStackTrace()
        }

        getUserProfileApi()


        switchCurrency.setOnCheckedChangeListener { compoundButton, b ->
            if (b)
                currencyText.text = "$"
            else
                currencyText.text = "AED"
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )*/
        //setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }*/

    private fun loadFragment(fragment: Fragment?) {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.earnings -> {
                toolbar.background = ContextCompat.getDrawable(this, R.drawable.toolbar_img)
                headingTitle.text = "Home"
                switchCurrency.visibility = View.GONE
                currencyText.visibility = View.GONE
                val earningsFragment = EarningsFragment()
                loadFragment(earningsFragment)
            }
            R.id.payment -> {
                toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                headingTitle.text = "Payment"
                switchCurrency.visibility = View.VISIBLE
                currencyText.visibility = View.VISIBLE
                val paymentFragment = PaymentFragment()
                loadFragment(paymentFragment)
            }
            R.id.payout -> {
                toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                headingTitle.text = "Payout"
                switchCurrency.visibility = View.GONE
                currencyText.visibility = View.GONE
                val payOutFragment = PayOutFragment()
                loadFragment(payOutFragment)
            }
            R.id.profile -> {
                toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                headingTitle.text = "My Profile"
                switchCurrency.visibility = View.GONE
                currencyText.visibility = View.GONE
                val myProfileFragment = MyProfileFragment()
                loadFragment(myProfileFragment)
            }
            R.id.businessProfile -> {
                val intent = Intent(this, BusinessProfile::class.java)
                startActivity(intent)
                /*val intent = Intent(this, AddBusiness::class.java)
                startActivity(intent)*/
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.contacts -> {
                val intent = Intent(this, ContactsListActivity::class.java)
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.contactUs -> {
                val intent = Intent(this, ContactUs::class.java)
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.privacy -> {
                val intent = Intent(this, PrivacyPolicy::class.java)
                intent.putExtra("title", "Privacy Policy")
                intent.putExtra("webUrl", "https://teletap.devtechnosys.info/privacy")
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.termsOfUse -> {
                val intent = Intent(this, PrivacyPolicy::class.java)
                intent.putExtra("title", "Terms of use")
                intent.putExtra("webUrl", "https://teletap.devtechnosys.info/terms")
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.aboutUs -> {
                val intent = Intent(this, PrivacyPolicy::class.java)
                intent.putExtra("title", "About Us")
                intent.putExtra("webUrl", "https://teletap.devtechnosys.info/about_us")
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.faq -> {
                /*val intent = Intent(this, Faq::class.java)
                startActivity(intent)*/
                val intent = Intent(this, PrivacyPolicy::class.java)
                intent.putExtra("title", "FAQ")
                intent.putExtra("webUrl", "https://teletap.devtechnosys.info/faq")
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.logOut -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                val logoutDialog: LogoutDialog = LogoutDialog()
                logoutDialog.setDataCompletionCallback(object : LogoutDialog.LogoutDialogInterface {
                    override fun onYesClick() {
                        logoutDialog.dismiss()
                        logoutApi()
                    }

                    override fun onNoClick() {
                        logoutDialog.dismiss()
                    }

                })
                logoutDialog.show(supportFragmentManager, "LogoutDialog")
            }
        }

        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun logoutApi(){
        if (userInfo.token!=null){
            if (Utility.hasConnection(this@HomeActivity)) {
                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""+userInfo.token!!
                map["language"] =""+ LanguagePreference(this@HomeActivity).languageType(this@HomeActivity)
                presenterHome.logoutApi(this@HomeActivity, map)
            }else {
                Utility.showToast(this@HomeActivity, getString(R.string.no_network_message))
            }
        }
    }

    override fun onLogoutSuccess(body: SimpleModel?) {
        val status = body!!.status
        if (status == 1) {
            Toast.makeText(this@HomeActivity, body.message, Toast.LENGTH_SHORT).show()
            Session(this@HomeActivity).logoutUser()
            val intent = Intent(this@HomeActivity, LoginOptions::class.java)
            startActivity(intent)
            finishAffinity()
        }else{
            Toast.makeText(this@HomeActivity, body.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserProfileApi(){
        if (userInfo.token!=null){
            if (Utility.hasConnection(this@HomeActivity)) {
                val map: MutableMap<String, Any> = HashMap()
                map["token"] = ""+userInfo.token!!
                map["language"] =""+ LanguagePreference(this@HomeActivity).languageType(this@HomeActivity)
                presenterHome.getUserProfile(this@HomeActivity, map)
            }else {
                Utility.showToast(this@HomeActivity, getString(R.string.no_network_message))
            }
        }
    }

    override fun onProfileSuccess(body: ProfileModel?) {
        val status = body!!.status
        if (status == 1) {
                usernameNav.text = body.data?.first_name + " "+ body.data?.last_name
                emailNav.text = body.data?.email
                Glide.with(this@HomeActivity).applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.default_user_profile))
                    .load(body.data?.profile_image).into(imageViewNav)
        }
    }

    override fun getContext(): Context {
        return this
    }

    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/
}