# Stop all Java processes (microservices)
# Run from project root: .\scripts\stop-java.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Stopping Java processes..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Stop by window titles
$processNames = @(
    "Shopping Store",
    "Warehouse", 
    "Shopping Cart",
    "Order",
    "Delivery",
    "Payment",
    "Collector",
    "Aggregator",
    "Analyzer",
    "Hub Router",
    "API Gateway",
    "Config Server",
    "Discovery Server"
)

foreach ($name in $processNames) {
    $processes = Get-Process | Where-Object { $_.MainWindowTitle -like "$name*" }
    foreach ($p in $processes) {
        Write-Host "Stopping: $($p.MainWindowTitle) (PID: $($p.Id))" -ForegroundColor Yellow
        Stop-Process -Id $p.Id -Force -ErrorAction SilentlyContinue
    }
}

# Stop all java.exe (except IDE)
Write-Host "Stopping remaining Java processes..." -ForegroundColor Yellow
Get-Process java -ErrorAction SilentlyContinue | ForEach-Object {
    Write-Host "Stopping java.exe (PID: $($_.Id))" -ForegroundColor Yellow
    Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Java processes stopped!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Note: Docker containers are still running." -ForegroundColor Gray
Write-Host "To stop Docker: docker-compose down" -ForegroundColor Gray
