package ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(ctx: Context)
    : SQLiteOpenHelper(ctx, DATABASENAME,null, DATABASEVERSION) {
    companion object {
        private val DATABASEVERSION = 1
        private val DATABASENAME = "EmailDatabase"

        val TABLE_EMAILS = "EmailsTable"
        val KEYID = "id"
        val KEYRECIPIENT = "recipient"
        val KEYSUBJECT = "subject"
        val KEYBODY = "body"
        val KEYDRAFT = "is_draft"
        val KEYDATE = "updated_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_EMAILSTABLE = (
                "CREATE TABLE $TABLE_EMAILS " +
                        "($KEYID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$KEYRECIPIENT TEXT, " +
                        "$KEYSUBJECT TEXT, " +
                        "$KEYBODY TEXT," +
                        "$KEYDRAFT TEXT," +
                        "$KEYDATE TEXT"+")"
                )
        db?.execSQL(CREATE_EMAILSTABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS "+ TABLE_EMAILS)
        onCreate(db)
    }
}