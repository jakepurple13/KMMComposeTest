package com.programmersbox.kmmtest

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import platform.UIKit.UIDevice
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.cinterop.ExportObjCClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import platform.Foundation.NSCoder
import platform.UIKit.UIViewController
import androidx.compose.ui.window.Application
import platform.UIKit.UIStackView
import platform.UIKit.UIView
import platform.UIKit.insertSubview
import platform.UIKit.removeFromSuperview
import platform.UIKit.subviews
import platform.UIKit.translatesAutoresizingMaskIntoConstraints
import androidx.compose.ui.main.defaultUIKitMain

fun main() {
    defaultUIKitMain("FallingBalls", Application("Falling Balls") {
        Column {
            GroupButton(
                1,
                options = listOf(
                    GroupButtonModel(1) { Icon(Icons.Default.Add, null) },
                    GroupButtonModel(2) { Icon(Icons.Default.Add, null) }
                )
            ) {

            }
        }
    })
}