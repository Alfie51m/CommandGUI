# **CommandGUI Plugin**

## **Overview**
CommandGUI is a Minecraft Bukkit plugin that provides players with a custom GUI to execute commands as the player interacting with the GUI. The GUI is fully configurable, allowing you to define items, commands, and layout. Players can also receive a Knowledge Book that opens the GUI when used.

---

## **Features**
- **Custom GUI**: A player-friendly interface to execute commands with a click.
- **Configurable Items**: Define items, their appearance, and the commands they execute in the GUI via the configuration file.
- **Dynamic Inventory Sizing**: Automatically adjusts the GUI size based on the number of items.
- **Knowledge Book Integration**: Players can use a custom Knowledge Book to open the GUI.
- **Command Reload**: Easily reload the plugin's configuration with `/cgreload`.

---

## **Commands**
### `/commandgui`
- **Description:** Opens the Command GUI for the player.
- **Aliases:** `/cg`

### `/commandgui book`
- **Description:** Gives the player a Knowledge Book that opens the Command GUI when used.
- **Aliases:** `/cg book`

### `/commandgui give <player>`
- **Description:** Gives the specified player the Command GUI Knowledge Book.
- **Aliases:** `/cg give <player>`

### `/commandgui help`
- **Description:** Displays all the plugin's commands.
- **Aliases:** `/cg help`

### `/commandgui reload`
- **Description:** Reloads the plugin's configuration.
- **Aliases:** `/cg reload`

---

## **Permissions**
| Permission          | Description                         | Default |
|---------------------|-------------------------------------|---------|
| `commandgui.use`    | Allows player to open the GUI.      | OP      |
| `commandgui.bypass` | Allows player to bypass cooldowns.  | OP      |
| `commandgui.book`   | Gives player book to open the GUI.  | OP      |
| `commandgui.give`   | Gives another player the GUI Book.  | OP      |
| `commandgui.reload` | Allows reloading the plugin config. | OP      |

---

## **Installation**
1. Download the CommandGUI plugin `.jar` file.
2. Place the `.jar` file in your server's `plugins` directory.
3. Start the server to generate the default configuration file.
4. Configure the plugin by editing `plugins/CommandGUI/config.yml`.
5. Reload the plugin with `/cgreload` or restart the server.

---

## **Configuration**
The `config.yml` file allows you to customize the items in the GUI.

```yaml
# General settings
language-file: "en_us.yml"  # Default language file
verbose-mode: false # Set to true to enable verbose messages globally for all items (default fallback)
gui-size-mode: "dynamic" # Options: "dynamic", "fixed"

gui-items:
   - slot: 0
     name: "&aGo to Spawn"
     command: "spawn"
     item: "COMPASS"
     run-as-player: true
     cooldown: 0 # No cooldown
     verbose: true # Always log interaction for this item
     lore:
        - "&7Click to teleport to spawn"

   - slot: 1
     name: "&cHeal"
     command: "heal"
     item: "GOLDEN_APPLE"
     run-as-player: true
     cooldown: 30 # 30 seconds cooldown
     verbose: false # Never log interaction for this item
     lore:
        - "&7Click to heal yourself"

   - slot: 2
     name: "&bDiamonds"
     command: "give %player% diamond 1"
     item: "DIAMOND"
     run-as-player: false
     cooldown: 60 # 60 seconds cooldown
      # No verbose field; falls back to global verbose-mode
     lore:
        - "&7Receive a diamond!"
        - "&7This is a server reward."
```

### Configuration Options

#### General Settings
- **`language-file`**: The language file to use (default: `en_us.yml`).
- **`verbose-mode`**: If `true`, displays verbose messages for item interactions globally (default: `false`).
- **`gui-size-mode`**: Determines the size of the GUI. Options:
   - `"dynamic"`: Adjusts the inventory size based on the number of items.
   - `"fixed"`: Sets the GUI size to a fixed double chest (54 slots).

#### GUI Items
Each item in the GUI has the following options:

- **`name`**: The display name of the item in the GUI, with color codes supported (e.g., `&aGo to Spawn`).
- **`command`**: The command executed when the item is clicked (e.g., `spawn` or `give %player% diamond 1`).
- **`item`**: The item's material (e.g., `DIAMOND`, `GOLDEN_APPLE`, `COMPASS`).
- **`slot`**: The inventory slot for the item (optional, auto-increment if not provided).
- **`lore`**: A list of text lines displayed as the item's lore (optional, supports color codes).
- **`run-as-player`**: If `true`, the command will run as the player. If `false`, the command will run as the console (default: `false`).
- **`cooldown`**: The cooldown in seconds before the item can be used again (default: `0` for no cooldown).
- **`verbose`**: If `true`, overrides the global `verbose-mode` and enables verbose logging for this item (optional).

---

## **Usage**
1. **Opening the GUI**:
   - Use the `/commandgui` command to open the GUI.
   - Alternatively, use the Knowledge Book provided with `/commandgui book`.

2. **Adding Items**:
   - Edit the `config.yml` file to add or modify items in the GUI.
   - Reload the configuration with `/commandgui reload`.

---

## **Contributing**
Contributions are welcome! Please fork the repository, make your changes, and submit a pull request.

---

## **License**
This plugin is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt).

--- 

### **Thanks for using CommandGUI**
You can also find this plugin on [Modrinth](https://modrinth.com/plugin/commandgui-spigot), [Paper Hangar](https://hangar.papermc.io/Alfie51m/CommandGUI), [SpigotMC](https://www.spigotmc.org/resources/commandgui.121547/)
