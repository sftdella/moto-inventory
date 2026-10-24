<div align="center">

# 🏍️ MotoInventory

**Aplikasi Android untuk manajemen inventaris & keuangan showroom motor bekas**

Catat stok motor, lacak biaya perbaikan, hitung keuntungan per unit, dan buat laporan keuangan PDF dalam satu aplikasi — lengkap dengan login multi-peran (Admin, Manager, Staff).

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white)
![Min SDK](https://img.shields.io/badge/minSdk-24-blue)
![Target SDK](https://img.shields.io/badge/targetSdk-35-blue)
![Room](https://img.shields.io/badge/Room-2.6.1-orange)
![Version](https://img.shields.io/badge/version-2.0-informational)

</div>

---

## 📑 Daftar Isi

- [Fitur Utama](#-fitur-utama)
- [Hak Akses per Peran](#-hak-akses-per-peran)
- [Alur Status Motor](#-alur-status-motor)
- [Tech Stack](#️-tech-stack)
- [Arsitektur & Struktur Proyek](#️-arsitektur--struktur-proyek)
- [Memulai](#-memulai)
- [Akun Default](#-akun-default)
- [Skema Database](#️-skema-database)

---

## ✨ Fitur Utama

| Fitur | Keterangan |
|---|---|
| 🔐 **Login & Sesi** | Autentikasi pengguna dengan password ter-hash (SHA-256) dan sesi tersimpan di `EncryptedSharedPreferences`. |
| 📊 **Dashboard** | Sapaan sesuai waktu, ringkasan stok, dan keuntungan bulan berjalan. |
| 📦 **Inventaris Motor** | Daftar motor dengan pencarian dan filter berdasarkan status. |
| ➕ **Tambah / Edit Motor** | Data lengkap: nama, merk, tahun, warna, nomor polisi, rangka, mesin, harga beli & jual — input otomatis berformat Rupiah dan selisih harga dihitung langsung. |
| 🔧 **Biaya Tambahan** | Catat biaya servis/perbaikan per unit; keuntungan bersih dihitung otomatis. |
| 💰 **Penjualan & Approval** | Staff mengajukan penjualan, Manager/Admin menyetujui. |
| 📈 **Laporan Keuangan** | Filter per periode, pie chart pemasukan vs pengeluaran (MPAndroidChart), ekspor & bagikan sebagai **PDF**. |
| 👥 **Manajemen Pengguna** | Tambah, ubah, reset password, nonaktifkan, dan hapus akun. |
| 🕒 **Log Aktivitas** | Jejak audit setiap aksi penting (tambah, edit, jual, kelola user). |
| 🔔 **Notifikasi** | Pengingat motor yang menunggu approval dan motor yang terlalu lama di stok (WorkManager). |

---

## 🔑 Hak Akses per Peran

| Aksi | Admin | Manager | Staff |
|---|:---:|:---:|:---:|
| Lihat Dashboard | ✅ | ✅ | — |
| Lihat Inventaris | ✅ | ✅ | ✅ |
| Tambah / Edit Motor | ✅ | — | ✅ |
| Hapus Motor | ✅ | — | — |
| Ubah Status & Tambah Biaya | ✅ | — | ✅ |
| Approve Penjualan | ✅ | ✅ | — |
| Laporan & Ekspor PDF | ✅ | ✅ | — |
| Manajemen Pengguna | ✅ | — | — |
| Log Aktivitas | ✅ | — | — |

Aturan ini didefinisikan di [`SessionManager.kt`](app/src/main/java/com/example/motoinventory/utils/SessionManager.kt), dan menu navigasi bawah menyesuaikan diri dengan peran pengguna yang login.

---

## 🔄 Alur Status Motor

```
  ┌───────────┐     ┌───────────────┐     ┌────────────────────┐     ┌─────────┐
  │ Tersedia  │ ──▶ │ Proses Servis │ ──▶ │ Menunggu Approval  │ ──▶ │ Terjual │
  └───────────┘     └───────────────┘     └────────────────────┘     └─────────┘
        ▲                   │
        └───────────────────┘
```

---

## 🛠️ Tech Stack

- **Bahasa:** Kotlin 2.0.21
- **UI:** XML Layout + ViewBinding, Material Components, CardView, RecyclerView
- **Navigasi:** Jetpack Navigation Component (single-activity + bottom navigation)
- **Arsitektur:** MVVM — ViewModel, LiveData, Repository
- **Database lokal:** Room 2.6.1 (dengan KSP)
- **Async:** Kotlin Coroutines
- **Background task:** WorkManager
- **Keamanan:** AndroidX Security Crypto (EncryptedSharedPreferences), hashing SHA-256
- **Grafik:** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- **Ekspor:** Android `PdfDocument` + `FileProvider` untuk berbagi file

---

## 🏛️ Arsitektur & Struktur Proyek

```
UI (Fragment/Activity) ──▶ ViewModel ──▶ Repository ──▶ DAO ──▶ Room Database
        ▲                      │
        └────── LiveData ──────┘
```

```
app/src/main/java/com/example/motoinventory/
├── MainActivity.kt              # Host navigasi & bottom nav berbasis peran
├── data/
│   ├── dao/                     # MotorDao, BiayaTambahanDao, UserDao, AktivitasDao
│   ├── database/                # InventoryDatabase (Room) + seeding admin default
│   ├── entity/                  # Motor, BiayaTambahan, User, Aktivitas
│   └── repository/              # InventoryRepository, UserRepository, AktivitasRepository
├── ui/
│   ├── login/                   # LoginActivity
│   ├── dashboard/               # Ringkasan & keuntungan
│   ├── inventory/               # Daftar motor + pencarian/filter
│   ├── add/                     # Form tambah/edit motor
│   ├── detail/                  # Detail motor, biaya tambahan, penjualan
│   ├── laporan/                 # Laporan keuangan & ekspor PDF
│   ├── users/                   # Manajemen pengguna
│   └── aktivitas/               # Log aktivitas
├── utils/                       # FormatUtils, HashUtils, SessionManager,
│                                # NotificationHelper, PdfExporter
└── viewmodel/                   # InventoryViewModel, UserViewModel, AktivitasViewModel
```

---

## 🚀 Memulai

### Prasyarat

- [Android Studio](https://developer.android.com/studio) versi terbaru (Ladybug atau lebih baru)
- JDK 11+
- Perangkat / emulator Android 7.0 (API 24) ke atas

### Instalasi

```bash
# 1. Clone repositori
git clone https://github.com/sftdella/moto-inventory.git
cd moto-inventory

# 2. Build APK debug
./gradlew assembleDebug        # Windows: gradlew.bat assembleDebug

# 3. Install ke perangkat yang terhubung
./gradlew installDebug
```

Atau cukup buka folder proyek di Android Studio, tunggu Gradle sync selesai, lalu klik **Run ▶**.

> **Catatan:** MPAndroidChart diambil dari JitPack. Pastikan `maven { url = uri("https://jitpack.io") }` ada di blok `repositories` pada `settings.gradle.kts`.

---

## 👤 Akun Default

Saat aplikasi pertama kali dijalankan, akun admin dibuat otomatis:

| Username | Password | Peran |
|---|---|---|
| `admin` | `admin123` | ADMIN |

> ⚠️ Segera ganti password default ini melalui menu **Manajemen Pengguna** setelah login pertama.

---

## 🗄️ Skema Database

Database Room `pcm_inventory_database` (versi 2) berisi empat tabel:

| Tabel | Isi |
|---|---|
| `motors` | Data unit motor: identitas kendaraan, harga beli/jual, status, tanggal masuk & terjual |
| `biaya_tambahan` | Biaya servis/perbaikan yang terkait dengan motor tertentu |
| `users` | Akun pengguna: nama, username, hash password, peran, status aktif, login terakhir |
| `aktivitas` | Log audit aksi pengguna |

---

<div align="center">

Dibuat dengan ❤️ menggunakan Kotlin & Jetpack

</div>
