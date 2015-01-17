/*
 * Copyright (C) 2014 cnaude
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cnaude.purpleirc;

import com.cnaude.purpleirc.GameListeners.AdminChatListener;
import com.cnaude.purpleirc.GameListeners.CleverNotchListener;
import com.cnaude.purpleirc.GameListeners.DeathMessagesListener;
import com.cnaude.purpleirc.GameListeners.DynmapListener;
import com.cnaude.purpleirc.GameListeners.EssentialsListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerChatListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerCommandPreprocessingListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerDeathListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerGameModeChangeListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerJoinListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerKickListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerPlayerAchievementAwardedListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerQuitListener;
import com.cnaude.purpleirc.GameListeners.GameServerCommandListener;
import com.cnaude.purpleirc.GameListeners.HeroChatListener;
import com.cnaude.purpleirc.GameListeners.McMMOChatListener;
import com.cnaude.purpleirc.GameListeners.OreBroadcastListener;
import com.cnaude.purpleirc.GameListeners.PrismListener;
import com.cnaude.purpleirc.GameListeners.RedditStreamListener;
import com.cnaude.purpleirc.GameListeners.ReportRTSListener;
import com.cnaude.purpleirc.GameListeners.TitanChatListener;
import com.cnaude.purpleirc.GameListeners.TownyChatListener;
import com.cnaude.purpleirc.Hooks.AdminPrivateChatHook;
import com.cnaude.purpleirc.Hooks.DynmapHook;
import com.cnaude.purpleirc.Hooks.FactionChatHook;
import com.cnaude.purpleirc.Hooks.JobsHook;
import com.cnaude.purpleirc.Hooks.JobsHookOld;
import com.cnaude.purpleirc.Hooks.ReportRTSHook;
import com.cnaude.purpleirc.Hooks.ShortifyHook;
import com.cnaude.purpleirc.Hooks.SuperVanishHook;
import com.cnaude.purpleirc.Hooks.TownyChatHook;
import com.cnaude.purpleirc.Hooks.VanishHook;
import com.cnaude.purpleirc.Hooks.VaultHook;
import com.cnaude.purpleirc.Utilities.CaseInsensitiveMap;
import com.cnaude.purpleirc.Utilities.ChatTokenizer;
import com.cnaude.purpleirc.Utilities.ColorConverter;
import com.cnaude.purpleirc.Utilities.IRCMessageHandler;
import com.cnaude.purpleirc.Utilities.NetPackets;
import com.cnaude.purpleirc.Utilities.Query;
import com.cnaude.purpleirc.Utilities.RegexGlobber;
import com.cnaude.purpleirc.Utilities.UpdateChecker;
import com.google.common.base.Joiner;
import com.onarandombox.MultiverseCore.api.MVPlugin;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.pircbotx.IdentServer;

/**
 *
 * @author Chris Naudé
 */
public class PurpleIRC extends JavaPlugin {

    public String LOG_HEADER;
    public String LOG_HEADER_F;
    static final Logger log = Logger.getLogger("Minecraft");
    private final String sampleFileName;
    private final String MAINCONFIG;
    private File pluginFolder;
    public File botsFolder;
    private File configFile;
    public static long startTime;
    public boolean identServerEnabled;
    private final CaseInsensitiveMap<HashMap<String, String>> messageTmpl;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> ircHeroChannelMessages;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> ircHeroActionChannelMessages;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> ircTownyChannelMessages;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> heroChannelMessages;
    private final CaseInsensitiveMap<CaseInsensitiveMap<String>> heroActionChannelMessages;
    private final Map<String, String> hostCache;
    public String defaultPlayerSuffix,
            defaultPlayerPrefix,
            defaultPlayerGroup,
            defaultGroupPrefix,
            defaultPlayerWorld,
            defaultGroupSuffix,
            customTabPrefix,
            heroChatEmoteFormat,
            listFormat,
            listSeparator,
            listPlayer,
            ircNickPrefixIrcOp,
            ircNickPrefixSuperOp,
            ircNickPrefixOp,
            ircNickPrefixHalfOp,
            ircNickPrefixVoice;
    private final CaseInsensitiveMap<String> displayNameCache;
    public CaseInsensitiveMap<UUID> uuidCache;

    public ArrayList<String> kickedPlayers = new ArrayList<>();

    public final String invalidBotName = ChatColor.RED + "Invalid bot name: "
            + ChatColor.WHITE + "%BOT%"
            + ChatColor.RED + ". Type '" + ChatColor.WHITE + "/irc listbots"
            + ChatColor.RED + "' to see valid bots.";

    public final String invalidChannelName = ChatColor.RED + "Invalid channel name: "
            + ChatColor.WHITE + "%CHANNEL%";

    public final String invalidChannel = ChatColor.RED + "Invalid channel: "
            + ChatColor.WHITE + "%CHANNEL%";
    public final String noPermission = ChatColor.RED + "You do not have permission to use this command.";

    private boolean updateCheckerEnabled;
    private String updateCheckerMode;
    private boolean debugEnabled;
    private boolean stripGameColors;
    private boolean stripIRCColors;
    private boolean stripIRCBackgroundColors;
    public boolean customTabList;
    public String customTabGamemode;
    private boolean listSortByName;
    public boolean exactNickMatch;
    public boolean ignoreChatCancel;
    public Long ircConnCheckInterval;
    public Long ircChannelCheckInterval;
    public ChannelWatcher channelWatcher;
    public ColorConverter colorConverter;
    public RegexGlobber regexGlobber;
    public CaseInsensitiveMap<PurpleBot> ircBots;
    public FactionChatHook fcHook;
    public TownyChatHook tcHook;
    public DynmapHook dynmapHook;
    public JobsHook jobsHook;
    public JobsHookOld jobsHookOld;
    public AdminPrivateChatHook adminPrivateChatHook;
    public ShortifyHook shortifyHook;
    public ReportRTSHook reportRTSHook;
    public NetPackets netPackets;
    public CommandHandlers commandHandlers;
    public PurpleTabCompleter ircTabCompleter;
    private BotWatcher botWatcher;
    public IRCMessageHandler ircMessageHandler;

