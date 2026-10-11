# Cargar variables del .env
Get-Content .env | Where-Object { 
    $_ -match "=" -and $_ -notmatch "^\s*#" 
} | ForEach-Object {
    $key, $value = $_ -split "=", 2
    [System.Environment]::SetEnvironmentVariable($key.Trim(), $value.Trim(), "Process")
}

Write-Host "Variables cargadas:" -ForegroundColor Green
Write-Host "  NOMINATIM_URL: $env:NOMINATIM_URL"
Write-Host "  NOMINATIM_USER_AGENT: $env:NOMINATIM_USER_AGENT"
Write-Host "  SERVER_PORT: $env:SERVER_PORT"
Write-Host ""

# Arrancar la aplicacion
./gradlew bootRun
