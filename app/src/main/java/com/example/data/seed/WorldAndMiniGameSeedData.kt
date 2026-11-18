package com.example.data.seed

import com.example.data.local.AchievementEntity
import com.example.data.local.AnnouncementEntity
import com.example.data.local.InventoryItemEntity
import com.example.data.local.LeaderboardCacheEntity
import com.example.data.local.StageEntity
import com.example.data.local.WorldEntity
import com.example.data.model.DocumentField
import com.example.data.model.MissingFileChallenge
import com.example.data.model.VaultChallenge
import com.example.util.PersianUtils

object WorldAndMiniGameSeedData {

    fun worlds(): List<WorldEntity> = listOf(
        WorldEntity(
            id = 1,
            title = "قلعه خزانه",
            subtitle = "قلمرو اول • مراحل ۱ تا ۵",
            description = "قلعه‌ای با گنبدهای فیروزه‌ای که سلطان کاغذباز ترازنامه‌های آن را جابه‌جا کرده است. حساب‌ها، گاوصندوق‌ها و اسناد مالی را مرتب کنید و اژدهای مغایرت را شکست دهید!",
            bossName = "اژدهای مغایرت",
            stageStart = 1,
            stageEnd = 5,
            isUnlocked = true,
            isFutureContent = false
        ),
        WorldEntity(
            id = 2,
            title = "شهر کارگزینی",
            subtitle = "قلمرو دوم • مراحل ۶ تا ۱۰",
            description = "بازار پرجنب‌وجوش کارگزینی برای شعب، بیمارستان‌ها و مراکز درمانی؛ جایی که احکام تخیلی، محاسبات و مسیرهای اداری به هم گره خورده‌اند.",
            bossName = "غول اضافه‌کاری",
            stageStart = 6,
            stageEnd = 10,
            isUnlocked = false,
            isFutureContent = false
        ),
        WorldEntity(
            id = 3,
            title = "جنگل بخشنامه‌ها",
            subtitle = "قلمرو سوم • مراحل ۱۱ تا ۱۵",
            description = "جنگلی سحرآمیز از درختان سرو که طومارهای کهن و بخشنامه‌های سرگردان میان شاخه‌هایش آویخته‌اند. (در به‌روزرسانی آینده)",
            bossName = "نگهبان طومار گمشده",
            stageStart = 11,
            stageEnd = 15,
            isUnlocked = false,
            isFutureContent = true
        ),
        WorldEntity(
            id = 4,
            title = "برج مکاتبات",
            subtitle = "قلمرو چهارم • مراحل ۱۶ تا ۲۰",
            description = "برج عاج کبوترهای نامه‌رسان و ارجاع‌های سریع میان ستاد، ادارات کل و واحدهای درمانی. (در به‌روزرسانی آینده)",
            bossName = "شاهین بایگانی",
            stageStart = 16,
            stageEnd = 20,
            isUnlocked = false,
            isFutureContent = true
        ),
        WorldEntity(
            id = 5,
            title = "آزمایشگاه هوش و فناوری",
            subtitle = "قلمرو پنجم • مراحل ۲۱ تا ۲۵",
            description = "رصدخانه اسطرلاب‌های برنجی و ماشین‌های جادویی داده که با جدول‌های هوشمند درخشان می‌شوند. (در به‌روزرسانی آینده)",
            bossName = "خودکارساز سرکش",
            stageStart = 21,
            stageEnd = 25,
            isUnlocked = false,
            isFutureContent = true
        ),
        WorldEntity(
            id = 6,
            title = "میدان قهرمانان",
            subtitle = "قلمرو ششم • مراحل ۲۶ تا ۳۰",
            description = "تالار بزرگ رویارویی نهایی با سلطان کاغذباز و جشن بزرگ خانواده تأمین اجتماعی! (در به‌روزرسانی آینده)",
            bossName = "سلطان کاغذباز",
            stageStart = 26,
            stageEnd = 30,
            isUnlocked = false,
            isFutureContent = true
        )
    )

