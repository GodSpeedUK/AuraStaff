package tech.aurasoftware.aurastaff.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.aurasoftware.aurastaff.configuration.Config;
import tech.aurasoftware.aurastaff.configuration.Messages;
import tech.aurasoftware.aurastaff.gui.HistoryGUIMetaData;
import tech.aurasoftware.aurastaff.punishment.PunishmentUtility;
import tech.aurasoftware.aurautilities.command.AuraCommand;
import tech.aurasoftware.aurautilities.command.annotation.Parameters;
import tech.aurasoftware.aurautilities.command.annotation.Permission;
import tech.aurasoftware.aurautilities.command.annotation.RequiresPlayer;
import tech.aurasoftware.aurautilities.command.annotation.Usage;
import tech.aurasoftware.aurautilities.gui.AuraGUI;
import tech.aurasoftware.aurautilities.gui.AuraGUIUtility;
import tech.aurasoftware.aurautilities.gui.OpenedAuraGUI;
import tech.aurasoftware.aurautilities.sql.SQLRow;
import tech.aurasoftware.aurautilities.util.Placeholder;
import tech.aurasoftware.aurautilities.util.Schedulers;
import tech.aurasoftware.aurautilities.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryCommand extends AuraCommand {

    @RequiresPlayer
    @Permission("aurastaff.history")
    @Usage("/history <player>")
    @Parameters(
            optional = {false}, value = {OfflinePlayer.class}
    )
    public HistoryCommand() {
        super("history");
    }

    @Override
    public boolean run(CommandSender commandSender, String[] strings) {

        OfflinePlayer target = Util.getParameter(OfflinePlayer.class, strings[0]);
        Messages.HISTORY_LOADING.send(commandSender, new Placeholder("{player}", target.getName()));

        PunishmentUtility.getPunishments(target.getUniqueId(), false, 0, "BAN", "MUTE", "KICK", "WARN").thenAccept(punishments -> {

            HistoryGUIMetaData historyGUIMetaData = new HistoryGUIMetaData("history");

            List<SQLRow> banList = new ArrayList<>();
            List<SQLRow> muteList = new ArrayList<>();
            List<SQLRow> kickList = new ArrayList<>();
            List<SQLRow> warnList = new ArrayList<>();

            boolean currentlyBanned = false;
            boolean currentlyMuted = false;

            for (SQLRow punishment : punishments.getRows()) {
                switch (punishment.getColumn("type").as(String.class)) {
                    case "BAN":
                        banList.add(punishment);
                        if (punishment.getColumn("active").as(Boolean.class)){
                            currentlyBanned = true;
                        }
                        break;
                    case "MUTE":
                        muteList.add(punishment);
                        if (punishment.getColumn("active").as(Boolean.class)){
                            currentlyMuted = true;
                        }
                        break;
                    case "KICK":
                        kickList.add(punishment);
                        break;
                    case "WARN":
                        warnList.add(punishment);
                        break;
                }
            }

            List<Placeholder> placeholderList = new ArrayList<>();
            placeholderList.add(new Placeholder("{player}", target.getName()));
            placeholderList.add(new Placeholder("{currently_banned}", currentlyBanned ? "Yes" : "No"));
            placeholderList.add(new Placeholder("{currently_muted}", currentlyMuted ? "Yes" : "No"));
            placeholderList.add(new Placeholder("{ban_count}", String.valueOf(banList.size())));
            placeholderList.add(new Placeholder("{mute_count}", String.valueOf(muteList.size())));
            placeholderList.add(new Placeholder("{kick_count}", String.valueOf(kickList.size())));
            placeholderList.add(new Placeholder("{warn_count}", String.valueOf(warnList.size())));

            historyGUIMetaData.setTarget(target);
            historyGUIMetaData.setBanList(banList);
            historyGUIMetaData.setMuteList(muteList);
            historyGUIMetaData.setKickList(kickList);
            historyGUIMetaData.setWarnList(warnList);

            AuraGUI auraGUI = Config.HISTORY_GUI.getAuraGUI();

            Schedulers.sync(() -> {
                AuraGUIUtility.openGUI((Player) commandSender, auraGUI, placeholderList, historyGUIMetaData);
                Messages.HISTORY_LOADED.send(commandSender, new Placeholder("{player}", target.getName()));
            });

        });

        return true;
    }

}
