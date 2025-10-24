package com.example.speakright.ui.theme

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.speakright.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.concurrent.thread

class LevelDetailActivity : AppCompatActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var tvSpeechResult: TextView
    private lateinit var tvCorrected: TextView
    private lateinit var tvTips: TextView
    private lateinit var tvFluency: TextView
    private lateinit var btnMic: ImageButton
    private lateinit var btnNext: Button

    private lateinit var allQuestions: JSONObject
    private var currentLevel = "simple"
    private var currentQuestionIndex = 0
    private var currentQuestions: List<String> = emptyList()

    private var recorder: MediaRecorder? = null
    private var audioFilePath: String? = null
    private var isRecording = false

    private val flaskUrl = "http://192.168.0.143:5000/analyze" // Flask server
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_detail)

        tvQuestion = findViewById(R.id.tvQuestion)
        tvSpeechResult = findViewById(R.id.tvSpeechResult)
        tvCorrected = findViewById(R.id.tvCorrected)
        tvTips = findViewById(R.id.tvTips)
        tvFluency = findViewById(R.id.tvFluency)
        btnMic = findViewById(R.id.btnMic)
        btnNext = findViewById(R.id.btnNext)

        loadAllQuestions()
        val levelFromIntent = intent.getStringExtra("LEVEL_NAME")?.lowercase() ?: "simple"
        setLevel(levelFromIntent)

        // Request permission to record audio
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)

        btnMic.setOnClickListener {
            if (!isRecording) startRecording() else stopRecordingAndSend()
        }

        btnNext.setOnClickListener { nextQuestion() }
    }

    private fun loadAllQuestions() {
        val inputStream = assets.open("questions.json")
        val json = inputStream.bufferedReader().use { it.readText() }
        allQuestions = JSONObject(json)
    }

    private fun setLevel(level: String) {
        currentLevel = level
        val jsonArray = allQuestions.optJSONArray(level)
        currentQuestions = List(jsonArray.length()) { i -> jsonArray.getString(i) }
        currentQuestionIndex = 0
        tvQuestion.text = currentQuestions[currentQuestionIndex]
        clearResults()
    }

    private fun nextQuestion() {
        currentQuestionIndex++
        if (currentQuestionIndex < currentQuestions.size) {
            tvQuestion.text = currentQuestions[currentQuestionIndex]
            clearResults()
        } else {
            Toast.makeText(this, "🎉 You finished all $currentLevel questions!", Toast.LENGTH_LONG).show()
        }
    }

    private fun clearResults() {
        tvSpeechResult.text = ""
        tvCorrected.text = ""
        tvTips.text = ""
        tvFluency.text = ""
    }

    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
            return
        }

        val dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val audioFile = File(dir, "speech_${System.currentTimeMillis()}.wav")
        audioFilePath = audioFile.absolutePath

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            setOutputFile(audioFilePath)
            try {
                prepare()
                start()
                isRecording = true
                tvSpeechResult.text = "🎙 Recording... Tap again to stop."
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@LevelDetailActivity, "Failed to start recording", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopRecordingAndSend() {
        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false
            tvSpeechResult.text = "✅ Recording saved!"
            audioFilePath?.let { filePath ->
                val file = File(filePath)
                if (file.exists()) sendAudioToFlask(file)
                else Toast.makeText(this, "❌ Audio file not found!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendAudioToFlask(file: File) {
        thread {
            try {
                val client = OkHttpClient()
                val fileBody = file.asRequestBody("audio/wav".toMediaTypeOrNull())
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("audio", file.name, fileBody)
                    .addFormDataPart("expected_text", currentQuestions[currentQuestionIndex])
                    .build()

                val request = Request.Builder()
                    .url(flaskUrl)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        tvSpeechResult.text = "🗣 You said: ${jsonResponse.optString("transcript")}"
                        tvCorrected.text = "✅ Corrected: ${jsonResponse.optString("corrected_sentence")}"
                        tvTips.text = "💡 Tips: ${jsonResponse.optString("tips")}"
                        tvFluency.text = "🗣 Fluency Score: ${jsonResponse.optDouble("fluency_score", 0.0)}"
                    } else {
                        Toast.makeText(this, "❌ Server error", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "⚠️ Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