    fun stages(): List<StageEntity> = listOf(
        // World 1 — قلعه خزانه (Playable 1..5)
        StageEntity(
            id = 1, worldId = 1, stageNumber = 1,
            title = "دروازه اسرارآمیز",
            storyIntro = "به دروازه فیروزه‌ای قلعه خزانه خوش آمدید! سلطان کاغذباز قفل عددی ورودی را دستکاری کرده و گربه فندق منتظر رمز درست است.",
            objective = "رمز ورودی دروازه را از روی تراز اولیه محاسبه کنید.",
            miniGameType = "VAULT_PUZZLE", difficulty = 1, xpReward = 100, coinReward = 25,
            isBossStage = false, isUnlocked = true
        ),
        StageEntity(
            id = 2, worldId = 1, stageNumber = 2,
            title = "گاوصندوق گمشده",
            storyIntro = "در تالار دوم، صندوق زرین خزانه باز نمی‌شود! مبلغ ثبت‌شده در دفتر با جمع واقعی فاکتورها اختلاف دارد.",
            objective = "مغایرت دقیق گاوصندوق را پیدا کنید تا در آن باز شود.",
            miniGameType = "VAULT_PUZZLE", difficulty = 1, xpReward = 100, coinReward = 25,
            isBossStage = false, isUnlocked = false
        ),
        StageEntity(
            id = 3, worldId = 1, stageNumber = 3,
            title = "اتاق اسناد",
            storyIntro = "برای پیدا کردن سند گمشده به اتاق اسناد بروید! یکی از فیلدهای پرونده تخیلی تدارکات دستکاری شده است.",
            objective = "فیلد نادرست سند را لمس و اصلاح کنید.",
            miniGameType = "MISSING_FILE", difficulty = 2, xpReward = 100, coinReward = 25,
            isBossStage = false, isUnlocked = false
        ),
        StageEntity(
            id = 4, worldId = 1, stageNumber = 4,
            title = "پل اعداد",
            storyIntro = "پل معلق روی حوض فیروزه‌ای با معمای چرتکه‌ای بسته شده و نگهبان کاغذی راه را سد کرده است.",
            objective = "در نبرد فکری با نگهبان کاغذی، ۳ پرسش را درست پاسخ دهید.",
            miniGameType = "MONSTER_BATTLE", difficulty = 2, xpReward = 100, coinReward = 25,
            isBossStage = false, isUnlocked = false
        ),
        StageEntity(
            id = 5, worldId = 1, stageNumber = 5,
            title = "نبرد با اژدهای مغایرت",
            storyIntro = "اژدهای بامزه اما شلوغ‌کار مغایرت میان کوهی از قبض‌های رنگی نشسته و تا تعادل حساب‌ها برنگردد، کلید شهر کارگزینی را پس نمی‌دهد!",
            objective = "با پاسخ صحیح به ۳ چالش ترکیبی، اژدهای مغایرت را آرام و شکست دهید.",
            miniGameType = "MONSTER_BATTLE", difficulty = 3, xpReward = 150, coinReward = 45,
            isBossStage = true, isUnlocked = false
        ),

        // World 2 — شهر کارگزینی (Playable 6..10)
        StageEntity(
            id = 6, worldId = 2, stageNumber = 6,
            title = "میدان ورود",
            storyIntro = "به شهر پرهیاهوی کارگزینی رسیدید! کاروان‌های اداری بیمارستان‌ها و شعب برای عبور به هماهنگی شما نیاز دارند.",
            objective = "مانده سهمیه تجهیز مرکز تخیلی را از صندوق میدان کشف کنید.",
            miniGameType = "VAULT_PUZZLE", difficulty = 2, xpReward = 100, coinReward = 25,
            isBossStage = false, isUnlocked = false
        ),
        StageEntity(
            id = 7, worldId = 2, stageNumber = 7,
            title = "پرونده مرموز",
            storyIntro = "در بایگانی کلینیک خیالی آفتاب، پرونده‌ای پیدا شده که تاریخ شروع و پایان حکم آن با هم نمی‌خواند.",
            objective = "اطلاعات گمشده یا ناسازگار پرونده تخیلی را بیابید و اصلاح کنید.",
            miniGameType = "MISSING_FILE", difficulty = 2, xpReward = 100, coinReward = 25,
            isBossStage = false, isUnlocked = false
        ),
        StageEntity(
            id = 8, worldId = 2, stageNumber = 8,
            title = "برج محاسبات",
            storyIntro = "در بالای برج ساعت کارگزینی، اعداد حق‌الزحمه تخیلی به هم ریخته‌اند و زنگ برج متوقف شده است.",
            objective = "اختلاف واقعی جدول محاسباتی تخیلی را بیابید.",
            miniGameType = "VAULT_PUZZLE", difficulty = 2, xpReward = 100, coinReward = 25,
            isBossStage = false, isUnlocked = false
        ),
        StageEntity(
            id = 9, worldId = 2, stageNumber = 9,
            title = "راهروی تصمیم‌ها",
            storyIntro = "در راهروی پر از تابلوهای راهنما، کاردستی‌های کاغذی سلطان مراحل یک پرونده آموزشی را جابه‌جا کرده‌اند.",
            objective = "خطای ترتیب فرآیند پرونده را در برگه بازبینی کشف کنید.",
            miniGameType = "MISSING_FILE", difficulty = 3, xpReward = 100, coinReward = 25,
            isBossStage = false, isUnlocked = false
        ),
        StageEntity(
            id = 10, worldId = 2, stageNumber = 10,
            title = "نبرد با غول اضافه‌کاری",
            storyIntro = "غول مهربان اما سرسخت اضافه‌کاری با یک فنجان چای بزرگ و ساعت شنی غول‌پیکر راه ورود به جنگل بخشنامه‌ها را بسته است!",
            objective = "با هوش و دقت در ۳ راند نبرد، غول اضافه‌کاری را شکست دهید.",
            miniGameType = "MONSTER_BATTLE", difficulty = 3, xpReward = 160, coinReward = 50,
            isBossStage = true, isUnlocked = false
        )
    ) + (11..30).map { num ->
        val worldId = ((num - 1) / 5) + 1
        StageEntity(
            id = num,
            worldId = worldId,
            stageNumber = num,
            title = "مرحله ${PersianUtils.toPersianDigits(num)} (به‌زودی)",
            storyIntro = "این مرحله از قلمرو جدید سرزمین صفر در به‌روزرسانی بعدی فعال خواهد شد.",
            objective = "آماده‌سازی برای ماجراجویی‌های آینده",
            miniGameType = "MONSTER_BATTLE",
            difficulty = 3,
            xpReward = 120,
            coinReward = 35,
            isBossStage = num % 5 == 0,
            isUnlocked = false
        )
    }

