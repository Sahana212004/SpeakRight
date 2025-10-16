package com.example.speakright

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDB.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"

        private const val COL_ID = "id"
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "password"
        private const val COL_FIRST_NAME = "first_name"
        private const val COL_LAST_NAME = "last_name"
        private const val COL_PHONE = "phone"
        private const val COL_PROFILE_PIC = "profile_pic"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_USERS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_EMAIL TEXT UNIQUE,
                $COL_PASSWORD TEXT,
                $COL_FIRST_NAME TEXT,
                $COL_LAST_NAME TEXT,
                $COL_PHONE TEXT,
                $COL_PROFILE_PIC BLOB
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun userExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COL_EMAIL=?", arrayOf(email))
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    fun validateUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COL_EMAIL=? AND $COL_PASSWORD=?", arrayOf(email, password))
        val valid = cursor.moveToFirst()
        cursor.close()
        db.close()
        return valid
    }

    fun insertUser(email: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_EMAIL, email)
            put(COL_PASSWORD, password)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun updateUserProfile(email: String, firstName: String, lastName: String, phone: String, imageBytes: ByteArray?): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_FIRST_NAME, firstName)
            put(COL_LAST_NAME, lastName)
            put(COL_PHONE, phone)
            put(COL_PROFILE_PIC, imageBytes)
        }
        val result = db.update(TABLE_USERS, values, "$COL_EMAIL=?", arrayOf(email))
        db.close()
        return result > 0
    }

    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email=?", arrayOf(email))
        var user: User? = null

        if (cursor.moveToFirst()) {
            val firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"))
            val lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
            val profilePic = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_pic"))
            user = User(email, firstName, lastName, phone, profilePic)
        }

        cursor.close()
        db.close()
        return user
    }
}