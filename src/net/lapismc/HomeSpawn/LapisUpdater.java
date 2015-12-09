package net.lapismc.HomeSpawn;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LapisUpdater {

    public static String updateCheck() {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            c.setDoOutput(true);
            c.setRequestMethod("POST");
            c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4" +
                    "&resource=/*insert id of resource here*/").getBytes("UTF-8"));
            String oldVersion = HomeSpawn.getPlugin(HomeSpawn.class).getDescription().getVersion();
            String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream()))
                    .readLine().replaceAll("[a-zA-Z ]", "");
            if (!newVersion.equals(oldVersion)) {
                return newVersion;
            } else {
                return "No Update";
            }
        } catch (Exception e) {
            String message = e.getMessage();
            return "Update Failed: " + message;
        }
    }

}
