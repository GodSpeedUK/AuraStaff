package tech.aurasoftware.aurastaff;

import lombok.Getter;
import tech.aurasoftware.aurastaff.command.*;
import tech.aurasoftware.aurastaff.configuration.Config;
import tech.aurasoftware.aurastaff.configuration.Messages;
import tech.aurasoftware.aurastaff.listener.GUIClickListener;
import tech.aurasoftware.aurastaff.listener.PlayerChatListener;
import tech.aurasoftware.aurastaff.listener.PlayerPreJoinListener;
import tech.aurasoftware.aurautilities.configuration.Configuration;
import tech.aurasoftware.aurautilities.file.YamlFile;
import tech.aurasoftware.aurautilities.main.AuraPlugin;
import tech.aurasoftware.aurautilities.sql.SQLDatabase;

@Getter
public final class AuraStaff extends AuraPlugin {

    @Getter
    private static AuraStaff instance;

    private SQLDatabase sqlDatabase;
    @Override
    public void onEnable() {
        instance = this;
        Configuration.loadConfig(new YamlFile("config.yml", this.getDataFolder().getAbsolutePath(), null, this), Config.values());
        Configuration.loadConfig(new YamlFile("messages.yml", this.getDataFolder().getAbsolutePath(), null, this), Messages.values());
        registerCommands(new BanCommand(), new HistoryCommand(), new KickCommand(), new MuteCommand(), new WarnCommand(), new UnbanCommand(), new UnmuteCommand());
        registerListener(new PlayerChatListener(), new PlayerPreJoinListener(), new GUIClickListener());
        this.sqlDatabase = (SQLDatabase) Config.DATABASE.getValue();
        this.sqlDatabase.createDataSource();
        // Test Query
        this.sqlDatabase.update("CREATE TABLE IF NOT EXISTS `punishments` " +
                "(`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, `type` VARCHAR(16) NOT NULL, `player` VARCHAR(36) NOT NULL, `judge` VARCHAR(36) NOT NULL, `reason` TEXT NOT NULL, `time` BIGINT NOT NULL, `duration` BIGINT NOT NULL, `active` BOOLEAN NOT NULL)"
        );
    }

    @Override
    public void onDisable() {
        instance = null;
        this.sqlDatabase.closeDataSource();
    }
}
