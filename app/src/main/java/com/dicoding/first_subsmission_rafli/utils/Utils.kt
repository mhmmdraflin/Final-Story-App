package com.dicoding.first_subsmission_rafli.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.dicoding.first_subsmission_rafli.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAX_FILE_SIZE = 1000000
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"


fun getImageUri(context: Context): Uri {
    val filename = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/MyCamera/"
            )
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: getImageUriForPreQ(context)
    } else {
        getImageUriForPreQ(context)
    }
}

private fun getImageUriForPreQ(context: Context): Uri {
    val filename = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    // Ambil direktori external files, dan periksa apakah `null`
    val files = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ?: throw IllegalStateException("External files directory not available")

    // Buat file untuk gambar
    val imageFile = File(files, "/MyCamera/$filename.jpg")

    // Pastikan parent directory ada dan bisa dibuat
    val parentFile = imageFile.parentFile
    if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
        throw IllegalStateException("Failed to create directory: ${parentFile.absolutePath}")
    }

    // Kembalikan URI file provider
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}

fun createCustomTempFile(context: Context): File {
    val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

fun uriToFile(imageUri: Uri, context: Context): File {
    val file = createCustomTempFile(context)
    context.contentResolver.openInputStream(imageUri)?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file
}

fun File.reduceFileImage(): File {
    val bitmap = BitmapFactory.decodeFile(this.path)?.getRotatedBitmap(this)
    bitmap?.let {
        var compressQuality = 100
        var streamLength: Int
        val bmpStream = ByteArrayOutputStream()
        do {
            bmpStream.reset()
            it.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            streamLength = bmpStream.size()
            compressQuality -= 5
        } while (streamLength > MAX_FILE_SIZE && compressQuality > 0)

        FileOutputStream(this).use { fos ->
            fos.write(bmpStream.toByteArray())
            fos.flush()
        }
    }
    return this
}

fun Bitmap.getRotatedBitmap(file: File): Bitmap {
    return try {
        // Baca metadata EXIF dari file
        val ei = ExifInterface(file)
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        // Tentukan rotasi berdasarkan orientasi
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
            else -> this // Tidak ada rotasi
        }
    } catch (e: Exception) {
        // Jika terjadi error, log dan kembalikan bitmap asli
        e.printStackTrace()
        this
    }
}


fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}