    public CommandQueueWatcher commandQueue;
    public UpdateChecker updateChecker;
    public ChatTokenizer tokenizer;
    private File heroConfigFile;
    public VaultHook vaultHelpers;
    public VanishHook vanishHook;
    public SuperVanishHook superVanishHook;
    private YamlConfiguration heroConfig;
    private final File cacheFile;
    private final File uuidCacheFile;

    public PurpleIRC() {
        this.MAINCONFIG = "MAIN-CONFIG";
        this.sampleFileName = "SampleBot.yml";
        this.netPackets = null;
        this.ircBots = new CaseInsensitiveMap<>();
        this.messageTmpl = new CaseInsensitiveMap<>();
        this.ircHeroChannelMessages = new CaseInsensitiveMap<>();
        this.ircHeroActionChannelMessages = new CaseInsensitiveMap<>();
        this.ircTownyChannelMessages = new CaseInsensitiveMap<>();
        this.heroChannelMessages = new CaseInsensitiveMap<>();
        this.heroActionChannelMessages = new CaseInsensitiveMap<>();
        this.displayNameCache = new CaseInsensitiveMap<>();
        this.uuidCache = new CaseInsensitiveMap<>();
        this.hostCache = new HashMap<>();
        this.cacheFile = new File("plugins/PurpleIRC/displayName.cache");
        this.uuidCacheFile = new File("plugins/PurpleIRC/uuid.cache");
    }

