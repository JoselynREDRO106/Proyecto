$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $root ".env"

if (-not (Test-Path $envFile)) {
    throw "No existe el archivo .env en $root"
}

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if (-not $line -or $line.StartsWith("#")) {
        return
    }

    $parts = $line.Split("=", 2)
    if ($parts.Count -ne 2) {
        return
    }

    $name = $parts[0].Trim()
    $value = $parts[1].Trim().Trim('"')
    [Environment]::SetEnvironmentVariable($name, $value, "Process")
}

Write-Host "Probando conexion con Supabase..." -ForegroundColor Cyan
$connection = Test-NetConnection $env:SUPABASE_DB_HOST -Port $env:SUPABASE_DB_PORT

if (-not $connection.TcpTestSucceeded) {
    throw "No se pudo conectar a $env:SUPABASE_DB_HOST en el puerto $env:SUPABASE_DB_PORT"
}

Write-Host "Conexion OK. Levantando Jetty..." -ForegroundColor Green
Set-Location $root

$port = 8080
if (Test-NetConnection -ComputerName localhost -Port $port -InformationLevel Quiet) {
    Write-Host "El puerto $port ya esta ocupado." -ForegroundColor Yellow
    Get-NetTCPConnection -LocalPort $port -State Listen |
        Select-Object LocalAddress, LocalPort, OwningProcess,
            @{Name = "ProcessName"; Expression = { (Get-Process -Id $_.OwningProcess -ErrorAction SilentlyContinue).ProcessName }}
    throw "Cierra el proceso que usa el puerto $port y vuelve a ejecutar el script."
}

Write-Host "Aplicacion disponible en http://localhost:$port/smartcampus" -ForegroundColor Green
mvn jetty:run
