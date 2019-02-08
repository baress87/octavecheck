package kr.co.baress.octavecheck

import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_permission.*
import android.Manifest.permission.*
import android.app.Activity
import android.content.pm.PackageManager

class PermissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        btn_permission.setOnClickListener {
            ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                var DENIED = false
                for (result in grantResults) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        DENIED = true
                        break
                    }
                }

                if (DENIED) {
                    setResult(Activity.RESULT_CANCELED)
                } else {
                    setResult(Activity.RESULT_OK)
                }
                finish()
            }
        }
    }
}