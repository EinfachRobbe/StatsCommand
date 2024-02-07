package de.einfachrobbe.stats;

import de.einfachrobbe.stats.command.StatsCommand;
import de.einfachrobbe.stats.plan.PlanHook;
import de.einfachrobbe.stats.plan.PlanAccessor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Optional;

public final class main extends Plugin {

    public static String prefix = "\u1FA0 §7§o";
    public static Optional<PlanAccessor> acc;
    public static LuckPerms lp;
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            acc = new PlanHook().hookIntoPlan();
            lp = LuckPermsProvider.get();
            ProxyServer.getInstance().getPluginManager().registerCommand(this, new StatsCommand());
            ProxyServer.getInstance().getLogger().info("[StatsCommand] plugin enabled!");

        } catch (IllegalStateException planIsNotInstalled) {
            ProxyServer.getInstance().getLogger().info("[StatsCommand] plan or luckperms isn't installed!");
            this.onDisable();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ProxyServer.getInstance().getLogger().info("[StatsCommand] plugin disabled!");
    }
}
