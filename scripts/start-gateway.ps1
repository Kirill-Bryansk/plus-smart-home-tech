# Start API Gateway in new PowerShell window
# Run from project root: .\scripts\start-gateway.ps1

$projectRoot = "C:\Users\Admin\java\plus-smart-home-tech"
$path = Join-Path $projectRoot "infra\gateway"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting API Gateway..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$command = @"
cd '$path'
Write-Host 'Starting API Gateway...' -ForegroundColor Cyan
mvn spring-boot:run
"@

Start-Process powershell -ArgumentList "-NoExit", "-Command", $command -WindowStyle Normal

Write-Host "API Gateway starting in new window..." -ForegroundColor Green
Write-Host "Port: http://localhost:8080" -ForegroundColor Gray
