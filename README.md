# Songs of Syx Mods

My mods for [Songs of Syx](https://store.steampowered.com/app/1162750/Songs_of_Syx/).

| Mod | Description | Steam Workshop |
|-----|-------------|----------------|
| [Improved Line Tool](improved-line-tool) | The line tool draws lines in any angle instead of only 45° and 90°. | [Subscribe](https://steamcommunity.com/sharedfiles/filedetails/?id=3824020669) |

## Building

Requirements:

* Java 21 JDK (the game runs on Java 21, newer JDKs are not supported)
* Maven
* Songs of Syx installed via Steam

Install the game jar into the local Maven repository. Needed once and after every game update:

```
mvn validate
```

Build all mods and copy them into the games mod folder:

```
mvn install
```

Only a single mod:

```
mvn install -pl improved-line-tool
```

`mvn clean` removes the build output and the installed mods again.
With `-P mods-uploader` the mods are also copied into (or removed from) the Steam Workshop uploader directory.

The game and mod folders for Windows, Linux and macOS are set in the profiles of the [pom.xml](pom.xml).
Adjust them there if your game is installed somewhere else.

The game version the mods are built against is set via `game.version.major` and `game.version.minor` in the [pom.xml](pom.xml).

## Adding a mod

1. Copy the `improved-line-tool` folder and change `artifactId`, `mod.name`, `mod.description` and `mod.info` in its `pom.xml`
2. Add it to the `<modules>` in the [pom.xml](pom.xml)
3. Code goes into `src/main/java`, game files (assets, configs) into `src/main/resources/mod-files/`

## Credits

The build setup is based on the [Songs of Syx mod template](https://github.com/4rg0n/songs-of-syx-mod-example) by 4rg0n,
which also has a lot of general modding documentation.

The build setup and the mods are implemented with the support of [Claude Code](https://claude.com/claude-code).
