package tech.aurasoftware.aurastaff.punishment;


import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.aurasoftware.aurastaff.AuraStaff;
import tech.aurasoftware.aurastaff.configuration.Messages;
import tech.aurasoftware.aurautilities.message.Message;
import tech.aurasoftware.aurautilities.sql.SQLResponse;
import tech.aurasoftware.aurautilities.sql.SQLRow;
import tech.aurasoftware.aurautilities.util.Pair;
import tech.aurasoftware.aurautilities.util.Placeholder;
import tech.aurasoftware.aurautilities.util.Text;

import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class PunishmentUtility {


    private static final Map<UUID, ActiveMute> ACTIVE_MUTE_MAP = new HashMap<>();

    public void cacheMute(UUID player, ActiveMute activeMute) {
        ACTIVE_MUTE_MAP.put(player, activeMute);
    }

    public void punish(PunishmentType punishmentType, CommandSender judge, OfflinePlayer player, String reason, boolean silent, long durationMillis) {
        long time = System.currentTimeMillis();

        Placeholder[] placeholders = getPlaceholders(reason, judge.getName(), player.getName(), durationMillis, time);

        Message broadcastMessage = null;

        switch (punishmentType) {
            case KICK:
                if (player.isOnline()) {
                    player.getPlayer().kickPlayer(buildDisconnectString(Messages.KICK_MESSAGE, placeholders));
                } else {
                    return;
                }
                broadcastMessage = Messages.KICK_BROADCAST;
                break;
            case BAN:
                if (player.isOnline()) {
                    player.getPlayer().kickPlayer(buildDisconnectString(Messages.BAN_MESSAGE, placeholders));
                }
                broadcastMessage = Messages.BAN_BROADCAST;
                break;
            case MUTE:
                broadcastMessage = Messages.MUTE_BROADCAST;
                if (player.isOnline()) {
                    player.getPlayer().sendMessage(buildDisconnectString(Messages.MUTE_MESSAGE, placeholders));
                }
                break;
            case WARN:
                broadcastMessage = Messages.WARN_BROADCAST;
                if (player.isOnline()) {
                    player.getPlayer().sendMessage(buildDisconnectString(Messages.WARN_MESSAGE, placeholders));
                }
                break;
        }

        // Log punishment
        log(punishmentType, judge, player, reason, durationMillis, time);

        if (broadcastMessage != null) {
            if (!silent) {
                broadcastMessage.send(judge, placeholders);
                return;
            }
            broadcastMessage.broadcast(placeholders);
        }

    }

    public void unban(UUID player) {
        AuraStaff.getInstance().getSqlDatabase().update(
                "UPDATE `punishments` SET `active` = ? WHERE `player` = ? AND `type` = ?",
                false, player.toString(), "BAN"
        );
    }

    public void unmute(UUID player) {
        AuraStaff.getInstance().getSqlDatabase().update(
                "UPDATE `punishments` SET `active` = ? WHERE `player` = ? AND `type` = ?",
                false, player.toString(), "MUTE"
        );
        if (ACTIVE_MUTE_MAP.containsKey(player)) {
            ACTIVE_MUTE_MAP.remove(player);
        }
    }

    public ActiveMute getMute(UUID player) {

        if (!ACTIVE_MUTE_MAP.containsKey(player)) {
            return null;
        }

        // Get the mute object
        ActiveMute activeMute = ACTIVE_MUTE_MAP.get(player);
        long currentTime = System.currentTimeMillis();
        long duration = activeMute.getDuration();
        long expires = activeMute.getTime() + duration;

        if (duration != -1 && expires <= currentTime) {
            ACTIVE_MUTE_MAP.remove(player);
            AuraStaff.getInstance().getSqlDatabase().update(
                    "UPDATE `punishments` SET `active` = ? WHERE `id` = ?",
                    false, activeMute.getId()
            );
            return null;
        }

        return activeMute;
    }

    //TODO: Log punishment
    private void log(PunishmentType punishmentType, CommandSender judge, OfflinePlayer player, String reason, long durationMillis, long time) {

        String punishmentTypeString = punishmentType.name();
        String judgeString;
        if (judge instanceof Player) {
            judgeString = ((Player) judge).getUniqueId().toString();
        } else {
            judgeString = "Console";
        }
        String playerString = player.getUniqueId().toString();

        AuraStaff.getInstance().getSqlDatabase().update("INSERT INTO punishments (type, player, judge, reason, duration, time, active) VALUES (?, ?, ?, ?, ?, ?, ?)",
                punishmentTypeString, playerString, judgeString, reason, durationMillis, time, true).thenAccept(result -> {

            if (!punishmentType.equals(PunishmentType.MUTE)) {
                return;
            }
            // Get last inserted ID
            AuraStaff.getInstance().getSqlDatabase().query("SELECT `id` FROM punishments WHERE player = ? AND time = ?;", playerString, time).thenAccept(resultSet -> {

                if (resultSet.isEmpty()) {
                    return;
                }

                SQLRow sqlRow = resultSet.getRows().stream().findFirst().get();
                int id = sqlRow.getColumn("id").as(Integer.class);

                cacheMute(player.getUniqueId(), new ActiveMute(id, judgeString, reason, durationMillis, time));
            });
        });

    }

    public CompletableFuture<SQLResponse> getPunishments(UUID player, boolean requiresActive, long startTime, String... types) {

        List<Object> objects = new ArrayList<>();

        objects.add(player.toString());

        String typeIn = "`type` IN(";

        for (int i = 0; i < types.length; i++) {
            typeIn += "?";
            if (i != types.length - 1) {
                typeIn += ", ";
            }
        }

        typeIn += ")";
        objects.addAll(Arrays.asList(types));

        String active = "";
        if (requiresActive) {
            active = " AND `active` = ?";
            objects.add(true);
        }

        objects.add(startTime);

        String queries = "SELECT * FROM `punishments` WHERE `player` = ? AND " + typeIn + active + " AND `time` >= ?";

        return AuraStaff.getInstance().getSqlDatabase().query(
                queries,
                objects.toArray(new Object[0])
        );


    }


    public Placeholder[] getPlaceholders(String reason, String judge, String player, long durationMillis, long time) {

        List<Placeholder> placeholderList = new ArrayList<>();
        placeholderList.add(new Placeholder("{reason}", reason));
        placeholderList.add(new Placeholder("{judge}", judge));
        placeholderList.add(new Placeholder("{player}", player));


        if (durationMillis == -1) {

            placeholderList.add(new Placeholder("{duration}", "Permanent"));
        } else {
            long expires = time + durationMillis;
            long currentTime = System.currentTimeMillis();
            long remaining = expires - currentTime + 100;
            placeholderList.add(new Placeholder("{duration}", Text.convertMillis(remaining)));
        }

        return placeholderList.toArray(new Placeholder[0]);
    }

    public String buildDisconnectString(Message message, Placeholder... placeholders) {
        StringBuilder messageBuilder = new StringBuilder();


        for (String messageString : message.getStringList()) {
            messageBuilder.append(Text.c(Placeholder.apply(messageString, placeholders)));
            messageBuilder.append("\n");
        }

        return messageBuilder.toString();
    }

    public ReasonDetail getReasonDetail(String[] args) {
        String reason;
        boolean silent = false;
        long duration = -1;

        StringBuilder reasonBuilder = new StringBuilder();
        // convert args to string with spaces

        int durationIndex = -1;

        int i = 0;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-d")) {
                durationIndex = i;
                i++;
                continue;
            }

            if(durationIndex != -1 && i == durationIndex + 1){
                i++;
                continue;
            }

            if (arg.equalsIgnoreCase("-s")) {
                i++;
                silent = true;
                continue;
            }
            i++;
            reasonBuilder.append(arg);
            if(i != args.length-1){
                reasonBuilder.append(" ");
            }
        }
        reason = reasonBuilder.toString();

        if (durationIndex != -1) {
            if (args.length <= durationIndex + 1) {
                return new ReasonDetail(reason, duration, silent);
            }

            String timeString = args[durationIndex + 1];
            // Last character is the time unit
            String timeUnit = timeString.substring(timeString.length() - 1);
            // Time value
            String timeValue = timeString.substring(0, timeString.length() - 1);

            try {
                Long.parseLong(timeValue);
            } catch (NumberFormatException e) {
                return new ReasonDetail(reason, duration, silent);
            }

            switch (timeUnit) {
                case "s":
                    duration = Long.parseLong(timeValue) * 1000;
                    break;
                case "m":
                    duration = Long.parseLong(timeValue) * 1000 * 60;
                    break;
                case "h":
                    duration = Long.parseLong(timeValue) * 1000 * 60 * 60;
                    break;
                case "d":
                    duration = Long.parseLong(timeValue) * 1000 * 60 * 60 * 24;
                    break;
                case "w":
                    duration = Long.parseLong(timeValue) * 1000 * 60 * 60 * 24 * 7;
                    break;
                default:
                    break;
            }
        }
        if(reason.isEmpty()){
            reason = "No reason specified";
        }
        return new ReasonDetail(reason, duration, silent);
    }

    public String[] getReasonArray(int startIndex, String[] args) {
        String[] reasonArray = new String[0];
        if (args.length - 1 >= startIndex) {
            reasonArray = Arrays.copyOfRange(args, startIndex, args.length);
        }
        return reasonArray;
    }

}
