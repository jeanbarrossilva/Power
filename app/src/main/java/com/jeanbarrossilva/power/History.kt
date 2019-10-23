package com.jeanbarrossilva.power

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class History(context: Context) : SQLiteOpenHelper(context, DATABASE, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE $TABLE (ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL2 TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE")
    }

    private val db = this.writableDatabase
    private val contentValues = ContentValues()

    fun addData(item: String): Boolean {
        contentValues.put(COL2, item)
        return db.insert(TABLE, null, contentValues) != ERROR
    }

    fun contentList(): Cursor {
        return db.rawQuery("SELECT * FROM $TABLE", null)
    }

    companion object {
        internal const val DATABASE = "history.db"
        internal const val TABLE = "history"

        internal const val COL1 = "ID"
        internal const val COL2 = "EXPRESSION"

        private const val ERROR = (-1).toLong()
    }
}