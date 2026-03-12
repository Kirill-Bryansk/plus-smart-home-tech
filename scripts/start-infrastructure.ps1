# Start Docker infrastructure
# Run from project root: .\scripts\start-infrastructure.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting Docker infrastructure..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

docker-compose up -d

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Infrastructure started!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "- PostgreSQL: localhost:5432" -ForegroundColor Gray
Write-Host "- Kafka: localhost:9092" -ForegroundColor Gray
Write-Host ""
Write-Host "Check status: docker-compose ps" -ForegroundColor Yellow