    /**
     *
     */
    @Override
    public void onEnable() {
        LOG_HEADER = "[" + this.getName() + "]";
        LOG_HEADER_F = ChatColor.DARK_PURPLE + "[" + this.getName() + "]" + ChatColor.RESET;
        if (!getServer().getVersion().contains("Spigot")) {
            logError("This plugin is only compatible with Spigot. Please download the CraftBukkit version from the BukkitDev site.");
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        pluginFolder = getDataFolder();
        botsFolder = new File(pluginFolder + "/bots");
        configFile = new File(pluginFolder, "config.yml");
        createConfigDirs();
        createConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        loadConfig();
        loadDisplayNameCache();
        loadUuidCache();
        if (identServerEnabled) {
            logInfo("Starting Ident Server ...");
            try {
                IdentServer.startServer();
            } catch (Exception ex) {
                logError(ex.getMessage());
            }
        }
        getServer().getPluginManager().registerEvents(new GamePlayerPlayerAchievementAwardedListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerGameModeChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerCommandPreprocessingListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerKickListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new GameServerCommandListener(this), this);
        if (isPluginEnabled("Herochat")) {
            logInfo("Enabling HeroChat support.");
            getServer().getPluginManager().registerEvents(new HeroChatListener(this), this);
            heroConfig = new YamlConfiguration();
            heroConfigFile = new File(getServer().getPluginManager()
                    .getPlugin("Herochat").getDataFolder(), "config.yml");
            try {
                heroConfig.load(heroConfigFile);
            } catch (IOException | InvalidConfigurationException ex) {
                logError(ex.getMessage());
            }
            heroChatEmoteFormat = heroConfig.getString("format.emote", "");
        } else {
            logInfo("HeroChat not detected.");
        }
        if (isPluginEnabled("TitanChat")) {
            logInfo("Enabling TitanChat support.");
            getServer().getPluginManager().registerEvents(new TitanChatListener(this), this);
        } else {
            logInfo("TitanChat not detected.");
        }
        if (isPluginEnabled("Prism")) {
            logInfo("Enabling Prism support.");
            getServer().getPluginManager().registerEvents(new PrismListener(this), this);
        } else {
            logInfo("Prism not detected.");
        }
        if (isPluginEnabled("RedditStream")) {
            logInfo("Enabling RedditStream support.");
            getServer().getPluginManager().registerEvents(new RedditStreamListener(this), this);
        } else {
            logInfo("RedditStream not detected.");
        }
        if (isPluginEnabled("TownyChat")) {
            logInfo("Enabling TownyChat support.");
            getServer().getPluginManager().registerEvents(new TownyChatListener(this), this);
            tcHook = new TownyChatHook(this);
        } else {
            logInfo("TownyChat not detected.");
        }
        if (isPluginEnabled("CleverNotch")) {
            logInfo("Enabling CleverNotch support.");
            getServer().getPluginManager().registerEvents(new CleverNotchListener(this), this);
        } else {
            logInfo("CleverNotch not detected.");
        }
        if (isPluginEnabled("mcMMO")) {
            logInfo("Enabling mcMMO support.");
            getServer().getPluginManager().registerEvents(new McMMOChatListener(this), this);
        } else {
            logInfo("mcMMO not detected.");
        }
        if (isFactionsEnabled()) {
            if (isPluginEnabled("FactionChat")) {
                String factionChatVersion = getServer().getPluginManager().getPlugin("FactionChat").getDescription().getVersion();
                if (factionChatVersion.startsWith("1.7")) {
                    logError("FactionChat v" + factionChatVersion + " not supported. Please install 1.8 or newer.");
                } else {
                    logInfo("Enabling FactionChat support.");
                    fcHook = new FactionChatHook(this);
                }
            } else {
                logInfo("FactionChat not detected.");
            }
        }
        if (isPluginEnabled("AdminPrivateChat")) {
            logInfo("Enabling AdminPrivateChat support.");
            adminPrivateChatHook = new AdminPrivateChatHook(this);
            getServer().getPluginManager().registerEvents(new AdminChatListener(this), this);
        } else {
            logInfo("AdminPrivateChat not detected.");
        }
        if (isPluginEnabled("Jobs")) {
            String m = getServer().getPluginManager().getPlugin("Jobs").getDescription().getMain();
            String jobsVersion = getServer().getPluginManager().getPlugin("Jobs").getDescription().getVersion();
            if (m.contains("me.zford")) {
                logInfo("Enabling legacy Jobs support: " + jobsVersion);
                jobsHookOld = new JobsHookOld(this);
            } else if (m.contains("com.gamingmesh")) {
                logInfo("Enabling new Jobs support: " + jobsVersion);
                jobsHook = new JobsHook(this);
            } else {
                logError("Unable to hook into Jobs: " + m);
            }
        } else {
            logInfo("Jobs not detected.");
        }
        if (isPluginEnabled("DeathMessages")) {
            logInfo("Enabling DeathMessages support.");
            getServer().getPluginManager().registerEvents(new DeathMessagesListener(this), this);
        } else {
            logInfo("DeathMessages not detected.");
        }
        if (isPluginEnabled("Shortify")) {
            String shortifyVersion = getServer().getPluginManager().getPlugin("Shortify").getDescription().getVersion();
            if (shortifyVersion.startsWith("1.8")) {
                logInfo("Enabling Shortify v" + shortifyVersion + " support.");
                shortifyHook = new ShortifyHook(this);
            } else {
                logError("Shortify v" + shortifyVersion + " not supported. Please use the latest version from http://jenkins.cnaude.org/job/Shortify/");
            }
        } else {
            logInfo("Shortify not detected.");
        }
        if (isPluginEnabled("dynmap")) {
            logInfo("Enabling Dynmap support.");
            getServer().getPluginManager().registerEvents(new DynmapListener(this), this);
            dynmapHook = new DynmapHook(this);
        } else {
            logInfo("Dynmap not detected.");
        }
        if (isPluginEnabled("OreBroadcast")) {
            logInfo("Enabling OreBroadcast support.");
            getServer().getPluginManager().registerEvents(new OreBroadcastListener(this), this);
        } else {
            logInfo("OreBroadcast not detected.");
        }
        vanishHook = new VanishHook(this);
        if (isPluginEnabled("SuperVanish")) {
            logInfo("Enabling SuperVanish support.");
            superVanishHook = new SuperVanishHook(this);
        } else {
            logInfo("SuperVanish not detected.");
        }
        if (isPluginEnabled("ReportRTS")) {
            logInfo("Enabling ReportRTS support.");
            getServer().getPluginManager().registerEvents(new ReportRTSListener(this), this);
            reportRTSHook = new ReportRTSHook(this);
        } else {
            logInfo("ReportRTS not detected.");
        }
        if (isPluginEnabled("Essentials")) {
            logInfo("Enabling Essentials support.");
            getServer().getPluginManager().registerEvents(new EssentialsListener(this), this);
        } else {
            logInfo("Essentials not detected.");
        }
        commandHandlers = new CommandHandlers(this);
        ircTabCompleter = new PurpleTabCompleter(this);
        getCommand("irc").setExecutor(commandHandlers);
        getCommand("irc").setTabCompleter(ircTabCompleter);
        regexGlobber = new RegexGlobber();
        tokenizer = new ChatTokenizer(this);
        loadBots();
        createSampleBot();
        channelWatcher = new ChannelWatcher(this);
        setupVault();
        if (customTabList) {
            if (checkForProtocolLib()) {
                logInfo("Hooked into ProtocolLib! Custom tab list is enabled.");
                netPackets = new NetPackets(this);
            } else {
                logError("ProtocolLib not found! The custom tab list is disabled.");
                netPackets = null;
            }
        } else {
            netPackets = null;
        }
        botWatcher = new BotWatcher(this);
        ircMessageHandler = new IRCMessageHandler(this);
        commandQueue = new CommandQueueWatcher(this);
        updateChecker = new UpdateChecker(this);
    }

    /**
     *
     */
    @Override
    public void onDisable() {
        if (channelWatcher != null) {
            logDebug("Disabling channelWatcher ...");
            channelWatcher.cancel();
        }
        if (botWatcher != null) {
            logDebug("Disabling botWatcher ...");
            botWatcher.cancel();
        }
        if (updateChecker != null) {
            logDebug("Disabling updateChecker ...");
            updateChecker.cancel();
        }
        if (ircBots.isEmpty()) {
            logInfo("No IRC bots to disconnect.");
        } else {
            logInfo("Disconnecting IRC bots.");
            for (PurpleBot ircBot : ircBots.values()) {
                commandQueue.cancel();
                ircBot.saveConfig(getServer().getConsoleSender());
                ircBot.quit();
            }
            ircBots.clear();
        }
        if (identServerEnabled) {
            logInfo("Stopping Ident Server");
            try {
                IdentServer.stopServer();
            } catch (IOException ex) {
                logError(ex.getMessage());
            }
        }
        saveDisplayNameCache();
        saveUuidCache();
    }

    /**
     *
     * @param debug
     */
    public void debugMode(boolean debug) {
        debugEnabled = debug;
        getConfig().set("Debug", debug);
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            logError("Problem saving to " + configFile.getName() + ": " + ex.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public boolean debugMode() {
        return debugEnabled;
    }

    public String getHeroMsgTemplate(String botName, String tmpl) {
        if (messageTmpl.containsKey(botName)) {
            if (messageTmpl.get(botName).containsKey(tmpl)) {
                return messageTmpl.get(botName).get(tmpl);
            }
        }
        if (messageTmpl.get(MAINCONFIG).containsKey(tmpl)) {
            return messageTmpl.get(MAINCONFIG).get(tmpl);
        }
        return "";
    }

    public String getMsgTemplate(String botName, String tmpl) {
        if (messageTmpl.containsKey(botName)) {
            if (messageTmpl.get(botName).containsKey(tmpl)) {
                return messageTmpl.get(botName).get(tmpl);
            }
        }
        if (messageTmpl.get(MAINCONFIG).containsKey(tmpl)) {
            return messageTmpl.get(MAINCONFIG).get(tmpl);
        }
        return "INVALID TEMPLATE";
    }

    public String getMsgTemplate(String tmpl) {
        return getMsgTemplate(MAINCONFIG, tmpl);
    }

    public String getHeroTemplate(CaseInsensitiveMap<CaseInsensitiveMap<String>> hc,
            String botName, String hChannel) {
        if (hc.containsKey(botName)) {
            logDebug("HC1 => " + hChannel);
            for (String s : hc.get(botName).keySet()) {
                logDebug("HT => " + s);
            }
            if (hc.get(botName).containsKey(hChannel)) {
                logDebug("HC2 => " + hChannel);
                return hc.get(botName).get(hChannel);
            }
        }
        if (hc.containsKey(MAINCONFIG)) {
            logDebug("HC3 => " + hChannel);
            for (String s : hc.get(MAINCONFIG).keySet()) {
                logDebug("HT => " + s);
            }
            if (hc.get(MAINCONFIG).containsKey(hChannel)) {
                logDebug("HC4 => " + hChannel);
                return hc.get(MAINCONFIG).get(hChannel);
            }
        }
        return "";
    }

    public String getHeroChatChannelTemplate(String botName, String hChannel) {
        String tmpl = getHeroTemplate(heroChannelMessages, botName, hChannel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.HERO_CHAT);
        }
        return getHeroTemplate(heroChannelMessages, botName, hChannel);
    }

    public String getHeroActionChannelTemplate(String botName, String hChannel) {
        String tmpl = getHeroTemplate(heroActionChannelMessages, botName, hChannel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.HERO_ACTION);
        }
        return getHeroTemplate(heroActionChannelMessages, botName, hChannel);
    }

    public String getIRCHeroChatChannelTemplate(String botName, String hChannel) {
        String tmpl = getHeroTemplate(ircHeroChannelMessages, botName, hChannel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.IRC_HERO_CHAT);
        }
        return getHeroTemplate(ircHeroChannelMessages, botName, hChannel);
    }

