package ca.unb.mobiledev.readwritedemo

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

class FileUtils(private val appContext: Context) {

    fun writeDataToFile(fileName: String, fileData: String) {
        try {
            val fileOutputStream = appContext.openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(fileData.toByteArray())
        } catch (e: Exception) {
            when (e) {
                is FileNotFoundException -> Log.e(
                    TAG,
                    "writeDataToFile - File not found: " + e.message
                )
                is IOException -> Log.e(
                    TAG,
                    "writeDataToFile - IO Exception: " + e.message
                )
                else -> e.printStackTrace()
            }
        }
    }

    fun readDataFromFile(fileName: String): String {
        var fileContents = ""

        try {
            val fileInputStream = appContext.openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()

            var text: String?
            while (run {
                    text = bufferedReader.readLine()
                    text
                } != null) {
                stringBuilder.append(text)
            }

            fileContents = stringBuilder.toString()
        } catch (e: IOException) {
            when (e) {
                is FileNotFoundException -> Log.e(
                    TAG,
                    "readDataFromFile - File not found: " + e.message
                )
                else -> e.printStackTrace()
            }
        }

        return fileContents
    }

    companion object {
        private const val TAG = "FileUtils"
    }
}