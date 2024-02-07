package de.einfachrobbe.stats.plan;

import com.djrapitops.plan.query.QueryService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlanAccessor {

    private final QueryService queryService;

    public PlanAccessor(QueryService queryService) {
        this.queryService = queryService;
        createTable();
        queryService.subscribeDataClearEvent(this::recreateTable);
        queryService.subscribeToPlayerRemoveEvent(this::removePlayer);
    }

    private void createTable() {
        String dbType = queryService.getDBType();
        boolean sqlite = dbType.equalsIgnoreCase("SQLITE");

        String sql = "CREATE TABLE IF NOT EXISTS plan_version_protocol (" +
                "id int " + (sqlite ? "PRIMARY KEY" : "NOT NULL AUTO_INCREMENT") + ',' +
                "uuid varchar(36) NOT NULL UNIQUE," +
                "protocol_version int NOT NULL" +
                (sqlite ? "" : ",PRIMARY KEY (id)") +
                ')';

        queryService.execute(sql, PreparedStatement::execute);
    }

    private void dropTable() {
        queryService.execute("DROP TABLE IF EXISTS plan_version_protocol", PreparedStatement::execute);
    }

    private void recreateTable() {
        dropTable();
        createTable();
    }

    private void removePlayer(UUID playerUUID) {
        queryService.execute(
                "DELETE FROM plan_version_protocol WHERE uuid=?",
                statement -> {
                    statement.setString(1, playerUUID.toString());
                    statement.execute();
                }
        );
    }

    //PS. Ich hab überall longs genommen, weil BTE ja vielleicht jetzt oder irgendwann mehr registrierte Spieler hat als eine int aufnehmen kann

    private long getUserIdfromUUID(UUID uuid) {
        String sql = "SELECT id FROM plan_users WHERE uuid=?";

        return (long) queryService.query(sql, preparedStatement -> {
            preparedStatement.setString(1, uuid.toString());
            try (ResultSet set = preparedStatement.executeQuery()) {
                return set.next() ? set.getInt("id") : -1L;
            }
        });
    }

    private long getSessionCountfromServer(UUID uuid, int server_id) {
        long user_id = getUserIdfromUUID(uuid);

        String sql = "SELECT session_start FROM plan_sessions WHERE user_id=? AND server_id=?";

        return (long) queryService.query(sql, preparedStatement -> {
            try {
                preparedStatement.setString(1, String.valueOf(user_id));
                preparedStatement.setString(2, String.valueOf(server_id));
                ResultSet set = preparedStatement.executeQuery();
                List<Long> ss = new ArrayList<Long>();

                while (set.next()) {
                    ss.add(set.getLong("session_start"));
                }

                return ss.size();

            } catch (SQLException ex) {
                return null;
            }
        });
    }

    public long getTotalSessionCount(UUID uuid) {
        long user_id = getUserIdfromUUID(uuid);

        String sql = "SELECT session_start FROM plan_sessions WHERE user_id=?";

        return (long) queryService.query(sql, preparedStatement -> {
            try {
                preparedStatement.setString(1, String.valueOf(user_id));
                ResultSet set = preparedStatement.executeQuery();
                List<Long> ss = new ArrayList<Long>();

                while (set.next()) {
                    ss.add(set.getLong("session_start"));
                }

                return ss.size();

            } catch (SQLException ex) {
                return null;
            }
        });
    }

    public String getAverageSessionLengthin(UUID uuid) {
        long user_id = getUserIdfromUUID(uuid);

        String sql = "SELECT * FROM plan_sessions WHERE user_id=?";

        return (String) queryService.query(sql, preparedStatement -> {
            try {
                preparedStatement.setString(1, String.valueOf(user_id));
                ResultSet set = preparedStatement.executeQuery();
                List<Long> ss = new ArrayList<Long>();
                List<Long> se = new ArrayList<Long>();
                List<Long> durations = new ArrayList<Long>();
                List<Long> afk_time = new ArrayList<Long>();

                //Alle start und end zeichen abrufen

                while (set.next()) {
                    ss.add(set.getLong("session_start"));
                    se.add(set.getLong("session_end"));
                    afk_time.add(set.getLong("afk_time"));
                }

                //Für alle die zeitspanne ausrechnen

                for (long start : ss) {
                    int index = ss.indexOf(start);
                    long end = se.get(index);

                    long diff = end - start + afk_time.get(index);

                    durations.add(diff);
                }

                //Den Durschnitt rechnen

                long sum = 0;

                for (int i = 0; i < durations.size(); i++) {
                    sum = sum + durations.get(i);
                }

                double average = sum / (durations.size() + 1);

                double timemin = average / 60000;
                double timehours = average / 3600000;

                if (String.valueOf(timehours).startsWith("0.")) {
                    return round(timemin, 2) + "m";
                } else return round(timehours, 2) + "h";

            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        });

    }

    public String getGeolocationfromUUID(UUID uuid) {
        long user_id = getUserIdfromUUID(uuid);

        String sql = "SELECT geolocation FROM plan_geolocations WHERE user_id=?";

        return (String) queryService.query(sql, preparedStatement -> {
            preparedStatement.setString(1, String.valueOf(user_id));
            try (ResultSet set = preparedStatement.executeQuery()) {
                return set.next() ? set.getString("geolocation") : null;
            }
        });
    }

    public String getTotalPlaytime(UUID uuid) {
        long user_id = getUserIdfromUUID(uuid);

        String sql = "SELECT * FROM plan_sessions WHERE user_id=?";

        return (String) queryService.query(sql, preparedStatement -> {
            try {
                preparedStatement.setString(1, String.valueOf(user_id));
                ResultSet set = preparedStatement.executeQuery();
                List<Long> ss = new ArrayList<Long>();
                List<Long> se = new ArrayList<Long>();
                List<Long> durations = new ArrayList<Long>();
                List<Long> afk_time = new ArrayList<Long>();

                //Alle start und end zeichen abrufen

                while (set.next()) {
                    ss.add(set.getLong("session_start"));
                    se.add(set.getLong("session_end"));
                    afk_time.add(set.getLong("afk_time"));
                }

                //Für alle die zeitspanne ausrechnen

                for (long start : ss) {
                    int index = ss.indexOf(start);
                    long end = se.get(index);

                    long diff = end - start + afk_time.get(index);

                    durations.add(diff);
                }

                //Den Durschnitt rechnen

                long sum = 0;

                for (int i = 0; i < durations.size(); i++) {
                    sum = sum + durations.get(i);
                }

                double timemin = sum / 60000;
                double timehours = sum / 3600000;

                if (String.valueOf(timehours).startsWith("0.")) {
                    return round(timemin, 2) + "m";
                } else return round(timehours,2) + "h";

            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        });

    }

    public double getAveragePing(UUID uuid) {
        long user_id = getUserIdfromUUID(uuid);

        String sql = "SELECT avg_ping FROM plan_ping WHERE user_id=?";

        return (double) queryService.query(sql, preparedStatement -> {
            try {
                preparedStatement.setString(1, String.valueOf(user_id));
                ResultSet set = preparedStatement.executeQuery();
                List<Integer> avg_ping = new ArrayList<Integer>();

                //Alle start und end zeichen abrufen

                while (set.next()) {
                    avg_ping.add(set.getInt("avg_ping"));
                }

                //Den Durschnitt rechnen

                long sum = 0;

                for (int i = 0; i < avg_ping.size(); i++) {
                    sum = sum + avg_ping.get(i);
                }

                double average = sum / avg_ping.size();

                return round(average, 2);

            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        });
    }
    public String getMostUsedJoinAddressFromUUID(UUID uuid) {
        int addressId = getMostUsedJoinAddressIdfromUUID(uuid);

        String sql = "SELECT join_address FROM plan_join_address WHERE id=?";

        return (String) queryService.query(sql, preparedStatement -> {
            preparedStatement.setString(1, String.valueOf(addressId));
            try (ResultSet set = preparedStatement.executeQuery()) {
                return set.next() ? set.getString("join_address") : "n/a";
            }
        });
    }

    private int getMostUsedJoinAddressIdfromUUID(UUID uuid) {
        long user_id = getUserIdfromUUID(uuid);

        String sql = "SELECT join_address_id FROM plan_sessions WHERE user_id=?";

        return (int) queryService.query(sql, preparedStatement -> {
            try {
                preparedStatement.setString(1, String.valueOf(user_id));
                ResultSet set = preparedStatement.executeQuery();
                List<Integer> joinAddresses = new ArrayList<Integer>();

                //Alle start und end zeichen abrufen

                while (set.next()) {
                   joinAddresses.add(set.getInt("join_address_id"));
                }

                Map<String, Integer> hm = new HashMap<String, Integer>();

                for (int i : joinAddresses) {
                    Integer j = hm.get(i);
                    hm.put(String.valueOf(i), (j == null) ? 1 : j + 1);
                }

                int max = Collections.max(hm.values());

                return Integer.parseInt(getKeyByValue(hm, max).toString());

            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            } catch (NoSuchElementException ex) {
                return -1;
            }
        });
    }

    private double round(double value, int decimalPoints) {
        double d = Math.pow(10, decimalPoints);
        return Math.round(value * d) / d;
    }

    private <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }










}
