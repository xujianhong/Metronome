package com.daomingedu.metronome.widget

import android.app.Application
import android.content.Intent
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import com.daomingedu.metronome.MainActivity
import com.daomingedu.metronome.R
import com.yhao.floatwindow.FloatWindow
import com.yhao.floatwindow.MoveType
import com.yhao.floatwindow.PermissionListener
import com.yhao.floatwindow.ViewStateListener


/**
 * @创建者 chendong
 * @创建时间 2019/2/25 10:09
 * @描述
 */
class Utils {
    companion object {
        private val TAG = "Utils"

        lateinit var app: Application


        fun showFloatWindow() {
//            FloatWindow.get()?.show()
            val floatWindow = FloatWindow.get()
            if (floatWindow == null) {
                val imageView = ImageView(app).apply {
                    setImageResource(R.drawable.metronome_play_icon)
                }
                imageView.onClick {
                    val intent = Intent(app, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    Utils.app.startActivity(intent)
                }
                FloatWindow.with(Utils.app)
                    .setView(imageView)
                    .setX(MyDevice.sWidth - 100.px)
                    .setY(MyDevice.sHeight - 180.px)
                    .setDesktopShow(false)
                    .setMoveType(MoveType.slide)
                    .setMoveStyle(500, AccelerateInterpolator())  //贴边动画时长为500ms，加速插值器
                    .setViewStateListener(object : ViewStateListener {
                        override fun onBackToDesktop() {
                            Log.d(TAG, "onBackToDesktop")
                        }

                        override fun onMoveAnimStart() {
                            Log.d(TAG, "onMoveAnimStart")
                        }

                        override fun onMoveAnimEnd() {
                            Log.d(TAG, "onMoveAnimEnd")
                        }

                        override fun onPositionUpdate(p0: Int, p1: Int) {
                            Log.d(TAG, "onPositionUpdate,p0=$p0,p1=$p1")
                        }

                        override fun onDismiss() {
                            Log.d(TAG, "onDismiss")
                        }

                        override fun onShow() {
                            Log.d(TAG, "onShow")
                        }

                        override fun onHide() {
                            Log.d(TAG, "onHide")
                        }
                    }).setPermissionListener(object : PermissionListener {
                        override fun onSuccess() {
                            Log.d(TAG, "onSuccess")
                        }

                        override fun onFail() {
                            Log.d(TAG, "onFail")
                        }
                    }).build()
            } else {
                floatWindow.show()
            }
        }

        fun hideFloatWindow() {
            FloatWindow.get()?.hide()
        }
    }
}