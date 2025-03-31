package com.ehsanmsz.kiwanolog.presentation.ui

import androidx.lifecycle.ViewModel
import com.ehsanmsz.kiwanolog.data.repository.KiwanoRepositoryProvider

/**
 * Created by Ehsan Msz on 29 Mar, 2025
 */

internal class KiwanoViewModel : ViewModel() {

    val logs = KiwanoRepositoryProvider.getOrNull()?.logs()
}