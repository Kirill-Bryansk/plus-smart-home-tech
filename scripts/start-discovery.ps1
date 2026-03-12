# Start Discovery Server (Eureka) in new PowerShell window
# Run from project root: .\scripts\start-discovery.ps1

$projectRoot = "C:\Users\Admin\java\plus-smart-home-tech"
$path = Join-Path $projectRoot "infra\discovery-server"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting Discovery Server (Eureka)..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$command = @"
cd '$path'
Write-Host 'Starting Eureka Server...' -ForegroundColor Cyan
mvn spring-boot:run
"@

Start-Process powershell -ArgumentList "-NoExit", "-Command", $command -WindowStyle Normal

Write-Host "Discovery Server starting in new window..." -ForegroundColor Green
Write-Host "Wait for: 'Started DiscoveryServer in X.XXX seconds'" -ForegroundColor Yellow
Write-Host "Port: http://localhost:8761" -ForegroundColor Gray
