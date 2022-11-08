package com.daomingedu.metronome

import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.daomingedu.metronome.widget.*
import com.yhao.floatwindow.FloatWindow
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        private var beatCount2 = 8//一小节多少拍
        private var speed = 160//每分钟多少拍
        private var beatCount = 4//拍子数量
        private var playing = false//是否正在播放
        private val soundPool by lazy { SoundPool(5, AudioManager.STREAM_MUSIC, 0) }
        private var soundIdA = 0//第一节拍子音效
        private var soundIdB = 0//第一节之后的拍子音效
        private var pp = 1//记录当前拍的是当前小节的第几拍

        //        private var disposable: Disposable? = null
        private var playDisposable: Disposable? = null
        private var realWidth = 0;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSound()

        tvSpeed.text = "$speed"
        arcSpeedProgress.progress = speed
        tvTips.text = "$beatCount/$beatCount2"
        val temp = beatCount - 4
        if (temp > 0) {
            for (i in 1..temp) {
                addBeat()
            }
        } else if (temp < 0) {
            for (i in -1 downTo temp) {
                reduceBeat()
            }
        }


        if (pp > 1 && playing) {
            val childView = llBeatContainer.getChildAt(0) as? ImageView
            childView?.setImageResource(R.drawable.beat_white_icon1)
        }

        SoundUtil.setSoundListener(object : SoundUtil.SoundListener {
            override fun countdown() {
                if (beatCount == 1) {
                    playBeatSoundA()
                    return
                }
                if (pp < beatCount) {//如果还没满一小节则将当前节拍数PP加1
                    playBeatSoundB(pp++)
                } else { //如果满了一小节
                    playBeatSoundA()
                    pp = 1
                }
            }
        })

        arcSpeedProgress.mProgressListener = object : ArcProgress.ProgressListener {
            override fun changeProgress(progress: Int) {
                speed = progress
                tvSpeed.text = "$speed"
                playDisposable = playDisposable?.let {
                    if (!it.isDisposed) {
                        it.dispose()
                    }
                    null
                }
                playDisposable = Observable.timer(200, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        playMetronome()
                    }
            }
        }

        ivLargeReduce.onClick {
            if (speed == 1) {
                return@onClick
            }
            speed -= 1
            tvSpeed.text = "$speed"
            arcSpeedProgress.progress = speed
            playMetronome()
        }

        ivLargeAdd.onClick {
            if (speed == 360) {
                return@onClick
            }
            speed += 1
            tvSpeed.text = "$speed"
            arcSpeedProgress.progress = speed
            playMetronome()
        }
        ivReduceBeat.onClick {
            reduceBeat()
            playMetronome()
        }
        ivAddBeat.onClick {
            addBeat()
            playMetronome()
        }
        ivReduceBeat2.onClick {
            if (beatCount2 == 1) {
                return@onClick
            }
            beatCount2 /= 2
            tvTips.text = "$beatCount/$beatCount2"
            playMetronome()
        }
        ivAddBeat2.onClick {
            if (beatCount2 == 16) {
                return@onClick
            }
            beatCount2 *= 2
            tvTips.text = "$beatCount/$beatCount2"
            playMetronome()
        }
        FloatWindow.get()?.apply {
            if (!isShowing) {
                ivPlayOrPause.setImageResource(R.drawable.metronome_pause_icon)
            } else {
                ivPlayOrPause.setImageResource(R.drawable.metronome_play_icon)
            }
        }

        ivPlayOrPause.onClick {
            playing = !playing
            if (playing) {
                ivPlayOrPause.setImageResource(R.drawable.metronome_pause_icon)
                playMetronome()
            } else {
                ivPlayOrPause.setImageResource(R.drawable.metronome_play_icon)
//                pauseMetronome()
                SoundUtil.stop()
            }
        }
    }

    /**
     * 开始节拍
     */
    private fun playMetronome() {
        if (playing) {
//            pauseMetronome()
            val pSpeed = (60 * 1000.0f / speed * (4 * 1f / beatCount2)).toLong()
            playBeatSoundA(true)
            pp = 1
            SoundUtil.start(pSpeed)
            /*disposable = Observable.interval(pSpeed, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (beatCount == 1) {
                        playBeatSoundA()
                        return@subscribe
                    }
                    if (pp < beatCount) {//如果还没满一小节则将当前节拍数PP加1
                        playBeatSoundB(pp++)
                    } else { //如果满了一小节
                        playBeatSoundA()
                        pp = 1
                    }
                }*/
        }
    }

    /**
     * flag:true更新除第一个外所有子view图片，false更新上一个子view图片
     */
    private fun playBeatSoundA(flag: Boolean = false) {
        soundPool.play(soundIdA, 1f, 1f, 0, 0, 1f)
        val childView = llBeatContainer.getChildAt(0) as? ImageView
        childView?.setImageResource(R.drawable.beat_blue_icon1)
        if (flag) {
            for (i in 1 until llBeatContainer.childCount) {
                val lastChildView = llBeatContainer.getChildAt(i) as? ImageView
                lastChildView?.setImageResource(R.drawable.beat_white_icon2)
            }
        } else {
            if (beatCount > 1) {
                val lastChildView = llBeatContainer.getChildAt(beatCount - 1) as? ImageView
                lastChildView?.setImageResource(R.drawable.beat_white_icon2)
            }
        }
    }

    private fun playBeatSoundB(position: Int) {
        soundPool.play(soundIdB, 1f, 1f, 0, 0, 1f)
        val childView = llBeatContainer.getChildAt(position) as? ImageView
        childView?.setImageResource(R.drawable.beat_blue_icon2)
        if (position > 0) {
            val lastPos = position - 1
            val lastChildView = llBeatContainer.getChildAt(lastPos) as? ImageView
            if (lastPos > 0) {
                lastChildView?.setImageResource(R.drawable.beat_white_icon2)
            } else {
                lastChildView?.setImageResource(R.drawable.beat_white_icon1)
            }
        }
    }

    private fun initSound() {
        soundIdA = soundPool.load(this, R.raw.tone1_1, 1)
        soundIdB = soundPool.load(this, R.raw.tone1, 1)
    }

    /**
     * 增加拍子
     */
    private fun addBeat() {
        if (llBeatContainer.childCount >= 16) {

            return
        }
        val imageView = ImageView(this).apply {
            setImageResource(R.drawable.beat_white_icon2)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(5.px, 0, 5.px, 0)
            }
        }

        llBeatContainer.addView(imageView)
        updateLlBeatChildWidth()
    }

    var flag = 0

    /**
     * 更新所有节拍宽度
     */
    private fun updateLlBeatChildWidth() {
        beatCount = llBeatContainer.childCount
        val screenWidth = MyDevice.sWidth
        Log.e(this.toString(), "节拍宽度：" + llBeatContainer.getChildAt(0).width.toString())
        Log.e(this.toString(), "上一次宽度：$realWidth")
        val width =
            if (realWidth != 0) realWidth else llBeatContainer.getChildAt(0).width
        realWidth = width
        val allBeatWidth = beatCount * (10.px + width)
        tvTips.text = "$beatCount/$beatCount2"
        if (allBeatWidth > screenWidth || (flag in 1..beatCount)) {
            if (flag == 0) {
                flag = beatCount
            }
            val beatWidth = (screenWidth - beatCount * 10.px) / beatCount
            val beatHeight = (beatWidth / (86 * 1.0f / 103)).toInt()
            for (i in 0 until beatCount) {
                val childView = llBeatContainer.getChildAt(i)
                val lp = childView.layoutParams
                lp.width = beatWidth
                lp.height = beatHeight
                childView.layoutParams = lp
            }
        } else {
            val beatWidth = LinearLayout.LayoutParams.WRAP_CONTENT
            val beatHeight = LinearLayout.LayoutParams.WRAP_CONTENT
            for (i in 0 until beatCount) {
                val childView = llBeatContainer.getChildAt(i)
                val lp = childView.layoutParams
                lp.width = beatWidth
                lp.height = beatHeight
                childView.layoutParams = lp
            }
        }
    }

    /**
     * 减少拍子
     */
    private fun reduceBeat() {
        if (llBeatContainer.childCount > 1) {
            llBeatContainer.removeViewAt(llBeatContainer.childCount - 1)
            updateLlBeatChildWidth()
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.hideFloatWindow()


    }

    override fun onPause() {
        super.onPause()
        if (playing) {
            Utils.showFloatWindow()
        } else {
            FloatWindow.destroy()

        }
    }
}