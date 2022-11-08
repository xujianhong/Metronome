package com.daomingedu.metronome.widget

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SoundUtil {
    companion object {
        private var disposable: Disposable? = null
        private var soundListener: SoundListener? = null

        fun start(speed: Long) {
            stop()
            disposable = Observable.interval(speed, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    soundListener?.countdown()
                }
        }

        fun stop() {
            disposable?.isDisposed?.let {
                if (!it) {
                    disposable?.dispose()
                    disposable = null
                }
            }
        }

        fun setSoundListener(soundListener: SoundListener) {
            this.soundListener = null
            this.soundListener = soundListener
        }
    }

    interface SoundListener {
        fun countdown()
    }
}