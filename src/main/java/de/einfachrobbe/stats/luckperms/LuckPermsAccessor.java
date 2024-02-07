package de.einfachrobbe.stats.luckperms;

import de.einfachrobbe.stats.main;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LuckPermsAccessor {

    public static String getPrefixfromPlayer(ProxiedPlayer player) {
        CachedMetaData cmd = main.lp.getPlayerAdapter(ProxiedPlayer.class).getMetaData(player);
        return cmd.getPrefix();
    }

    public static int getPrimaryGroupWeightfromPlayer(ProxiedPlayer player) {
        CachedMetaData cmd = main.lp.getPlayerAdapter(ProxiedPlayer.class).getMetaData(player);
        Group grp = main.lp.getGroupManager().getGroup(cmd.getPrimaryGroup());
        return grp.getWeight().getAsInt();
    }


}
