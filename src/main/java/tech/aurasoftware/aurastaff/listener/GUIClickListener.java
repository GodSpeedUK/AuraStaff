package tech.aurasoftware.aurastaff.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tech.aurasoftware.aurautilities.gui.event.AuraGUIClickEvent;

public class GUIClickListener implements Listener {

    @EventHandler
    public void onClick(AuraGUIClickEvent e){
        e.setCancelled(true);
    }

}
