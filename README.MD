# ISeeYou-Fork

<img alt="logo" height="100" src=".github/logo/logo.png" title="logo" width="100"/>

_Also known as ICU_

This project is a third-party version of [ISeeYou](https://github.com/MC-XiaoHei/ISeeYou), aiming to bring a high-performance branch with more practical features.

[中文](README_CN.md) | [English](README.MD)

---

## Warning

This plugin can only run on servers using the [Leaves](https://leavesmc.org/) core, it does not support other cores!

---

## Introduction

ISeeYou can use the Replay API provided by the [Leaves](https://leavesmc.org/) core to record every move of the player in `.mcpr` format.

### Features

- **Automatic recording**: No manual operation is required, the plugin will automatically record all players by default.
- **Flexible configuration**: You can set a whitelist and blacklist, as well as the recording path, etc., through the configuration file.
- **Anti-cheat support**: Adapted to [Themis Anti Cheat](https://www.spigotmc.org/resources/themis-anti-cheat-1-17-1-20-bedrock-support-paper-compatibility-free-optimized.90766/) and [Matrix](https://matrix.rip/), it automatically records when suspicious players are found (Beta)

Currently, it only adapts to [Themis Anti Cheat](https://www.spigotmc.org/resources/themis-anti-cheat-1-17-1-20-bedrock-support-paper-compatibility-free-optimized.90766/) and [Matrix](https://matrix.rip/). If you need to adapt more anti-cheat plugins, please open an [issue](https://github.com/Xavier-MC/ISeeYou/issues) to propose!

## Instructions for use

### Dependencies

- Server: **Leaves**
- Themis and its dependency ProtocolLib (optional)

### Tutorial

1. **Install the plugin**: Place the plugin file in the plugin directory of the Leaves server and restart the server.
2. **Configure settings**: Edit the `plugins/ISeeYou/config.toml` file and adjust the recording parameters and anti-cheat settings as needed.

## Explanation of configuration items

```toml

# Default: true
# Description: Whether to delete temporary files when loading the plugin. Default is true.
deleteTmpFileOnLoad = true

# Default: false
# Description: Whether to pause recording instead of stopping recording when the player quits the game. Default is false.
pauseInsteadOfStopRecordingOnPlayerQuit = false

# Default: "replay/player/${name}@${uuid}"
# Description: Template for the recording storage path, supports ${name} and ${uuid} variables.
recordPath = "replay/player/${name}@${uuid}"

# Default: true
# Description: Whether to enable Bstats . Default is false.
enableBstats = true

[pauseRecordingOnHighSpeed]
# Description: Whether to enable the high-speed recording pause function. This function pauses recording when the player moves at high speed. Default is false.
enabled = false
# Description: Speed threshold for triggering high-speed recording pause. Default is 20.00.
threshold = 20.0

[filter]
# Description: The basis for checking the blacklist and whitelist. Optional values are "name" or "uuid". Default is "name", which means the player's names are filled in the blacklist and whitelist below.
checkBy = "name"
# Description: Recording mode. Optional values are "blacklist" or "whitelist". Default is "blacklist".
recordMode = "blacklist"
# Description: Blacklist. Only effective when the recording mode is "blacklist".
blacklist = []
# Description: Whitelist. Only effective when the recording mode is "whitelist".
whitelist = []

[clearOutdatedRecordFile]
# Description: Whether to enable the function of cleaning up expired recording files. Default is false.
enabled = false
# Description: The number of days to keep expired recording files. Default is 7 days.
days = 7

[recordSuspiciousPlayer]
# Description: Whether to enable the function of recording suspicious players (Themis). Default is true (it's ineffective if Themis isn't installed).
enableThemisIntegration = false
# Description: Whether to enable the function of recording suspicious players (Matrix). Default is true (it's ineffective if Matrix isn't installed).
enableMatrixIntegration = false
# Description: If enabled, recording starts as soon as a player is flagged by any anti-cheat system, and it won't duplicate recordings. If disabled, mixed flags may cause duplicate recordings.
aggregateMonitoring = false
# Description: The number of minutes to record suspicious players. Default is 5 minutes.
recordMinutes = 5
# Description: Template for the suspicious player recording storage path, supports ${name} and ${uuid} variables. Only meaningful if aggregateMonitoring = false.
recordPath = "replay/suspicious/${name}@${uuid}"
# Description: Template for the suspicious player recording storage path, supports ${name} and ${uuid} variables. Only meaningful if aggregateMonitoring = true.
themisRecordPath = "replay/suspicious/Themis/${name}@${uuid}"
# Description: Template for the suspicious player recording storage path, supports ${name} and ${uuid} variables. Only meaningful if aggregateMonitoring = true.
matrixRecordPath = "replay/suspicious/Matrix/${name}@${uuid}"

```

## Precautions

- This plugin can only run under the [Leaves](https://leavesmc.top/) server environment, and does not support other common Spigot and its downstream cores (such as Paper, Purpur, etc.).
- Please read and configure the `config.toml` file carefully before using the plugin to ensure that the plugin can run normally.
- Although there are currently no bug reports caused by reload, try not to use plugins like Plugman to hot reload this plugin, which may cause many unknown problems!

## Statistics
[_![](https://bstats.org/signatures/bukkit/ISeeYou-Fork.svg)](https://bstats.org/plugin/bukkit/ISeeYou-Fork/21068)

## Thanks for the support

Thank you for using the ISeeYou-Fork plugin! If you encounter any problems or have any suggestions during use, please feel free to contact the author or submit an [Issue](https://github.com/Xavier-MC/ISeeYou/issues) to the GitHub repository.