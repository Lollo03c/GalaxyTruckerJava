#!/bin/bash

set -e

# Default: 1 client, no server, localhost IP
NUM_CLIENTS=1
START_SERVER=false
SERVER_IP="127.0.0.1"

# Parsing degli argomenti
for arg in "$@"; do
  if [[ "$arg" =~ ^[0-9]+$ ]]; then
    NUM_CLIENTS=$arg
  elif [[ "$arg" == "--server" ]]; then
    START_SERVER=true
  elif [[ "$arg" =~ ^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    SERVER_IP=$arg
  fi
done

echo "➡️  Pulizia e build del progetto..."
mvn clean package

CLIENT_JAR="target/client-jar-with-dependencies.jar"
SERVER_JAR="target/server-jar-with-dependencies.jar"

if [ ! -f "$CLIENT_JAR" ]; then
  echo "❌ JAR client non trovato: $CLIENT_JAR"
  exit 1
fi

if [ "$START_SERVER" = true ] && [ ! -f "$SERVER_JAR" ]; then
  echo "❌ JAR server non trovato: $SERVER_JAR"
  exit 1
fi

# Funzione per aprire un terminale e lanciare un comando
launch_terminal() {
  CMD=$1
  if [[ "$OSTYPE" == "darwin"* ]]; then
    osascript -e "tell application \"Terminal\" to do script \"cd $(pwd); $CMD\""
  else
    gnome-terminal -- bash -c "$CMD; exec bash" &
  fi
}

# Avvio server se richiesto
if [ "$START_SERVER" = true ]; then
  echo "🟢 Avvio server..."
  launch_terminal "java -jar $SERVER_JAR $SERVER_IP"
fi

# Avvio client
echo "🚀 Avvio $NUM_CLIENTS client con IP $SERVER_IP..."
for ((i=1; i<=NUM_CLIENTS; i++)); do
  launch_terminal "java -jar $CLIENT_JAR $SERVER_IP"
done

echo "✅ Avvio completato!"