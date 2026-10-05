@echo off
title Celenganku Windows 11 Uninstaller

set "UNINSTALL_EXE=%ProgramFiles%\Celenganku\Uninstall-Celenganku.exe"
if not exist "%UNINSTALL_EXE%" (
    echo Celenganku belum terpasang di %ProgramFiles%\Celenganku.
    pause
    exit /b 1
)

start "" "%UNINSTALL_EXE%"
exit /b
