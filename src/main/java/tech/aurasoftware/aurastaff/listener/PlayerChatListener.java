package tech.aurasoftware.aurastaff.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import tech.aurasoftware.aurastaff.configuration.Messages;
import tech.aurasoftware.aurastaff.punishment.ActiveMute;
import tech.aurasoftware.aurastaff.punishment.PunishmentUtility;
import tech.aurasoftware.aurautilities.util.Placeholder;

import java.util.UUID;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        ActiveMute activeMute = PunishmentUtility.getMute(e.getPlayer().getUniqueId());
        if (activeMute != null) {
            e.setCancelled(true);

            String judgeString = activeMute.getJudge();
            String judge = judgeString.equalsIgnoreCase("Console") ? "Console" : Bukkit.getOfflinePlayer(UUID.fromString(judgeString)).getName();

            Placeholder[] placeholders = PunishmentUtility.getPlaceholders(activeMute.getReason(), judge, e.getPlayer().getName(), activeMute.getDuration(), activeMute.getTime());
            Messages.MUTE_MESSAGE.send(e.getPlayer(), placeholders);
        }

    }

}
