package com.teletap.activity

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import be.appfoundry.nfclibrary.utilities.sync.NfcReadUtilityImpl
import com.teletap.BaseActivity
import com.teletap.R
import com.teletap.databinding.ActivityNfcBinding
import kotlinx.android.synthetic.main.toolbar_blue_layout.view.*

class NfcActivity : BaseActivity() {

    lateinit var binding : ActivityNfcBinding
    private var amount : String = "0.0"

    private var pendingIntent: PendingIntent? = null
    private lateinit var mIntentFilters: Array<IntentFilter>
    private lateinit var mTechLists: Array<Array<String>>
    private var mNfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nfc)
        if (intent!=null)
            amount = intent.getStringExtra("amount").toString()

        binding.toolBarAC.title.text = "Payment"
        binding.toolBarAC.imgBack.setOnClickListener{onBackPressed()}

        binding.tvAmount.text = "Amount to be paid AED$amount"

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        mIntentFilters = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED))
        mTechLists = arrayOf(arrayOf(Ndef::class.java.name), arrayOf(NdefFormatable::class.java.name))

        binding.btnCheckNfc.setOnClickListener {
            if (mNfcAdapter == null) {
                // This device doesn't support NFC.
                Toast.makeText(this@NfcActivity, "This device doesn't support NFC", Toast.LENGTH_SHORT).show()
            } else if (mNfcAdapter != null){

                if (!mNfcAdapter!!.isEnabled) {
                    // NFC is Disabled
                    Toast.makeText(this@NfcActivity, "NFC is Disabled", Toast.LENGTH_SHORT).show()
                } else {
                    // NFC is enabled
                    Toast.makeText(this@NfcActivity, "NFC is enabled", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mNfcAdapter != null) {
            mNfcAdapter!!.enableForegroundDispatch(this, pendingIntent, mIntentFilters, mTechLists)
        }
    }

    override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) {
            mNfcAdapter!!.disableForegroundDispatch(this)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val res = NfcReadUtilityImpl().readFromTagWithSparseArray(intent)
        for (i in 0 until res.size()) {
            Toast.makeText(this, res.valueAt(i), Toast.LENGTH_SHORT).show()
        }
    }
}