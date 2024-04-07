package tech.aurasoftware.aurastaff.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import tech.aurasoftware.aurastaff.configuration.Messages;
import tech.aurasoftware.aurastaff.punishment.PunishmentUtility;
import tech.aurasoftware.aurautilities.command.AuraCommand;
import tech.aurasoftware.aurautilities.command.annotation.Parameters;
import tech.aurasoftware.aurautilities.command.annotation.Permission;
import tech.aurasoftware.aurautilities.command.annotation.Usage;
import tech.aurasoftware.aurautilities.util.Placeholder;
import tech.aurasoftware.aurautilities.util.Util;

import java.util.UUID;

public class UnbanCommand extends AuraCommand {

    @Permission("aurastaff.unban")
    @Usage("/unban <player>")
    @Parameters(
            value = {OfflinePlayer.class},
            optional = {false})
    public UnbanCommand() {
        super("unban");
    }

    @Override
    public boolean run(CommandSender commandSender, String[] strings) {

        OfflinePlayer offlinePlayer = Util.getParameter(OfflinePlayer.class, strings[0]);
        UUID uuid = offlinePlayer.getUniqueId();

        PunishmentUtility.unban(uuid);

        Messages.BAN_UNBAN.send(commandSender, new Placeholder("{player}", offlinePlayer.getName()));

        return true;
    }
}
