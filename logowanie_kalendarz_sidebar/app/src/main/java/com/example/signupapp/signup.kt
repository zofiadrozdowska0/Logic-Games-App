package com.example.signupapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class signup : AppCompatActivity() {

    private lateinit var uname: EditText
    private lateinit var pword: EditText
    private lateinit var cpword: EditText
    private lateinit var signupbtn: Button
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        uname = findViewById(R.id.editTextText3)
        pword = findViewById(R.id.editTextText4)
        cpword = findViewById(R.id.editTextText5)
        signupbtn = findViewById(R.id.button3)
        db = DBHelper(this)

        signupbtn.setOnClickListener {
            val unametext = uname.text.toString()
            val pwordtext = pword.text.toString()
            val cpwordtext = cpword.text.toString()

            if (TextUtils.isEmpty(unametext) || TextUtils.isEmpty(pwordtext) || TextUtils.isEmpty(cpwordtext)) {
                Toast.makeText(this, "Add Username, Password & Confirm Password", Toast.LENGTH_SHORT).show()
            } else {
                if (pwordtext == cpwordtext) {
                    if (db.checkUsernameExists(unametext)) {
                        Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        val savedata = db.insertUser(unametext, pwordtext)
                        if (savedata) {
                            Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, login::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Signup Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
