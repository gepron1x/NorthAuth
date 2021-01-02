package me.gepron1x.southsideauth.utils.config;

import me.gepron1x.southsideauth.SouthSideAuth;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;

public class Config {
    private static final ConfigurationProvider provider;
    private static File dataFolder;
    private static final SouthSideAuth plugin;
    private File file;
    private Configuration configuration;

    public Config(String filename) {
        dataFolder = SouthSideAuth.getInstance().getDataFolder();
        if(!dataFolder.exists()) dataFolder.mkdir();
        file = new File(dataFolder, filename + ".yml");

            try {
                if(!file.exists()) {
                    Files.copy(plugin.getResourceAsStream("config.yml"), file.toPath(), new CopyOption[0]);
                }
                configuration = provider.load(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
    public void saveConfig() {
        try {
            provider.save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void reloadConfig() {
    }
    public Configuration get() {
        return this.configuration;
    }


static {
        plugin = SouthSideAuth.getInstance();
        provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
}
}
