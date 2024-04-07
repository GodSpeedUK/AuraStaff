package tech.aurasoftware.aurastaff.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tech.aurasoftware.aurautilities.AuraUtilities;
import tech.aurasoftware.aurautilities.message.Message;
import tech.aurasoftware.aurautilities.message.UtilityMessages;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Messages implements Message {

    PREFIX("prefix", UtilityMessages.PREFIX.getString()),
    HISTORY_LOADING("history.loading", "{prefix}Loading history for {player}..."),
    HISTORY_LOADED("history.loaded", "{prefix}History for {player}"),
    KICK_MESSAGE("kick.message", Arrays.asList(
            "&cYou have been kicked from the server.",
            "&cReason: &7{reason}",
            "&cJudge: &7{judge}"
    )),
    KICK_BROADCAST("kick.broadcast", "{prefix}{player} has been kicked by {judge} for {reason}"),
    BAN_MESSAGE("ban.message", Arrays.asList(
            "&cYou have been banned from the server.",
            "&cReason: &7{reason}",
            "&cDuration: &7{duration}",
            "&cJudge: &7{judge}"
    )),
    BAN_BROADCAST("ban.broadcast", "{prefix}{player} has been banned by {judge} for {reason}. Duration: {duration}"),
    BAN_UNBAN("ban.unban", "{prefix}{player} has been unbanned."),
    MUTE_MESSAGE("mute.message", Arrays.asList(
            "&cYou have been muted.",
            "&cReason: &7{reason}",
            "&cDuration: &7{duration}",
            "&cJudge: &7{judge}"
    )),
    MUTE_BROADCAST("mute.broadcast", "{prefix}{player} has been muted by {judge} for {reason}. Duration: {duration}"),
    MUTE_UNMUTE("mute.unmute", "{prefix}{player} has been unmuted."),
    WARN_MESSAGE("warn.message", Arrays.asList(
            "&cYou have been warned.",
            "&cReason: &7{reason}",
            "&cJudge: &7{judge}"
    )),
    WARN_BROADCAST("warn.broadcast", "{prefix}{player} has been warned by {judge} for {reason}"),;

    private final String path;
    @Setter
    private Object value;

    @Override
    public String getPrefix(){
        return PREFIX.getString();
    }

}
