package com.teletap.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.teletap.AppPreference
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.Session
import com.teletap.model.SignupModel
import com.teletap.presenterClasses.SignupPresenter
import com.teletap.utilities.LanguagePreference
import com.teletap.utilities.Utility
import com.teletap.view.ISignUpView
import kotlinx.android.synthetic.main.sign_up_activity.*
import java.util.HashMap

class LoginOptions : BaseActivity(),  ISignUpView, GoogleApiClient.OnConnectionFailedListener {

    var googlefName:String=""
    var googlelName:String=""
    var googleemail:String=""
    var googleId:String=""
    var idToken= ""
    private var googleApiClient: GoogleApiClient? = null
    private val RC_SIGN_IN = 1

    lateinit var  presnter: SignupPresenter
    private var deviceToken : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_options_activity)
        presnter = SignupPresenter()
        presnter.view = this
        getFcmToken()

        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result != null) {
                handleSignInResult(result)
            }
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account = result.signInAccount
            idToken = account!!.idToken.toString()
//            name = account.displayName
//            email = account.email
            Log.e("all", "" + account.id + account.givenName)
            // you can store user data to SharedPreference
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            googlefName= account.givenName.toString();
            googlelName= account.familyName.toString();
            googleemail= account.email.toString();
            googleId= account.id.toString();
            Log.e("dsfdsf", "" + googleId + googlefName)
            callGoogleApi(googleemail, googlefName, googleId, googlelName)
//            account.photoUrl?.let {
//            }
        }
        else {
            Toast.makeText(applicationContext, "Sign in cancel", Toast.LENGTH_LONG).show()
        }
    }

    fun onclick(view: View) {
        when(view.id){

            R.id.emailLogin ->{
                startActivity(Intent(this@LoginOptions, Login::class.java))
                finish()
            }
            R.id.googleLogin ->{
                val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
                startActivityForResult(intent, RC_SIGN_IN)
            }
        }
    }

    private fun callGoogleApi(email: Any, givenName: Any, id: Any, familyName: Any) {
        if (Utility.hasConnection(this@LoginOptions)) {

            val map: MutableMap<String, Any> = HashMap()
            map["email"] = "" + email
            //map["country_code"] = countryCode
            //map["mobile"] = binding.edMobileNo.text.toString().trim()
            //map["password"] = binding.edPassword.text.toString().trim()
            map["google_token"] = "" + id
            map["first_name"] = "" + givenName
            map["last_name"] = "" + familyName
            map["device_type"] = "" + "1"
            map["device_token"] = "" + deviceToken
            map["language"] =""+ LanguagePreference(this@LoginOptions).languageType(this@LoginOptions)
            presnter.googleLogin(this@LoginOptions, map)
        } else {
            Utility.showToast(this@LoginOptions, getString(R.string.no_network_message))
        }
    }



    override fun onGoogleLogin(body: SignupModel?) {
        val status = body!!.status
        if (status==1) {
            Toast.makeText(this@LoginOptions, body.message, Toast.LENGTH_SHORT).show()
            if(body.data?.mobile == null){
                val intent = Intent(this, SignUp::class.java)
                intent.putExtra("cameFrom", "googleLogin")
                intent.putExtra("googleId", googleId)
                intent.putExtra("firstName", googlefName)
                intent.putExtra("lastName", googlelName)
                intent.putExtra("email", googleemail)
                startActivity(intent)

            } else if(body.data?.sms_verified_status == 0 && body.data?.business_setup_status == 0){
                val intent = Intent(this, OtpVerification::class.java)
                intent.putExtra("cameFrom", "googleLogin")
                body.data?.let { intent.putExtra("user_id", it.user_id) }
                intent.putExtra("isNumberVerified", false)
                startActivity(intent)

            }else if (body.data?.sms_verified_status == 1 && body.data?.business_setup_status == 0) {
                val intent = Intent(this, AddBusiness::class.java)
                AppPreference.saveUserInfo(applicationContext, body.data!!)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finishAffinity()
            }else if (body.data?.sms_verified_status == 1 && body.data?.business_setup_status == 1){
                val intent = Intent(this, HomeActivity::class.java)
                Session(this@LoginOptions).createLoginSession()
                AppPreference.saveUserInfo(applicationContext, body.data!!)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finishAffinity()
            }
            //val intent = Intent(this@OtpVerification, HomeActivity::class.java)
            //startActivity(intent)
        }
    }

    override fun onSuccess(body: SignupModel?) {

    }

    override fun getContext(): Context {
        return this
    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("token", token.toString())
            deviceToken = token.toString()

        })
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


}