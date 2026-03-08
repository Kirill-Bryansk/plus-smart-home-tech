# Start all Commerce services in separate PowerShell windows
# Run from project root: .\scripts\start-commerce.ps1

$projectRoot = "C:\Users\Admin\java\plus-smart-home-tech"

$services = @(
    @{ Name = "Shopping Store"; Path = "commerce\shopping-store" },
    @{ Name = "Warehouse"; Path = "commerce\warehouse" },
    @{ Name = "Shopping Cart"; Path = "commerce\shopping-cart" },
    @{ Name = "Order"; Path = "commerce\order" },
    @{ Name = "Delivery"; Path = "commerce\delivery" },
    @{ Name = "Payment"; Path = "commerce\payment" }
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting Commerce Services..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

foreach ($service in $services) {
    $title = $service.Name
    $path = Join-Path $projectRoot $service.Path
    
    Write-Host "Starting: $title ..." -ForegroundColor Green
    
    $command = @"
cd '$path'
Write-Host 'Starting $title...' -ForegroundColor Cyan
mvn spring-boot:run
"@
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command -WindowStyle Normal
    
    Start-Sleep -Seconds 2
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "All Commerce Services are starting!" -ForegroundColor Green
Write-Host "Check Eureka: http://localhost:8761" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "To stop: Press Ctrl+C in each window or run:" -ForegroundColor Gray
Write-Host "  .\scripts\stop-java.ps1" -ForegroundColor Gray