    val vaultChallenges: Map<Int, VaultChallenge> = mapOf(
        1 to VaultChallenge(
            id = "vault_1",
            stageNumber = 1,
            title = "قفل زرین دروازه قلعه",
            scenario = "حسابدار تخیلی دروازه دو کیسه سکه تحویل گرفته، اما در دفتر کل عدد کمتری نوشته است!",
            lines = listOf(
                "کیسه اول (خزانه شرق)" to "۱٬۲۵۰ سکه",
                "کیسه دوم (خزانه غرب)" to "۲٬۷۵۰ سکه",
                "مجموع واقعی" to "۴٬۰۰۰ سکه",
                "مبلغ ثبت‌شده در دفتر" to "۳٬۸۰۰ سکه"
            ),
            questionPrompt = "اختلاف واقعی بین جمع درست و عدد ثبت‌شده چقدر است؟",
            correctAnswerNumber = 200L,
            unitLabel = "سکه",
            choices = listOf(150L, 200L, 250L, 400L),
            explanation = "جمع واقعی ۴٬۰۰۰ سکه است؛ چون ۳٬۸۰۰ سکه ثبت شده، اختلاف دقیقاً ۲۰۰ سکه می‌شود."
        ),
        2 to VaultChallenge(
            id = "vault_2",
            stageNumber = 2,
            title = "گاوصندوق تالار فیروزه",
            scenario = "برای خرید تخیلی تجهیزات روشنایی قلعه دو فاکتور صادر شده ولی صندوق‌دار جمع نهایی را اشتباه نوشته است.",
            lines = listOf(
                "فاکتور فانوس‌های حیاط" to "۱٬۸۵۰٬۰۰۰ تومان",
                "فاکتور آینه‌کاری ایوان" to "۲٬۶۵۰٬۰۰۰ تومان",
                "مبلغ ثبت‌شده در تراز" to "۴٬۱۵۰٬۰۰۰ تومان"
            ),
            questionPrompt = "مبلغ واقعی کسریِ ثبت‌نشده چند هزار تومان است؟ (فقط عدد را به تومان بنویسید یا انتخاب کنید)",
            correctAnswerNumber = 350000L,
            unitLabel = "تومان",
            choices = listOf(250000L, 350000L, 450000L, 500000L),
            explanation = "۱٬۸۵۰٬۰۰۰ + ۲٬۶۵۰٬۰۰۰ = ۴٬۵۰۰٬۰۰۰ تومان؛ اختلاف با ۴٬۱۵۰٬۰۰۰ برابر ۳۵۰٬۰۰۰ تومان است."
        ),
        6 to VaultChallenge(
            id = "vault_6",
            stageNumber = 6,
            title = "صندوق کاروان میدان ورود",
            scenario = "کاروان تجهیزات کلینیک تخیلی نرگس به میدان شهر کارگزینی رسیده و تعداد بسته‌های تحویلی با برگ بارنامه اختلاف دارد.",
            lines = listOf(
                "محموله اول" to "۶۴۰ بسته",
                "محموله دوم" to "۳۶۰ بسته",
                "برگشتی بین‌راهی" to "۱۲۰ بسته",
                "عدد ثبت‌شده در انبار" to "۹۲۰ بسته"
            ),
            questionPrompt = "اختلاف موجودی واقعی و عدد ثبت‌شده انبار چند بسته است؟",
            correctAnswerNumber = 40L,
            unitLabel = "بسته",
            choices = listOf(20L, 40L, 60L, 80L),
            explanation = "موجودی واقعی ۶۴۰ + ۳۶۰ - ۱۲۰ = ۸۸۰ بسته است؛ با ۹۲۰ بسته دقیقاً ۴۰ بسته اختلاف دارد."
        ),
        8 to VaultChallenge(
            id = "vault_8",
            stageNumber = 8,
            title = "ساعت بزرگ برج محاسبات",
            scenario = "در نمونه آموزشی برج محاسبات، ساعت کار تیم شبانه‌روزی با مبلغ پرداختی هم‌خوانی ندارد.",
            lines = listOf(
                "پاداش پایه تیم تخیلی" to "۵٬۲۰۰٬۰۰۰ تومان",
                "مزایای نوبت شب" to "۱٬۸۰۰٬۰۰۰ تومان",
                "کسور نمونه صندوق رفاه" to "۵۰۰٬۰۰۰ تومان",
                "مبلغ واریزشده در برگ آزمایشی" to "۶٬۲۵۰٬۰۰۰ تومان"
            ),
            questionPrompt = "چه مبلغی (به تومان) باید به برگ آزمایشی اضافه شود تا تراز دقیق شود؟",
            correctAnswerNumber = 250000L,
            unitLabel = "تومان",
            choices = listOf(150000L, 250000L, 350000L, 450000L),
            explanation = "خالص درست ۵٬۲۰۰٬۰۰۰ + ۱٬۸۰۰٬۰۰۰ - ۵۰۰٬۰۰۰ = ۶٬۵۰۰٬۰۰۰ تومان است؛ پس ۲۵۰٬۰۰۰ تومان کم ثبت شده است."
        )
    )

