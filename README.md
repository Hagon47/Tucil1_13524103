a. Penjelasan Singkat Program
Queen's Game adalah sebuah program yang dibuat berbasis Java (JavaFX) dengan memanfaatkan algoritma brute-force lebih tepatnya exhaustive serach. Program ini bertujuan untuk menunjukan solusi dari permainan Queen's Puzzle Game di LinkedIn. Dimana dalam konteks ini, huruf yang sama akan menjadi satu daerah atau area yang sama, dan di dalam setiap area harus terdapat satu queen. Queen yang ditempatkan juga memiliki beberapa peraturan seperti tidak boleh berada dalam satu baris, kolom, dan diagonal berdekatan. 

b. Requirements Program
1. Java Development Kit (JDK): Versi 11 atau lebih baru (disarankan JDK 17 atau 21).
2. Apache Maven: Apache Maven 3.8.0 atau yang lebih baru.
3. Sistem Operasi: Windows, macOS, atau Linux.

c. Cara Mengkompilasi Program
1. Buka terminal/CMD di folder root project (folder di mana file pom.xml berada).
2. Kemudian jalankan perintah berikut:
mvn clean compile
atau jika ingin file executable
mvn clean package

d. Cara Menjalankan dan Menggunakan Program
- Cara Menjalankan
1. Gunakan perintah berikut di terminal (pastikan masih di folder root): java -jar bin/queens-solver-1.0.jar

- Cara Menggunakan Program
1. Load File: Klik menu File di menubar -> Load.
2. Pilih file .txt yang berisi representasi board.

Contoh isi file test.txt:

AABB
AACC
DDEE
DDEE

3. Centang Checkbox Live Update jika ingin melihat animasi proses pencarian.
4. Centang Checkbox Show Letters jika ingin melihat label huruf wilayah.
5. Klik button Solve.
6. Simpan Hasil (Jika solusi ditemukan, tombol Save As File dan Export pada menu File di menubar akan aktif).
7. Klik menu Save As File -> Text (.txt) untuk menyimpan solusi board sebagai file teks.
8. Klik menu Export -> Image (.png) untuk menyimpan solusi board sebagai gambar png.

e. Author/Identitas Pembuat
1. Nama: Reynard Nathanael
2. NIM: 13524103
3. Mata Kuliah: Strategi Algoritma (IF2211)

