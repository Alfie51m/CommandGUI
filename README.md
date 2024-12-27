# **CommandGUI Plugin**

## **Overview**
CommandGUI is a Minecraft Bukkit plugin that provides players with a custom GUI to execute commands. The GUI is fully configurable, allowing you to define items, commands, and layout. Players can also receive a Knowledge Book that opens the GUI when used.

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
Opens the Command GUI for the player.

### `/cgbook`
Gives the player a Knowledge Book that opens the Command GUI when used.

### `/cgreload`
Reloads the plugin's configuration.

---

## **Permissions**
| Permission              | Description                           | Default |
|--------------------------|---------------------------------------|---------|
| `commandgui.use`         | Allows player to open the GUI.        | OP      |
| `commandgui.book`        | Gives player book to open the GUI.    | OP      |
| `commandgui.reload`      | Allows reloading the plugin config.   | OP      |

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
gui-items:
  - slot: 0
    name: "&aGo to Spawn"
    command: "spawn"
    material: "COMPASS"
  - slot: 1
    name: "&cHeal"
    command: "heal"
    material: "GOLDEN_APPLE"
```

### Configuration Options
- **`name`**: The display name of the item in the GUI.
- **`command`**: The command executed when the item is clicked.
- **`material`**: The item's material (e.g., `DIAMOND`, `BED`).
- **`slot`**: The inventory slot for the item (optional, auto-increment if not provided).

---

## **Usage**
1. **Opening the GUI**:
   - Use the `/commandgui` command to open the GUI.
   - Alternatively, use the Knowledge Book provided with `/cgbook`.

2. **Adding Items**:
   - Edit the `config.yml` file to add or modify items in the GUI.
   - Reload the configuration with `/cgreload`.

---

## **Contributing**
Contributions are welcome! Please fork the repository, make your changes, and submit a pull request.

---

## **License**
This plugin is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

--- 

### **Thanks for using CommandGUI**
