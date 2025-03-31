package com.ehsanmsz.kiwanolog.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

/**
 * Created by Ehsan Msz on 29 Mar, 2025
 */

internal class KiwanoActivity : ComponentActivity() {

    private val viewModel by viewModels<KiwanoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

        }
    }
}