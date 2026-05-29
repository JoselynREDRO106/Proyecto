param(
    [Parameter(Mandatory = $true)]
    [string]$BackupFile
)

$ErrorActionPreference = "Stop"

. "$PSScriptRoot\_load-env.ps1"

if (-not (Test-Path $BackupFile)) {
    throw "No existe el archivo de backup: $BackupFile"
}

if (-not (Get-Command pg_restore -ErrorAction SilentlyContinue)) {
    throw "No se encontro pg_restore. Instala PostgreSQL client y agrega la carpeta bin al PATH."
}

Write-Host "Restaurando backup en Supabase..." -ForegroundColor Yellow
Write-Host "Archivo: $BackupFile"

$env:PGPASSWORD = $env:SUPABASE_DB_PASSWORD

try {
    pg_restore `
        --host $env:SUPABASE_DB_HOST `
        --port $env:SUPABASE_DB_PORT `
        --username $env:SUPABASE_DB_USER `
        --dbname $env:SUPABASE_DB_NAME `
        --clean `
        --if-exists `
        --no-owner `
        --no-privileges `
        $BackupFile
} finally {
    Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue
}

Write-Host "Restauracion finalizada." -ForegroundColor Green
