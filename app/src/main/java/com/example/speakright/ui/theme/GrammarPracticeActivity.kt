package com.example.speakright.ui.theme

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R
import com.google.android.material.card.MaterialCardView

class GrammarPracticeActivity : AppCompatActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var btnOption1: Button
    private lateinit var btnOption2: Button
    private lateinit var btnOption3: Button
    private lateinit var btnOption4: Button
    private lateinit var btnNext: Button

    // Sample Questions
    private val questions = listOf(
        Question("Choose the correct sentence:", listOf(
            "He go to school every day.",
            "He goes to school every day.",
            "He going to school every day.",
            "He gone to school every day."
        ), correctIndex = 1),

        Question("Identify the passive voice sentence:", listOf(
            "The chef cooked the meal.",
            "The meal is cooking.",
            "The meal was cooked by the chef.",
            "The chef has been cooking."
        ), correctIndex = 2)
    )

    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grammar_practice)

        tvQuestion = findViewById(R.id.tvGrammarQuestion)
        btnOption1 = findViewById(R.id.btnOption1)
        btnOption2 = findViewById(R.id.btnOption2)
        btnOption3 = findViewById(R.id.btnOption3)
        btnOption4 = findViewById(R.id.btnOption4)
        btnNext = findViewById(R.id.btnNextGrammar)

        loadQuestion()

        val buttons = listOf(btnOption1, btnOption2, btnOption3, btnOption4)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                checkAnswer(index)
            }
        }

        btnNext.setOnClickListener {
            currentQuestionIndex = (currentQuestionIndex + 1) % questions.size
            loadQuestion()
        }
    }

    private fun loadQuestion() {
        val question = questions[currentQuestionIndex]
        tvQuestion.text = question.text
        btnOption1.text = question.options[0]
        btnOption2.text = question.options[1]
        btnOption3.text = question.options[2]
        btnOption4.text = question.options[3]
        btnNext.isEnabled = false

        animateCard(tvQuestion)
    }

    private fun checkAnswer(selectedIndex: Int) {
        val correctIndex = questions[currentQuestionIndex].correctIndex
        if (selectedIndex == correctIndex) {
            Toast.makeText(this, "✅ Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "❌ Incorrect!", Toast.LENGTH_SHORT).show()
        }
        btnNext.isEnabled = true
    }

    private fun animateCard(view: TextView) {
        val animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        animator.duration = 600
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

    data class Question(val text: String, val options: List<String>, val correctIndex: Int)
}
