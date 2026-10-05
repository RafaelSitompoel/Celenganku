@echo off
title Pasang Sertifikat Celenganku Official - Windows 11
cd /d "%~dp0"

echo ========================================================
echo   🔐 PEMASANG SERTIFIKAT PUBLISHER "Celenganku Official"
echo ========================================================
echo.

:: Minta izin Administrator jika belum
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo [INFO] Meminta izin Administrator...
    powershell -Command "Start-Process '%~f0' -Verb RunAs"
    exit /b
)

echo Memasang sertifikat ke Trusted Root dan Trusted Publisher...
certutil -addstore -f "Root" CelengankuOfficial.cer >nul 2>&1
certutil -addstore -f "TrustedPublisher" CelengankuOfficial.cer >nul 2>&1

echo.
echo ========================================================
echo   ✅ SUKSES! Sertifikat "Celenganku Official" terpasang.
echo   Sekarang Windows 11 UAC akan menampilkan:
echo   Verified Publisher: "Celenganku Official"
echo ========================================================
echo.
pause
