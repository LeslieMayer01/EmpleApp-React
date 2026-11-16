$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
$claveAnterior = $env:DB_PASSWORD
$usuarioAnterior = $env:DB_USERNAME
try {
    $env:DB_USERNAME = 'root'
    $claveSegura = Read-Host 'Escribe la contrasena de MySQL y presiona Enter' -AsSecureString
    $credencial = New-Object System.Net.NetworkCredential -ArgumentList '', $claveSegura
    $env:DB_PASSWORD = $credencial.Password
    if ([string]::IsNullOrEmpty($env:DB_PASSWORD)) {
        throw 'La contrasena quedo vacia. Ejecuta de nuevo e ingresala cuando se solicite.'
    }
    Write-Host 'Contrasena recibida. Iniciando EmpleApp...'
    & .\mvnw.cmd spring-boot:run
}
finally {
    $env:DB_PASSWORD = $claveAnterior
    $env:DB_USERNAME = $usuarioAnterior
    $credencial = $null
    $claveSegura = $null
    Pop-Location
}
