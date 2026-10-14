# Genshinecraft

Minecraft 模组：七元素战斗、神像、外置魔力器官。

A Minecraft mod featuring elemental combat, statues, and external magic organs.

**[简体中文](#简体中文) · [English](#english) · [Issues](https://github.com/contsulia-real/Genshinecraft/issues) · [Source](https://github.com/contsulia-real/Genshinecraft)**

<p align="center">
  <img src="src/main/resources/assets/genshinecraft/textures/item/vision/mondstadt/mondstadt_anemo.png" width="64" alt="Anemo Vision" />
  &nbsp;&nbsp;&nbsp;
  <img src="src/main/resources/assets/genshinecraft/textures/item/lunar_wheel/lunar_wheel_hydro.png" width="64" alt="Hydro Moon Wheel" />
  &nbsp;&nbsp;&nbsp;
  <img src="src/main/resources/assets/genshinecraft/textures/item/star_wedge/star_wedge_pyro.png" width="64" alt="Pyro Star Wedge" />
</p>

## 支持版本 / Supported Versions

| Minecraft | Fabric | Forge | NeoForge |
|:---:|:---:|:---:|:---:|
| 1.20.1 | ✓ | ✓ | — |
| 1.21.1 | ✓ | — | ✓ |
| 26.3 | ✓ | — | ✓ |

## 简体中文

### 模组内容

- **七元素战斗**：风、岩、雷、草、水、火、冰的元素附着与反应，包含月曜、星烁反应。
- **外置魔力器官**：神之眼、月之轮、星之楔；可以绑定武器，赋予元素攻击。
- **神像**：探索七天神像并共鸣，收集元素之印供奉；月神像提供独立供奉和传送。
- **世界内容**：元素矿石、元素史莱姆、世界等级及额外的箱子战利品。

### 下载与安装

目前**没有发布预编译安装包**，可按下方指令从源码构建。

1. 安装上表对应的 Minecraft 模组加载器。
2. 安装 **Architectury API**；Fabric 版本还需要 **Fabric API**。
3. 将匹配游戏版本和加载器的模组 JAR 放入 `mods` 文件夹。

### 基本操作

- **共鸣、供奉**：与神像交互。
- **武器绑定**：在物品栏中用已认主的外置魔力器官右键武器。
- **神像传送**：手持有效器官或月之印，按 `V`（可改键）。
- **砂轮**：解除武器绑定；也可放弃器官所有权。

宝箱获得的无主器官无法认主。放弃已认主器官的所有权后，该器官永久失效。

## English

### Features

- **Elemental combat**: Anemo, Geo, Electro, Dendro, Hydro, Pyro, and Cryo, including Lunar and Stellar reactions.
- **External magic organs**: Visions, Moon Wheels, and Star Wedges; bind them to weapons for elemental attacks.
- **Statues**: Discover and resonate with Statues of the Seven, collect sigils for offerings, and use Moon Statues for separate progression and teleportation.
- **World content**: Elemental ores, elemental slimes, world levels, and additional chest loot.

### Download & Installation

**No prebuilt mod JARs have been published yet.** Build from source using the instructions below.

1. Install a supported Minecraft mod loader from the table above.
2. Install **Architectury API**; Fabric also requires **Fabric API**.
3. Put the mod JAR matching your game version and loader in the `mods` folder.

### Getting Started

- **Resonance and offerings**: Interact with statues.
- **Weapon binding**: Pick up an owned external magic organ in your inventory and right-click a weapon.
- **Statue teleportation**: Hold a usable organ or a Moon Sigil and press `V` (rebindable).
- **Grindstone**: Remove weapon bindings or abandon organ ownership.

Ownerless organs found in chests cannot be claimed. Abandoning an owned organ permanently disables it.

## 源码构建 / Build from Source

JDK：Minecraft 1.20.1 → **Java 17**；1.21.1 → **Java 21**；26.3 → **Java 25**。

在仓库根目录运行 / Run from the repository root:

```powershell
.\gradlew.bat :fabric:1.21.1:build
```

其他构建目标 / Other targets: `:<loader>:<version>:build`

产物位置 / Output: `<loader>/versions/<version>/build/libs/`

## 许可 / License

[MIT License](LICENSE) · [报告问题 / Report an issue](https://github.com/contsulia-real/Genshinecraft/issues)

非官方同人模组，与 Mojang、Microsoft 或 HoYoverse 无官方关联。

Unofficial fan project; not affiliated with Mojang, Microsoft, or HoYoverse.
