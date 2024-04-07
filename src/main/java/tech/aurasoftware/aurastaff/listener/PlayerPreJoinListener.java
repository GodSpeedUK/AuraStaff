package tech.aurasoftware.aurastaff.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import tech.aurasoftware.aurastaff.AuraStaff;
import tech.aurasoftware.aurastaff.configuration.Messages;
import tech.aurasoftware.aurastaff.punishment.ActiveMute;
import tech.aurasoftware.aurastaff.punishment.PunishmentUtility;
import tech.aurasoftware.aurautilities.sql.SQLResponse;
import tech.aurasoftware.aurautilities.sql.SQLRow;
import tech.aurasoftware.aurautilities.util.Placeholder;

import java.util.UUID;
import java.util.concurrent.ExecutionException;


public class PlayerPreJoinListener implements Listener {

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent e) throws ExecutionException, InterruptedException {

        SQLResponse sqlResponse = PunishmentUtility.getPunishments(
                e.getUniqueId(),
                true,
                0,
                "BAN"
        ).get();

        if (process(sqlResponse, e)) {
            return;
        }
        SQLResponse muteResponse = PunishmentUtility.getPunishments(
                e.getUniqueId(),
                true,
                0,
                "MUTE"
        ).get();

        this.process(muteResponse, e);

    }

    private boolean process(SQLResponse sqlResponse, AsyncPlayerPreLoginEvent e) {
        if (sqlResponse.isEmpty()) {
            return false;
        }

        for (SQLRow sqlRow : sqlResponse.getRows()) {

            long time = sqlRow.getColumn("time").as(Long.class);
            long duration = sqlRow.getColumn("duration").as(Long.class);
            int id = sqlRow.getColumn("id").as(Integer.class);

            long expires = time + duration;

            if (duration != -1 && expires <= System.currentTimeMillis()) {
                AuraStaff.getInstance().getSqlDatabase().update(
                        "UPDATE `punishments` SET `active` = ? WHERE `id` = ?",
                        false, id
                );
                continue;
            }

            String type = sqlRow.getColumn("type").as(String.class);
            String reason = sqlRow.getColumn("reason").as(String.class);
            String judgeString = sqlRow.getColumn("judge").as(String.class);

            String judge = judgeString.equalsIgnoreCase("Console") ? "Console" : Bukkit.getOfflinePlayer(UUID.fromString(judgeString)).getName();

            if (type.equalsIgnoreCase("BAN")) {

                Placeholder[] placeholders = PunishmentUtility.getPlaceholders(reason, judge, e.getUniqueId().toString(), duration, time);

                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, PunishmentUtility.buildDisconnectString(Messages.BAN_MESSAGE, placeholders));
                return true;
            }

            if (type.equalsIgnoreCase("MUTE")) {
                ActiveMute activeMute = new ActiveMute(
                        id,
                        sqlRow.getColumn("judge").as(String.class),
                        sqlRow.getColumn("reason").as(String.class),
                        duration,
                        time
                );
                PunishmentUtility.cacheMute(e.getUniqueId(), activeMute);
            }


        }
        return false;
    }

}
