package tech.aurasoftware.aurastaff.gui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import tech.aurasoftware.aurautilities.gui.OpenedAuraGUI;
import tech.aurasoftware.aurautilities.gui.listener.AuraGUIMetaData;
import tech.aurasoftware.aurautilities.sql.SQLRow;

import java.util.List;

@Setter
@Getter
public class HistoryGUIMetaData extends AuraGUIMetaData {

    public HistoryGUIMetaData(String guiKey) {
        super(guiKey);
    }

    private OfflinePlayer target;
    private List<SQLRow> banList;
    private List<SQLRow> muteList;
    private List<SQLRow> kickList;
    private List<SQLRow> warnList;
    private OpenedAuraGUI current;


}
