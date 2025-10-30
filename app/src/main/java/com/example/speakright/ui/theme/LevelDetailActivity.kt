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
import com.example.speakright.network.AnalysisResponse
import com.example.speakright.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

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

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_RECORD_AUDIO_PERMISSION
        )

        btnMic.setOnClickListener {
            if (!isRecording) startRecording() else stopRecordingAndAnalyze()
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
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
            return
        }

        val dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val audioFile = File(dir, "speech_${System.currentTimeMillis()}.m4a")
        audioFilePath = audioFile.absolutePath

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
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

    private fun stopRecordingAndAnalyze() {
        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false
            tvSpeechResult.text = "✅ Recording saved!"

            audioFilePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    analyzeSpeechWithRetrofit(file)
                } else {
                    Toast.makeText(this, "❌ Audio file not found!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun analyzeSpeechWithRetrofit(file: File) {
        val audioPart = MultipartBody.Part.createFormData(
            "audio", file.name, file.asRequestBody("audio/wav".toMediaTypeOrNull())
        )
        val textPart = RequestBody.create("text/plain".toMediaTypeOrNull(), currentQuestions[currentQuestionIndex])

        RetrofitClient.apiService.analyzeSpeech(audioPart, textPart)
            .enqueue(object : Callback<AnalysisResponse> {
                override fun onResponse(call: Call<AnalysisResponse>, response: Response<AnalysisResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()!!
                        tvSpeechResult.text = "🗣 You said: ${result.recognized_text}"
                        tvCorrected.text = "✅ Pronunciation: ${result.pronunciation_score}/100"
                        tvFluency.text = "💬 Fluency: ${result.fluency_score}/100\n🧠 Grammar: ${result.grammar_score}/100"
                        tvTips.text = "💡 Feedback: ${result.feedback}"
                    } else {
                        Toast.makeText(this@LevelDetailActivity, "❌ Server error", Toast.LENGTH_SHORT).show()
                        Log.e("AnalyzeError", "Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<AnalysisResponse>, t: Throwable) {
                    Toast.makeText(this@LevelDetailActivity, "⚠️ Network error: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("AnalyzeError", "Failure: ${t.message}")
                }
            })
    }

}
