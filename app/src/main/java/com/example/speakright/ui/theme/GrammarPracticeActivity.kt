package com.example.speakright.ui.theme

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.speakright.R
import com.google.android.material.button.MaterialButton
import java.util.*

class GrammarPracticeActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tvPassage: TextView
    private lateinit var llQuestions: LinearLayout
    private lateinit var btnNext: MaterialButton
    private lateinit var btnReplay: MaterialButton
    private lateinit var tts: TextToSpeech

    data class GrammarQuestion(
        val mistakeText: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String
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
        btnReplay = findViewById(R.id.btnReplay)

        tts = TextToSpeech(this, this)

        generatePassages()
        loadPassage(currentPassageIndex)

        btnReplay.setOnClickListener {
            tts.speak(passages[currentPassageIndex].text, TextToSpeech.QUEUE_FLUSH, null, null)
        }

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
        val passageList = mutableListOf<GrammarPassage>()

        // Base + Extra = 50 total questions
        val allPairs = listOf(
            Triple("Hi John, I hope you is doing good.", "is doing good", "‘You’ takes plural verb → are doing well."),
            Triple("She go to school every day.", "go to school", "For ‘she’, verb takes -es → goes to school."),
            Triple("I am enjoy learning new languages.", "am enjoy", "Use ‘enjoy’, not ‘am enjoy’."),
            Triple("They was happy to see their friends.", "was happy", "Plural subject → were happy."),
            Triple("He don’t knows how to drive.", "don’t knows", "Use ‘doesn’t know’ for singular ‘he’."),
            Triple("The teacher give us homework yesterday.", "give us", "Past tense → gave us."),
            Triple("My brother have bought a new car.", "have bought", "Singular subject → has bought."),
            Triple("There is many people waiting.", "is many people", "Plural → There are many people."),
            Triple("We enjoyed our trip, but it was rain all day.", "was rain", "Use continuous form → was raining."),
            Triple("She didn’t told me that she was coming.", "didn’t told", "Use base form after ‘didn’t’ → didn’t tell."),
            Triple("If I will see him tomorrow, I’ll tell him.", "If I will see", "Use simple present in conditionals → If I see."),
            Triple("He is married with a doctor.", "married with", "Correct preposition → married to."),
            Triple("I prefer coffee than tea.", "prefer coffee than", "Use ‘prefer … to …’."),
            Triple("He is good in playing football.", "good in", "Correct form → good at."),
            Triple("It depends of the weather.", "depends of", "Correct form → depends on."),
            Triple("He didn’t knew that the shop was closed.", "didn’t knew", "Use base form after ‘didn’t’ → didn’t know."),
            Triple("She enjoy to dance every evening.", "enjoy to dance", "Use gerund → enjoys dancing."),
            Triple("The news are very surprising today.", "news are", "‘News’ is singular → news is."),
            Triple("Neither of the boys have done homework.", "have done", "Use singular verb → has done."),
            Triple("He has visited Paris last year.", "has visited", "Past time marker → visited."),
            Triple("I look forward to see you soon.", "to see", "Use gerund after ‘to’ → to seeing."),
            Triple("He is one of those players who works hard.", "works", "Relative clause matches plural → who work hard."),
            Triple("She said me that she would come.", "said me", "Say to someone → told me."),
            Triple("It’s time we go home.", "go", "Use past form → went."),
            Triple("I am used to wake up early.", "to wake", "Use gerund → to waking."),
            Triple("He explained me the lesson.", "explained me", "Use ‘explained to me’."),
            Triple("I suggested her to take rest.", "suggested her", "Correct → suggested that she take rest."),
            Triple("She is better than me in singing.", "than me", "Formal → than I am."),
            Triple("She said that she will come yesterday.", "will come", "Reported speech → would come."),
            Triple("He is senior than me.", "senior than", "Use ‘senior to’."),
            Triple("Each of the students have a pen.", "have", "Singular subject → has."),
            Triple("The furniture are old.", "are", "Uncountable noun → is."),
            Triple("She has been to London last week.", "has been", "Use past tense → went."),
            Triple("She told that she was tired.", "told that", "Use ‘said that’."),
            Triple("I am interested on history.", "interested on", "Correct → interested in."),
            Triple("He prefer coffee to tea.", "prefer", "Singular subject → prefers."),
            Triple("I look forward to meet you.", "to meet", "Use gerund → to meeting."),
            Triple("I wish I am rich.", "am", "Use past form → were."),
            Triple("She has visited the museum yesterday.", "has visited", "Use simple past → visited."),
            Triple("She is one of the best student in the class.", "student", "Plural → students."),
            Triple("He said me the truth.", "said me", "Correct → told me."),
            Triple("It is high time we go.", "go", "Use past tense → went."),
            Triple("Neither of the boys were absent.", "were", "Use singular verb → was."),
            Triple("She told to me that she was busy.", "told to me", "Correct → told me."),
            Triple("He discussed about the plan.", "discussed about", "‘Discuss’ doesn’t take ‘about’."),
            Triple("She explained me the topic yesterday.", "explained me", "Correct → explained to me."),
        )

        allPairs.forEach { (text, mistake, explanation) ->
            passageList.add(
                GrammarPassage(
                    text = text,
                    questions = listOf(
                        GrammarQuestion(
                            mistake,
                            listOf(
                                text.replace(mistake, explanation.split("→").last().trim()),
                                mistake,
                                "Not sure",
                                "Skip"
                            ),
                            0,
                            explanation
                        )
                    )
                )
            )
        }

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
            tvQ.text = "Select the correct form for: '${question.mistakeText}'"
            tvQ.textSize = 16f
            tvQ.setTextColor(ContextCompat.getColor(this, R.color.black))
            llQuestions.addView(tvQ)

            val buttonList = mutableListOf<MaterialButton>()

            question.options.forEachIndexed { idx, option ->
                val btn = MaterialButton(this)
                btn.text = option
                btn.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 8, 0, 8) }

                btn.setOnClickListener {
                    if (userSelections.containsKey(question)) return@setOnClickListener
                    userSelections[question] = idx

                    val startColor = ContextCompat.getColor(this, R.color.white)
                    val endColor = if (idx == question.correctIndex)
                        ContextCompat.getColor(this, android.R.color.holo_green_light)
                    else
                        ContextCompat.getColor(this, android.R.color.holo_red_light)

                    // Animate button color
                    val animator = ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor)
                    animator.duration = 400
                    animator.addUpdateListener { anim ->
                        btn.setBackgroundColor(anim.animatedValue as Int)
                    }
                    animator.start()

                    if (idx == question.correctIndex) {
                        tts.speak("Correct!", TextToSpeech.QUEUE_FLUSH, null, null)
                    } else {
                        tts.speak(
                            "Wrong! The correct answer is ${
                                question.options[question.correctIndex]
                            }",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                        val correctBtn = buttonList[question.correctIndex]
                        correctBtn.setBackgroundColor(
                            ContextCompat.getColor(this, android.R.color.holo_green_light)
                        )
                    }

                    // Disable all buttons after one attempt
                    buttonList.forEach { it.isEnabled = false }

                    // Show grammar tip
                    val tvExp = TextView(this)
                    tvExp.text = "💡 ${question.explanation}"
                    tvExp.textSize = 14f
                    tvExp.setTextColor(ContextCompat.getColor(this, R.color.teal_700))
                    tvExp.setPadding(12, 6, 12, 12)
                    llQuestions.addView(tvExp)

                    // Enable Next only if all questions are answered
                    btnNext.isEnabled = userSelections.size == passage.questions.size
                }

                buttonList.add(btn)
                llQuestions.addView(btn)
            }
        }

        // Speak passage
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
