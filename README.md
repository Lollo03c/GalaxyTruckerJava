# Galaxy Truckers: IS25-AM34
The development of this project is part of the software engineering course at the Polytechnic University of Milan, 
and as a final examination, it is necessary for the bachelor's degree in computer engineering. The course was 
held by prof. Alessandro Margara in the academic year 2024-2025.<br>

## Status of Work

| Functionality                | State |
|:-----------------------------|:-----:|
| Basic rules                  |  🟡   |
| Complete rules               |  🟡   |
| CLI                          |  🟡   |
| GUI                          |  🟡   |
| Socket                       |  ✅   |
| RMI                          |  ✅   |
| First flight                 |  🟡   |
| Multiple matches             |  🟡   |
| Persistence                  |  ❌   |
| Resilience to disconnections |  ❌   |

#### Legend
❌ Not implemented
🟡 Implementing
✅ Implemented

## Team - IS25-AM34
- ### [Antonio Augello](https://github.com/AntonioAh)<br/>antonio1.augello@mail.polimi.it
- ### [Lorenzo Baggi](https://github.com/Lollo03c)<br/>lorenzo1.baggi@mail.polimi.it
- ### [Stefano Bernardotto](https://github.com/StefanoBernardotto)<br/>stefano.bernardotto@mail.polimi.it
- ### [Andrea Brugnera](https://github.com/Brugni24)<br/>andrea.brugnera@mail.polimi.it


## Project Startup Guide

To simplify running the code, custom scripts have been created that automate building and launching the server and clients. 
These scripts allow you to start one or more clients and optionally the server with a single command, opening new terminal 
windows for each process where supported.

**Important:** Maven must be installed and available in your system PATH for these scripts to work.

---

### Linux/macOS

Script: `client.sh`

#### Usage

```bash
./client.sh [number_of_clients] [--server] [ip_address]
````

#### Examples

* `./client.sh` — builds the project and launches 1 client
* `./client.sh 2 --server` — builds the project, launches the server and 2 clients
* `./client.sh 2 --server 127.0.0.1` — builds the project, launches the server and 2 clients on 127.0.0.1 address

#### First time setup

Make the script executable (only once):

```bash
chmod +x client.sh
```

> Note: On macOS, the script opens new windows using Terminal.app. Other terminals may not support automatic window opening.

---

### Windows

Script: `client.ps1`

#### Usage

```powershell
.\client.ps1 [-numClients N] [-server]
```

#### Examples

* `.\client.ps1` — builds the project and launches 1 client
* `.\client.ps1 -numClients 3 -server` — builds the project, launches the server and 3 clients

#### First time setup

If PowerShell scripts are not enabled yet, run this command once:

```powershell
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
```

---

### Notes

* The `.jar` files are generated in the `target/` folder
* The scripts automatically run `mvn clean package` before launching
* Each process runs in a new terminal window where supported