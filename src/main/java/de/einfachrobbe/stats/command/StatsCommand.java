package de.einfachrobbe.stats.command;

import de.einfachrobbe.stats.util.ColorUtil;
import de.einfachrobbe.stats.luckperms.LuckPermsAccessor;
import de.einfachrobbe.stats.main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;
import java.util.List;

public class StatsCommand extends Command {

    public StatsCommand() {
        super("stats", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage("[StatsCommand] Dieser Command kann nur als Spieler ausgeführt werden!");
        } else {
            ProxiedPlayer p = (ProxiedPlayer) sender;

            p.sendMessage(main.prefix + "Lade stats für §r§e" + p.getName() + "§7§o...");

            List<String> head = null;

            try {
                head = ColorUtil.getPixelHeadFromUUID(p.getUniqueId());
            } catch (IOException e) {
                e.printStackTrace();
            }

            p.sendMessage(main.prefix + "\n"
                    + head.get(0) + " §6Name: §r§7§o" + p.getName() + "\n"
                    + head.get(1) + " §6Rank: §r" + LuckPermsAccessor.getPrefixfromPlayer(p) + " @ " + LuckPermsAccessor.getPrimaryGroupWeightfromPlayer(p) + "§r\n"
                    + head.get(2) + " §6\u2300Ping: §r§7§o" + main.acc.get().getAveragePing(p.getUniqueId()) + "ms\n"
                    + head.get(3) + " §6Geolocation: §r§7§o" + main.acc.get().getGeolocationfromUUID(p.getUniqueId()) + "\n"
                    + head.get(4) + " §6Playtime: §r§7§o" + main.acc.get().getTotalPlaytime(p.getUniqueId()) + "\n"
                    + head.get(5) + " §6Sessions: §r§7§o" + main.acc.get().getTotalSessionCount(p.getUniqueId()) + "\n"
                    + head.get(6) + " §6\u2300Session length: §r§7§o" + main.acc.get().getAverageSessionLengthin(p.getUniqueId()) + "\n"
                    + head.get(7) + " §6Most used ip: §r§7§o" + main.acc.get().getMostUsedJoinAddressFromUUID(p.getUniqueId())
            );

        }

    }
}
