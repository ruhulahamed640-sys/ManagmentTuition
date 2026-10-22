# ManagementTution — Native Android App (Kotlin + XML + GitHub Actions CI/CD)

## ১. প্রজেক্টের সারসংক্ষেপ (Overview)

**ManagementTution** একটি সম্পূর্ণ Native Android অ্যাপ্লিকেশন যা **Kotlin**, **XML Layout (Material Design 3)**, **MVVM Architecture**, **ViewBinding**, **Retrofit2/OkHttp3** এবং **Gradle 8.7 (AGP 8.4.2, Java 17)** ব্যবহার করে তৈরি করা হয়েছে। প্রজেক্টটি সরাসরি GitHub-এ push করলেই **GitHub Actions** স্বয়ংক্রিয়ভাবে `./gradlew assembleDebug` চালিয়ে ডাউনলোডযোগ্য **Debug APK (`ManagementTution-debug-apk`)** তৈরি করবে।

## ২. সম্পূর্ণ Project Structure

```text
ManagementTution/
├── .github/
│   └── workflows/
│       └── build.yml                                   # GitHub Actions CI/CD APK Build Workflow
├── app/
│   ├── build.gradle.kts                                # App module Gradle config (AGP 8.4.2, SDK 34, Java 17)
│   ├── proguard-rules.pro                              # ProGuard rules for Retrofit & Gson models
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml                     # App permissions, HTTPS security & MainActivity
│       │   ├── java/
│       │   │   └── com/managementtution/app/
│       │   │       ├── MainActivity.kt                 # Main UI controller with BottomNavigation & Dialogs
│       │   │       ├── data/
│       │   │       │   └── TuitionRepository.kt        # Repository managing students, batches, attendance & fees
│       │   │       ├── model/
│       │   │       │   └── TuitionModels.kt            # Data classes & enums (StudentRecord, TuitionBatch, FeeInvoice)
│       │   │       ├── network/
│       │   │       │   ├── ApiResult.kt                # Sealed network result wrapper
│       │   │       │   ├── RetrofitClient.kt           # OkHttp3 + Retrofit2 singleton client
│       │   │       │   └── TuitionApiService.kt        # REST API interface (/api/v1/health)
│       │   │       └── ui/
│       │   │           ├── MainViewModel.kt            # MVVM ViewModel + Factory
│       │   │           └── adapter/
│       │   │               ├── AttendanceAdapter.kt    # Daily attendance roll-call RecyclerView adapter
│       │   │               ├── BatchAdapter.kt         # Active tuition batches RecyclerView adapter
│       │   │               ├── FeeInvoiceAdapter.kt    # Fee invoices & collection RecyclerView adapter
│       │   │               └── StudentAdapter.kt       # Student directory RecyclerView adapter
│       │   └── res/
│       │       ├── drawable/                           # Vector icons & pill backgrounds
│       │       ├── layout/                             # activity_main.xml, 4 item layouts & 3 dialog layouts
│       │       ├── menu/                               # bottom_nav_menu.xml
│       │       ├── mipmap-anydpi-v26/                  # Adaptive launcher icon XMLs
│       │       ├── mipmap-hdpi/                        # 72x72 PNG launcher icons
│       │       ├── mipmap-mdpi/                        # 48x48 PNG launcher icons
│       │       ├── mipmap-xhdpi/                       # 96x96 PNG launcher icons
│       │       ├── mipmap-xxhdpi/                      # 144x144 PNG launcher icons
│       │       ├── mipmap-xxxhdpi/                     # 192x192 PNG launcher icons
│       │       ├── values/                             # colors.xml, strings.xml, themes.xml
│       │       └── xml/                                # network_security_config.xml, backup_rules.xml, data_extraction_rules.xml
│       └── test/
│           └── java/com/managementtution/app/
│               └── TuitionRepositoryTest.kt            # JUnit 4 unit tests for attendance & fee calculations
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar                          # Gradle Wrapper JAR
│       └── gradle-wrapper.properties                   # Gradle 8.7 distribution configuration
├── build.gradle.kts                                    # Root Gradle configuration
├── settings.gradle.kts                                 # Project repositories & module inclusion
├── gradle.properties                                   # JVM args, AndroidX & Kotlin style flags
├── gradlew                                             # POSIX executable Gradle wrapper script (0755)
├── gradlew.bat                                         # Windows Gradle wrapper script
├── .gitignore                                          # Git ignore rules
└── README.md                                           # Documentation & build guide
```

