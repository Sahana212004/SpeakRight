package com.example.speakright.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class ProgressEntry(
    val id: Int = 0,
    val pronunciation: Double,
    val accuracy: Double,
    val grammar: Double,
    val timestamp: String
)

class ProgressDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "progress.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE progress (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                pronunciation REAL,
                accuracy REAL,
                grammar REAL,
                timestamp TEXT
            )"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS progress")
        onCreate(db)
    }

    // ✅ Save scores after each speech analysis
    fun insertProgress(pronunciation: Double, accuracy: Double, grammar: Double) {
        val values = ContentValues().apply {
            put("pronunciation", pronunciation)
            put("accuracy", accuracy)
            put("grammar", grammar)
            put("timestamp", System.currentTimeMillis().toString())
        }
        writableDatabase.insert("progress", null, values)
    }

    // ✅ Get all records
    fun getAllProgress(): List<ProgressEntry> {
        val list = mutableListOf<ProgressEntry>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM progress", null)
        while (cursor.moveToNext()) {
            list.add(
                ProgressEntry(
                    id = cursor.getInt(0),
                    pronunciation = cursor.getDouble(1),
                    accuracy = cursor.getDouble(2),
                    grammar = cursor.getDouble(3),
                    timestamp = cursor.getString(4)
                )
            )
        }
        cursor.close()
        return list
    }

    // ✅ Calculate averages
    fun getAverages(): Triple<Double, Double, Double> {
        val cursor = readableDatabase.rawQuery(
            "SELECT AVG(pronunciation), AVG(accuracy), AVG(grammar) FROM progress",
            null
        )
        var avgPron = 0.0
        var avgAcc = 0.0
        var avgGram = 0.0
        if (cursor.moveToFirst()) {
            avgPron = cursor.getDouble(0)
            avgAcc = cursor.getDouble(1)
            avgGram = cursor.getDouble(2)
        }
        cursor.close()
        return Triple(avgPron, avgAcc, avgGram)
    }
}
