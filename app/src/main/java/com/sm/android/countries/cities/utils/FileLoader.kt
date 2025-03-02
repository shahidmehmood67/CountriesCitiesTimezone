package com.sm.android.countries.cities.utils

import android.content.Context
import java.io.File
import java.io.IOException
import java.io.InputStream

object FileLoader {

    fun loadFile(fileName: String, context: Context): String {
        var tContents: String? = ""

        try {
            val stream: InputStream = context.assets.open(fileName)
            val buffer = ByteArray(stream.available())
            stream.read(buffer)
            stream.close()
            tContents = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return tContents!!
    }

    fun getQuranFormattedFileNumbers(index: Int) =
        if (index < 10) "00$index" else if (index < 100) "0$index" else "" + index

    fun getHadithFormattedFileNumbers(index: Int) = if (index < 10) "0$index" else "" + index

    fun removeCharacter(str: String) = str.replace("\r", "")

    fun getFilePath(directory: String, filename: String, context: Context): String {
        val myDir = context.getDir(directory, Context.MODE_PRIVATE)
        val file = File(myDir, filename)
        return file.path.toString()
    }

    fun getTranslationFile(context: Context, languageKey: String, surahId: Int): String {
        val myDir = context.getDir(Constants.TRANSLATION_FOLDER, Context.MODE_PRIVATE)
        val folder = File(myDir, languageKey)
        if (folder.exists()) {
            val file = File(folder, "${getQuranFormattedFileNumbers(surahId)}.txt")
            if (file.exists()) {
                val stream = file.inputStream()
                val buffer = ByteArray(stream.available())
                stream.read(buffer)
                stream.close()
                return String(buffer)
            }
        }
        return ""
    }

    fun checkTranslationFilesExistInDirectory(context: Context, languageKey: String): Boolean {
        val dir = context.getDir(Constants.TRANSLATION_FOLDER, Context.MODE_PRIVATE)
        val folder = File(dir.path, languageKey)
        if (!folder.exists()) {
            return false
        }
        var count = 0
        folder.walk().forEach {
            if (it.exists()) {
                count++
            }
        }
        return count >= 114
    }
}