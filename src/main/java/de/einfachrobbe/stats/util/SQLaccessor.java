package de.einfachrobbe.stats.util;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.checkerframework.checker.units.qual.A;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SQLaccessor {

    public static Connection con;
    static String url = "jdbc:mysql://localhost:3306/lp";
    static String usr = "root";
    static String pwd = "";

    public static void init() throws SQLException {
        con = DriverManager.getConnection(url, usr, pwd);
        ProxyServer.getInstance().getLogger().info("[StatsCommand] LuckPerms database connection established!");
    }

    public static String getPrimaryGroupFromUUID(UUID uuid) {
        try (PreparedStatement ps = con.prepareStatement("SELECT `primary_group` FROM `luckperms_players` WHERE `uuid` = '" + uuid.toString() + "';")) {
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    String str = resultSet.getString("primary_group");
                    return str;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        };
        return null;
    }

    public static String getPrefixFromGroup(String group_name) {
        try (PreparedStatement ps = con.prepareStatement("SELECT `permission` FROM `luckperms_group_permissions` WHERE `name` = '" + group_name + "';")) {
            try (ResultSet resultSet = ps.executeQuery()) {
                List<Integer> weights = new ArrayList<Integer>();
                List<String> prefixes = new ArrayList<String>();

                while (resultSet.next()) {
                    String perm = resultSet.getString("permission");
                    if (perm.startsWith("prefix.")) {
                        String segments[] = perm.split("\\.");
                        weights.add(Integer.parseInt(segments[1]));
                        prefixes.add(segments[2]);
                    }
                }

                int maxweight = Collections.max(weights);
                int maxindex = weights.indexOf(maxweight);

                return prefixes.get(maxindex);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


}
