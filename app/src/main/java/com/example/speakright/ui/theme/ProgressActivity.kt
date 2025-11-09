package com.example.speakright.ui.theme

import android.graphics.Color
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

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = ProgressDatabaseHelper(this)
        val progressList = dbHelper.getAllProgress()
        val (avgPron, avgAcc, avgGram) = dbHelper.getAverages()

        // Update averages on top
        binding.avgPronunciation.text = "%.1f".format(avgPron)
        binding.avgAccuracy.text = "%.1f".format(avgAcc)
        binding.avgGrammar.text = "%.1f".format(avgGram)

        // Compute overall score (SVAR score)
        val svarScore = ((avgPron + avgAcc + avgGram) / 3).toInt()
        binding.tvSvarScore.text = svarScore.toString()

        // Streak
        val streakDays = if (progressList.isNotEmpty()) progressList.size else 0
        binding.tvStreak.text = "$streakDays-Day Streak!"

        // Only show chart if data exists
        if (progressList.isNotEmpty()) {
            setupBarChart(progressList)
        }
    }

    private fun setupBarChart(progressList: List<ProgressEntry>) {
        // Limit to last 7 entries (one week)
        val lastSeven = progressList.takeLast(7)
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        // Prepare data entries
        val pronEntries = ArrayList<BarEntry>()
        val accEntries = ArrayList<BarEntry>()
        val gramEntries = ArrayList<BarEntry>()

        for ((index, p) in lastSeven.withIndex()) {
            pronEntries.add(BarEntry(index.toFloat(), p.pronunciation.toFloat()))
            accEntries.add(BarEntry(index.toFloat(), p.accuracy.toFloat()))
            gramEntries.add(BarEntry(index.toFloat(), p.grammar.toFloat()))
        }

        // Bar sets
        val pronSet = BarDataSet(pronEntries, "Pronunciation").apply {
            color = Color.parseColor("#4CAF50")
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }
        val accSet = BarDataSet(accEntries, "Accuracy").apply {
            color = Color.parseColor("#2196F3")
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }
        val gramSet = BarDataSet(gramEntries, "Grammar").apply {
            color = Color.parseColor("#FFC107")
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        // Combine data sets
        val data = BarData(pronSet, accSet, gramSet)

        // Group bars (3 per day)
        val groupSpace = 0.2f
        val barSpace = 0.05f
        val barWidth = 0.25f
        data.barWidth = barWidth

        binding.barChart.data = data

        // X-axis settings
        val xAxis = binding.barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(days)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.axisMinimum = 0f
        xAxis.textSize = 12f
        xAxis.textColor = Color.BLACK

        // Group the bars
        binding.barChart.xAxis.axisMaximum = 0f + data.getGroupWidth(groupSpace, barSpace) * lastSeven.size
        binding.barChart.groupBars(0f, groupSpace, barSpace)

        // Y-axis
        binding.barChart.axisLeft.axisMinimum = 0f
        binding.barChart.axisLeft.axisMaximum = 100f
        binding.barChart.axisRight.isEnabled = false

        // General chart settings
        binding.barChart.description.isEnabled = false
        binding.barChart.legend.isEnabled = true
        binding.barChart.setFitBars(true)
        binding.barChart.animateY(1000)
        binding.barChart.invalidate()
    }

    private fun ProgressEntry.totalScore(): Double {
        return (pronunciation + accuracy + grammar) / 3
    }
}
