param(
    [int]$numClients = 1,
    [switch]$server,
    [string]$ip = "127.0.0.1"
)

# Parsing degli argomenti aggiuntivi
foreach ($arg in $args) {
    if ($arg -match "^\d{1,3}(\.\d{1,3}){3}$") {
        $ip = $arg
    }
}

Write-Host "➡️  Cleaning and building the project..."
mvn clean package

$clientJar = "target\client-jar-with-dependencies.jar"
$serverJar = "target\server-jar-with-dependencies.jar"

if (-Not (Test-Path $clientJar)) {
    Write-Error "❌ Client JAR not found: $clientJar"
    exit 1
}

if ($server -and -Not (Test-Path $serverJar)) {
    Write-Error "❌ Server JAR not found: $serverJar"
    exit 1
}

# Funzione per aprire una nuova finestra PowerShell
function Launch-Terminal {
    param([string]$cmd)
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $cmd
}

# Avvio server se richiesto
if ($server) {
    Write-Host "🟢 Starting server..."
    Launch-Terminal "cd `"$PWD`"; java -jar `"$serverJar`" $ip"
}

# Avvio client
Write-Host "🚀 Starting $numClients client(s) with IP $ip..."
for ($i = 1; $i -le $numClients; $i++) {
    Launch-Terminal "cd `"$PWD`"; java -jar `"$clientJar`" $ip"
}

Write-Host "✅ Launch complete!"