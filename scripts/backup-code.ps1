$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$backupDir = Join-Path $root "backups"
New-Item -ItemType Directory -Path $backupDir -Force | Out-Null

$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$output = Join-Path $backupDir "smartcampus_code_$timestamp.zip"
$staging = Join-Path $env:TEMP "smartcampus_code_backup_$timestamp"

$items = @(
    ".github",
    "aplicacion",
    "docs",
    "dominio",
    "persistencia",
    "presentacion",
    "scripts",
    ".gitignore",
    "pom.xml",
    "README.md"
)

New-Item -ItemType Directory -Path $staging -Force | Out-Null

try {
    foreach ($item in $items) {
        $source = Join-Path $root $item
        if (-not (Test-Path $source)) {
            continue
        }

        $destination = Join-Path $staging $item
        if (Test-Path $source -PathType Container) {
            New-Item -ItemType Directory -Path $destination -Force | Out-Null
            Copy-Item -Path (Join-Path $source "*") -Destination $destination -Recurse -Force
        } else {
            Copy-Item -Path $source -Destination $destination -Force
        }
    }

    $paths = Get-ChildItem $staging -Force
    if (-not $paths) {
        throw "No se encontraron archivos del proyecto para respaldar."
    }

    Compress-Archive -Path $paths.FullName -DestinationPath $output -Force
} finally {
    Remove-Item $staging -Recurse -Force -ErrorAction SilentlyContinue
}

Write-Host "Backup de codigo creado:" -ForegroundColor Green
Write-Host $output
