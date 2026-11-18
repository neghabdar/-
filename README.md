# پارسمیندا و قهرمانان تأمین (Parsminda: Heroes of Social Security)

**شعار بازی:** «اینجا هر پرونده یک ماجراست!»

یک بازی ماجراجویی فانتزی بومی اندروید (Offline-First) با هویت بصری اصیل ایرانی برای خانواده بزرگ سازمان تأمین اجتماعی (شعب، بیمارستان‌ها، مراکز درمانی، کلینیک‌ها، ادارات کل استان‌ها و ستاد مرکزی).

---

## ۱. معماری پروژه

پروژه با زبان **Kotlin**، رابط کاربری **Jetpack Compose + Material 3** (کاملاً راست‌به‌چپ RTL و فونت **Vazirmatn**)، معماری **MVVM** و الگوی **Repository** پیاده‌سازی شده است:

- `ui/theme/`: پالت رنگ ایرانی (فیروزه‌ای، لاجوردی، طلایی زعفرانی، کاغذ طومار)، تایپوگرافی فارسی و تنظیم اجباری `LayoutDirection.Rtl`.
- `ui/components/`: اجزای اختصاصی هنر ایرانی، نوار بالای بازی، ناوبری ۵ بخشی پایین، پرتره‌های ۶ قهرمان و هیولاهای بامزه (`Canvas` + دارایی‌های محلی `res/drawable/`).
- `ui/screens/`: ۲۰ صفحه کامل شامل اسپلش، خوش‌آمدگویی، نام مستعار، انتخاب ۶ شخصیت، خانه اصلی، نقشه شش قلمرو، جزئیات قلمرو، انتخاب مرحله، معرفی مأموریت، ۳ مینی‌گیم (`گاوصندوق اسرارآمیز`، `پرونده گمشده`، `نبرد با هیولا`)، نبرد رؤسای مرحله ۵ و ۱۰، صفحه نتایج، پروفایل و ۸ سطح پیشرفت، افتخارات، کوله‌پشتی تزئینی، تنظیمات، وضعیت همگام‌سازی، اطلاعیه‌ها، جدول قهرمانان و پیش‌نمایش قلمروهای آینده.
- `data/local/`: پایگاه داده محلی **Room** (`ParsmindaDatabase`) + **DataStore** (`PreferencesManager`) برای اجرای ۱۰۰٪ آفلاین.
- `data/remote/`: **Firebase Authentication** (ورود با گوگل از طریق Jetpack Credential Manager)، **Cloud Firestore** با شناسه دیتابیس اختصاصی (`R.string.firestore_database_id`) و **WorkManager** (`SyncWorker`).

---

## ۲. همگام‌سازی آفلاین و امنیت

- تمام مراحل، ۶۴ پرسش بومی (~۶۰٪ سرگرمی و هوش / ~۴۰٪ تخصصی تخیلی)، پیشرفت و سکه به‌صورت محلی در Room ذخیره می‌شوند و برنامه بدون اینترنت کاملاً قابل بازی است.
- هر تکمیل مرحله یک `operationId` یکتا (`UUID`) و وضعیت اولیه `PENDING_VERIFICATION` می‌سازد.
- هنگام اتصال به اینترنت و ورود کاربر، `SyncWorker` جلسات در صف را به‌صورت Idempotent به مجموعه `gameSessions` ارسال می‌کند.
- کلاینت اندروید **هیچ‌گاه** اجازه تغییر مستقیم امتیاز معتبر (`authoritativeScore`)، سکه یا جدول `leaderboards` را ندارد (`firestore.rules` جلوی آن را می‌گیرد). تأیید نهایی امتیازها بر عهده Cloud Functions و پنل مدیریت وب است.

---

## ۳. ساختار داده‌های Firestore (قرارداد مشترک با پنل مدیریت وب)

جزئیات کامل در `/firebase-blueprint.json` و `/firestore.rules` تعریف شده است و شامل ۱۷ مجموعه است:

- `users`, `admins`, `characters`, `worlds`, `stages`, `missions`, `questions` (فقط ادمین؛ پاسخ‌ها هرگز به کلاینت عمومی ارسال نمی‌شوند), `publishedQuestions`, `gameSessions`, `answers`, `scores`, `rewards`, `achievements`, `inventory`, `leaderboards`, `announcements`, `settings`, `auditLogs`.

### توابع سمت سرور مورد نیاز در فاز متصل به وب (Cloud Functions)
1. `verifyGameSession`: بررسی `gameSessions` با وضعیت `PENDING`، راستی‌آزمایی امتیاز و به‌روزرسانی اتمیک `users`, `scores`, `rewards`, `leaderboards`.
2. `publishApprovedQuestions`: کپی پرسش‌های تأییدشده از `questions` به `publishedQuestions` بدون فیلد `correctOptionIndex`.
3. `grantAdminRole`: مدیریت سندهای `admins/{uid}` فقط توسط مدیر ارشد.

---

## ۴. راهنمای افزودن محتوای جدید

- **افزودن قلمرو جدید:** رکورد قلمرو در `WorldAndMiniGameSeedData.worlds()` (و مجموعه `worlds` در Firestore) با `isFutureContent = false` فعال می‌شود.
- **افزودن مرحله جدید:** یک `StageEntity` در `WorldAndMiniGameSeedData.stages()` با `miniGameType` مناسب (`VAULT_PUZZLE`, `MISSING_FILE`, `MONSTER_BATTLE`) اضافه کنید.
- **افزودن پرسش جدید:** از ساختار `QuestionSeedData.kt` با ارقام فارسی، موضوع کاملاً تخیلی و `publicationStatus = "APPROVED"` پیروی کنید.

---

## ۵. اجرا در Android Studio و تست قوانین

1. پروژه را در Android Studio باز کرده و با Gradle همگام‌سازی کنید (`app/google-services.json` و `firebase_applet_config.xml` پیکربندی شده‌اند).
2. برای تست محلی قوانین امنیتی Firestore:
   ```bash
   FIRESTORE_EMULATOR_HOST="127.0.0.1:8085" node --test firestore.test.js
   ```
3. برای اجرای تست‌های واحد و Robolectric:
   ```bash
   gradle :app:testDebugUnitTest
   ```