    val missingFileChallenges: Map<Int, MissingFileChallenge> = mapOf(
        3 to MissingFileChallenge(
            id = "file_3",
            stageNumber = 3,
            documentTitle = "سند تخیلی تدارکات باغ فیروزه",
            documentCode = "سند نمونه: خز-۴۰۳/۱۲",
            briefing = "سلطان کاغذباز یکی از سطرهای این سند ساختگی را دستکاری کرده است. ابتدا فیلد مشکوک را لمس کنید و سپس اصلاح درست را برگزینید.",
            fields = listOf(
                DocumentField("unit", "واحد درخواست‌کننده (تخیلی)", "درمانگاه گلستان سحر", false, ""),
                DocumentField("item", "شرح قلم", "۴۰ عدد چراغ مطالعه رومیزی", false, ""),
                DocumentField("unit_price", "قیمت واحد", "۲۰۰٬۰۰۰ تومان", false, ""),
                DocumentField("total", "جمع کل ثبت‌شده", "۶٬۰۰۰٬۰۰۰ تومان", true, "حاصل‌ضرب ۴۰ × ۲۰۰٬۰۰۰ با عدد ثبت‌شده نمی‌خواند!")
            ),
            fixQuestion = "مبلغ صحیح برای جمع کل این سند تخیلی کدام است؟",
            fixOptions = listOf("۷٬۰۰۰٬۰۰۰ تومان", "۸٬۰۰۰٬۰۰۰ تومان", "۸٬۴۰۰٬۰۰۰ تومان", "۹٬۰۰۰٬۰۰۰ تومان"),
            correctFixIndex = 1,
            explanation = "۴۰ عدد ضربدر ۲۰۰٬۰۰۰ تومان دقیقاً برابر ۸٬۰۰۰٬۰۰۰ تومان است."
        ),
        7 to MissingFileChallenge(
            id = "file_7",
            stageNumber = 7,
            documentTitle = "حکم آزمایشی مأموریت کاروان",
            documentCode = "پرونده تخیلی: کز-۱۴۰۵/۸۸",
            briefing = "در این پرونده کاملاً تخیلی، یک ناسازگاری زمانی عجیب پنهان شده است. فیلد مشکل‌دار را پیدا کنید!",
            fields = listOf(
                DocumentField("hero", "نام مستعار قهرمان", "سهراب پرنده", false, ""),
                DocumentField("branch", "محل خدمت تخیلی", "شعبه خیالی سپیده", false, ""),
                DocumentField("start", "تاریخ آغاز مأموریت", "۱۵ مهر ۱۴۰۵", false, ""),
                DocumentField("end", "تاریخ پایان مأموریت", "۱۰ شهریور ۱۴۰۵", true, "تاریخ پایان پیش از تاریخ آغاز ثبت شده است!")
            ),
            fixQuestion = "کدام اصلاح تاریخ این پرونده تخیلی را منطقی و کامل می‌کند؟",
            fixOptions = listOf("۱۵ شهریور ۱۴۰۵", "۲۰ مهر ۱۴۰۵", "۱ فروردین ۱۴۰۴", "ثبت بدون تاریخ"),
            correctFixIndex = 1,
            explanation = "تاریخ پایان باید پس از ۱۵ مهر ۱۴۰۵ باشد؛ بنابراین «۲۰ مهر ۱۴۰۵» صحیح است."
        ),
        9 to MissingFileChallenge(
            id = "file_9",
            stageNumber = 9,
            documentTitle = "برگه گردش پرونده مرخصی",
            documentCode = "فرم تخیلی: رپ-۷۰۲",
            briefing = "در راهروی تصمیم‌ها، یکی از مراحل یا فیلدهای این پرونده تخیلی جا افتاده یا نادرست چیده شده است.",
            fields = listOf(
                DocumentField("requester", "نام مستعار متقاضی", "بانو مریم پاییزی", false, ""),
                DocumentField("days", "مدت درخواست", "۳ روز کاری", false, ""),
                DocumentField("balance", "تأییدیه مانده مرخصی", "بدون بررسی — ثبت مستقیم در بایگانی نهایی!", true, "بدون بررسی مانده و تأیید سرپرست، پرونده مستقیماً بایگانی شده است!"),
                DocumentField("date", "تاریخ ثبت", "۱۲ آبان ۱۴۰۵", false, "")
            ),
            fixQuestion = "گام صحیح پس از ثبت درخواست در فرآیند نمونه کدام است؟",
            fixOptions = listOf("حذف پرونده", "بررسی مانده و سپس تأیید مسئول مربوطه", "چاپ ده نسخه اضافه", "بستن سیستم بدون ذخیره"),
            correctFixIndex = 1,
            explanation = "توالی استاندارد اداری شامل ثبت درخواست، بررسی مانده و تأیید مسئول پیش از ثبت نهایی است."
        )
    )

