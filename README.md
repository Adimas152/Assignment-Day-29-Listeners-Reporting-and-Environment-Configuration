Assignment Day 29 – TestNG Listeners, Reporting, dan Environment Configuration

Project ini adalah **lanjutan** dari:

➡️ [Assignment Day 28 – Test Runner, Assertions, Data-Driven Testing](https://github.com/Adimas152/Assignment-Day-28-Test-Runner-Assertions-Data-Driven-Testing)

Pada assignment Day 29 ini, fokus utama adalah:

- Implementasi **TestNG Listener** (custom listener untuk reporting & screenshot).
- Integrasi **Log4j2** untuk logging ke file.
- Penggunaan **environment configuration** (properties) untuk kredensial dan base URL.
- Tetap menggunakan konsep **Test Runner + Assertions + Data-Driven Testing** dari Day 28.

---

## 1. Tech Stack

- **Java** (mis. 17)
- **Gradle** (via `gradlew`)
- **TestNG**
- **Selenium WebDriver 4**
- **Log4j2**
- **ExtentReports** (untuk HTML report)
- **Apache POI** (untuk baca file Excel – data driven)
- Browser: **Google Chrome**


---

## 2. Cara Menjalankan Test

Jalankan dari root project:

```bash
# Menjalankan semua test (hard, soft, dan data driven)
./gradlew clean test -Psuite=all-tests-suite.xml -Penv=staging

# Hanya test Hard Assert
./gradlew clean test -Psuite=hard-assert-suite.xml -Penv=staging

# Hanya test Soft Assert
./gradlew clean test -Psuite=soft-assert-suite.xml -Penv=staging

# Hanya test Data Driven
./gradlew clean test -Psuite=data-driven-suite.xml -Penv=staging