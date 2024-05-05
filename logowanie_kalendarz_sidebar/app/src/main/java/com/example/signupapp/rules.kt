package com.example.signupapp
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class rules : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        val dropdown1 = findViewById<RelativeLayout>(R.id.dropdown1)
        val content1 = findViewById<TextView>(R.id.content1)
        dropdown1.setOnClickListener {
            toggleContentVisibility(content1)
        }
        val dropdown2 = findViewById<RelativeLayout>(R.id.dropdown2)
        val content2 = findViewById<TextView>(R.id.content2)
        dropdown2.setOnClickListener {
            toggleContentVisibility(content2)
        }
        val dropdown3 = findViewById<RelativeLayout>(R.id.dropdown3)
        val content3 = findViewById<TextView>(R.id.content3)
        dropdown3.setOnClickListener {
            toggleContentVisibility(content3)
        }
        val dropdown4 = findViewById<RelativeLayout>(R.id.dropdown4)
        val content4 = findViewById<TextView>(R.id.content4)
        dropdown4.setOnClickListener {
            toggleContentVisibility(content4)
        }

    }

    private fun toggleContentVisibility(content: TextView) {
        if (content.visibility == View.GONE) {
            content.visibility = View.VISIBLE
        } else {
            content.visibility = View.GONE
        }
    }
}
