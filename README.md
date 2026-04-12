# Quotes App 🌟

Aplikasi pencari dan sekumpulan kutipan (Quotes) interaktif dibangun dengan menerapkan arsitektur modern Android (*Modern Android Development*). Proyek ini ditujukan baik untuk keperluan pembelajaran pembuatan aplikasi solid dari nol hingga ke level mahir menggunakan teknologi terkini buatan Google.

## 🛠️ Stack Teknologi

Aplikasi ini dibangun menggunakan alat dan _library_ standar industri yang sangat disarankan oleh arsitektur Android:
- **UI & Layout**: XML + Data/View Binding
- **Bahasa**: Kotlin (v2.0)
- **Arsitektur**: MVVM (Model-View-ViewModel)
- **Database (Lokal)**: Room Database (Penyimpanan Data Offline Cache)
- **Networking/API**: Retrofit2 + OkHttp (Pemanggilan API Eksternal)
- **Loading Gambar**: Glide (Caching dan Parsing Media)
- **Dependency Injection**: Dagger Hilt
- **Dependency Management**: Gradle Version Catalogs (TOML)
- **Asynchrony**: Kotlin Coroutines & LiveData/Flow
- **Navigasi**: Android Jetpack Navigation Components

## 🚀 Cara Menjalankan Proyek

1. **Clone repositori ini** ke dalam penyimpanan komputer Anda.
   ```bash
   git clone <url-repositori-anda>
   ```
2. Pastikan Anda memiliki **Android Studio versi terbaru** (Ladybug ke atas).
3. **Konfigurasi Lokal (Penting):**
   - Buat sebuah file bernama `local.properties` pada akar (root) direktori/folder project.
   - Anda dapat meniru struktur file yang sudah disediakan melalui file templatenya: `local.properties.example`.
   - File konfigurasi lokal Anda **tidak akan bisa terunggah/bocor ke internet** karena sudah terhalang otomatis oleh setup `.gitignore`.
4. Buka direktori repositori proyek `<Quotes_App>` tersebut menggunakan Android Studio.
5. Tunggu proses **Gradle Sync** sampai dinyatakan sukses 100%. (Pastikan koneksi internet pada komputer Anda stabil untuk pemuatan _dependency_).
6. Sambungkan Handphone / Emulator.
7. Tekan symbol Play (▶) ber-warna hijau / `Run 'app'`.

## 🛡️ Aturan Kontribusi / Keamanan
File-file _Credentials_ berharga seperti (`.jks`, `keystore`, `local.properties`) telah dikunci di dalam file `.gitignore`. Mohon pastikan menaruh segala referensi String unik yang berhubungan dengan kata sandi Server atau token API pada `local.properties` (Gunakan `BuildConfig` merujuk ke properti tersebut lewat gradle).

## 📄 Struktur Pembelajaran
Proyek ini di-set selaras guna mengakomodasi materi pembelajaran Android fundamental seperti **Activity**, **Fragment**, **ListView/RecyclerView**, **Design UI Lanjutan**, dan **REST API JSON Parsing**. Pengaturan Plugin telah difosilkan (LTS 8.5/8.9) untuk mempermudah pengembangan pemula dan mencegah ketidakcocokan plugin di sistem yang dinamis!
