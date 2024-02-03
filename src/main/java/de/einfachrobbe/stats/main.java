package de.einfachrobbe.stats;

import de.einfachrobbe.stats.command.StatsCommand;
import de.einfachrobbe.stats.util.PlanHook;
import de.einfachrobbe.stats.util.QueryAPIAccessor;
import de.einfachrobbe.stats.util.SQLaccessor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.SQLException;
import java.util.Optional;

public final class main extends Plugin {

    public static String prefix = "\u1FA0 §7§o";
    public static Optional<QueryAPIAccessor> acc;
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            acc = new PlanHook().hookIntoPlan();
            SQLaccessor.init();
            ProxyServer.getInstance().getPluginManager().registerCommand(this, new StatsCommand());
            ProxyServer.getInstance().getLogger().info("[StatsCommand] plugin enabled!");

        } catch (IllegalStateException planIsNotInstalled) {
            ProxyServer.getInstance().getLogger().info("[StatsCommand] plan isn't installed!");
            this.onDisable();
        } catch (SQLException e) {
            ProxyServer.getInstance().getLogger().info("[StatsCommand] LuckPerms database connection initialisation failed!");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            SQLaccessor.con.close();
            ProxyServer.getInstance().getLogger().info("[StatsCommand] connection closed!");
        } catch (SQLException e) {
            ProxyServer.getInstance().getLogger().info("[StatsCommand] an error occurred while closing the LuckPerms database connection!");
            e.printStackTrace();
        }
        ProxyServer.getInstance().getLogger().info("[StatsCommand] plugin disabled!");
    }
}
