param (
    [int]$numClients = 1,
    [switch]$server
)

Write-Host "➡️  Pulizia e build del progetto..."
mvn clean package

$clientJar = "target/client-jar-with-dependencies.jar"
$serverJar = "target/server-jar-with-dependencies.jar"

if (-Not (Test-Path $clientJar)) {
    Write-Host "❌ JAR client non trovato: $clientJar"
    exit 1
}

if ($server -and -Not (Test-Path $serverJar)) {
    Write-Host "❌ JAR server non trovato: $serverJar"
    exit 1
}

function Start-InNewWindow($cmd) {
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd `"$PWD`"; $cmd"
}

# Avvia server se richiesto
if ($server) {
    Write-Host "🟢 Avvio server..."
    Start-InNewWindow "java -jar $serverJar"
}

# Avvia client
Write-Host "🚀 Avvio $numClients client..."
for ($i = 1; $i -le $numClients; $i++) {
    Start-InNewWindow "java -jar $clientJar"
}

Write-Host "✅ Avvio completato!"