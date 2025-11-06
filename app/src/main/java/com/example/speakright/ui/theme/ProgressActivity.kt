package com.example.speakright.ui.theme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.data.ProgressDatabaseHelper
import com.example.speakright.data.ProgressEntry
import com.example.speakright.databinding.ActivityProgressBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import android.graphics.Color

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = ProgressDatabaseHelper(this)
        val progressList = dbHelper.getAllProgress()
        val (avgPron, avgAcc, avgGram) = dbHelper.getAverages()

        // Update averages
        binding.avgPronunciation.text = "%.1f".format(avgPron)
        binding.avgAccuracy.text = "%.1f".format(avgAcc)
        binding.avgGrammar.text = "%.1f".format(avgGram)

        // Compute SVAR score (average of all)
        val svarScore = ((avgPron + avgAcc + avgGram) / 3).toInt()
        binding.tvSvarScore.text = svarScore.toString()

        // Simple streak logic
        val streakDays = if (progressList.isNotEmpty()) progressList.size else 0
        binding.tvStreak.text = "$streakDays-Day Streak!"

        if (progressList.isNotEmpty()) {
            val entries = progressList.mapIndexed { index, p ->
                BarEntry(index.toFloat(), p.totalScore().toFloat())
            }

            val dataSet = BarDataSet(entries, "Performance Over Time").apply {
                color = Color.parseColor("#4CAF50")
                valueTextColor = Color.BLACK
                valueTextSize = 12f
            }

            val data = BarData(dataSet)

            binding.barChart.data = data
            binding.barChart.xAxis.valueFormatter =
                IndexAxisValueFormatter(progressList.map { it.timestamp.substring(5, 10) })
            binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            binding.barChart.axisRight.isEnabled = false
            binding.barChart.description.isEnabled = false
            binding.barChart.animateY(1000)
            binding.barChart.invalidate()
        }
    }

    private fun ProgressEntry.totalScore(): Double {
        return (pronunciation + accuracy + grammar) / 3
    }
}
