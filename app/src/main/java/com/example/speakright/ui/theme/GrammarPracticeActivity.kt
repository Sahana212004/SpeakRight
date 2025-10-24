package com.example.speakright.ui.theme

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R
import com.google.android.material.button.MaterialButton
import java.util.*

class GrammarPracticeActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tvPassage: TextView
    private lateinit var llQuestions: LinearLayout
    private lateinit var btnNext: MaterialButton
    private lateinit var tts: TextToSpeech

    data class GrammarQuestion(
        val mistakeText: String,
        val options: List<String>,
        val correctIndex: Int
    )

    data class GrammarPassage(
        val text: String,
        val questions: List<GrammarQuestion>
    )

    private lateinit var passages: List<GrammarPassage>
    private var currentPassageIndex = 0
    private var userSelections = mutableMapOf<GrammarQuestion, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grammar_practice)

        tvPassage = findViewById(R.id.tvExercise)
        llQuestions = findViewById(R.id.llOptions)
        btnNext = findViewById(R.id.btnNext)

        tts = TextToSpeech(this, this)

        generatePassages()
        loadPassage(currentPassageIndex)

        btnNext.setOnClickListener {
            var correctCount = 0
            for ((question, selected) in userSelections) {
                if (selected == question.correctIndex) correctCount++
            }

            Toast.makeText(
                this,
                "You got $correctCount/${userSelections.size} correct!",
                Toast.LENGTH_LONG
            ).show()

            currentPassageIndex++
            if (currentPassageIndex < passages.size) {
                loadPassage(currentPassageIndex)
            } else {
                Toast.makeText(this, "🎉 You completed all passages!", Toast.LENGTH_LONG).show()
                btnNext.isEnabled = false
            }
        }
    }

    private fun generatePassages() {
        // Example with 3 passages, you can expand to 30+
        val passageList = mutableListOf<GrammarPassage>()

        passageList.add(
            GrammarPassage(
                text = "Hi John, I hope you is doing good. I want to discuss about the new project tomorrow. Let me know if your available.",
                questions = listOf(
                    GrammarQuestion(
                        "is doing good",
                        listOf("are doing well", "is doing good", "am doing good", "be doing good"),
                        correctIndex = 0
                    ),
                    GrammarQuestion(
                        "discuss about",
                        listOf("discuss", "discuss about", "talk about", "discussion on"),
                        correctIndex = 0
                    ),
                    GrammarQuestion(
                        "your available",
                        listOf("you are available", "your available", "you're availability", "your being available"),
                        correctIndex = 0
                    )
                )
            )
        )

        passageList.add(
            GrammarPassage(
                text = "She go to school every day but she don't like math. She enjoys playing football on weekends.",
                questions = listOf(
                    GrammarQuestion(
                        "go to school",
                        listOf("goes to school", "go to school", "gone to school", "going to school"),
                        correctIndex = 0
                    ),
                    GrammarQuestion(
                        "don't like math",
                        listOf("doesn't like math", "don't like math", "didn't liked math", "not like math"),
                        correctIndex = 0
                    )
                )
            )
        )

        passageList.add(
            GrammarPassage(
                text = "I am enjoy learning new languages every day. It help me to communicate better with people from other countries.",
                questions = listOf(
                    GrammarQuestion(
                        "am enjoy",
                        listOf("enjoy", "am enjoy", "is enjoying", "are enjoying"),
                        correctIndex = 0
                    ),
                    GrammarQuestion(
                        "help me",
                        listOf("helps me", "help me", "helped me", "helping me"),
                        correctIndex = 0
                    )
                )
            )
        )

        passages = passageList.shuffled()
    }

    private fun loadPassage(index: Int) {
        val passage = passages[index]
        userSelections.clear()
        btnNext.isEnabled = false

        tvPassage.text = passage.text
        llQuestions.removeAllViews()

        for (question in passage.questions) {
            val tvQ = TextView(this)
            tvQ.text = "Select correct form for: '${question.mistakeText}'"
            tvQ.textSize = 16f
            tvQ.setTextColor(resources.getColor(R.color.black))
            llQuestions.addView(tvQ)

            question.options.forEachIndexed { idx, option ->
                val btn = MaterialButton(this)
                btn.text = option
                btn.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 8, 0, 8) }

                btn.setOnClickListener {
                    userSelections[question] = idx
                    // Enable Next only if all questions have selections
                    btnNext.isEnabled = userSelections.size == passage.questions.size
                }
                llQuestions.addView(btn)
            }
        }

        // Speak the passage
        tts.speak(passage.text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) tts.language = Locale.US
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
}
