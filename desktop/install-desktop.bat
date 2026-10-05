@echo off
title Celenganku Windows 11 Installer
cd /d "%~dp0"

set "SETUP_EXE=%~dp0release\single-installer-normal-app\Celenganku-Windows11.exe"
if not exist "%SETUP_EXE%" (
    echo Installer belum ada. Jalankan build-windows.ps1 terlebih dahulu.
    pause
    exit /b 1
)

start "" "%SETUP_EXE%"