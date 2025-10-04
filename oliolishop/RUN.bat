@echo off
echo Starting OlioliShop...
echo Make sure MySQL and Redis are running!
echo.
mvnw.cmd spring-boot:run -Dspring.config.location=config/application-local.yml
pause