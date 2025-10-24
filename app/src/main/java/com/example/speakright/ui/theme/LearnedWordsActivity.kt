package com.example.speakright.ui.theme

import android.graphics.Color
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R
import com.example.speakright.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LearnedWordsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learned_words)

        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)
        val db = AppDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            val words = db.learnedWordDao().getAllWords()

            runOnUiThread {
                if (words.isEmpty()) {
                    val msg = TextView(this@LearnedWordsActivity)
                    msg.text = "No learned words yet!"
                    msg.textSize = 18f
                    msg.setTextColor(Color.DKGRAY)
                    msg.setPadding(16, 32, 16, 16)
                    tableLayout.addView(msg)
                } else {
                    words.forEach {
                        val row = TableRow(this@LearnedWordsActivity)
                        row.setPadding(8, 8, 8, 8)

                        val tvWord = TextView(this@LearnedWordsActivity)
                        tvWord.text = it.word
                        tvWord.textSize = 17f
                        tvWord.setPadding(8, 8, 16, 8)
                        tvWord.setTextColor(Color.BLACK)

                        val tvMeaning = TextView(this@LearnedWordsActivity)
                        tvMeaning.text = it.meaning
                        tvMeaning.textSize = 16f
                        tvMeaning.setPadding(8, 8, 8, 8)
                        tvMeaning.setTextColor(Color.DKGRAY)

                        row.addView(tvWord)
                        row.addView(tvMeaning)
                        tableLayout.addView(row)
                    }
                }
            }
        }
    }
}
