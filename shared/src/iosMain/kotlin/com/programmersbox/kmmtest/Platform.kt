package com.programmersbox.kmmtest

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import platform.UIKit.UIDevice
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.main.defaultUIKitMain
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

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

class UIKitApplier(
    root: UIView
) : AbstractApplier<UIView>(root) {

    override fun insertTopDown(index: Int, instance: UIView) {
        println("insertTopDown(index = $index, instance = $instance)")

        val subView: UIView = instance

        subView.translatesAutoresizingMaskIntoConstraints = false

        val current = this.current
        if (current is UIStackView) {
            current.insertArrangedSubview(subView, index.toULong())
        } else {
            current.insertSubview(subView, index.toLong())
        }
    }

    override fun insertBottomUp(index: Int, instance: UIView) = Unit

    override fun remove(index: Int, count: Int) {
        println("remove(index = $index, count = $count)")

        val current = current
        val subviews = if (current is UIStackView) {
            current.arrangedSubviews
        } else {
            current.subviews
        }
        subviews.subList(index, index + count)
            .filterIsInstance<UIView>()
            .forEach {
                println("i got $it to remove")
                it.removeFromSuperview()
            }
    }

    override fun move(from: Int, to: Int, count: Int) {
        println("move(from = $from, to = $to, count = $count)")
    }

    override fun onClear() {
        println("onClear()")
        root.subviews
            .filterIsInstance<UIView>()
            .forEach { it.removeFromSuperview() }
    }
}

@ExportObjCClass
class ComposeViewController : UIViewController {
    @OverrideInit
    constructor() : super(nibName = null, bundle = null)

    @OverrideInit
    constructor(coder: NSCoder) : super(coder)

    private val job = Job()
    private val dispatcher = Dispatchers.Main
    private val frameClock = BroadcastFrameClock(onNewAwaiters = { })
    private val coroutineScope = CoroutineScope(job + dispatcher + frameClock)
    private val renderCoroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var recomposer: Recomposer
    private lateinit var composition: Composition

    private lateinit var content: @Composable () -> Unit

    override fun viewDidLoad() {
        super.viewDidLoad()

        // useful links
        // https://github.com/JakeWharton/mosaic/blob/4cb027c2074d86b3389cbfb5da35468fe7591178/mosaic/mosaic-runtime/src/main/kotlin/com/jakewharton/mosaic/mosaic.kt
        // https://github.com/JetBrains/androidx/blob/3ebf1d446089cc8da34bb1b906982327af7b8249/compose/ui/ui/src/skikoMain/kotlin/androidx/compose/ui/ComposeScene.skiko.kt

        recomposer = Recomposer(coroutineScope.coroutineContext)
        composition = Composition(UIKitApplier(this.view), recomposer)

        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            recomposer.runRecomposeAndApplyChanges()
        }

        composition.setContent(content)

        renderCoroutineScope.launch {
            while (isActive) {
                frameClock.sendFrame(0L) // Frame time value is not used by Compose runtime.
                delay(50)
            }
        }

        Snapshot.registerGlobalWriteObserver {
            coroutineScope.launch {
                Snapshot.sendApplyNotifications()
            }
        }
    }

    override fun viewDidUnload() {
        super.viewDidUnload()

        renderCoroutineScope.cancel()
        composition.dispose()
        recomposer.cancel()
        job.cancel()
    }

    fun setContent(content: @Composable () -> Unit) {
        this.content = content
    }
}

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