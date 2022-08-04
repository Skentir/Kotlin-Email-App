package ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.dao

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.model.Email
import java.lang.reflect.Type

class EmailsDAOSharedPref// MODE_PRIVATE means SharedPreferences is only for the application
    (context: Context) {
    // Shared Preferences is not safe, parang text file lang sya
    private var appPreferences: SharedPreferences? = null // Parang settings
    private val PREFS = "appPreferences"

    init {
        appPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun saveArrayListData(key: String, data: ArrayList<Email>) {
        val prefsEditor = appPreferences!!.edit()
        val gson = Gson()
        val json : String = gson.toJson(data)
        prefsEditor.putString("emails", json)
        prefsEditor.apply()
    }

    fun getArrayListData(): ArrayList<Email> {
        val gson = Gson()
        var emails = ArrayList<Email>()
        val json : String? = appPreferences!!.getString("emails", "No emails in sharedpref.")
        println("json ${json} of length ${json!!.length}")
        if (json.length > 24) {
            // 24 because it is the length of the empty msg
            val type : Type = object : TypeToken<ArrayList<Email>>() {}.type
            emails = gson.fromJson<ArrayList<Email>>(json, type)
        } else {
            println("No emails were found")
        }
        return emails
    }

    fun addEmail(newEmail: Email) {
        val emails = getArrayListData()
        val size = emails.size
        // If emails is not empty
        if (size > 0) {
            val newId = emails.get(size-1).id
            newEmail.id = newId + 1
            emails.add(newEmail)
            saveArrayListData("emails",emails)
        } else {
            // If empty, tag it id 0
            val newId = 0
            emails.add(newEmail)
            saveArrayListData("emails",emails)
        }
    }

    fun getDraft(): Email? {
        val emails = getArrayListData()
        // If emails not empty
        if (!emails.isEmpty()) {
            // Find draft in emails
            for (e in emails) {
                // Return if draft is found
                if (e.isDraft == 1)
                    return e
            }
            // Return null otherwise
            return null
        } else {
            return null
        }
    }

    fun updateEmail(email: Email) {
        val emails = getArrayListData()
        if (!emails.isEmpty()) {
            val idx = findEmailIdx(emails, email.id)
            if (idx != -1) {
               val target = emails.get(idx)
                target.id = email.id
                target.recipient = email.recipient
                target.subject = email.subject
                target.body = email.body
                target.isDraft = email.isDraft
                target.updated_at = email.updated_at
                // Save data
                saveArrayListData("emails",emails)
            } else {
                println("Cannot find email to update")
            }
        }
    }

    fun findEmailIdx(emails: ArrayList<Email>, id: Int): Int {
        for (i in 0..emails.size-1) {
            // Return if id matches
            if (id == emails.get(i).id) {
                return i
            }
        }
        return -1
    }

    fun deleteDraft(email: Email) {
        val emails = getArrayListData()
        if (!emails.isEmpty() && getDraft() != null) {
            val idx = findEmailIdx(emails, email.id)
            if (idx != -1) {
                emails.removeAt(idx)
                // Save data
                saveArrayListData("emails",emails)
            } else {
                println("Cannot find draft to remove")
            }
        }
    }
    fun deleteDraftID(id : Int) {
        val emails = getArrayListData()
        // Delete draft if emails aren't empty and draft exists
        if (!emails.isEmpty() && getDraft() != null) {
            val email = getDraft()
            val idx = findEmailIdx(emails, email!!.id)
            if (idx != -1) {
                emails.removeAt(idx)
                // Save data
                saveArrayListData("emails",emails)
            } else {
                println("Cannot find draft to remove")
            }
        }
    }
}

// UNCOMMENT BELOW FOR SQLite Implementation <3

//class EmailsDAOSQLImpl(var ctx: Context) {
//
//    fun addEmail(email: Email) {
//        val databaseHandler = DatabaseHandler(ctx)
//        val db = databaseHandler.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(DatabaseHandler.KEYRECIPIENT, email.recipient)
//        contentValues.put(DatabaseHandler.KEYSUBJECT, email.subject)
//        contentValues.put(DatabaseHandler.KEYBODY, email.body)
//        contentValues.put(DatabaseHandler.KEYDRAFT, email.isDraft)
//        contentValues.put(DatabaseHandler.KEYDATE, email.updated_at)
//        val success = db.insert(DatabaseHandler.TABLE_EMAILS, null, contentValues)
//        db.close()
//    }
//
//    fun getDraft(): Email? {
//        val selectQuery = "SELECT * " + "FROM ${DatabaseHandler.TABLE_EMAILS} WHERE is_draft=1"
//        val databaseHandler: DatabaseHandler = DatabaseHandler(ctx)
//        val db = databaseHandler.readableDatabase
//
//        var cursor: Cursor? = null
//        try {
//            cursor = db.rawQuery(selectQuery, null)
//            if (cursor.moveToFirst()) {
//                    val email = Email(
//                        cursor.getInt(0),
//                        cursor.getString(1),
//                        cursor.getString(2),
//                        cursor.getString(3),
//                        cursor.getInt(4),
//                        cursor.getString(5)
//                    )
//                    return email
//            }
//            return null
//        } catch (e:SQLiteException){
//            db.execSQL(selectQuery)
//            return null
//        }
//    }
//
//    fun getEmails(): ArrayList<Email> {
//        val emailList: ArrayList<Email> = ArrayList<Email>()
//
//        val selectQuery = "SELECT * " + "FROM ${DatabaseHandler.TABLE_EMAILS} ORDER BY ${DatabaseHandler.KEYDATE} ASC"
//
//        val databaseHandler = DatabaseHandler(ctx)
//        val db = databaseHandler.readableDatabase
//
//        var cursor: Cursor? = null
//        try {
//            cursor = db.rawQuery(selectQuery, null)
////            cursor.columnNames.forEach {
////                println("Name: $it")
////            }
//        } catch (e: SQLiteException) {
//            db.execSQL(selectQuery)
//            return ArrayList()
//        }
//
//        if (cursor.moveToFirst()) {
//            do {
//                val email = Email(
//                    cursor.getInt(0),
//                    cursor.getString(1),
//                    cursor.getString(2),
//                    cursor.getString(3),
//                    cursor.getInt(4),
//                    cursor.getString(5)
//                )
//                emailList.add(email)
//            } while (cursor.moveToNext())
//        }
//        return emailList
//    }
//
//    fun updateEmail(email: Email): Int {
//        var databaseHandler:DatabaseHandler = DatabaseHandler(ctx)
//        val db = databaseHandler.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(DatabaseHandler.KEYRECIPIENT, email.recipient)
//        contentValues.put(DatabaseHandler.KEYSUBJECT, email.subject)
//        contentValues.put(DatabaseHandler.KEYBODY, email.body)
//        contentValues.put(DatabaseHandler.KEYDRAFT, email.isDraft)
//        contentValues.put(DatabaseHandler.KEYDATE, email.updated_at)
//
//        val success = db.update(DatabaseHandler.TABLE_EMAILS,
//            contentValues,
//            "id=+${email.id}",null)
//        db.close()
//        return success // If negative then it failed, if positive it succeeded
//    }
//
//    fun deleteDraft(email: Email): Int {
//        val databaseHandler = DatabaseHandler(ctx)
//        val db = databaseHandler.writableDatabase
//        val success = db.delete(DatabaseHandler.TABLE_EMAILS,
//            "id=+${email.id}", null)
//        db.close()
//        return success
//    }
//
//    fun deleteDraftID(id: Int): Int {
//        val databaseHandler = DatabaseHandler(ctx)
//        val db = databaseHandler.writableDatabase
//        val success = db.delete(DatabaseHandler.TABLE_EMAILS,
//            "id=+${id}", null)
//        db.close()
//        return success
//    }
//
//}