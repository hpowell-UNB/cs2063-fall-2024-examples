package mobiledev.unb.ca.sqlitetest

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class DBManager(context: Context?) {
    private val dbHelper: DatabaseHelper = DatabaseHelper(context)
    fun close() {
        dbHelper.close()
    }

    fun listAllRecords(): Cursor? {
        val cursor = openReadOnlyDatabase().query(DatabaseHelper.TABLE_NAME,
            DatabaseHelper.COLUMNS,
            null,
            null,
            null,
            null,
            null)
        cursor?.moveToFirst()
        return cursor
    }

    fun insertRecord(item: String?, num: String?) {
        val contentValue = ContentValues()
        contentValue.put(DatabaseHelper.ITEM, item)
        contentValue.put(DatabaseHelper.NUM, num)
        openWriteDatabase().insert(DatabaseHelper.TABLE_NAME, null, contentValue)
    }

    fun deleteRecord(id: Int) {
        openWriteDatabase().delete(DatabaseHelper.TABLE_NAME,
            DatabaseHelper.ITEM_ID + "=?", arrayOf(id.toString()))
    }

    private fun openReadOnlyDatabase(): SQLiteDatabase {
        return dbHelper.readableDatabase
    }

    private fun openWriteDatabase(): SQLiteDatabase {
        return dbHelper.writableDatabase
    }

}