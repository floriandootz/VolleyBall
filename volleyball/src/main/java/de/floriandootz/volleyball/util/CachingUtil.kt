package de.floriandootz.volleyball.util

import android.content.Context
import android.net.Uri
import android.support.annotation.RawRes
import de.floriandootz.volleyball.parse.Parser
import java.io.*

object CachingUtil {

    fun cacheExists(ctx: Context, url: String): Boolean {
        val filename = Uri.parse(url).lastPathSegment ?: return false
        val file = File(ctx.cacheDir, filename)
        return file.exists()
    }

    fun writeCache(ctx: Context, jsonString: String, url: String) {
        val filename = Uri.parse(url).lastPathSegment ?: return
        writeFile(ctx, jsonString, filename)
    }

    private fun writeFile(ctx: Context, jsonString: String, filename: String) {
        try {
            val file = File(ctx.cacheDir, filename)
            val fileWriter = FileWriter(file)
            fileWriter.write(jsonString)
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun <T> readCache(ctx: Context, url: String, customParser: Parser<T>): T? {
        val filename = Uri.parse(url).lastPathSegment ?: return null
        val cacheString: String = readFile(ctx, filename) ?: return null
        return customParser.parse(cacheString, null)
    }

    private fun readFile(ctx: Context, filename: String): String? {
        val file = File(ctx.cacheDir, filename)
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        if (!file.exists()) return null
        try {
            val fileReader = FileReader(file)
            val reader: Reader = BufferedReader(fileReader)
            // Reading the contents of the file , line by line
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
            writer.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return writer.toString()
    }

    fun <T> readRawAndroidResource(ctx: Context, @RawRes resId: Int, parser: Parser<T>): T {
        val inputStream = ctx.resources.openRawResource(resId)
        val inputReader = InputStreamReader(inputStream)
        val buffReader = BufferedReader(inputReader)
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        try {
            val reader: Reader = BufferedReader(buffReader)
            // Reading the contents of the file , line by line
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return parser.parse(writer.toString(), null)
    }

}
