package kr.co.baress.octavecheck

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_load.*
import kr.co.baress.octavecheck.utility.FrequencyDB
import kr.co.baress.octavecheck.utility.FrequencyModel
import java.lang.Exception

class LoadActivity : AppCompatActivity() {
    private lateinit var frequencyDb: FrequencyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        try {
            frequencyDb = FrequencyDB.getInstance(this)!!
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkVerify()
            }
        } catch (ex: Exception) {
            Log.d("OctaveCheck", ex.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK)
                animLogo()
            else
                finish()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun checkVerify() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            startActivityForResult(Intent(this, PermissionActivity::class.java), 1)
        } else {
            animLogo()
        }
    }

    private fun animLogo() {
        txtLoadAppName.visibility = View.VISIBLE
        val alphaAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    DataCheck().execute()
                }
            })
        }
        txtLoadAppName.startAnimation(alphaAnim)
    }

    inner class DataCheck : AsyncTask<Void, Double, Unit>() {
        override fun doInBackground(vararg params: Void) {
            try {
                frequencyDb.FrequencyDao().deleteAll()
                var count = frequencyDb.FrequencyDao().getCount()
                if (count == 0) {
                    var i = 0
                    var octave = 0
                    var codes = this@LoadActivity.resources.getStringArray(R.array.code)
                    var scale = this@LoadActivity.resources.getStringArray(R.array.scale)
                    var items = this@LoadActivity.resources.getStringArray(R.array.frequency)
                    for (data in items) {
                        if (i == codes.size) {
                            i = 0
                            octave++
                        }
                        var model = FrequencyModel(
                            "${codes.get(i)}${octave + 2}",
                            "${octave}옥타브 ${scale.get(i)}",
                            data.toDouble()
                        )
                        frequencyDb.FrequencyDao().insert(model)
                        i++
                    }
                }
            } catch (t: Throwable) {
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            frequencyDb.destroyInstance()
            startActivity(Intent(this@LoadActivity, MainActivity::class.java))
            finish()
        }
    }
}