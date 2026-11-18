package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.JadeEmerald
import com.example.ui.theme.LapisBlue
import com.example.ui.theme.PersianTurquoise
import com.example.ui.theme.RoyalViolet
import com.example.ui.theme.RubyCrimson
import com.example.ui.theme.SaffronGold

enum class MiniGameType(val persianTitle: String, val persianSubtitle: String) {
    VAULT_PUZZLE("گاوصندوق اسرارآمیز", "محاسبه و کشف مغایرت‌های عددی"),
    MISSING_FILE("پرونده گمشده", "بررسی سند تخیلی و یافتن اطلاعات نادرست یا گمشده"),
    MONSTER_BATTLE("نبرد با هیولا", "پاسخ سریع و شکست هیولای کاغذبازی")
}

enum class StageRating(val persianTitle: String, val stars: Int) {
    NONE("تکمیل‌نشده", 0),
    BRONZE("نشان برنزی", 1),
    SILVER("نشان نقره‌ای", 2),
    GOLD("نشان طلایی", 3)
}

enum class QuestionCategory(val persianTitle: String) {
    SPECIALIZED("تخصصی و اداری"),
    ENTERTAINMENT("سرگرمی و هوش")
}

enum class SyncStatus(val persianLabel: String) {
    PENDING_VERIFICATION("در انتظار همگام‌سازی"),
    SYNCED_PENDING_REVIEW("ارسال‌شده • در انتظار تأیید سرور"),
    VERIFIED("تأییدشده در تالار قهرمانان"),
    REJECTED("نیازمند بررسی مجدد"),
    SYNC_FAILED("خطا در ارسال • آماده تلاش مجدد")
}

data class HeroCharacter(
    val id: String,
    val name: String,
    val title: String,
    val bio: String,
    val personalityTag: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val motifName: String
)

data class LevelRank(
    val level: Int,
    val title: String,
    val minXp: Int,
    val nextLevelXp: Int
)

object LevelProgression {
    val ranks = listOf(
        LevelRank(1, "تازه‌وارد سرزمین صفر", 0, 200),
        LevelRank(2, "جستجوگر", 200, 450),
        LevelRank(3, "ماجراجو", 450, 750),
        LevelRank(4, "کاوشگر", 750, 1100),
        LevelRank(5, "استاد مأموریت", 1100, 1550),
        LevelRank(6, "قهرمان قلمرو", 1550, 2100),
        LevelRank(7, "فرمانده", 2100, 2800),
        LevelRank(8, "افسانه سرزمین صفر", 2800, 4000)
    )

    fun rankForXp(xp: Int): LevelRank {
        return ranks.lastOrNull { xp >= it.minXp } ?: ranks.first()
    }
}

object HeroCatalog {
    val heroes = listOf(
        HeroCharacter(
            id = "hesabgar",
            name = "استاد حسابگر",
            title = "نگهبان ترازنامه خزانه",
            bio = "با چرتکه جادویی و گربه نارنجی وفادارش «فندق»، پیچیده‌ترین مغایرت‌های عددی قلعه خزانه را در یک چشم به هم زدن کشف می‌کند.",
            personalityTag = "دقیق و خوش‌مشرب",
            primaryColor = PersianTurquoise,
            secondaryColor = SaffronGold,
            motifName = "چرتکه زرین"
        ),
        HeroCharacter(
            id = "karagah",
            name = "کارآگاه پرونده",
            title = "مفتش اسناد مرموز",
            bio = "ذره‌بین کهربایی‌اش کوچک‌ترین خطای ثبت‌شده در پرونده‌های تخیلی را شکار می‌کند و هیچ جاخالی‌ای از چشم تیزبینش پنهان نمی‌ماند.",
            personalityTag = "تیزبین و کنجکاو",
            primaryColor = LapisBlue,
            secondaryColor = SaffronGold,
            motifName = "ذره‌بین یاقوت"
        ),
        HeroCharacter(
            id = "jadoogar",
            name = "جادوگر بخشنامه",
            title = "حافظ طومارهای کهن",
            bio = "طومارهای درهم‌ریخته سلطان کاغذباز را با جادوی نظم و دانایی دوباره مرتب می‌کند و آرامش را به قلمروها بازمی‌گرداند.",
            personalityTag = "دانا و باحوصله",
            primaryColor = RoyalViolet,
            secondaryColor = PersianTurquoise,
            motifName = "طومار فیروزه‌ای"
        ),
        HeroCharacter(
            id = "farmandeh",
            name = "فرمانده مکاتبات",
            title = "سفیر نامه‌های بالدار",
            bio = "پیک‌های سریع سرزمین را هدایت می‌کند تا هیچ مأموریتی در بیمارستان‌ها، کلینیک‌ها و شعب معطل نماند.",
            personalityTag = "سریع و مصمم",
            primaryColor = RubyCrimson,
            secondaryColor = SaffronGold,
            motifName = "پرچم شاهنامه"
        ),
        HeroCharacter(
            id = "excel",
            name = "نابغه اکسل",
            title = "معمار جدول‌های جادویی",
            bio = "ستون‌ها و ردیف‌های جادویی را به هم گره می‌زند و عددهای سرگردان را به سازه‌های استوار تبدیل می‌کند.",
            personalityTag = "خلاق و هوشمند",
            primaryColor = JadeEmerald,
            secondaryColor = SaffronGold,
            motifName = "جدول زمردین"
        ),
        HeroCharacter(
            id = "nashnas",
            name = "قهرمان ناشناس",
            title = "مسافر نقاب‌دار سرزمین",
            bio = "از دوردست‌ترین مراکز درمانی و شعب آمده تا بی‌نام‌ونشان و فقط با تکیه بر مهارت خود، تعادل را به شش قلمرو برگرداند.",
            personalityTag = "مرموز و شجاع",
            primaryColor = Color(0xFF3949AB),
            secondaryColor = Color(0xFFFFB300),
            motifName = "نقاب لاجوردی"
        )
    )

    fun byId(id: String): HeroCharacter = heroes.firstOrNull { it.id == id } ?: heroes.first()
}

data class VaultChallenge(
    val id: String,
    val stageNumber: Int,
    val title: String,
    val scenario: String,
    val lines: List<Pair<String, String>>,
    val questionPrompt: String,
    val correctAnswerNumber: Long,
    val unitLabel: String,
    val choices: List<Long>,
    val explanation: String
)

data class DocumentField(
    val key: String,
    val label: String,
    val value: String,
    val isSuspicious: Boolean,
    val correctionHint: String
)

data class MissingFileChallenge(
    val id: String,
    val stageNumber: Int,
    val documentTitle: String,
    val documentCode: String,
    val briefing: String,
    val fields: List<DocumentField>,
    val fixQuestion: String,
    val fixOptions: List<String>,
    val correctFixIndex: Int,
    val explanation: String
)
