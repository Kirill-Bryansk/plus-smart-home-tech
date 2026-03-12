# Start Hub Router in new PowerShell window
# Run from project root: .\scripts\start-hub-router.ps1

$projectRoot = "C:\Users\Admin\java\plus-smart-home-tech"
$path = Join-Path $projectRoot "hub-router"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting Hub Router..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$command = @"
cd '$path'
Write-Host 'Starting Hub Router...' -ForegroundColor Cyan
mvn spring-boot:run
"@

Start-Process powershell -ArgumentList "-NoExit", "-Command", $command -WindowStyle Normal

Write-Host "Hub Router starting in new window..." -ForegroundColor Green
