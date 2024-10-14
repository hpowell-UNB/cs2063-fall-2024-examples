package ca.unb.mobiledev.readwritedemo

import android.content.Context
import android.util.Log
import ca.unb.mobiledev.readwritedemo.FileUtils.Companion
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

class FileUtilsV2(private val appContext: Context) {

    fun writeDataToFile(fileName: String, fileData: String) {
        try {
            appContext.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
                fos.write(fileData.toByteArray())
            }
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
        return try {
            appContext.openFileInput(fileName).use { fis ->
                InputStreamReader(fis).use { isr ->
                    BufferedReader(isr).use { br ->
                        val stringBuilder = StringBuilder()
                        br.useLines { lines ->
                            lines.forEach { text ->
                                stringBuilder.append(text)
                            }
                        }

                        stringBuilder.toString()
                    }
                }
            }
        } catch (e: IOException) {
            when (e) {
                is FileNotFoundException -> Log.e(
                    TAG,
                    "readDataFromFile - File not found: " + e.message
                )
                else -> e.printStackTrace()
            }
            ""
        }
    }

    companion object {
        private const val TAG = "FileUtilsV2"
    }
}