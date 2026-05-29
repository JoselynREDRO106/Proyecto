$ErrorActionPreference = "Stop"

Write-Host "Creando backup de codigo..." -ForegroundColor Cyan
& "$PSScriptRoot\backup-code.ps1"

Write-Host "Creando backup de base de datos..." -ForegroundColor Cyan
& "$PSScriptRoot\backup-db.ps1"

Write-Host "Backup completo finalizado." -ForegroundColor Green
