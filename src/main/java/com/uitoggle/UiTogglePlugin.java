package com.uitoggle;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;
import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
    name = "UI Toggle",
    description = "Toggle UI elements with config and hotkey, respecting game hides"
)
@Slf4j
public class UiTogglePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private UiToggleConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private KeyManager keyManager;

    private final Map<Integer, Boolean> lastVisibleStates = new HashMap<>(); // Memory for hotkey restore
    private final Map<Integer, Boolean> userHiddenBeforeGame = new HashMap<>(); // Track user hides before game
    private final Map<Integer, Boolean> disableUnhide = new HashMap<>(); // Disable flag for game hides
    private final Map<Integer, Boolean> lastPollHidden = new HashMap<>(); // Last hidden state for polling
    private final Map<Integer, Widget> widgetCache = new HashMap<>(); // Cached widgets for efficiency
    private boolean isToggled = false; // Hotkey state (true if hidden)
    private GameState previousGameState = GameState.UNKNOWN;

    @Override
    protected void startUp() {
        keyManager.registerKeyListener(toggleHotkeyListener);
        if (client.getGameState() == GameState.LOGGED_IN) {
            cacheWidgets();
            initializeStates();
        } else {
            resetAll();
        }
    }

    @Override
    protected void shutDown() {
        if (client.getGameState() == GameState.LOGGED_IN) {
            saveStates();
        }
        keyManager.unregisterKeyListener(toggleHotkeyListener);
        resetAll();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState currentState = event.getGameState();
        if (currentState == GameState.LOGIN_SCREEN && previousGameState == GameState.LOGGED_IN) {
            saveStates();
        }
        if (currentState == GameState.LOGGED_IN) {
            cacheWidgets();
            initializeStates();
        }
        previousGameState = currentState;
    }

    // Poll widgets on game tick for game hides and config sync
    @Subscribe
    public void onGameTick(GameTick event) {
        for (WidgetConfig wc : WidgetConfig.VALUES) {
            int interfaceId = wc.getInterfaceId();
            Widget widget = widgetCache.getOrDefault(interfaceId, client.getWidget(interfaceId));
            if (widget != null) {
                boolean currentHidden = widget.isHidden();
                boolean prevHidden = lastPollHidden.getOrDefault(interfaceId, false);
                boolean configShow = isShownInConfig(wc);

                // Detect game-driven visibility changes
                if (currentHidden != prevHidden) {
                    if (currentHidden) {
                        // Game hid it (if not our action)
                        if (configShow && !lastVisibleStates.containsKey(interfaceId)) {
                            setConfigShow(wc, false); // Auto-toggle config off
                            disableUnhide.put(interfaceId, true); // Disable unhiding
                            userHiddenBeforeGame.put(interfaceId, !configShow); // Track prior user hide
                            log.debug("Game hid interface {} - disabled unhiding", interfaceId);
                        }
                    } else {
                        // Game showed it
                        disableUnhide.put(interfaceId, false); // Re-enable unhiding
                        if (userHiddenBeforeGame.getOrDefault(interfaceId, false)) {
                            setVisibility(interfaceId, false); // Re-hide if user wanted it hidden
                            setConfigShow(wc, false); // Sync config
                        }
                        log.debug("Game showed interface {} - re-enabled unhiding", interfaceId);
                    }
                }

                // Sync widget visibility with config (if not game-blocked)
                if (!disableUnhide.getOrDefault(interfaceId, false) && currentHidden != !configShow) {
                    setVisibility(interfaceId, configShow);
                    log.debug("Synced interface {} to config: {}", interfaceId, configShow ? "visible" : "hidden");
                }

                lastPollHidden.put(interfaceId, currentHidden);
            }
        }
    }

    // Hotkey to toggle visible elements with memory
    private final HotkeyListener toggleHotkeyListener = new HotkeyListener(() -> config.toggleKey()) {
        @Override
        public void hotkeyPressed() {
            isToggled = !isToggled;
            if (isToggled) {
                // Hide visible ones, cache states
                lastVisibleStates.clear();
                for (WidgetConfig wc : WidgetConfig.VALUES) {
                    int interfaceId = wc.getInterfaceId();
                    Widget widget = widgetCache.getOrDefault(interfaceId, client.getWidget(interfaceId));
                    if (widget != null && !widget.isHidden()) {
                        lastVisibleStates.put(interfaceId, true);
                        setVisibility(interfaceId, false);
                        setConfigShow(wc, false); // Sync config
                    }
                }
            } else {
                // Restore from memory, respecting disables
                for (Map.Entry<Integer, Boolean> entry : lastVisibleStates.entrySet()) {
                    int interfaceId = entry.getKey();
                    if (!disableUnhide.getOrDefault(interfaceId, false)) {
                        setVisibility(interfaceId, true);
                        for (WidgetConfig wc : WidgetConfig.VALUES) {
                            if (wc.getInterfaceId() == interfaceId) {
                                setConfigShow(wc, true); // Sync config
                                break;
                            }
                        }
                    }
                }
            }
            log.debug("UI toggled to {}", isToggled ? "hidden" : "visible");
        }
    };

    // Helper to set visibility
    private void setVisibility(int interfaceId, boolean visible) {
        Widget widget = widgetCache.getOrDefault(interfaceId, client.getWidget(interfaceId));
        if (widget != null) {
            widget.setHidden(!visible);
        }
    }

    // Check if shown in config - updated to match our new config methods
    private boolean isShownInConfig(WidgetConfig wc) {
        switch (wc) {
            case CHATBOX: return config.showChatbox();
            case INVENTORY: return config.showInventory();
            case MINIMAP_ORBS: return config.showMinimapOrbs();
            case EQUIPMENT: return config.showEquipment();
            case COMBAT_STYLES: return config.showCombatStyles();
            case PRAYER: return config.showPrayer();
            case MAGIC: return config.showMagic();
            case SKILLS: return config.showSkills();
            case QUEST_TAB: return config.showQuestTab();
            case MUSIC_PLAYER: return config.showMusicPlayer();
            case FRIENDS_LIST: return config.showFriendsList();
            case IGNORE_LIST: return config.showIgnoreList();
            case LOGOUT_TAB: return config.showLogoutTab();
            case WORLD_MAP: return config.showWorldMap();
            case XP_DROPS: return config.showXpDrops();
            default: return true;
        }
    }

    // Set config show value
    private void setConfigShow(WidgetConfig wc, boolean show) {
        configManager.setConfiguration("uitoggle", wc.getShowKey(), String.valueOf(show));
    }

    // Initialize states on login
    private void initializeStates() {
        for (WidgetConfig wc : WidgetConfig.VALUES) {
            int interfaceId = wc.getInterfaceId();
            setVisibility(interfaceId, isShownInConfig(wc));
            lastPollHidden.put(interfaceId, !isShownInConfig(wc));
            disableUnhide.put(interfaceId, false);
            userHiddenBeforeGame.put(interfaceId, false);
        }
    }

    // Save states on logout
    private void saveStates() {
        for (WidgetConfig wc : WidgetConfig.VALUES) {
            int interfaceId = wc.getInterfaceId();
            Widget widget = widgetCache.getOrDefault(interfaceId, client.getWidget(interfaceId));
            if (widget != null) {
                boolean visible = !widget.isHidden();
                setConfigShow(wc, visible);
            }
        }
    }

    // Reset all states
    private void resetAll() {
        for (WidgetConfig wc : WidgetConfig.VALUES) {
            setVisibility(wc.getInterfaceId(), true);
            setConfigShow(wc, true);
        }
        lastVisibleStates.clear();
        userHiddenBeforeGame.clear();
        disableUnhide.clear();
        lastPollHidden.clear();
        widgetCache.clear();
        isToggled = false;
    }

    // Cache widgets
    private void cacheWidgets() {
        widgetCache.clear();
        for (WidgetConfig wc : WidgetConfig.VALUES) {
            Widget widget = client.getWidget(wc.getInterfaceId());
            if (widget != null) {
                widgetCache.put(wc.getInterfaceId(), widget);
            }
        }
    }

    @Provides
    UiToggleConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(UiToggleConfig.class);
    }
}