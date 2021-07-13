package com.project.design.utils


import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Build
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.thecode.aestheticdialogs.*
import com.project.design.BuildConfig
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import kotlin.collections.ArrayList


fun checkAboveOreo(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.O
}

fun checkAboveKitkat(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT
}

fun checkAboveLollipop(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP
}

fun isAboveR(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.R
}

fun isAboveP(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.P
}


fun Context.rateUs() {

    try {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("amzn://apps/android?p=$packageName")
            )
        )
    } catch (e: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=$packageName")
            )
        )
    }

}


fun getAfterDate(count: Int = 0): String {

    val currentCalendar = Calendar.getInstance()
    val myFormat = "MMM dd ,yyyy"  // you can use your own date format
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

    currentCalendar.add(Calendar.DAY_OF_YEAR, count)


    return sdf.format(currentCalendar.time)
}


fun get1DayBeforeDate():String{
    val  cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -1);
    return SimpleDateFormat.getDateInstance().format(cal.time)

}


fun getTwoDecimalPlaces(amount: Double): String {


    val a = Math.round(amount).toInt()

    val df: DecimalFormat = DecimalFormat("#.##")
    val maltRequiredString: String = df.format(amount)
    return a.toString()

}

fun getTodaysDate(): String {
    return SimpleDateFormat.getDateInstance().format(Date())
}

fun getCurrentTime(): String {

    val date = Date(System.currentTimeMillis())
    val dateFormat = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
    val time = dateFormat.format(date)
    return time

}

fun getURLForResource(resourceId: Int): String? {

    return Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/" + resourceId)
        .toString()
}


fun getTimeDiff(time: String): ArrayList<Int> {

    val format = SimpleDateFormat("hh:mm aa")
    val date1 = format.parse(time)
    val date2 = format.parse(getCurrentTime())
    val mills = date1.time - date2.time

    val hours = (mills / (1000 * 60 * 60)).toInt()
    val mins = (mills / (1000 * 60)).toInt() % 60

    val diff = "$hours:$mins"

    val diffList: ArrayList<Int> = arrayListOf(hours, mins)
    return diffList

}


fun checkTimeLessThanCurrentTime(time: String?): Boolean {

    val format = SimpleDateFormat("hh:mm aa")
    // time of game
    val date1 = format.parse(time).time
    // current time
    val date2 = format.parse(getCurrentTime()).time


    return (date2 >= date1)

}

fun Activity.showCustomDialog(title:String, message:String, isCancelable:Boolean, dialogStyle: DialogStyle, dialogType: DialogType, dialogAnimation: DialogAnimation){


    AestheticDialog.Builder(this, dialogStyle, dialogType)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(isCancelable)
        .setDarkMode(false)
        .setGravity(Gravity.CENTER)
        .setAnimation(dialogAnimation)
        .setOnClickListener(object : OnDialogClickListener {
            override fun onClick(dialog: AestheticDialog.Builder) {
                dialog.dismiss()
            }
        })
        .show()

}

fun getDaysRemaining(endDate: String): Int {

    val cal = Calendar.getInstance()
    val myFormat = "MMM dd, yyyy"  // you can use your own date format
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

    var daysDiff = ""


    var diffDays = 0
    try {

        val startDate = sdf.parse(getTodaysDate())
        val endDate = sdf.parse(endDate)
        val diff = endDate.time - startDate.time
        diffDays = (diff / (24 * 60 * 60 * 1000)).toInt()
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return diffDays


}

fun Context.copyToClipboard(text: CharSequence){
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    clipboard?.setPrimaryClip(ClipData.newPlainText("",text))
}