    fun achievements(): List<AchievementEntity> = listOf(
        AchievementEntity(
            id = "ach_first_step",
            title = "نخستین گام در سرزمین صفر",
            description = "اولین مرحله قلعه خزانه را با موفقیت به پایان برسانید.",
            medalReward = 1,
            coinReward = 30,
            isUnlocked = false,
            progress = 0,
            target = 1,
            iconKey = "star"
        ),
        AchievementEntity(
            id = "ach_vault_master",
            title = "کلیددار گاوصندوق‌ها",
            description = "۲ چالش گاوصندوق اسرارآمیز را حل کنید.",
            medalReward = 2,
            coinReward = 50,
            isUnlocked = false,
            progress = 0,
            target = 2,
            iconKey = "vault"
        ),
        AchievementEntity(
            id = "ach_treasure_hunter",
            title = "کارآگاه تیزبین اسناد",
            description = "۲ چالش پرونده گمشده را بدون اشتباه کامل کنید.",
            medalReward = 2,
            coinReward = 60,
            isUnlocked = false,
            progress = 0,
            target = 2,
            iconKey = "scroll"
        ),
        AchievementEntity(
            id = "ach_dragon_tamer",
            title = "رام‌کننده اژدهای مغایرت",
            description = "مرحله ۵ (رئیس قلعه خزانه) را با پیروزی پشت سر بگذارید.",
            medalReward = 3,
            coinReward = 100,
            isUnlocked = false,
            progress = 0,
            target = 1,
            iconKey = "dragon"
        ),
        AchievementEntity(
            id = "ach_giant_slayer",
            title = "فاتح شهر کارگزینی",
            description = "مرحله ۱۰ (غول اضافه‌کاری) را شکست دهید و دروازه قلمرو سوم را ببینید.",
            medalReward = 4,
            coinReward = 150,
            isUnlocked = false,
            progress = 0,
            target = 1,
            iconKey = "crown"
        ),
        AchievementEntity(
            id = "ach_daily_hero",
            title = "قهرمان روزانه",
            description = "مأموریت روزانه «سه پاسخ صحیح بده» را کامل کنید.",
            medalReward = 1,
            coinReward = 40,
            isUnlocked = false,
            progress = 0,
            target = 1,
            iconKey = "target"
        ),
        AchievementEntity(
            id = "ach_gold_collector",
            title = "کلکسیونر نشان طلا",
            description = "در ۳ مرحله مختلف نشان طلایی کسب کنید.",
            medalReward = 2,
            coinReward = 80,
            isUnlocked = false,
            progress = 0,
            target = 3,
            iconKey = "trophy"
        ),
        AchievementEntity(
            id = "ach_bazaar_buyer",
            title = "خریدار خوش‌سلیقه بازارچه",
            description = "اولین آیتم تزئینی خود را از کوله‌پشتی و بازارچه تهیه کنید.",
            medalReward = 1,
            coinReward = 25,
            isUnlocked = false,
            progress = 0,
            target = 1,
            iconKey = "bag"
        )
    )

