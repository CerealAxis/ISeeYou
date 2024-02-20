# ISeeYou-Fork

<img alt="logo" height="100" src=".github/logo/logo.png" title="logo" width="100"/>

_也可以叫ICU_

本项目为[ISeeYou](https://github.com/MC-XiaoHei/ISeeYou)的第三方版本，旨在带来更多实用功能的高性能分支。

[中文](README_CN.md) | [English](README.MD)

---

## 警告

本插件只能在使用 [Leaves](https://leavesmc.org/) 核心的服务器中运行，不支持其他核心！

---

## 简介

ISeeYou 可以利用 [Leaves](https://leavesmc.org/) 核心提供的 Replay API，以`.mcpr`格式录制玩家的一举一动。

### 功能特点

- **自动录制**：无需手动操作，默认情况下插件会自动记录所有玩家。
- **灵活配置**：可以通过配置文件设置黑白名单，以及录制路径等。
- **反作弊支持**：适配 [Themis Anti Cheat](https://www.spigotmc.org/resources/themis-anti-cheat-1-17-1-20-bedrock-support-paper-compatibility-free-optimized.90766/)和[Matrix](https://matrix.rip/)，在发现可疑玩家时自动进行录制 (Beta)

目前已适配 [Themis Anti Cheat](https://www.spigotmc.org/resources/themis-anti-cheat-1-17-1-20-bedrock-support-paper-compatibility-free-optimized.90766/)和[Matrix](https://matrix.rip/)，需要适配更多反作弊插件请开 Issue 提出！

## 使用说明

### 依赖项

- 服务端：**Leaves**
- Themis 及其依赖 ProtocolLib（可选）
- Matrix 及其依赖 ProtocolLib（可选）

### 使用教程

1. **安装插件**：将插件文件放置在 Leaves 服务器的插件目录下，并重新启动服务器。
2. **配置设置**：编辑 `plugins/ISeeYou/config.toml` 文件，根据需求调整录像参数和反作弊设置。

## 配置项说明

```toml
# 默认值: true
# 描述: 在加载插件时是否删除临时文件。默认值为true。
deleteTmpFileOnLoad = true

# 默认值: false
# 描述: 当玩家退出游戏时是否暂停录制而不是停止录制。默认值为false。
pauseInsteadOfStopRecordingOnPlayerQuit = false

# 默认值: "replay/player/${name}@${uuid}"
# 描述: 录制存储路径的模板，支持${name}和${uuid}变量。
recordPath = "replay/player/${name}@${uuid}"

# 默认值: true
# 描述: 是否启用Bstats（BungeeCord统计）。默认值为false。
enableBstats = true

[pauseRecordingOnHighSpeed]
# 描述: 是否启用高速录制暂停功能。此功能在玩家以高速移动时暂停录制。默认值为false。
enabled = false
# 描述: 触发高速录制暂停的速度阈值。默认值为20.00。
threshold = 20.0

[filter]
# 描述: 检查黑名单和白名单的基准。可选值为"name"或"uuid"。默认值为"name"，这意味着玩家的名称填写在下面的黑名单和白名单中。
checkBy = "name"
# 描述: 录制模式。可选值为"blacklist"或"whitelist"。默认值为"blacklist"。
recordMode = "blacklist"
# 描述: 黑名单。仅在录制模式为"blacklist"时有效。
blacklist = []
# 描述: 白名单。仅在录制模式为"whitelist"时有效。
whitelist = []

[clearOutdatedRecordFile]
# 描述: 是否启用清理过期录制文件的功能。默认值为false。
enabled = false
# 描述: 保留过期录制文件的天数。默认值为7天。
days = 7

[recordSuspiciousPlayer]
# 描述: 是否启用记录可疑玩家（Themis）的功能。默认为true（如果未安装Themis则无效）。
enableThemisIntegration = false
# 描述: 是否启用记录可疑玩家（Matrix）的功能。默认为true（如果未安装Themis则无效）。
enableMatrixIntegration = false
# 描述: 如果启用，只要玩家被任何反作弊系统标记，录制就会开始，并且不会重复录制。如果禁用，混合标记可能导致重复录制。
aggregateMonitoring = false
# 描述: 记录可疑玩家的分钟数。默认为5分钟。
recordMinutes = 5
# 描述: 可疑玩家录制存储路径的模板，支持${name}和${uuid}变量。仅在aggregateMonitoring = false时有意义。
recordPath = "replay/suspicious/${name}@${uuid}"
# 描述: 可疑玩家录制存储路径的模板，支持${name}和${uuid}变量。仅在aggregateMonitoring = true时有意义。
themisRecordPath = "replay/suspicious/Themis/${name}@${uuid}"
# 描述: 可疑玩家录制存储路径的模板，支持${name}和${uuid}变量。仅在aggregateMonitoring = true时有意义。
matrixRecordPath = "replay/suspicious/Matrix/${name}@${uuid}"

```

## 注意事项

- 本插件的运行只能在 [Leaves](https://leavesmc.top/) 服务端环境下使用，不支持其他常见的 Spigot 及其下游核心（例如 Paper、Purpur 等）。
- 请在使用插件前仔细阅读并配置好 `config.toml` 文件，以确保插件能够正常运行。
- 尽管目前没有因为 reload 导致的 bug 报告，但尽量不要使用 Plugman 等插件热重载本插件,这可能会导致许多未知的问题！

## Bstats统计
[_![](https://bstats.org/signatures/bukkit/ISeeYou-Fork.svg)](https://bstats.org/plugin/bukkit/ISeeYou-Fork/21068)


## 感谢支持

感谢您使用 ISeeYou 插件！如果您在使用过程中遇到任何问题或有任何建议，请随时联系作者或提交 [Issue](https://github.com/Xavier-MC/ISeeYou) 到 GitHub 仓库。
