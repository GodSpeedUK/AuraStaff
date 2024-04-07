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

public class MuteCommand extends AuraCommand {
    @Usage("/mute <player> [-s] [-d duration] [reason]")
    @Permission("aurastaff.mute")
    @Parameters(
            value = {OfflinePlayer.class},
            optional = {false}
    )
    public MuteCommand() {
        super("mute");
    }

    public boolean run(CommandSender commandSender, String[] strings) {

        // Get String array starting from index 1
        String[] reasonArray = PunishmentUtility.getReasonArray(1, strings);
        ReasonDetail reason = PunishmentUtility.getReasonDetail(reasonArray);

        String target = Util.getParameter(OfflinePlayer.class, strings[0]).getName();

        System.out.println("Target: " + target);

        PunishmentUtility.punish(
                PunishmentType.MUTE,
                commandSender,
                Util.getParameter(OfflinePlayer.class, strings[0]),
                reason.getReason(),
                reason.isSilent(),
                reason.getDuration());

        return true;
    }
}
