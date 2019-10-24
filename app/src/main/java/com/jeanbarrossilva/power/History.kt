package com.jeanbarrossilva.power

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class History(context: Context) : SQLiteOpenHelper(context, DATABASE, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE $TABLE ($EXPRESSION_ID INTEGER PRIMARY KEY AUTOINCREMENT," + StringUtils.SPACE +
                "$CALC TEXT NOT NULL," + StringUtils.SPACE +
                "$RESULT TEXT NOT NULL"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    companion object {
        internal const val DATABASE = "history.db"
        internal const val TABLE = "history"

        internal const val EXPRESSION_ID = "id"
        internal const val CALC = "calc"
        internal const val RESULT = "result"
    }
}