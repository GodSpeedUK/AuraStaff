package tech.aurasoftware.aurastaff.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import tech.aurasoftware.aurautilities.configuration.Configuration;
import tech.aurasoftware.aurautilities.gui.AuraGUI;
import tech.aurasoftware.aurautilities.gui.AuraGUIItem;
import tech.aurasoftware.aurautilities.item.AuraItem;
import tech.aurasoftware.aurautilities.sql.SQLDatabase;

import java.util.Arrays;
import java.util.Collections;

@Getter
@AllArgsConstructor
public enum Config implements Configuration {

    DATABASE("database",
            new SQLDatabase()
                    .setHost("localhost")
                    .setPort(3306)
                    .setUsername("root")
                    .setPassword("password")
                    .setDatabase("database")),
    HISTORY_GUI("history.menu-gui", new AuraGUI()
            .name("&7History, {player}")
            .size(36)
            .items(Arrays.asList(
                    new AuraGUIItem()
                            .key("filler")
                            .auraItem(
                                    new AuraItem()
                                            .material("WHITE_STAINED_GLASS_PANE")
                                            .name(" ")
                                            .enchantments(Collections.singletonList("ARROW_INFINITE:1"))
                                            .hideAttributes(true)
                                            .hideEnchants(true)
                            )
                            .slot(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35),
                    new AuraGUIItem()
                            .key("target")
                            .slot(13)
                            .auraItem(
                                    new AuraItem()
                                            .material("PLAYER_HEAD")
                                            .name("&b{player}")
                                            .skullOwner("{player}")
                                            .lore(Arrays.asList(
                                                    " ",
                                                    "&cCurrently Banned: &a{currently_banned}",
                                                    "&cCurrently Muted: &a{currently_muted}"
                                            ))
                                            .hideAttributes(true)
                                            .hideEnchants(true)
                            ),
                    new AuraGUIItem()
                            .key("ban")
                            .slot(19)
                            .auraItem(
                                    new AuraItem()
                                            .material("RED_WOOL")
                                            .name("&cBan")
                                            .lore(Arrays.asList(
                                                    " ",
                                                    "&cHistorical Bans: {ban_count}"
                                            ))
                                            .hideAttributes(true)
                                            .hideEnchants(true)
                            ),
                    new AuraGUIItem()
                            .key("mute")
                            .slot(21)
                            .auraItem(
                                    new AuraItem()
                                            .material("ORANGE_WOOL")
                                            .name("&6Mute")
                                            .lore(Arrays.asList(
                                                    " ",
                                                    "&cHistorical Mutes: {mute_count}"
                                            ))
                                            .hideAttributes(true)
                                            .hideEnchants(true)
                            ),
                    new AuraGUIItem()
                            .key("kick")
                            .slot(23)
                            .auraItem(
                                    new AuraItem()
                                            .material("YELLOW_WOOL")
                                            .name("&eKick")
                                            .lore(Arrays.asList(
                                                    " ",
                                                    "&cHistorical Kicks: {kick_count}"
                                            ))
                                            .hideAttributes(true)
                                            .hideEnchants(true)
                            ),
                    new AuraGUIItem()
                            .key("warn")
                            .slot(25)
                            .auraItem(
                                    new AuraItem()
                                            .material("GREEN_WOOL")
                                            .name("&aWarn")
                                            .lore(Arrays.asList(
                                                    " ",
                                                    "&cHistorical Warns: {warn_count}"
                                            ))
                                            .hideAttributes(true)
                                            .hideEnchants(true)
                            )

            )));

    private final String path;
    @Setter
    private Object value;

}
