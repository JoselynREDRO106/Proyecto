$ErrorActionPreference = "Stop"

. "$PSScriptRoot\_load-env.ps1"

$root = Split-Path -Parent $PSScriptRoot
$backupDir = Join-Path $root "backups"
New-Item -ItemType Directory -Path $backupDir -Force | Out-Null

if (-not (Get-Command pg_dump -ErrorAction SilentlyContinue)) {
    throw "No se encontro pg_dump. Instala PostgreSQL client y agrega la carpeta bin al PATH."
}

$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$output = Join-Path $backupDir "smartcampus_db_$timestamp.dump"

$env:PGPASSWORD = $env:SUPABASE_DB_PASSWORD

try {
    pg_dump `
        --host $env:SUPABASE_DB_HOST `
        --port $env:SUPABASE_DB_PORT `
        --username $env:SUPABASE_DB_USER `
        --dbname $env:SUPABASE_DB_NAME `
        --format custom `
        --no-owner `
        --no-privileges `
        --file $output
} finally {
    Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue
}

Write-Host "Backup de base de datos creado:" -ForegroundColor Green
Write-Host $output
