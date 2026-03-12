# Start Config Server in new PowerShell window
# Run from project root: .\scripts\start-config.ps1

$projectRoot = "C:\Users\Admin\java\plus-smart-home-tech"
$path = Join-Path $projectRoot "infra\config-server"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting Config Server..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$command = @"
cd '$path'
Write-Host 'Starting Config Server...' -ForegroundColor Cyan
mvn spring-boot:run
"@

Start-Process powershell -ArgumentList "-NoExit", "-Command", $command -WindowStyle Normal

Write-Host "Config Server starting in new window..." -ForegroundColor Green
Write-Host "Wait for: 'Started ConfigServer in X.XXX seconds'" -ForegroundColor Yellow
