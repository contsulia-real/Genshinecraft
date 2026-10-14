# Genshinecraft

**Elemental combat meets Minecraft. · 让七元素融入 Minecraft 的冒险。**

[简体中文](#简体中文) · [English](#english)

---

## 简体中文

### 关于模组

**Genshinecraft** 是一款受《原神》元素系统启发的非官方 Minecraft 模组。探索世界中的神像，获得属于自己的外置魔力器官，为武器注入元素力量，并通过元素反应、神像供奉和世界等级逐步成长。

**开发状态：功能开发已完成。** 本仓库目前提供源代码，尚未发布 GitHub Release 安装包。

### 模组特色

- **七元素与元素反应** — 风、岩、雷、草、水、火、冰七种元素，包含蒸发、融化、扩散、绽放、超载、冻结等反应；天气和环境也会影响元素附着。
- **三类外置魔力器官** — 神之眼、月之轮与星之楔。将已认主的器官绑定到武器，进行元素攻击；月之轮和星之楔还能引发专属的月曜、星烁反应。
- **神像与供奉** — 探索七天神像并尝试共鸣，每座神像仅有一次机会，成功率为 80%。收集元素之印供奉神像以提升元素精通；月神像提供独立的供奉成长。
- **新的世界内容** — 七元素矿石、七种敌对元素史莱姆、神像结构，以及箱子中额外的印记与器官战利品。
- **世界等级与战斗反馈** — 世界等级影响生物强度和部分掉落；元素附着图标、反应特效和伤害数字让战斗变化更直观。

### 兼容版本

| Minecraft | Fabric | Forge | NeoForge |
| --- | :---: | :---: | :---: |
| 1.20.1 | ✅ | ✅ | — |
| 1.21.1 | ✅ | — | ✅ |
| 26.3 | ✅ | — | ✅ |

**必需依赖：** 所有版本均需 **Architectury API**；使用 Fabric 时还需 **Fabric API**。请选择与 Minecraft 版本及模组加载器一致的依赖。

### 安装

1. 安装上表所列、与你的 Minecraft 版本一致的 Fabric、Forge 或 NeoForge。
2. 安装对应版本的 Architectury API；Fabric 玩家另外安装 Fabric API。
3. 将对应加载器和游戏版本的 Genshinecraft JAR 放入游戏实例的 `mods` 文件夹。

**获取模组：** 当前尚无正式 Release 安装包，可以按下文说明从源码构建；不要将 GitHub 下载的源码 ZIP 直接放入 `mods` 文件夹。

### 如何开始

1. **探索并共鸣：** 在世界中寻找七天神像，与神像交互，有机会获得对应元素的外置魔力器官。
2. **绑定武器：** 在物品栏中拿起已认主的器官，对支持的武器使用右键绑定。器官需要留在玩家背包中才能为武器提供元素能力。
3. **战斗与供奉：** 通过元素攻击触发反应；挖掘元素矿石、击败元素史莱姆并搜寻宝箱，收集印记供奉神像。
4. **神像传送：** 手持可用且有来源神像的器官，按 **V** 返回来源神像；手持月之印按 **V** 可前往当前维度最近的已登记月神像。按键可在控制设置中修改。

**器官所有权说明：** 原版箱子战利品表额外有 **5%** 概率提供随机元素和款式的**无主器官**，但这类器官**不能认主**，首次进入背包也不会自动认主。主动将自己的器官放入砂轮并取出结果，会永久放弃所有权、将生命值降至 **1 点**，并获得独立进度**「拥抱谎言，接受真实」**。无主器官提示显示红字「无主」。

### 从源码构建

仓库使用 Gradle Wrapper、Stonecutter 和 Architectury。按构建目标准备相应 JDK：**1.20.1 使用 Java 17、1.21.1 使用 Java 21、26.3 使用 Java 25**。

例如在 Windows 上构建 Minecraft 1.21.1 Fabric 版本：

```powershell
.\gradlew.bat :fabric:1.21.1:build
```

其他目标将 `:fabric:1.21.1:build` 替换为上表中对应的 `:<加载器>:<版本>:build`（如 `:neoforge:26.3:build`）。构建产物位于 `<加载器>/versions/<版本>/build/libs/`，请选择用于游戏安装的生产版 JAR，而不是源码 JAR。

### 许可与声明

代码以 [MIT License](LICENSE) 开源。本项目是非官方同人作品，与 Mojang、Microsoft、miHoYo / HoYoverse 无官方关联。《Minecraft》和《原神》的相关商标属于各自权利人。

---

## English

### About

**Genshinecraft** is an unofficial Minecraft mod inspired by Genshin Impact's elemental system. Discover statues throughout the world, obtain your own external magic organs, infuse weapons with elemental power, and progress through elemental reactions, offerings, and world levels.

**Development status: feature development is complete.** This repository currently provides source code; no installable GitHub Release has been published yet.

### Features

- **Seven elements and reactions** — Anemo, Geo, Electro, Dendro, Hydro, Pyro, and Cryo, with reactions such as Vaporize, Melt, Swirl, Bloom, Overloaded, and Frozen. Weather and environmental effects can also apply elements.
- **Three types of external magic organs** — Visions, Moon Wheels, and Star Wedges. Bind an owned organ to a weapon to deal elemental damage. Moon Wheels and Star Wedges also enable special Lunar and Stellar reactions.
- **Statues and offerings** — Discover Statues of the Seven and attempt resonance. Each statue offers just one attempt with an **80%** success chance. Offer elemental sigils to increase Elemental Mastery; Moon Statues have their own progression.
- **New world content** — Seven elemental ores, seven hostile elemental slimes, statue structures, and extra sigil and organ loot in chests.
- **World levels and combat feedback** — World level progression affects mob strength and some drops, while elemental aura icons, reaction effects, and damage numbers make combat more readable.

### Supported versions

| Minecraft | Fabric | Forge | NeoForge |
| --- | :---: | :---: | :---: |
| 1.20.1 | ✅ | ✅ | — |
| 1.21.1 | ✅ | — | ✅ |
| 26.3 | ✅ | — | ✅ |

**Required dependencies:** **Architectury API** for all versions, plus **Fabric API** for Fabric. Install dependency versions matching your Minecraft version and mod loader.

### Installation

1. Install a supported Fabric, Forge, or NeoForge loader for your Minecraft version.
2. Install the matching Architectury API, and Fabric API if you use Fabric.
3. Place the Genshinecraft JAR built for your Minecraft version and loader in the instance's `mods` folder.

**Getting the mod:** There is no packaged GitHub Release yet. Build a JAR from source as described below. A GitHub source ZIP is **not** an installable mod.

### Getting started

1. **Explore and resonate:** Find a Statue of the Seven and interact with it for a chance to receive an external magic organ of its element.
2. **Bind a weapon:** Pick up your owned organ in the inventory and right-click a supported weapon to bind it. Keep the organ in your inventory for the weapon to retain its elemental power.
3. **Fight and make offerings:** Trigger reactions with elemental attacks. Mine elemental ores, defeat elemental slimes, and explore chests for sigils to offer at statues.
4. **Teleport to statues:** Hold a usable organ linked to a source statue and press **V** to return there. Hold a Moon Sigil and press **V** to reach the nearest registered Moon Statue in the current dimension. The key can be rebound in Controls.

**Organ ownership:** Built-in chest loot tables have an additional **5%** chance to contain an **ownerless** organ with a random element and appearance. **Chest-found organs cannot be claimed**, even when first picked up. To voluntarily abandon an owned organ, place it in a grindstone and take the result: ownership is permanently relinquished, your health is set to **1 HP**, and the separate advancement **“Embrace Lies, Accept Reality”** is awarded. Ownerless organs display a red ownership label.

### Building from source

This project uses Gradle Wrapper, Stonecutter, and Architectury. Use the JDK required by your build target: **Java 17 for 1.20.1, Java 21 for 1.21.1, and Java 25 for 26.3**.

For example, build the Minecraft 1.21.1 Fabric version on Windows:

```powershell
.\gradlew.bat :fabric:1.21.1:build
```

For another target, replace `:fabric:1.21.1:build` with `:<loader>:<version>:build` (for example, `:neoforge:26.3:build`). Artifacts are generated in `<loader>/versions/<version>/build/libs/`. Use the production mod JAR, not a sources JAR.

### License & disclaimer

Source code is available under the [MIT License](LICENSE). This is an unofficial fan project and is not affiliated with Mojang, Microsoft, miHoYo, or HoYoverse. Minecraft and Genshin Impact trademarks belong to their respective owners.
