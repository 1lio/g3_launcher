@echo off
chcp 65001 >nul

echo Удаление файлов и папок...

:: Удаление файлов в корневой директории
del "CP_Changelog_en.txt" 2>nul
del "CP_Readme_en.txt" 2>nul
del "Disclaimer_en.txt" 2>nul
del "Exporter.dll" 2>nul
del "MSVCRT.DLL" 2>nul
del "SHW32.DLL" 2>nul
del "fmod.dll" 2>nul
del "ge3dialogs.dll" 2>nul
del "lib3ds.dll" 2>nul
del "libexpat.dll" 2>nul
del "msdbi.dll" 2>nul
del "msvcm80.dll" 2>nul
del "msvcp71.dll" 2>nul
del "msvcp80.dll" 2>nul
del "msvcr71.dll" 2>nul
del "msvcr80.dll" 2>nul
del "protect.dll" 2>nul
del "sapi_lipsync.dll" 2>nul
del "vcomp.dll" 2>nul

:: Удаление файлов в папке Data
del "Data\Infos.p01" 2>nul
del "Data\Quests.p01" 2>nul
del "Data\Sound.p01" 2>nul
del "Data\Speech_German.p00" 2>nul
del "Data\Templates.p01" 2>nul
del "Data\_compiledImage.p01" 2>nul
del "Data\_intern.pak" 2>nul
del "Data\gui.p01" 2>nul

:: Удаление папки Materials и её содержимого
if exist "Data\Materials" rmdir "Data\Materials" /s /q

:: Удаление видеофайлов
del "Data\Video\G3_Logo_02.bik" 2>nul
del "Data\Video\G3_Logo_03.bik" 2>nul
del "Data\Video\G3_Logo_04.bik" 2>nul

:: Удаление файлов в папке Ini
del "Ini\G3_World_01_local.wrldatasc" 2>nul
del "Ini\ge3local.ini" 2>nul
del "Ini\keyboard_and_console.txt" 2>nul

echo Удаление завершено.
pause