    fun inventoryItems(): List<InventoryItemEntity> = listOf(
        InventoryItemEntity("item_frame_turquoise", "قاب کاشی فیروزه‌ای", "قاب اصیل فیروزه‌ای با نقش اسلیمی برای پروفایل شما", "FRAME", 0, isOwned = true, isEquipped = true),
        InventoryItemEntity("item_pet_fandogh", "گربه نارنجی فندق", "همراه وفادار با کلاه سنتی و قلاده فیروزه‌ای", "COMPANION", 0, isOwned = true, isEquipped = true),
        InventoryItemEntity("item_frame_saffron", "قاب زرین شاهنامه", "حاشیه طلایی با الهام از تذهیب نسخ خطی ایرانی", "FRAME", 80, isOwned = false, isEquipped = false),
        InventoryItemEntity("item_frame_lapis", "قاب لاجورد نصف‌جهان", "کاشی‌کاری لاجوردی با ستاره‌های هشت‌پر", "FRAME", 140, isOwned = false, isEquipped = false),
        InventoryItemEntity("item_pet_simorgh", "جوجه‌سیمرغ طلایی", "همراه افسانه‌ای کوچک که در حیاط کاخ پرواز می‌کند", "COMPANION", 180, isOwned = false, isEquipped = false),
        InventoryItemEntity("item_pet_hoopoe", "هدهد نامه‌رسان", "پیک دانای قصه‌های کهن با تاج رنگین", "COMPANION", 120, isOwned = false, isEquipped = false),
        InventoryItemEntity("item_title_tamin", "عنوان: یاور برتر تأمین", "نمایش عنوان افتخاری زیر نام مستعار شما", "TITLE", 90, isOwned = false, isEquipped = false),
        InventoryItemEntity("item_title_explorer", "عنوان: کاوشگر اسرار", "عنوان ویژه برای کاوشگران قلعه و شهر", "TITLE", 130, isOwned = false, isEquipped = false),
        InventoryItemEntity("item_emblem_abacus", "نشان چرتکه زرین", "یادگار تالار حسابداران قلعه خزانه", "EMBLEM", 60, isOwned = false, isEquipped = false),
        InventoryItemEntity("item_emblem_lamp", "فانوس فیروزه‌ای دانایی", "روشنایی‌بخش راهروهای بایگانی", "EMBLEM", 110, isOwned = false, isEquipped = false)
    )

