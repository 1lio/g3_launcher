@echo off
setlocal enabledelayedexpansion

:: Параметры
set COMMIT_HASH=0653b9b5292fad4f6c38546e9b3fd349c5839223
set TARGET_DIR=backup
set DELETED_FILES_LIST=deleted_files.txt

:: Проверка аргументов
if "%COMMIT_HASH%"=="" (
    echo Usage: %0 ^<commit_hash^>
    exit /b 1
)

:: Проверка существования .git директории
if not exist ".git" (
    echo Error: .git directory not found. Please run this script from project root.
    exit /b 1
)

:: Создание целевой директории
if not exist "%TARGET_DIR%" (
    mkdir "%TARGET_DIR%"
)

:: Очистка предыдущих файлов
if exist "%DELETED_FILES_LIST%" (
    del "%DELETED_FILES_LIST%"
)

echo Getting list of deleted files...
git diff --name-only --diff-filter=D "%COMMIT_HASH%^" "%COMMIT_HASH%" > "%DELETED_FILES_LIST%"

echo Getting list of new and modified files...
for /f "tokens=*" %%F in ('git diff --name-only --diff-filter=AM "%COMMIT_HASH%^" "%COMMIT_HASH%"') do (
    set "file=%%F"
    :: Конвертируем / в \ для Windows
    set "file=!file:/=\!"
    set "target_file=%TARGET_DIR%\!file!"
    
    :: Создание целевой директории если нужно
    for %%D in ("!target_file!") do (
        set "dir_path=%%~dpD"
        if not "!dir_path!"=="" (
            :: Убираем последний обратный слэш
            set "dir_path=!dir_path:~0,-1!"
            if not exist "!dir_path!" (
                mkdir "!dir_path!" >nul 2>&1
            )
        )
    )
    
    :: Копирование файла
    if exist "!file!" (
        copy "!file!" "!target_file!" >nul
        echo Copied: !file!
    ) else (
        echo Warning: File !file! not found in working directory
        :: Если файла нет в рабочей директории, пытаемся восстановить из коммита
        git show "%COMMIT_HASH%:%%F" > "!target_file!" 2>nul
        if !errorlevel! equ 0 (
            echo Restored from commit: !file!
        ) else (
            echo Error: Cannot restore !file!
        )
    )
)

echo.
echo Done!
echo New and modified files copied to: %TARGET_DIR%
echo Deleted files list saved to: %DELETED_FILES_LIST%

:: Показываем статистику
set /a copied_count=0
for /f "tokens=*" %%F in ('git diff --name-only --diff-filter=AM "%COMMIT_HASH%^" "%COMMIT_HASH%" ^| find /c /v ""') do set copied_count=%%F

set /a deleted_count=0
if exist "%DELETED_FILES_LIST%" (
    for /f "tokens=*" %%F in ('type "%DELETED_FILES_LIST%" ^| find /c /v ""') do set deleted_count=%%F
)

echo.
echo Statistics:
echo Files copied: !copied_count!
echo Files deleted: !deleted_count!