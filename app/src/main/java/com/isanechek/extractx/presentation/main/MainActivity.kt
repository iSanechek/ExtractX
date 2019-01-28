package com.isanechek.extractx.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.isanechek.extractx.presentation._id
import com.isanechek.extractx.presentation._layout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_layout.activity_main)

        val controller = findNavController(_id.main_host_fragment)
        main_bottom_navigation.setupWithNavController(controller)
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(_id.main_host_fragment).navigateUp()
}
