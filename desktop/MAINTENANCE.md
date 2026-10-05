# Mode Maintenance Publik

Aplikasi Windows membaca `maintenance.json` dari branch `main` repo GitHub publik ini.

Untuk mengaktifkan maintenance, ubah `enabled` menjadi `true`, sesuaikan pesan/jadwal, lalu publikasikan perubahan ke branch `main`. Untuk membuka akses kembali, ubah `enabled` menjadi `false` dan publikasikan lagi.

Aplikasi memeriksa status sebelum dibuka dan setiap 30 detik saat berjalan. Status yang aktif menolak startup dan mengunci fitur. Jika file tidak tersedia atau formatnya tidak valid, aplikasi menahan akses sampai status dapat diverifikasi.

File ini bersifat publik dan tidak boleh berisi rahasia. Hanya pemilik/kolaborator dengan izin tulis repo yang dapat mengubah status server.