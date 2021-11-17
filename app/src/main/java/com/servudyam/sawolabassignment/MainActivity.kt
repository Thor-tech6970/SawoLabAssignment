package com.servudyam.sawolabassignment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.sawolabs.androidsdk.Sawo


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener{

            Sawo(
                    this,
                    "4ee3e884-9b3e-4fe6-823f-3a63ecb27b25", // your api key,
                    "61937840d77fb4b4c6f7824faZaERQdVeGXWdXNZIQ53bFQj" // your secret key
            ).login(
                    "email", // can be one of 'email' or 'phone_number_sms'
                    CallbackActivity::class.java.name // Callback class name
            )

        }


    }
}