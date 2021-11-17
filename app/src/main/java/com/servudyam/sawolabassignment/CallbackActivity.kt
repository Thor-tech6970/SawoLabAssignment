package com.servudyam.sawolabassignment

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sawolabs.androidsdk.LOGIN_SUCCESS_MESSAGE
import org.json.JSONException
import org.json.JSONObject
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


class CallbackActivity : FragmentActivity() , OnMapReadyCallback {

    var lat = ""
    var lon = ""
    lateinit var ipAddressTextView : TextView
    lateinit var locationTextView : TextView
    lateinit var timeZoneTextView : TextView
    lateinit var ISPTextView : TextView
    val base_url = "https://geo.ipify.org/api/v2/country,city,vpn?apiKey=at_PU4ZEJHi6uTii4GHLu9uNWUIN7YwQ&ipAddress="

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_callback)

        val message = intent.getStringExtra(LOGIN_SUCCESS_MESSAGE)

        val IPInputEditText = findViewById<EditText>(R.id.IPInputEditText)
        val goButton = findViewById<ImageView>(R.id.goButton)
        ipAddressTextView = findViewById<TextView>(R.id.ipAddressTextView)
        locationTextView = findViewById<TextView>(R.id.locationTextView)
        timeZoneTextView = findViewById<TextView>(R.id.timeZoneTextView)
        ISPTextView= findViewById<TextView>(R.id.ISPTextView)


        val myIp = getLocalIpAddress()
        val url = base_url + myIp
        search(url)

        goButton.setOnClickListener {

            if (IPInputEditText.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter IP address", Toast.LENGTH_SHORT).show()
            } else {

                val url = base_url + IPInputEditText.text.toString()
                search(url)

            }
        }


    }

    override fun onMapReady(p0: GoogleMap) {
        val map = p0

        map.clear()
        val location = LatLng(lat.toDouble(), lon.toDouble())
        map.addMarker(MarkerOptions().position(location).title("Maharashtra").icon(BitmapFromVector(applicationContext , R.drawable.locationarrow)))
        map.moveCamera(CameraUpdateFactory.newLatLng(location))

    }

    fun getLocalIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.getInetAddresses()
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress() && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }

    fun search(url: String){

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Fetching details...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val stringRequest: StringRequest = object : StringRequest(Method.GET, url,
                Response.Listener { response ->
//                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                    Log.e("API RESPONSE: ", response)
                    progressDialog.dismiss()
                    try {
                        val jsonObject = JSONObject(response)

                        val ip = jsonObject.getString("ip");
                        ipAddressTextView.text = ip

                        val locationJSON = jsonObject.getJSONObject("location")
                        val country = locationJSON.getString("country")
                        val region = locationJSON.getString("region")
                        val postalCode = locationJSON.getString("postalCode")
                        lat = locationJSON.getString("lat")
                        lon = locationJSON.getString("lng")
                        val locationToShow = region + " , " + country + " " + postalCode
                        locationTextView.text = locationToShow

                        val timeZone = locationJSON.getString("timezone")
                        val timeZoneToShow = timeZone
                        timeZoneTextView.text = timeZoneToShow

                        val ispJSON = jsonObject.getString("isp")
                        ISPTextView.text = ispJSON

                        val mapFragment =
                                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                        mapFragment?.getMapAsync(this)

                        progressDialog.dismiss()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }) {}

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)

    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {

        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)

        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


}