    public String getIRCHeroActionChannelTemplate(String botName, String hChannel) {
        String tmpl = getHeroTemplate(ircHeroActionChannelMessages, botName, hChannel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.IRC_HERO_ACTION);
        }
        return getHeroTemplate(ircHeroActionChannelMessages, botName, hChannel);
    }

    public String getIRCTownyChatChannelTemplate(String botName, String tChannel) {
        String tmpl = getHeroTemplate(ircTownyChannelMessages, botName, tChannel);
        if (tmpl.isEmpty()) {
            return getMsgTemplate(MAINCONFIG, TemplateName.IRC_TOWNY_CHAT);
        }
        return getHeroTemplate(ircTownyChannelMessages, botName, tChannel);
    }

    public void loadCustomColors(YamlConfiguration config) {
        for (String t : config.getConfigurationSection("irc-color-map").getKeys(false)) {
            colorConverter.addIrcColorMap(t, config.getString("irc-color-map." + t));
        }
        for (String t : config.getConfigurationSection("game-color-map").getKeys(false)) {
            colorConverter.addGameColorMap(t, config.getString("game-color-map." + t));
        }
    }

    public void loadTemplates(YamlConfiguration config, String configName) {
        messageTmpl.put(configName, new HashMap<String, String>());
        ircHeroChannelMessages.put(configName, new CaseInsensitiveMap<String>());
        ircHeroActionChannelMessages.put(configName, new CaseInsensitiveMap<String>());
        ircTownyChannelMessages.put(configName, new CaseInsensitiveMap<String>());
        heroChannelMessages.put(configName, new CaseInsensitiveMap<String>());
        heroActionChannelMessages.put(configName, new CaseInsensitiveMap<String>());

        if (config.contains("message-format")) {
            for (String t : config.getConfigurationSection("message-format").getKeys(false)) {
                if (!t.startsWith("MemorySection")) {
                    messageTmpl.get(configName).put(t, ChatColor.translateAlternateColorCodes('&',
                            config.getString("message-format." + t, "")));
                    logDebug("message-format: " + t + " => " + messageTmpl.get(configName).get(t));
                }
            }

            if (config.contains("message-format.irc-hero-channels")) {
                for (String hChannelName : config.getConfigurationSection("message-format.irc-hero-channels").getKeys(false)) {
                    ircHeroChannelMessages.get(configName).put(hChannelName,
                            ChatColor.translateAlternateColorCodes('&',
                                    config.getString("message-format.irc-hero-channels."
                                            + hChannelName)));
                    logDebug("message-format.irc-hero-channels: " + hChannelName
                            + " => " + ircHeroChannelMessages.get(configName).get(hChannelName));
                }
            }

            if (config.contains("message-format.irc-hero-action-channels")) {
                for (String hChannelName : config.getConfigurationSection("message-format.irc-hero-action-channels").getKeys(false)) {
                    ircHeroActionChannelMessages.get(configName).put(hChannelName,
                            ChatColor.translateAlternateColorCodes('&',
                                    config.getString("message-format.irc-hero-action-channels."
                                            + hChannelName)));
                    logDebug("message-format.irc-hero-action-channels: " + hChannelName
                            + " => " + ircHeroActionChannelMessages.get(configName).get(hChannelName));
                }
            }

            if (config.contains("message-format.irc-towny-channels")) {
                for (String tChannelName : config.getConfigurationSection("message-format.irc-towny-channels").getKeys(false)) {
                    ircTownyChannelMessages.get(configName).put(tChannelName,
                            ChatColor.translateAlternateColorCodes('&',
                                    config.getString("message-format.irc-towny-channels."
                                            + tChannelName)));
                    logDebug("message-format.irc-towny-channels: " + tChannelName
                            + " => " + ircTownyChannelMessages.get(configName).get(tChannelName));
                }
            }

            if (config.contains("message-format.hero-channels")) {
                for (String hChannelName : config.getConfigurationSection("message-format.hero-channels").getKeys(false)) {
                    heroChannelMessages.get(configName).put(hChannelName,
                            ChatColor.translateAlternateColorCodes('&',
                                    config.getString("message-format.hero-channels."
                                            + hChannelName)));
                    logDebug("message-format.hero-channels: " + hChannelName
                            + " => " + heroChannelMessages.get(configName).get(hChannelName));
                }
            }
            if (config.contains("message-format.hero-action-channels")) {
                for (String hChannelName : config.getConfigurationSection("message-format.hero-action-channels").getKeys(false)) {
                    heroActionChannelMessages.get(configName).put(hChannelName,
                            ChatColor.translateAlternateColorCodes('&',
                                    config.getString("message-format.hero-action-channels."
                                            + hChannelName)));
                    logDebug("message-format.hero-action-channels: " + hChannelName
                            + " => " + heroActionChannelMessages.get(configName).get(hChannelName));
                }
            }
        } else {
            logDebug("No message-format section found for " + configName);
        }
    }

    private void loadConfig() {
        try {
            getConfig().load(configFile);
        } catch (IOException | InvalidConfigurationException ex) {
            logError(ex.getMessage());
        }
        updateCheckerEnabled = getConfig().getBoolean("update-checker", true);
        updateCheckerMode = getConfig().getString("update-checker-mode", "stable");
        debugEnabled = getConfig().getBoolean("Debug");
        identServerEnabled = getConfig().getBoolean("enable-ident-server");
        logDebug("Debug enabled");
        stripGameColors = getConfig().getBoolean("strip-game-colors", false);
        stripIRCColors = getConfig().getBoolean("strip-irc-colors", false);
        stripIRCBackgroundColors = getConfig().getBoolean("strip-irc-bg-colors", true);
        exactNickMatch = getConfig().getBoolean("nick-exact-match", true);
        ignoreChatCancel = getConfig().getBoolean("ignore-chat-cancel", false);
        colorConverter = new ColorConverter(this, stripGameColors, stripIRCColors, stripIRCBackgroundColors);
        logDebug("strip-game-colors: " + stripGameColors);
        logDebug("strip-irc-colors: " + stripIRCColors);

        loadTemplates((YamlConfiguration) this.getConfig(), MAINCONFIG);
        loadCustomColors((YamlConfiguration) this.getConfig());

        defaultPlayerSuffix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-suffix", ""));
        defaultPlayerPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-prefix", ""));
        defaultPlayerGroup = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-group", ""));
        defaultGroupSuffix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-group-suffix", ""));
        defaultGroupPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-group-prefix", ""));
        defaultPlayerWorld = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-world", ""));

        ircNickPrefixIrcOp = ChatColor.translateAlternateColorCodes('&', getConfig().getString("nick-prefixes.ircop", "~"));
        ircNickPrefixSuperOp = ChatColor.translateAlternateColorCodes('&', getConfig().getString("nick-prefixes.ircsuperop", "&&"));
        ircNickPrefixOp = ChatColor.translateAlternateColorCodes('&', getConfig().getString("nick-prefixes.op", "@"));
        ircNickPrefixHalfOp = ChatColor.translateAlternateColorCodes('&', getConfig().getString("nick-prefixes.halfop", "%"));
        ircNickPrefixVoice = ChatColor.translateAlternateColorCodes('&', getConfig().getString("nick-prefixes.voice", "+"));

        listFormat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("list-format", ""));
        listSeparator = ChatColor.translateAlternateColorCodes('&', getConfig().getString("list-separator", ""));
        listPlayer = ChatColor.translateAlternateColorCodes('&', getConfig().getString("list-player", ""));
        listSortByName = getConfig().getBoolean("list-sort-by-name", true);

        ircConnCheckInterval = getConfig().getLong("conn-check-interval");
        ircChannelCheckInterval = getConfig().getLong("channel-check-interval");

        customTabGamemode = getConfig().getString("custom-tab-gamemode", "SPECTATOR");
        customTabList = getConfig().getBoolean("custom-tab-list", false);
        customTabPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("custom-tab-prefix", "[IRC] "));
        logDebug("custom-tab-list: " + customTabList);
        logDebug("custom-tab-prefix: " + customTabPrefix);
        logDebug("custom-tab-gamemode: " + customTabGamemode);
    }

    private void loadBots() {
        if (botsFolder.exists()) {
            logInfo("Checking for bot files in " + botsFolder);
            for (final File file : botsFolder.listFiles()) {
                if (file.getName().toLowerCase().endsWith(".yml")) {
                    logInfo("Loading bot file: " + file.getName());
                    ircBots.put(file.getName(), new PurpleBot(file, this));
                    logInfo("Loaded bot: " + file.getName() + "[" + ircBots.get(file.getName()).botNick + "]");
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isFactionsEnabled() {
        if (getServer().getPluginManager().getPlugin("Factions") != null) {
            String v = getServer().getPluginManager().getPlugin("Factions").getDescription().getVersion();
            logDebug("Factions version => " + v);
            if (v.startsWith("2.")) {
                return true;
            } else {
                logInfo("Invalid Factions version! Only version 2.0.0 and higher is supported.");
            }
        }
        return false;
    }

    public boolean isPluginEnabled(String pluginName) {
        return getServer().getPluginManager().getPlugin(pluginName) != null;
    }

    private void createSampleBot() {
        File file = new File(pluginFolder + "/" + sampleFileName);
        try {
            try (InputStream in = PurpleIRC.class.getResourceAsStream("/" + sampleFileName)) {
                byte[] buf = new byte[1024];
                int len;
                try (OutputStream out = new FileOutputStream(file)) {
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
            }
        } catch (IOException ex) {
            logError("Problem creating sample bot: " + ex.getMessage());
        }
    }

    /**
     *
     * @param worldName
     * @return
     */
    public String getWorldAlias(String worldName) {
        String alias = worldName;
        Plugin plugin = getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (plugin != null) {
            MVPlugin mvPlugin = (MVPlugin) plugin;
            MultiverseWorld world = mvPlugin.getCore().getMVWorldManager().getMVWorld(worldName);
            if (world != null) {
                alias = world.getAlias();
            }
        }
        if (alias == null) {
            alias = worldName;
        }
        logDebug("getWorldAlias: worldName => " + worldName);
        logDebug("getWorldAlias: alias => " + alias);
        return alias;
    }

    /**
     *
     * @param sender
     */
    public void reloadMainConfig(CommandSender sender) {
        sender.sendMessage(LOG_HEADER_F + " Reloading config.yml ...");
        reloadConfig();
        getConfig().options().copyDefaults(false);
        loadConfig();
        sender.sendMessage(LOG_HEADER_F + " Done.");
    }

    private void createConfigDirs() {
        if (!pluginFolder.exists()) {
            try {
                logInfo("Creating " + pluginFolder.getAbsolutePath());
                pluginFolder.mkdir();
            } catch (Exception e) {
                logError(e.getMessage());
            }
        }

        if (!botsFolder.exists()) {
            try {
                logInfo("Creating " + botsFolder.getAbsolutePath());
                botsFolder.mkdir();
            } catch (Exception e) {
                logError(e.getMessage());
            }
        }
    }

    private void createConfig() {
        if (!configFile.exists()) {
            try {
                logInfo("Creating config.yml");
                configFile.createNewFile();
            } catch (IOException e) {
                logError(e.getMessage());
            }
        }

    }

    /**
     *
     * @param message
     */
    public void logInfo(String message) {
        log.log(Level.INFO, String.format("%s %s", LOG_HEADER, message));
    }

    /**
     *
     * @param message
     */
    public void logError(String message) {
        log.log(Level.SEVERE, String.format("%s %s", LOG_HEADER, message));
    }

    /**
     *
     * @param message
     */
    public void logDebug(String message) {
        if (debugEnabled) {
            log.log(Level.INFO, String.format("%s [DEBUG] %s", LOG_HEADER, message));
        }
    }

    /**
     *
     * @return
     */
    public String getMCUptime() {
        long jvmUptime = ManagementFactory.getRuntimeMXBean().getUptime();
        String msg = "Server uptime: " + (int) (jvmUptime / 86400000L) + " days"
                + " " + (int) (jvmUptime / 3600000L % 24L) + " hours"
                + " " + (int) (jvmUptime / 60000L % 60L) + " minutes"
                + " " + (int) (jvmUptime / 1000L % 60L) + " seconds.";
        return msg;
    }

    public String getServerMotd() {
        return "MOTD: " + getServer().getMotd();
    }

    /**
     *
     * @param ircBot
     * @param channelName
     * @return
     */
    public String getMCPlayers(PurpleBot ircBot, String channelName) {
        Map<String, String> playerList = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Player player : getServer().getOnlinePlayers()) {
            if (ircBot.hideListWhenVanished.get(channelName)) {
                logDebug("List: Checking if player " + player.getName() + " is vanished.");
                if (vanishHook.isVanished(player)) {
                    logDebug("Not adding player to list command" + player.getName() + " due to being vanished.");
                    continue;
                }
            }
            String pName = tokenizer.playerTokenizer(player, listPlayer);
            playerList.put(player.getName(), pName);
        }

        String pList;
        if (!listSortByName) {
            // sort as before
            ArrayList<String> tmp = new ArrayList<>(playerList.values());
            Collections.sort(tmp, Collator.getInstance());
            pList = Joiner.on(listSeparator).join(tmp);
        } else {
            // sort without nick prefixes 
            pList = Joiner.on(listSeparator).join(playerList.values());
        }

        String msg = listFormat
                .replace("%COUNT%", Integer.toString(playerList.size()))
                .replace("%MAX%", Integer.toString(getServer().getMaxPlayers()))
                .replace("%PLAYERS%", pList);
        logDebug("L: " + msg);
        return colorConverter.gameColorsToIrc(msg);
    }

    public String getRemotePlayers(String commandArgs) {
        if (commandArgs != null) {
            String host;
            int port = 25565;
            if (commandArgs.contains(":")) {
                host = commandArgs.split(":")[0];
                port = Integer.parseInt(commandArgs.split(":")[1]);
            } else {
                host = commandArgs;
            }
            Query query = new Query(host, port);
            try {
                query.sendQuery();
            } catch (IOException ex) {
                return ex.getMessage();
            }
            String players[] = query.getOnlineUsernames();
            String m;
            if (players.length == 0) {
                m = "There are no players on " + host
                        + ":" + port;
            } else {
                m = "Players on " + host + "("
                        + players.length
                        + "): " + Joiner.on(", ")
                        .join(players);
            }
            return m;
        } else {
            return "Invalid host.";
        }
    }

    /**
     *
     */
    public void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            vaultHelpers = new VaultHook(this);
            String vv = getServer().getPluginManager().getPlugin("Vault").getDescription().getVersion();
            logInfo("Hooked into Vault v" + vv);
        } else {
            logInfo("Not hooked into Vault!");
        }
    }

    /**
     *
     * @param player
     * @return
     */
    public String getPlayerGroup(Player player) {
        String groupName = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.permission != null) {
                try {
                    groupName = vaultHelpers.permission.getPrimaryGroup(player);
                } catch (Exception ex) {
                    logDebug("Problem with primary group (" + player.getName() + "): " + ex.getMessage());
                }
            }
        }
        if (groupName == null) {
            groupName = "";
        }
        return ChatColor.translateAlternateColorCodes('&', groupName);
    }

    /**
     *
     * @param player
     * @return
     */
    public UUID getPlayerUuid(String player) {
        if (uuidCache.containsKey(player)) {
            return uuidCache.get(player);
        }
        return null;
    }

    /**
     *
     * @param worldName
     * @param player
     * @return
     */
    public String getPlayerGroup(String worldName, String player) {
        String groupName = "";
        UUID uuid = getPlayerUuid(player);
        if (vaultHelpers != null && uuid != null) {
            if (vaultHelpers.permission != null) {
                OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(uuid);
                if (offlinePlayer != null) {
                    try {
                        groupName = vaultHelpers.permission.getPrimaryGroup(worldName, offlinePlayer);
                    } catch (Exception ex) {
                        logDebug("getPlayerGroup (" + player + "): " + ex.getMessage());
                    }
                }
            }
        }
        if (groupName == null) {
            groupName = "";
        }
        return ChatColor.translateAlternateColorCodes('&', groupName);
    }

    /**
     *
     * @param player
     * @return
     */
    public String getPlayerPrefix(Player player) {
        String prefix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                prefix = vaultHelpers.chat.getPlayerPrefix(player);
            }
        }
        if (prefix == null) {
            prefix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    /**
     *
     * @param worldName
     * @param player
     * @return
     */
    public String getPlayerPrefix(String worldName, String player) {
        String prefix = "";
        UUID uuid = getPlayerUuid(player);
        if (vaultHelpers != null && uuid != null) {
            if (vaultHelpers.chat != null) {
                OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(uuid);
                if (offlinePlayer != null) {
                    try {
                        prefix = vaultHelpers.chat.getPlayerPrefix(worldName, offlinePlayer);
                    } catch (Exception ex) {
                        logDebug("getPlayerPrefix (" + player + "): " + ex.getMessage());
                    }
                }
            }
        }
        if (prefix == null) {
            prefix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    /**
     *
     * @param player
     * @return
     */
    public String getPlayerSuffix(Player player) {
        String suffix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                suffix = vaultHelpers.chat.getPlayerSuffix(player);
            }
        }
        if (suffix == null) {
            suffix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', suffix);
    }

    /**
     *
     * @param worldName
     * @param player
     * @return
     */
    public String getPlayerSuffix(String worldName, String player) {
        String suffix = "";
        UUID uuid = getPlayerUuid(player);
        if (vaultHelpers != null && uuid != null) {
            if (vaultHelpers.chat != null) {
                OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(uuid);
                if (offlinePlayer != null) {
                    try {
                        suffix = vaultHelpers.chat.getPlayerSuffix(worldName, offlinePlayer);
                    } catch (Exception ex) {
                        logDebug("getPlayerSuffix (" + player + "): " + ex.getMessage());
                    }
                }
            }
        }
        if (suffix == null) {
            suffix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', suffix);
    }

    /**
     *
     * @param pName
     * @return
     */
    public String getDisplayName(String pName) {
        String displayName = null;
        Player player = this.getServer().getPlayer(pName);
        logDebug("player: " + player);
        if (player != null) {
            displayName = player.getDisplayName();
        }
        if (displayName != null) {
            logDebug("Caching displayName for " + pName + " = " + displayName);
            displayNameCache.put(pName, displayName);
        } else if (displayNameCache.containsKey(pName)) {
            displayName = displayNameCache.get(pName);
        } else {
            displayName = pName;
        }
        return displayName;
    }

    /**
     *
     * @param player
     */
    public void updateDisplayNameCache(Player player) {
        logDebug("Caching displayName for " + player.getName() + " = " + player.getDisplayName());
        displayNameCache.put(player.getName(), player.getDisplayName());
    }

    /**
     *
     * @param player
     * @param displayName
     */
    public void updateDisplayNameCache(String player, String displayName) {
        logDebug("Caching displayName for " + player + " = " + displayName);
        displayNameCache.put(player, displayName);
    }

    /**
     *
     * @param player
     */
    public void updateUuidCache(Player player) {
        logDebug("Caching UUID for " + player.getName() + " = " + player.getUniqueId().toString());
        uuidCache.put(player.getName(), player.getUniqueId());
    }

    /**
     *
     * @param player
     * @param uuid
     */
    public void updateUuidCache(String player, UUID uuid) {
        logDebug("Caching UUID for " + player + " = " + uuid.toString());
        uuidCache.put(player, uuid);
    }

    /**
     *
     * @param player
     * @return
     */
    public String getGroupPrefix(Player player) {
        String prefix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                String group = "";
                try {
                    group = vaultHelpers.permission.getPrimaryGroup(player);
                } catch (Exception ex) {
                    logDebug("getGroupPrefix (" + player.getName() + "): " + ex.getMessage());
                }
                if (group == null) {
                    group = "";
                }
                prefix = vaultHelpers.chat.getGroupPrefix(player.getLocation().getWorld(), group);
            }
        }
        if (prefix == null) {
            prefix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    /**
     *
     * @param worldName
     * @param player
     * @return
     */
    public String getGroupPrefix(String worldName, String player) {
        String prefix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                String group = "";
                OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(player);
                if (offlinePlayer != null) {
                    try {
                        group = vaultHelpers.permission.getPrimaryGroup(worldName, offlinePlayer);
                    } catch (Exception ex) {
                        logDebug("getGroupPrefix (" + player + "): " + ex.getMessage());
                    }
                }
                if (group == null) {
                    group = "";
                }
                prefix = vaultHelpers.chat.getGroupPrefix(worldName, group);
            }
        }
        if (prefix == null) {
            prefix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    /**
     *
     * @param worldName
     * @return
     */
    public String getWorldColor(String worldName) {
        String color = worldName;
        Plugin plugin = getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (plugin != null) {
            MVPlugin mvPlugin = (MVPlugin) plugin;
            MultiverseWorld world = mvPlugin.getCore().getMVWorldManager().getMVWorld(worldName);
            if (world != null) {
                color = world.getColor().toString();
            }
        }
        if (color == null) {
            color = worldName;
        }
        logDebug("getWorldColor: worldName => " + worldName);
        logDebug("getWorldColor: color => " + color);
        return color;
    }

    /**
     *
     * @param player
     * @return
     */
    public String getGroupSuffix(Player player) {
        String suffix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                String group = "";
                try {
                    group = vaultHelpers.permission.getPrimaryGroup(player);
                } catch (Exception ex) {
                    logDebug("Problem with primary group (" + player.getName() + "): " + ex.getMessage());
                }
                if (group == null) {
                    group = "";
                }
                suffix = vaultHelpers.chat.getGroupSuffix(player.getLocation().getWorld(), group);
            }
        }
        if (suffix == null) {
            suffix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', suffix);
    }

    /**
     *
     * @param worldName
     * @param player
     * @return
     */
    public String getGroupSuffix(String worldName, String player) {
        String suffix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                String group = "";
                try {
                    group = vaultHelpers.permission.getPrimaryGroup(worldName, player);
                } catch (Exception ex) {
                    logDebug("Problem with primary group (" + player + "): " + ex.getMessage());
                }
                if (group == null) {
                    group = "";
                }
                suffix = vaultHelpers.chat.getGroupSuffix(worldName, group);
            }
        }
        if (suffix == null) {
            suffix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', suffix);
    }

    /**
     *
     * @return
     */
    public boolean checkForProtocolLib() {
        return (getServer().getPluginManager().getPlugin("ProtocolLib") != null);
    }

    public boolean isPrismEnabled() {
        return (getServer().getPluginManager().getPlugin("Prism") != null);
    }

    public void saveDisplayNameCache() {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(cacheFile));
        } catch (IOException ex) {
            logError(ex.getMessage());
            return;
        }

        try {
            for (String s : displayNameCache.keySet()) {
                logDebug("Saving to displayName.cache: " + s + "\t" + displayNameCache.get(s));
                writer.write(s + "\t" + displayNameCache.get(s) + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            logError(ex.getMessage());
        }
    }

    public void loadDisplayNameCache() {
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(cacheFile))) {
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("\n")) {
                        continue;
                    }
                    String[] parts = line.split("\t", 2);
                    updateDisplayNameCache(parts[0], parts[1]);
                }
            }
        } catch (IOException | NumberFormatException e) {
            logError(e.getMessage());
        }
    }

    public void saveUuidCache() {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(uuidCacheFile));
        } catch (IOException ex) {
            logError(ex.getMessage());
            return;
        }

        try {
            for (String s : uuidCache.keySet()) {
                logDebug("Saving to uuid.cache: " + s + "\t" + uuidCache.get(s).toString());
                writer.write(s + "\t" + uuidCache.get(s).toString() + "\n");
            }
            writer.close();
        } catch (IOException ex) {
            logError(ex.getMessage());
        }
    }

    public void loadUuidCache() {
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(uuidCacheFile))) {
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("\n")) {
                        continue;
                    }
                    String[] parts = line.split("\t", 2);
                    updateUuidCache(parts[0], UUID.fromString(parts[1]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            logError(e.getMessage());
        }
    }
   
    public String getPlayerHost(final String playerIP) {
        if (playerIP == null) {
            return "unknown";
        }
        if (hostCache.containsKey(playerIP)) {
            return hostCache.get(playerIP);
        } else {
            getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    long a = System.currentTimeMillis();
                    InetAddress addr = null;
                    try {
                        addr = InetAddress.getByName(playerIP);
                    } catch (UnknownHostException ex) {
                        logError(ex.getMessage());
                    }
                    String host;
                    if (addr != null) {
                        host = addr.getHostName();
                    } else {
                        host = playerIP;
                    }
                    hostCache.put(playerIP, host);
                    logDebug("getPlayerHost[" + (System.currentTimeMillis() - a) + "ms] " + playerIP + " = " + host);
                }
            }, 0);
            return playerIP;
        }
    }

    public void clearHostCache(final Player player) {
        String playerIP = player.getAddress().getAddress().getHostAddress();
        if (hostCache.containsKey(playerIP)) {
            hostCache.remove(playerIP);
        }
    }

    public String botify(String bot) {
        if (bot.toLowerCase().endsWith("yml")) {
            return bot;
        } else {
            return bot + ".yml";
        }
    }

    public boolean isUpdateCheckerEnabled() {
        return updateCheckerEnabled;
    }

    public String updateCheckerMode() {
        return updateCheckerMode;
    }

}
