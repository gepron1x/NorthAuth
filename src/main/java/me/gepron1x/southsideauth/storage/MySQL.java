package me.gepron1x.southsideauth.storage;

import me.gepron1x.southsideauth.AuthProfile;
import me.gepron1x.southsideauth.SouthSideAuth;
import me.gepron1x.southsideauth.status.VerificationStatus;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.net.SocketAddress;
import java.sql.*;
import java.util.UUID;

public class MySQL implements Storage {
    private Connection connection;
    private SouthSideAuth plugin;
    private TaskScheduler tasks;

    public MySQL(SouthSideAuth plugin, String url, String user, String database, String password) {
        this.plugin = plugin;
        this.tasks = plugin.getProxy().getScheduler();
        connect(url, user, database, password);
    }
    private void connect(String url, String user, String database, String password) {
       tasks.runAsync(plugin, () -> {
           try {
               synchronized (this) {
                   if (connection != null && !connection.isClosed()) {
                       return;
                   }
                   Class.forName("com.mysql.jdbc.Driver");
                   setConnection(DriverManager.getConnection("jdbc:mysql://" + url + "/" + database, user, password));

                   plugin.send(ChatColor.GREEN + "MYSQL CONNECTED");

                   Statement statement = connection.createStatement();
                   statement.executeUpdate("CREATE TABLE IF NOT EXISTS authdata (`uuid` VARCHAR(36), `password` VARCHAR(255), PRIMARY KEY(`uuid`))");
                   statement.executeUpdate("CREATE TABLE IF NOT EXISTS 2fadata (`uuid` VARCHAR(36), `vkid` INTEGER, PRIMARY KEY (`uuid`))");
                   statement.executeUpdate("CREATE TABLE IF NOT EXISTS sessions (`uuid` VARCHAR(36), `adress` VARCHAR(60))");
                   statement.executeUpdate("CREATE TABLE IF NOT EXISTS logoutTasks (`adress` VARCHAR(60), `how_many_left` INTEGER)");
                   loadProfiles();
               }
           } catch (SQLException e) {
               e.printStackTrace();

           } catch (ClassNotFoundException e) {
               e.printStackTrace();
           }
       });
    }
    @Override
    public void loadProfiles() {
        tasks.runAsync(plugin, () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM authdata");
                ResultSet rs = statement.executeQuery();
                while(rs.next()) {
                    AuthProfile.insert(UUID.fromString(rs.getString("uuid")),
                            new AuthProfile(rs.getString("password"), false));
                }
                statement = connection.prepareStatement("SELECT * FROM 2fadata");
                rs = statement.executeQuery();
                while(rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    VerificationStatus.insert(uuid, rs.getInt("vkid"));
                    AuthProfile.byUUID(uuid).set2FA(true);
                }
                statement = connection.prepareStatement("SELECT * FROM sessions");
                rs = statement.executeQuery();
                while(rs.next()) {
                    AuthProfile.byUUID(UUID.fromString(rs.getString("uuid"))).addAdress(rs.getString("adress"));
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public void addSession(final UUID uuid, final SocketAddress adress) {
        tasks.runAsync(plugin, () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO sessions (`uuid`,`adress`) VALUES (?,?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, adress.toString().split(":")[0]);
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public void removeSession(UUID uuid) {
        tasks.runAsync(plugin, () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM sessions WHERE `uuid`=?");
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public void createProfile(UUID uuid, AuthProfile pl) {
        tasks.runAsync(plugin, () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO authdata (`uuid`,`password`) VALUES (?,?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, pl.getHashedPassword());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public void removeProfile(UUID uuid) {
        tasks.runAsync(plugin, () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM authdata WHERE `uuid`=?");
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });

    }

    @Override
    public void updatePassword(UUID uuid, String password) {
        tasks.runAsync(plugin, () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE authdata SET `password`=? WHERE `uuid`=?");
                statement.setString(2, uuid.toString());
                statement.setString(1, password);
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }



    @Override
    public void createDualFactor(UUID uuid, int vkID) {
        tasks.runAsync(plugin, () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO 2fadata (`uuid`,`vkid`) VALUES (?,?)");
                statement.setString(1, uuid.toString());
                statement.setInt(2, vkID);
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public void removeDualFactor(UUID uuid) {
        tasks.runAsync(plugin, () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM 2fadata WHERE `uuid`=?");
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }


    public void setConnection(Connection c) {
        this.connection = c;
    }
}
