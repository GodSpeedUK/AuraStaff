package tech.aurasoftware.aurastaff.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tech.aurasoftware.aurastaff.punishment.PunishmentType;
import tech.aurasoftware.aurastaff.punishment.PunishmentUtility;
import tech.aurasoftware.aurastaff.punishment.ReasonDetail;
import tech.aurasoftware.aurautilities.command.AuraCommand;
import tech.aurasoftware.aurautilities.command.annotation.Parameters;
import tech.aurasoftware.aurautilities.command.annotation.Permission;
import tech.aurasoftware.aurautilities.command.annotation.Usage;
import tech.aurasoftware.aurautilities.util.Util;

public class BanCommand extends AuraCommand {
    @Usage("/ban <player> [-s] [-d duration] [reason]")
    @Permission("aurastaff.ban")
    @Parameters(
            value = {OfflinePlayer.class},
            optional = {false}
    )
    public BanCommand() {
        super("ban");
    }

    public boolean run(CommandSender commandSender, String[] strings) {

        // Get String array starting from index 1
        String[] reasonArray = PunishmentUtility.getReasonArray(1, strings);
        ReasonDetail reason = PunishmentUtility.getReasonDetail(reasonArray);

        PunishmentUtility.punish(
                PunishmentType.BAN,
                commandSender,
                Util.getParameter(OfflinePlayer.class, strings[0]),
                reason.getReason(),
                reason.isSilent(),
                reason.getDuration());

        return true;
    }
}
