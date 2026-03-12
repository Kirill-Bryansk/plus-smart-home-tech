# Build the project
# Run from project root: .\scripts\build.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Building project..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Running: mvn clean install -DskipTests" -ForegroundColor Gray
Write-Host "This may take several minutes on first run." -ForegroundColor Gray
Write-Host ""

mvn clean install -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "Build completed successfully!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "Build FAILED! Check logs above." -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    exit 1
}
