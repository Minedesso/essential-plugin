# Essential Plugin

**Essential-Plugin für das Minecraft-Netzwerk.**  
Core Essentials plugin used exclusively on the Citybuild server, providing fundamental player features such as 
warps, TPA, homes, and enderchest access. Acts as a shared utility layer for Citybuild systems, with clean APIs, 
permission control, and extensible command-based interactions.

---

## ⌨️ Commands

| Command                | Beschreibung                         | Permission                | Aliases |
|------------------------|--------------------------------------|---------------------------|---------|
| `/warp <name>`         | Teleportieren zu einem Warp          | `(essential.warp.<name>)` | `-`     |
| `/warps create <name>` | Erstelle einen Warp                  | `essential.warp.create`   | `-`     |
| `/warps delete <name>` | Lösche einen Warp                    | `essential.warp.delete`   | `-`     |
| `/warps`               | Liste alle Warps auf                 | `(essential.warp.<name>)` | `-`     |
| `/inv`                 | Zeige das Inventar des Spielers an   | `essential.inv.see`       | `-`     |
| `/ec`                  | Zeige die Endertruhe des Spielers an | `essential.inv.ec`        | `-`     |
| `/tpa <player>`        | Schickt eine Teleportationsanfrage   |                           | `-`     |
| `/tpaccept <player>`   | Akzeptiert Teleportationsanfrage     |                           | `-`     |
| `/tpdeny <player>`     | Lehnt Teleportationsanfrage ab       |                           | `-`     |
| `/tpacancel`           | Zieht Teleportationsanfrage zurück   |                           | `-`     |

---

## 🔐 Permission-Baum

```
essential.*
 ├─ essential.warp
 |    ├─ essential.warp.create
 |    ├─ essential.warp.delete
 |    └─ essential.warp.<warpname>
 └─ essential.inv
      ├─ essential.inv.see
      └─ essential.inv.ec
```

---

## Build

Voraussetzungen:

- Java 21
- Maven
- Spigot 1.21 API im lokalen/remote Repository
- Zugriff auf die gemeinsame Server-API

Build:

```bash
mvn clean package
```

Das fertige Plugin befindet sich anschließend unter:

```
/target/essential-plugin-VERSION.jar
```

## Run

1. Plugin-JAR in den `plugins/`-Ordner des Lobby-Servers legen
2. MySQL-Verbindungen in der Konfiguration setzen
3. Server starten

Das Plugin ist für den Betrieb **ausschließlich für den CityBuild-Server** vorgesehen.