package com.daomingedu.metronome

import android.app.Application
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.daomingedu.metronome.widget.MyDevice
import com.daomingedu.metronome.widget.Utils

/**
 * Description
 * Created by jianhongxu on 2021/10/18
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.app = this

        displayMetrics(this)
    }

    /**
     * 获取手机屏幕的宽高
     *
     * @param ctx
     */
    fun displayMetrics(ctx: Context) {
        val manager = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        manager.defaultDisplay.getMetrics(dm)
        val w = dm.widthPixels
        val h = dm.heightPixels
        MyDevice.sDensity = dm.density
        MyDevice.sScaledDensity = dm.scaledDensity
        MyDevice.sWidth = w
        MyDevice.sHeight = h
        if (w > h) {
            MyDevice.sWidth = h
            MyDevice.sHeight = w
        }

        Log.e(this.toString(),"宽："+MyDevice.sWidth+" 高："+MyDevice.sHeight+" 密度："+MyDevice.sDensity)
    }
}