## ৩. ফাইলগুলোর পারস্পরিক Dependency সম্পর্ক (File Dependency Graph)

| ফাইল | যে ফাইলগুলোর ওপর নির্ভরশীল (Dependencies) | কাজ |
| :--- | :--- | :--- |
| `settings.gradle.kts` & `build.gradle.kts` | `gradle/wrapper/gradle-wrapper.properties` | Gradle 8.7, Android Gradle Plugin `8.4.2`, এবং Kotlin `1.9.24` প্লাগইন সংজ্ঞায়িত করে |
| `app/build.gradle.kts` | Root `build.gradle.kts`, `app/proguard-rules.pro` | `namespace = "com.managementtution.app"`, `minSdk = 24`, `targetSdk = 34`, `JavaVersion.VERSION_17`, `viewBinding = true` এবং সব AndroidX/Material3/Retrofit লাইব্রেরি যুক্ত করে |
| `AndroidManifest.xml` | `MainActivity.kt`, `@style/Theme.ManagementTution`, `@xml/network_security_config`, `@mipmap/ic_launcher` | অ্যাপের স্টার্টআপ অ্যাক্টিভিটি, আইকন, থিম এবং HTTPS নেটওয়ার্ক সিকিউরিটি কনফিগার করে |
| `MainActivity.kt` | `MainViewModel.kt`, `BatchAdapter.kt`, `StudentAdapter.kt`, `AttendanceAdapter.kt`, `FeeInvoiceAdapter.kt`, `activity_main.xml` | ৪টি প্রধান ট্যাব (Dashboard, Students, Attendance, Fees) এবং নতুন ছাত্র/ব্যাচ/ফি যোগ করার ডায়ালগ পরিচালনা করে |
| `MainViewModel.kt` | `TuitionRepository.kt`, `TuitionModels.kt` | UI state (`LiveData`) ধারণ করে এবং ব্যাকগ্রাউন্ড করুটিনে API Sync পরিচালনা করে |
| `TuitionRepository.kt` | `TuitionApiService.kt`, `RetrofitClient.kt`, `TuitionModels.kt` | ব্যাচ, ছাত্র, উপস্থিতি এবং ফি ইনভয়েসের হিসাব ও সার্ভার সিঙ্ক সম্পন্ন করে |

## ৪. GitHub-এ Upload, Actions চালানো এবং APK Download করার নির্দেশনা

1. **GitHub Repository তৈরি ও কোড আপলোড**:
   `ManagementTution.zip` ফাইলটি আনজিপ করে `ManagementTution` ফোল্ডারের ভেতরে টার্মিনাল খুলুন এবং নিচের কমান্ডগুলো চালান:
   ```bash
   git init
   git branch -M main
   chmod +x gradlew
   git add .
   git commit -m "Initial commit: ManagementTution Android app with GitHub Actions CI"
   git remote add origin https://github.com/<your-username>/ManagementTution.git
   git push -u origin main
   ```
2. **GitHub Actions Workflow চালানো**:
   - `main` ব্রাঞ্চে push করার সাথে সাথেই `.github/workflows/build.yml` স্বয়ংক্রিয়ভাবে চালু হবে।
   - চাইলে GitHub রিপোজিটরির **Actions** ট্যাবে গিয়ে **ManagementTution Android APK Build** সিলেক্ট করে **Run workflow** বাটনে ক্লিক করে ম্যানুয়ালিও (`workflow_dispatch`) বিল্ড চালাতে পারবেন।
3. **APK Artifact ডাউনলোড**:
   - বিল্ড সম্পন্ন হলে (সবুজ টিক চিহ্ন দেখালে) ওই Workflow run-এর নিচে **Artifacts** সেকশনে **`ManagementTution-debug-apk`** দেখতে পাবেন।
   - সেখানে ক্লিক করলে জিপ আকারে `app-debug.apk` ডাউনলোড হবে, যা সরাসরি আপনার Android ফোনে ইনস্টল করতে পারবেন।
