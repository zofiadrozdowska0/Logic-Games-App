package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.signupapp.R
import com.example.signupapp.succes

class login : AppCompatActivity() {

    private lateinit var loginbtn: Button
    private lateinit var edituser: EditText
    private lateinit var editpword: EditText
    private lateinit var dbh: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginbtn = findViewById(R.id.button4)
        edituser = findViewById(R.id.editTextText)
        editpword = findViewById(R.id.editTextTextPassword)
        dbh = DBHelper(this)

        loginbtn.setOnClickListener{

            val useredtx = edituser.text.toString()
            val passedtx = editpword.text.toString()

            if (useredtx.isEmpty() || passedtx.isEmpty()) {
                Toast.makeText(this, "Add Username & Password", Toast.LENGTH_SHORT).show()
            } else {
                val checkuser = dbh.checkuserpass(useredtx, passedtx)
                if (checkuser) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, succes::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,"Wrong Username & Password",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}