package com.uitoggle;

import lombok.Getter;
import net.runelite.api.gameval.InterfaceID;

/**
 * Enum for core UI widget mappings using InterfaceID constants.
 */
@Getter
public enum WidgetConfig {
    CHATBOX(InterfaceID.CHATBOX, "showChatbox"),
    INVENTORY(InterfaceID.INVENTORY, "showInventory"),
    MINIMAP_ORBS(InterfaceID.ORBS, "showMinimapOrbs"),
    EQUIPMENT(InterfaceID.EQUIPMENT, "showEquipment"),
    COMBAT_STYLES(InterfaceID.COMBAT_INTERFACE, "showCombatStyles"),
    PRAYER(InterfaceID.PRAYERBOOK, "showPrayer"),
    MAGIC(InterfaceID.MAGIC_SPELLBOOK, "showMagic"),
    SKILLS(InterfaceID.STATS, "showSkills"),
    QUEST_TAB(InterfaceID.QUESTLIST, "showQuestTab"),
    MUSIC_PLAYER(InterfaceID.MUSIC, "showMusicPlayer"),
    FRIENDS_LIST(InterfaceID.FRIENDS, "showFriendsList"),
    IGNORE_LIST(InterfaceID.IGNORE, "showIgnoreList"),
    LOGOUT_TAB(InterfaceID.LOGOUT, "showLogoutTab"),
    WORLD_MAP(InterfaceID.WORLDMAP, "showWorldMap"),
    XP_DROPS(InterfaceID.XP_DROPS, "showXpDrops");

    private final int interfaceId;
    private final String showKey;

    WidgetConfig(int interfaceId, String showKey) {
        this.interfaceId = interfaceId;
        this.showKey = showKey;
    }

    public static final WidgetConfig[] VALUES = values();
}