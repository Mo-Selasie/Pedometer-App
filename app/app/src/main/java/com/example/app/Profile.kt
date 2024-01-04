package com.example.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class Profile : AppCompatActivity() {
    private lateinit var textViewPageName: TextView
    private lateinit var textViewName: TextView
    private lateinit var textViewAge: TextView
    private lateinit var textViewHeight: TextView
    private lateinit var textViewWeight: TextView
    private lateinit var textViewBMI: TextView
    private lateinit var textViewMinWeight: TextView
    private lateinit var textViewMaxWeight: TextView
    private lateinit var buttonSetGoal: Button
    private lateinit var buttonUpdateProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val currentUser = UserManager.getInstance().currentUser

        textViewPageName = findViewById(R.id.text_user_profile)
        textViewName = findViewById(R.id.user_name)
        textViewAge = findViewById(R.id.user_age)
        textViewHeight = findViewById(R.id.user_height)
        textViewWeight = findViewById(R.id.user_weight)
        textViewBMI = findViewById(R.id.user_bmi)
        textViewMinWeight = findViewById(R.id.user_minWeight)
        textViewMaxWeight = findViewById(R.id.user_maxWeight)
        buttonSetGoal = findViewById(R.id.buttonSetGoal)
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile)

        textViewName.text = currentUser.name
        textViewAge.text = currentUser.age.toString()
        textViewHeight.text = currentUser.height.toString()
        textViewWeight.text = currentUser.weight.toString()
        textViewBMI.text = currentUser.bmi.toString()
        textViewMinWeight.text = currentUser.calculateMinHealthyWeight().toString()
        textViewMaxWeight.text = currentUser.calculateMaxHealthyWeight().toString()


        //buttons initialization
        buttonSetGoal.setOnClickListener {
            val intent = Intent(this@Profile, Setting_goal::class.java)
            startActivity(intent)
        }

        buttonUpdateProfile.setOnClickListener {
            val intent = Intent(this@Profile, Update_profile::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        val currentUser = UserManager.getInstance().currentUser

        textViewName.text = "Name: %s".format(currentUser.name)
        textViewAge.text = "Age: %d y".format(currentUser.age)
        textViewHeight.text = "Height: %.02f cm".format(currentUser.height*100)
        textViewWeight.text = "Weight: %.02f kg".format(currentUser.weight)
        textViewBMI.text = "BMI: %.02f".format(currentUser.bmi)
        textViewMinWeight.text = "Min. Healthy Weight %.02f kg".format(currentUser.calculateMinHealthyWeight())
        textViewMaxWeight.text = "Max. Healthy Weight %.02f kg".format(currentUser.calculateMaxHealthyWeight())
    }
}