package com.example.speakright.ui.theme

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.*
import kotlin.concurrent.thread

class PronunciationPracticeActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val REQUEST_CODE_SPEECH = 100
    private lateinit var tvQuestion: TextView
    private lateinit var tvMeaning: TextView
    private lateinit var fabMic: FloatingActionButton
    private lateinit var btnNext: Button
    private lateinit var btnSearch: Button
    private lateinit var etSearch: EditText
    private lateinit var btnLearned: Button
    private lateinit var tts: TextToSpeech

    private var wordsList = mutableListOf<String>()
    private var currentIndex = 0
    private var ttsReady = false
    private var currentWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pronunciation_practice)

        tvQuestion = findViewById(R.id.tvQuestion)
        tvMeaning = findViewById(R.id.tvMeaning)
        fabMic = findViewById(R.id.fabMic)
        btnNext = findViewById(R.id.btnNext)
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)

        tts = TextToSpeech(this, this)

        // Randomize daily word list
        wordsList = mutableListOf(
            "Meticulous", "Ebullient", "Cacophony", "Euphoria", "Ephemeral",
            "Obfuscate", "Quintessential", "Serendipity", "Ubiquitous", "Resilient",
            "Benevolent", "Ambiguous", "Conundrum", "Loquacious", "Magnanimous",
            "Tenacious", "Ostentatious", "Auspicious", "Camaraderie", "Eloquent"
        ).shuffled().toMutableList()

        showWord()

        fabMic.setOnClickListener {
            if (ttsReady) {
                speakWord(currentWord)
                fabMic.postDelayed({ startSpeechRecognition() }, 3000)
            } else Toast.makeText(this, "TTS not ready yet", Toast.LENGTH_SHORT).show()
        }

        btnNext.setOnClickListener {
            if (currentIndex < wordsList.size - 1) {
                currentIndex++
                showWord()
                btnNext.isEnabled = false
            } else {
                Toast.makeText(this, "🎉 You completed today's session!", Toast.LENGTH_LONG).show()
            }
        }

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                fetchMeaning(query)
                currentWord = query
                tvQuestion.text = query.capitalize(Locale.ROOT)
                speakWord(currentWord)
                fabMic.postDelayed({ startSpeechRecognition() }, 3000)
                btnNext.isEnabled = false
            } else {
                Toast.makeText(this, "Please enter a word!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showWord() {
        currentWord = wordsList[currentIndex]
        tvQuestion.text = currentWord
        tvMeaning.text = ""
        btnNext.isEnabled = false
        fetchMeaning(currentWord)
    }

    private fun speakWord(word: String) {
        tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Now you try pronouncing it...")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH && resultCode == Activity.RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val userAnswer = matches[0].lowercase(Locale.getDefault())
                val correct = currentWord.lowercase(Locale.getDefault())

                if (userAnswer == correct) {
                    Toast.makeText(this, "✅ Perfect pronunciation!", Toast.LENGTH_SHORT).show()
                    btnNext.isEnabled = true
                    saveLearnedWord(currentWord)
                    tts.speak("Excellent pronunciation!", TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    Toast.makeText(this, "❌ Try Again", Toast.LENGTH_SHORT).show()
                    tts.speak("No! This was incorrect. It’s pronounced like this:", TextToSpeech.QUEUE_FLUSH, null, null)
                    tts.speak(correct, TextToSpeech.QUEUE_ADD, null, null)
                }
            }
        }
    }

    private fun fetchMeaning(word: String) {
        thread {
            try {
                val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val body = response.body?.string()

                val meaning = if (body != null && body.startsWith("[")) {
                    val jsonArray = JSONArray(body)
                    val meanings = jsonArray.getJSONObject(0)
                        .getJSONArray("meanings")
                        .getJSONObject(0)
                        .getJSONArray("definitions")
                        .getJSONObject(0)
                        .getString("definition")
                    meanings
                } else "Meaning not found."

                runOnUiThread {
                    tvMeaning.text = "Meaning: $meaning"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    tvMeaning.text = "Meaning not found."
                }
            }
        }
    }

    private fun saveLearnedWord(word: String) {
        val prefs = getSharedPreferences("LearnedWords", Context.MODE_PRIVATE)
        val set = prefs.getStringSet("words", mutableSetOf()) ?: mutableSetOf()
        set.add(word)
        prefs.edit().putStringSet("words", set).apply()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
}
