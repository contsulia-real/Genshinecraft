# Genshinecraft

[简体中文](#简体中文) | [English](#english)

## 简体中文

Minecraft 七元素模组。**开发完成。**

### 功能

- 风、岩、雷、草、水、火、冰元素附着与反应；包含月曜和星烁反应。
- 神之眼、月之轮、星之楔；武器元素绑定。
- 七天神像、月神像：共鸣、供奉、传送。
- 元素矿石、元素史莱姆、世界等级（0–9）。
- 宝箱额外掉落：75% 元素之印／月之印；5% 随机款式和元素的**无主器官（不可认主）**。
- 砂轮放弃器官所有权：生命值降至 **1 点**，解锁进度「拥抱谎言，接受真实」。

### 支持版本

| Minecraft | Fabric | Forge | NeoForge | Java |
| --- | :---: | :---: | :---: | --- |
| 1.20.1 | ✓ | ✓ | — | 17 |
| 1.21.1 | ✓ | — | ✓ | 21 |
| 26.3 | ✓ | — | ✓ | 25 |

### 安装

1. 安装对应版本的模组加载器与 **Architectury API**；Fabric 还需要 **Fabric API**。
2. 将匹配 Minecraft 版本和加载器的 JAR 放入 `mods` 文件夹。

当前未提供 GitHub Release 安装包，可从源码构建。

### 操作

- **共鸣／供奉**：与神像交互。
- **绑定武器**：在物品栏中持有已认主器官，右键武器。
- **神像传送**：手持可用的来源器官或月之印，按 **V**（可改键）。
- **解除武器绑定／放弃器官所有权**：使用砂轮。

### 构建

在项目根目录运行，例如构建 1.21.1 Fabric：

```powershell
.\gradlew.bat :fabric:1.21.1:build
```

其他目标：`:<loader>:<version>:build`。输出：`<loader>/versions/<version>/build/libs/`。

### 许可

[MIT](LICENSE)。非官方同人模组，与 Mojang、Microsoft、miHoYo / HoYoverse 无关。

---

## English

A seven-element Minecraft mod. **Development complete.**

### Features

- Anemo, Geo, Electro, Dendro, Hydro, Pyro, and Cryo auras and reactions, including Lunar and Stellar reactions.
- Visions, Moon Wheels, and Star Wedges; elemental weapon binding.
- Statues of the Seven and Moon Statues: resonance, offerings, and teleportation.
- Elemental ores, elemental slimes, and world levels (0–9).
- Extra chest loot: 75% elemental/Moon Sigil; 5% random **ownerless organ (cannot be claimed)**.
- Abandon organ ownership using a grindstone: health drops to **1 HP**, unlocking “Embrace Lies, Accept Reality.”

### Supported versions

| Minecraft | Fabric | Forge | NeoForge | Java |
| --- | :---: | :---: | :---: | --- |
| 1.20.1 | ✓ | ✓ | — | 17 |
| 1.21.1 | ✓ | — | ✓ | 21 |
| 26.3 | ✓ | — | ✓ | 25 |

### Installation

1. Install the matching mod loader and **Architectury API**; Fabric also requires **Fabric API**.
2. Place the JAR matching your Minecraft version and loader in the `mods` folder.

No GitHub Release binaries are available yet; build from source instead.

### Controls

- **Resonance / offerings:** Interact with a statue.
- **Weapon binding:** Hold a claimed organ on your inventory cursor and right-click a weapon.
- **Statue teleport:** Hold a usable organ with a source statue or a Moon Sigil; press **V** (rebindable).
- **Remove weapon binding / abandon organ ownership:** Use a grindstone.

### Build

Run from the repository root, for example for 1.21.1 Fabric:

```powershell
.\gradlew.bat :fabric:1.21.1:build
```

Other targets: `:<loader>:<version>:build`. Output: `<loader>/versions/<version>/build/libs/`.

### License

[MIT](LICENSE). Unofficial fan mod; not affiliated with Mojang, Microsoft, miHoYo, or HoYoverse.
