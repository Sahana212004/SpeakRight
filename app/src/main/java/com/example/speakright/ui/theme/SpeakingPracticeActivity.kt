package com.example.speakright.ui.theme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.speakright.R
import java.util.*

class SpeakingPracticeActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var rvChat: RecyclerView
    private lateinit var btnSpeak: Button
    private lateinit var tvTimer: TextView
    private lateinit var tts: TextToSpeech
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    private val topics = mutableListOf(
        "Describe your favorite hobby",
        "Talk about a memorable trip",
        "Describe your dream job",
        "Discuss your favorite book or movie",
        "Explain a recent achievement",
        "Talk about a person who inspires you",
        "Describe a challenge you overcame",
        "Discuss your goals for the next 5 years",
        "Describe your hometown",
        "Talk about a skill you want to learn"
    )
    private var currentTopic: String = ""
    private val speechRequestCode = 100
    private var countDownTimer: CountDownTimer? = null
    private val speakingTimeMillis: Long = 60000 // 1 min

    private val fillerWords = listOf("um", "uh", "like", "you know", "so", "actually", "basically", "right")
    private val advancedWords = mapOf(
        "good" to "excellent", "bad" to "detrimental", "happy" to "elated",
        "sad" to "melancholy", "big" to "enormous", "small" to "minute",
        "important" to "crucial", "start" to "commence", "end" to "conclude",
        "help" to "assist"
    )

    private val topicKeywords = mapOf(
        "Describe your dream job" to listOf("career", "skills", "company", "role", "passion"),
        "Talk about a memorable trip" to listOf("travel", "experience", "location", "people", "adventure"),
        "Describe your favorite hobby" to listOf("activity", "interest", "enjoy", "time", "practice"),
        "Discuss your favorite book or movie" to listOf("story", "characters", "plot", "author", "message"),
        "Explain a recent achievement" to listOf("success", "goal", "challenge", "effort", "result")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speaking_practice)

        rvChat = findViewById(R.id.rvChat)
        btnSpeak = findViewById(R.id.btnSpeak)
        tvTimer = findViewById(R.id.tvTimer)

        tts = TextToSpeech(this, this)

        chatAdapter = ChatAdapter(chatMessages)
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = chatAdapter

        // Show the first random topic
        startNextTopic()

        btnSpeak.setOnClickListener {
            startTimer()           // Start the 1-min timer when user clicks
            startSpeechRecognition()
        }
    }

    /** Pick a random topic */
    private fun startNextTopic() {
        currentTopic = topics.shuffled().first()
        sendBotMessage("Your topic: $currentTopic")
        speakBot(currentTopic)
        tvTimer.text = "01:00"  // Reset timer display
    }

    /** Start 1-minute timer */
    private fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(speakingTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val min = millisUntilFinished / 60000
                val sec = (millisUntilFinished % 60000) / 1000
                tvTimer.text = String.format("%02d:%02d", min, sec)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                sendBotMessage("⏰ Time's up! Here's feedback for your last response.")
            }
        }.start()
    }

    /** Start speech recognition */
    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start speaking now...")
        try {
            startActivityForResult(intent, speechRequestCode)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }

    /** Handle user speech result */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == speechRequestCode && resultCode == Activity.RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val transcript = matches[0]
                sendUserMessage(transcript)
                giveFeedback(transcript)
                startNextTopic()  // Show next random topic after feedback
            }
        }
    }

    /** Generate feedback for transcript */
    private fun giveFeedback(transcript: String) {
        val feedback = StringBuilder()

        // Filler words
        val fillersUsed = fillerWords.filter { transcript.contains(it, ignoreCase = true) }
        if (fillersUsed.isNotEmpty()) feedback.append("⚠️ Filler words: ${fillersUsed.joinToString(", ")}\n")

        // Advanced vocab
        val advancedSuggestions = transcript.split(" ", ",", ".", "!", "?")
            .mapNotNull { word -> advancedWords[word.lowercase(Locale.getDefault())]?.let { adv -> word to adv } }
            .toMap()
        if (advancedSuggestions.isNotEmpty()) {
            feedback.append("💡 Consider using advanced words:\n")
            advancedSuggestions.forEach { (simple, adv) -> feedback.append("$simple → $adv\n") }
        }

        // Topic relevance
        val relevance = topicKeywords[currentTopic] ?: emptyList()
        val usedKeywords = relevance.filter { transcript.lowercase(Locale.getDefault()).contains(it) }
        val missingKeywords = relevance - usedKeywords.toSet()
        if (missingKeywords.isNotEmpty()) feedback.append("💡 Try including: ${missingKeywords.joinToString(", ")}\n")
        if (usedKeywords.isNotEmpty()) feedback.append("✅ Good job including: ${usedKeywords.joinToString(", ")}\n")

        sendBotMessage(feedback.toString())
        speakBot("Here's your feedback.")
    }

    /** Chat helpers */
    private fun sendBotMessage(message: String) {
        chatMessages.add(ChatMessage(message, Sender.BOT))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        rvChat.scrollToPosition(chatMessages.size - 1)
    }

    private fun sendUserMessage(message: String) {
        chatMessages.add(ChatMessage(message, Sender.USER))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        rvChat.scrollToPosition(chatMessages.size - 1)
    }

    private fun speakBot(message: String) {
        if (::tts.isInitialized) tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) tts.language = Locale.US
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        tts.stop()
        tts.shutdown()
    }
}
