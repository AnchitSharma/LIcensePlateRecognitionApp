package com.qbs.platereader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface

open class Utils{
    companion object{

        fun removeWhiteSpace(str: String):String{
            var sentence:String
            sentence = str.replace("\\s".toRegex(), "")
            return sentence
        }

        fun optimizeBitmap(filePath: String): Bitmap? {
            // bitmap factory

            val ei = ExifInterface(filePath)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val options = BitmapFactory.Options()

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 3

            var bitmap = BitmapFactory.decodeFile(filePath, options)

            var rotatedBitmap: Bitmap? = null
            when (orientation) {

                ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90F)

                ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(bitmap, 180F)

                ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(bitmap, 270F)

                ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
                else -> rotatedBitmap = bitmap
            }



            return rotatedBitmap
        }

        fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(
                source, 0, 0, source.width, source.height,
                matrix, true
            )
        }
    }
}