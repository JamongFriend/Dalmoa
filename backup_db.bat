@echo off
setlocal

:: ==========================================
:: Dalmoa DB Backup Script for Windows
:: ==========================================

:: 1. Load variables from .env
for /f "tokens=1,2 delims==" %%a in (.env) do (
    if "%%a"=="MYSQL_ROOT_PASSWORD" set ROOT_PASSWORD=%%b
    if "%%a"=="MYSQL_DATABASE" set DB_NAME=%%b
)

:: 2. Set backup directory and filename
set BACKUP_DIR=./db_backups
set TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%
set FILENAME=%DB_NAME%_backup_%TIMESTAMP%.sql

:: 3. Create backup directory if it doesn't exist
if not exist "%BACKUP_DIR%" (
    mkdir "%BACKUP_DIR%"
)

:: 4. Run mysqldump inside the docker container
echo Starting DB backup for %DB_NAME%...
docker exec dalmoa-db /usr/bin/mysqldump -u root --password=%ROOT_PASSWORD% %DB_NAME% > %BACKUP_DIR%/%FILENAME%

if %ERRORLEVEL% eq 0 (
    echo Backup successful: %BACKUP_DIR%/%FILENAME%
) else (
    echo Backup failed! Please check if the docker container 'dalmoa-db' is running.
)

:: 5. (Optional) Delete backups older than 7 days
:: forfiles /p "%BACKUP_DIR%" /s /m *.sql /d -7 /c "cmd /c del @path"

pause
