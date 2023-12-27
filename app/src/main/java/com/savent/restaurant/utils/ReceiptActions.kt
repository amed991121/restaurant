package com.savent.restaurant.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.savent.restaurant.R
import com.savent.restaurant.toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun Activity.sendReceiptByEmail(email: String?, noteUri: Uri) {
    try {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, Array(1) { email })
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.receipt_subject))
            packageManager.queryIntentActivities(this, PackageManager.MATCH_ALL).forEach {
                grantUriPermission(
                    it.activityInfo.packageName,
                    noteUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            putExtra(Intent.EXTRA_STREAM, noteUri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(Intent.createChooser(this, getString(R.string.share_receipt_header)))
        }
    } catch (ex: ActivityNotFoundException) {
        toast(Message.StringResource(R.string.mail_app_not_found))
    }

}

fun Activity.sendReceiptBySms(phoneNumber: Long?, note: String) {
    try {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("smsto:+${phoneNumber}")
            putExtra("sms_body", note)
            startActivity(Intent.createChooser(this, getString(R.string.share_receipt_header)))
        }
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        toast(Message.StringResource(R.string.sms_app_not_found))
    }

}

fun Activity.sendReceiptByWhatsApp(phoneNumber: Long?, noteUri: Uri) {
    try {
        Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            packageManager.queryIntentActivities(this, PackageManager.MATCH_ALL).forEach {
                grantUriPermission(
                    it.activityInfo.packageName,
                    noteUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            putExtra("jid", "$phoneNumber@s.whatsapp.net")
            putExtra(Intent.EXTRA_STREAM, noteUri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            setPackage("com.whatsapp")
            startActivity(this)
        }


    } catch (e: ActivityNotFoundException) {
        toast(Message.StringResource(R.string.whatsapp_not_found))
    }

}

fun Activity.saveReceiptFile(note: String): Resource<File>{
    val file = File(
        filesDir,
        getString(R.string.file_receipt_name) +
                "-${System.currentTimeMillis()}.txt"
    )
    return try {
        val fos= FileOutputStream(file)
        fos.write(note.toByteArray())
        fos.close()
        Resource.Success(file)
    }catch (e: IOException){
        e.printStackTrace()
        Resource.Error(Message.StringResource(R.string.save_receipt_error))
    }
}