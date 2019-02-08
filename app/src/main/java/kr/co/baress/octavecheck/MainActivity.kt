package kr.co.baress.octavecheck

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*
import kr.co.baress.octavecheck.utility.FrequencyDB
import kr.co.baress.octavecheck.utility.FrequencyModel
import kr.co.baress.octavecheck.utility.TransformFFT
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var frequencyDb: FrequencyDB
    private lateinit var recordTask: RecordAudio

    private val sampleRate = 8000
    private val audioSource = MediaRecorder.AudioSource.MIC
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT

    private val buffer_size_in_ms = 3000
    private val chunk_size_in_samples = 4096
    private val chunk_size_in_ms = 1000 * chunk_size_in_samples / sampleRate
    private val buffer_size_in_bytes = sampleRate * buffer_size_in_ms / 1000 * 2
    private val chunk_size_in_bytes = sampleRate * chunk_size_in_ms / 1000 * 2

    private val min_frequency = 50
    private val max_frequency = 600
    private var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        try {
            frequencyDb = FrequencyDB.getInstance(this)!!
            startStopButton.setOnClickListener {
                if (started) {
                    started = false
                    codeTextView.text = ""
                    octaveTextView.text = ""
                    startStopButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_circle_outline_red_24dp))
                    recordTask.cancel(true)
                } else {
                    started = true
                    startStopButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause_circle_outline_red_24dp))
                    recordTask = RecordAudio()
                    recordTask.execute()
                }
            }
        } catch (ex: Exception) {
            Log.d("OctaveCheck", ex.message)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        frequencyDb.destroyInstance()
    }

    inner class RecordAudio : AsyncTask<Void, List<FrequencyModel>, Unit>() {
        override fun doInBackground(vararg params: Void?) {
            try {
                val audioRecord = AudioRecord(audioSource, sampleRate, channelConfig, audioEncoding, 6144)
                var audio_data = ShortArray(buffer_size_in_bytes / 2)
                var toTransform = DoubleArray(chunk_size_in_samples * 2)

                val min_frequency_fft = Math.round((min_frequency * chunk_size_in_samples / sampleRate).toFloat())
                val max_frequency_fft = Math.round((max_frequency * chunk_size_in_samples / sampleRate).toFloat())
                while (started) {
                    audioRecord.startRecording()
                    audioRecord.read(audio_data, 0, chunk_size_in_bytes / 2)
                    audioRecord.stop()
                    for (i in 0 until chunk_size_in_samples) {
                        toTransform[i * 2] = audio_data[i].toDouble()
                        toTransform[(i * 2) + 1] = 0.0
                    }

                    toTransform = TransformFFT(toTransform, chunk_size_in_samples).ResultFFT()
                    var best_frequency: Double = min_frequency_fft.toDouble()
                    var best_amplitude = 0.0
                    for (i in min_frequency_fft..max_frequency_fft) {
                        val current_frequency = i * 1.0 * sampleRate / chunk_size_in_samples
                        val current_amplitude =
                                Math.pow(toTransform[i * 2], 2.0) + Math.pow(toTransform[i * 2 + 1], 2.0)
                        val normalized_amplitude = current_amplitude * Math.pow((min_frequency * max_frequency).toDouble(), 0.5) / current_frequency;
                        if (normalized_amplitude > best_amplitude) {
                            best_frequency = current_frequency;
                            best_amplitude = normalized_amplitude;
                        }
                    }
                    var scaleArray = frequencyDb.FrequencyDao().getScale(best_frequency)
                    publishProgress(scaleArray)
                }
                audioRecord.stop()
            } catch (t: Throwable) {
                Log.d("OcvateCheck", t.message)
            }
        }

        override fun onProgressUpdate(vararg values: List<FrequencyModel>?) {
            try {
                for (data in values.get(0)!!.asIterable()) {
                    codeTextView.text = data.id
                    octaveTextView.text = data.scale
                }
            } catch (ex: Exception) {
                Log.d("OctaveCheck", ex.message)
            }
        }
    }
}
