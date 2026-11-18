package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import java.util.Calendar
import java.util.TimeZone

object PersianUtils {
    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    private val jalaliMonths = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    fun toPersianDigits(input: String): String {
        val sb = StringBuilder(input.length)
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toPersianDigits(number: Int): String = toPersianDigits(number.toString())

    fun toPersianDigits(number: Long): String = toPersianDigits(number.toString())

    fun formatPersianNumber(number: Long): String {
        val raw = number.toString()
        val neg = raw.startsWith("-")
        val digits = if (neg) raw.substring(1) else raw
        val sb = StringBuilder()
        var count = 0
        for (i in digits.length - 1 downTo 0) {
            sb.append(digits[i])
            count++
            if (count % 3 == 0 && i != 0) {
                sb.append('٬')
            }
        }
        val formatted = sb.reverse().toString()
        val withSign = if (neg) "-$formatted" else formatted
        return toPersianDigits(withSign)
    }

    fun formatPersianNumber(number: Int): String = formatPersianNumber(number.toLong())

    fun formatToman(amount: Long): String = "${formatPersianNumber(amount)} تومان"

    fun currentJalaliDate(): String = millisToJalaliDate(System.currentTimeMillis())

    fun millisToJalaliDate(epochMillis: Long): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"))
        cal.timeInMillis = epochMillis
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        val monthName = jalaliMonths.getOrElse(jm - 1) { "" }
        return "${toPersianDigits(jd)} $monthName ${toPersianDigits(jy)}"
    }

    fun millisToJalaliDateTime(epochMillis: Long): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"))
        cal.timeInMillis = epochMillis
        val hour = cal.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
        val min = cal.get(Calendar.MINUTE).toString().padStart(2, '0')
        return "${millisToJalaliDate(epochMillis)} • ${toPersianDigits("$hour:$min")}"
    }

    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gdm = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
        val gy2 = if (gm > 2) gy + 1 else gy
        var days = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd + gdm[gm - 1]
        var jy = -1595 + (33 * (days / 12053))
        days %= 12053
        jy += 4 * (days / 1461)
        days %= 1461
        if (days > 365) {
            jy += (days - 1) / 365
            days = (days - 1) % 365
        }
        val jm: Int
        val jd: Int
        if (days < 186) {
            jm = 1 + (days / 31)
            jd = 1 + (days % 31)
        } else {
            jm = 7 + ((days - 186) / 30)
            jd = 1 + ((days - 186) % 30)
        }
        return Triple(jy, jm, jd)
    }
}

object GameAudioManager {
    fun playSuccessChime(enabled: Boolean) {
        if (!enabled) return
        playTone(ToneGenerator.TONE_PROP_ACK, 120)
    }

    fun playClick(enabled: Boolean) {
        if (!enabled) return
        playTone(ToneGenerator.TONE_PROP_BEEP, 45)
    }

    fun playError(enabled: Boolean) {
        if (!enabled) return
        playTone(ToneGenerator.TONE_PROP_NACK, 140)
    }

    fun playVictory(enabled: Boolean) {
        if (!enabled) return
        playTone(ToneGenerator.TONE_PROP_PROMPT, 220)
    }

    private fun playTone(tone: Int, durationMs: Int) {
        try {
            val tg = ToneGenerator(AudioManager.STREAM_MUSIC, 40)
            tg.startTone(tone, durationMs)
        } catch (_: Exception) {
            // Audio hardware unavailable in headless/test environments
        }
    }
}