    fun announcements(): List<AnnouncementEntity> = listOf(
        AnnouncementEntity(
            id = "ann_weekly_special",
            title = "جشنواره هفته قهرمانان تأمین",
            body = "همکاران گرامی در شعب، بیمارستان‌ها، کلینیک‌ها و ستاد! تا پایان هفته با تکمیل مراحل قلعه خزانه، جایزه ویژه هفتگی صندوقچه زرین و XP اضافه دریافت کنید.",
            badgeText = "مأموریت ویژه",
            jalaliDate = PersianUtils.currentJalaliDate(),
            isSpecialEvent = true,
            daysRemaining = 3
        ),
        AnnouncementEntity(
            id = "ann_welcome",
            title = "به دنیای شگفت‌انگیز پارسمیندا خوش آمدید!",
            body = "در سرزمین صفر، سلطان کاغذباز اسناد را به هم ریخته است. آماده شوید تا بدون نیاز به اینترنت هم بازی کنید و هنگام اتصال به شبکه، امتیاز خود را همگام‌سازی نمایید.",
            badgeText = "اطلاعیه رسمی",
            jalaliDate = PersianUtils.currentJalaliDate(),
            isSpecialEvent = false,
            daysRemaining = 0
        ),
        AnnouncementEntity(
            id = "ann_world3_preview",
            title = "پیش‌نمایش قلمرو سوم: جنگل بخشنامه‌ها",
            body = "تیم هنری پارسمیندا در حال تکمیل مراحل ۱۱ تا ۱۵ در جنگل بخشنامه‌هاست. با شکست غول اضافه‌کاری در مرحله ۱۰، پیش‌نمایش آن را ببینید!",
            badgeText = "آینده نزدیک",
            jalaliDate = PersianUtils.currentJalaliDate(),
            isSpecialEvent = false,
            daysRemaining = 0
        )
    )

    fun initialLeaderboard(): List<LeaderboardCacheEntity> = listOf(
        LeaderboardCacheEntity("lb_1", 1, "مهندس پویا", "hesabgar", 7, 12500, isCurrentUser = false, isVerifiedOnline = false),
        LeaderboardCacheEntity("lb_2", 2, "کارآگاه دانا", "karagah", 6, 10800, isCurrentUser = false, isVerifiedOnline = false),
        LeaderboardCacheEntity("lb_3", 3, "نابغه اکسل", "excel", 5, 9200, isCurrentUser = false, isVerifiedOnline = false),
        LeaderboardCacheEntity("lb_4", 4, "جادوگر بهار", "jadoogar", 4, 6400, isCurrentUser = false, isVerifiedOnline = false),
        LeaderboardCacheEntity("lb_5", 5, "فرمانده سپهر", "farmandeh", 3, 3900, isCurrentUser = false, isVerifiedOnline = false)
    )
}
