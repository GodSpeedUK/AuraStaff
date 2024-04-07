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
import tech.aurasoftware.aurautilities.util.Pair;
import tech.aurasoftware.aurautilities.util.Util;

public class KickCommand extends AuraCommand {
    @Usage("/kick <player> [-s] [reason]")
    @Permission("aurastaff.kick")
    @Parameters(
            value = {Player.class},
            optional = {false}
    )
    public KickCommand() {
        super("kick");
    }

    public boolean run(CommandSender commandSender, String[] strings) {

        // Get String array starting from index 1
        String[] reasonArray = PunishmentUtility.getReasonArray(1, strings);
        ReasonDetail reason = PunishmentUtility.getReasonDetail(reasonArray);

        PunishmentUtility.punish(
                PunishmentType.KICK,
                commandSender,
                Util.getParameter(Player.class, strings[0]),
                reason.getReason(),
                reason.isSilent(),
                reason.getDuration());

        return true;
    }
}
