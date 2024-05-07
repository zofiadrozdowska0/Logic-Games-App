package com.example.signupapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
class DBHelper(context: Context) : SQLiteOpenHelper(context, "Userdata", null, 1) {
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table Users (user_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)")
        db?.execSQL("create table PointsHistory (user_id INTEGER, date TEXT, reflex_points INTEGER, memory_points INTEGER, concentration_points INTEGER, logic_points INTEGER, PRIMARY KEY(user_id, date))")
        db?.execSQL("create table Friends (user_id INTEGER, friend_id INTEGER, PRIMARY KEY(user_id, friend_id))")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists Users")
        db?.execSQL("drop table if exists PointsHistory")
        db?.execSQL("drop table if exists Friends")
    }

    fun insertUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put("username", username)
            put("password", password)
        }
        val result = db.insert("Users", null, cv)

// Firebase: Zapisanie danych użytkownika do kolekcji "users" w bazie danych Firebase Firestore
        val userMap = mapOf("username" to username, "password" to password)
        firestore.collection("users")
            .add(userMap)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
        return result != -1L
    }


    fun checkuserpass(username: String, password: String): Boolean{
        val p0 = this.writableDatabase
        val query = "select * from Users where username = '$username' and password= '$password'"
        val cursor = p0.rawQuery(query, null)
        if (cursor.count<=0){
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

    fun insertPoints(
        userId: Int,
        date: String,
        reflexPoints: Int,
        memoryPoints: Int,
        concentrationPoints: Int,
        logicPoints: Int
    ): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("user_id", userId)
        cv.put("date", date)
        cv.put("reflex_points", reflexPoints)
        cv.put("memory_points", memoryPoints)
        cv.put("concentration_points", concentrationPoints)
        cv.put("logic_points", logicPoints)
        val result = db.insert("pointsHistory", null, cv)
        val pointsMap = mapOf(
            "userId" to userId,
            "date" to date,
            "reflex_points" to reflexPoints,
            "memory_points" to memoryPoints,
            "concentration_points" to concentrationPoints,
            "logic_points" to logicPoints
        )

        return result != -1L
    }

    fun addFriend(userId: Int, friendId: Int): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("user_id", userId)
        cv.put("friend_id", friendId)
        val result = db.insert("Friends", null, cv)
        return result != -1L
    }

    fun getUserFriends(userId: Int): List<Int> {
        val friends = mutableListOf<Int>()
        val db = this.readableDatabase
        val query = "SELECT friend_id FROM Friends WHERE user_id = ?"
        val selectionArgs = arrayOf(userId.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        cursor.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex("friend_id")
                if (columnIndex >= 0) {
                    do {
                        val friendId = it.getInt(columnIndex)
                        friends.add(friendId)
                    } while (it.moveToNext())
                }
            }
        }

        return friends
    }


    fun getUsernameById(userId: Int): String {
        var username = ""
        val db = this.readableDatabase
        val query = "SELECT username FROM Users WHERE user_id = ?"
        val selectionArgs = arrayOf(userId.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        cursor.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex("username")
                if (columnIndex >= 0) {
                    username = it.getString(columnIndex)
                }
            }
        }

        return username
    }


    fun areFriends(userId1: Int, userId2: Int): Boolean { //funkcja celowo sprawdza jedynie połączenie w jedną stronę, tak aby było wiadomo czy druga osoba zaakceptowałą zaproszenie do znajomych
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM Friends WHERE (user_id = ? AND friend_id = ?)"
        val selectionArgs = arrayOf(userId1.toString(), userId2.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        var count = 0
        cursor.use {
            if (it.moveToNext()) {
                count = it.getInt(0)
            }
        }

        return count > 0
    }

    fun getInvitedFriends(): List<String> {
        val userId = MainActivity.CurrentUser.userId
        val invitedFriends = mutableListOf<String>()
        val db = this.readableDatabase

        // Zapytanie wybiera użytkowników, którzy zaprosili aktualnie zalogowanego użytkownika,
        // ale którzy nie są jego obustronnymi znajomymi
        val query = "SELECT user_id " +
                "FROM Friends " +
                "WHERE friend_id = ? " +
                "AND user_id != ? " +
                "AND user_id NOT IN (SELECT friend_id FROM Friends WHERE user_id = ?)"

        val selectionArgs = arrayOf(userId.toString(), userId.toString(), userId.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        cursor.use {
            val friendIdIndex = cursor.getColumnIndex("user_id")
            while (it.moveToNext()) {
                val friendId = it.getString(friendIdIndex)
                invitedFriends.add(friendId)
            }
        }

        return invitedFriends
    }




    fun getUsernames(): List<String> {
        val userId = MainActivity.CurrentUser.userId
        val usernames = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT username FROM Users " +
                "WHERE user_id NOT IN " +
                "(SELECT friend_id FROM Friends WHERE user_id = $userId) " +
                "AND user_id != $userId" // Wyklucz bieżącego użytkownika
        val cursor = db.rawQuery(query, null)

        cursor.use {
            val usernameIndex = it.getColumnIndex("username")
            while (it.moveToNext()) {
                val username = it.getString(usernameIndex)
                usernames.add(username)
            }
        }

        return usernames
    }

    fun getUserIdByUsername(username: String): Int? {
        val db = this.readableDatabase
        val query = "SELECT user_id FROM Users WHERE username = ?"
        val selectionArgs = arrayOf(username)
        var userId: Int? = null

        val cursor = db.rawQuery(query, selectionArgs)
        cursor.use {
            if (it.moveToFirst()) {
                val userIdIndex = it.getColumnIndex("user_id")
                if (userIdIndex != -1) {
                    userId = it.getInt(userIdIndex)
                }
            }
        }

        return userId
    }





    fun getUserIdByUsernameAndPassword(username: String, password: String): Int {
        val db = this.readableDatabase
        val query = "SELECT user_id FROM Users WHERE username = ? AND password = ?"
        val selectionArgs = arrayOf(username, password)
        var userId = -1
        val cursor = db.rawQuery(query, selectionArgs)
        cursor.use {
            if (it.moveToFirst()) {
                val userIdIndex = it.getColumnIndex("user_id")
                userId = it.getInt(userIdIndex)
            }
        }
        return userId
    }

    fun checkUsernameExists(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM Users WHERE username = ?"
        val selectionArgs = arrayOf(username)
        val cursor = db.rawQuery(query, selectionArgs)

        var count = 0
        cursor.use {
            if (it.moveToNext()) {
                count = it.getInt(0)
            }
        }

        return count > 0
    }

    fun fillPointsHistory(dbHelper: DBHelper, userId: Int) {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -29) // odejmujemy 29 dni, aby uzyskać 30 dni wstecz
        val startDate = calendar.time

        while (startDate <= today) {
            val date = sdf.format(startDate)
            val reflexPoints = (0..10).random() // losujemy punkty dla różnych kategorii
            val memoryPoints = (0..10).random()
            val concentrationPoints = (0..10).random()
            val logicPoints = (0..10).random()

            dbHelper.insertPoints(userId, date, reflexPoints, memoryPoints, concentrationPoints, logicPoints)

            calendar.add(Calendar.DAY_OF_YEAR, 1) // przechodzimy do kolejnego dnia
            startDate.time = calendar.timeInMillis
        }
    }
}
