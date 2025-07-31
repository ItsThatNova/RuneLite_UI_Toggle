package com.uitoggle;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@ConfigGroup("uitoggle")
public interface UiToggleConfig extends Config
{
    @ConfigItem(
            keyName = "showChatbox",
            name = "Show Chatbox",
            description = "Show or hide the chatbox interface"
    )
    boolean showChatbox();

    @ConfigItem(
            keyName = "showInventory",
            name = "Show Inventory",
            description = "Show or hide the inventory interface"
    )
    boolean showInventory();

    @ConfigItem(
            keyName = "showMinimapOrbs",
            name = "Show Minimap/Orbs",
            description = "Show or hide the minimap, compass, and orb area"
    )
    boolean showMinimapOrbs();

    @ConfigItem(
            keyName = "showEquipment",
            name = "Show Equipment",
            description = "Show or hide the equipment interface"
    )
    boolean showEquipment();

    @ConfigItem(
            keyName = "showCombatStyles",
            name = "Show Combat Styles",
            description = "Show or hide the combat styles interface"
    )
    boolean showCombatStyles();

    @ConfigItem(
            keyName = "showPrayer",
            name = "Show Prayer",
            description = "Show or hide the prayer book interface"
    )
    boolean showPrayer();

    @ConfigItem(
            keyName = "showMagic",
            name = "Show Magic",
            description = "Show or hide the spellbook interface"
    )
    boolean showMagic();

    @ConfigItem(
            keyName = "showSkills",
            name = "Show Skills",
            description = "Show or hide the skills/stats interface"
    )
    boolean showSkills();

    @ConfigItem(
            keyName = "showQuestTab",
            name = "Show Quest Tab",
            description = "Show or hide the quest list interface"
    )
    boolean showQuestTab();

    @ConfigItem(
            keyName = "showMusicPlayer",
            name = "Show Music Player",
            description = "Show or hide the music interface"
    )
    boolean showMusicPlayer();

    @ConfigItem(
            keyName = "showFriendsList",
            name = "Show Friends List",
            description = "Show or hide the friends list interface"
    )
    boolean showFriendsList();

    @ConfigItem(
            keyName = "showIgnoreList",
            name = "Show Ignore List",
            description = "Show or hide the ignore list interface"
    )
    boolean showIgnoreList();

    @ConfigItem(
            keyName = "showLogoutTab",
            name = "Show Logout Tab",
            description = "Show or hide the logout interface"
    )
    boolean showLogoutTab();

    @ConfigItem(
            keyName = "showWorldMap",
            name = "Show World Map",
            description = "Show or hide the world map interface"
    )
    boolean showWorldMap();

    @ConfigItem(
            keyName = "showXpDrops",
            name = "Show XP Drops",
            description = "Show or hide the XP drop notifications"
    )
    boolean showXpDrops();

    @ConfigItem(
            keyName = "toggleKey",
            name = "Toggle Hotkey",
            description = "The key to toggle visible UI elements (default Ctrl + Insert)"
    )
    default Keybind toggleKey() {
        return new Keybind(KeyEvent.VK_INSERT, InputEvent.CTRL_DOWN_MASK);
    }
}
