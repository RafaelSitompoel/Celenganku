@echo off
title Celenganku Desktop - Windows 11
cd /d "%~dp0"

echo ========================================================
echo   🐷 CELENGANKU DESKTOP - WINDOWS 11 EDITION
echo   Mode: Administrator & Manajemen Tabungan
echo ========================================================
echo.

:: Jalankan executable native hasil build
set "APP_EXE=%ProgramFiles%\Celenganku\Celenganku.exe"
set "SETUP_EXE=%~dp0release\single-installer\Celenganku-Windows11.exe"

if exist "%APP_EXE%" (
    start "" "%APP_EXE%"
    exit /b
)

if exist "%SETUP_EXE%" (
    start "" "%SETUP_EXE%"
    exit /b
)

echo Celenganku belum terpasang dan installer tunggal tidak ditemukan.
pause
exit /b
