
---

# 📖 Final Story App

**Final Story App** adalah aplikasi Android yang dikembangkan untuk menampilkan daftar cerita pengguna secara interaktif. Aplikasi ini merupakan pengembangan dari submission sebelumnya, dengan tambahan fitur seperti **tampilan peta lokasi**, **implementasi Paging 3**, dan **unit testing**. Aplikasi ini dibuat untuk memenuhi kriteria proyek akhir dalam studi Android Intermediate.

---

## ✅ Fitur Utama

### 🔁 Mempertahankan Fitur Submission Sebelumnya

* Registrasi dan login pengguna
* Menambahkan cerita dengan foto dan deskripsi
* Melihat daftar cerita dari pengguna lain
* Detail cerita dengan foto, deskripsi, dan waktu

### 🗺️ Menampilkan Daftar Cerita dalam Bentuk Peta

* Cerita dengan koordinat lokasi akan ditampilkan dalam **Google Maps**
* Lokasi ditandai dengan marker sesuai posisi pengguna saat membuat cerita
* Menggunakan Maps SDK dan integrasi API lokasi dari Story API

### 📄 Menampilkan List Story dengan Paging 3

* Daftar cerita ditampilkan menggunakan **Jetpack Paging 3**
* Loading data secara bertahap saat pengguna melakukan scroll
* Mendukung refresh dan retry dengan `RemoteMediator` (jika offline-first)

### 🧪 Menerapkan Unit Test

* Unit test pada ViewModel dan Repository
* Menggunakan JUnit, Mockito, dan Coroutine Test
* Menguji logika bisnis dan alur data dari API

---

## 🧩 Teknologi yang Digunakan

| Komponen                | Teknologi/Library         |
| ----------------------- | ------------------------- |
| Bahasa                  | Kotlin                    |
| Arsitektur              | MVVM + Repository Pattern |
| UI                      | XML, Material Design      |
| State Management        | LiveData, ViewModel       |
| API                     | Retrofit, OkHttp          |
| Paging                  | Paging 3                  |
| Maps                    | Google Maps SDK           |
| Test                    | JUnit, Mockito, Espresso  |
| Dependency Injection    | Hilt                      |
| Persistensi Token/Login | DataStore                 |

---

## 🧪 Contoh Struktur Unit Test

```kotlin
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var storyViewModel: StoryViewModel

    @Mock
    private lateinit var repository: StoryRepository

    @Before
    fun setup() {
        storyViewModel = StoryViewModel(repository)
    }

    @Test
    fun `getStories should return list of stories`() = runTest {
        val dummyStories = generateDummyStories()
        val expected = MutableLiveData<List<Story>>()
        expected.value = dummyStories

        `when`(repository.getStories()).thenReturn(expected)

        val actualStories = storyViewModel.getStories().getOrAwaitValue()
        assertEquals(dummyStories.size, actualStories.size)
    }
}
```

---

## 📷 Screenshot Aplikasi

| Halaman Login                                            | Daftar Cerita                                                 | Tampilan Peta                                        | Tambah Cerita                                              |
| -------------------------------------------------------- | ------------------------------------------------------------- | ---------------------------------------------------- | ---------------------------------------------------------- |
| ![Login](https://via.placeholder.com/200x400?text=Login) | ![List](https://via.placeholder.com/200x400?text=Paging+List) | ![Map](https://via.placeholder.com/200x400?text=Map) | ![Upload](https://via.placeholder.com/200x400?text=Upload) |

---

## 📦 Cara Menjalankan Aplikasi

1. Clone repository ini:

   ```bash
   git clone https://github.com/username/story-app.git
   ```
2. Buka di **Android Studio**.
3. Masukkan API Key Google Maps pada file `local.properties`:

   ```
   MAPS_API_KEY=your_api_key_here
   ```
4. Jalankan aplikasi menggunakan emulator atau perangkat fisik.

---

## 👨‍💻 Developer

* **Nama**: Muhammad Rafli Nurfathan
* **Email**: [nurfathanrafli85@gmail.com](mailto:nurfathanrafli85@gmail.com)
* **LinkedIn**: [linkedin.com/in/mhmmdraflin](https://www.linkedin.com/in/mhmmdraflin)

---

## 📌 Catatan

* Pastikan koneksi internet aktif saat menjalankan aplikasi.
* Beberapa fitur membutuhkan izin lokasi (location permission).

---

