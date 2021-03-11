
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;
import java.util.Map;

public class Main {
          
    public static void main(String[] args) {
        String token = System.getenv().get("token");
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        while (true) {
            updateDownloadCount(api);
            updateMemberCount(api);
            updateServersCount(api);
            try {
                Thread.sleep(1000 * 61 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    public static int getDownloads(int[] spigotIDs, int[] forgeIDs) {
        int downloads = 0;
        
        for (int ID : spigotIDs) {
           downloads += getDownload("https://api.spiget.org/v2/resources/" + ID, false);
        }
        for (int ID : forgeIDs) {
          downloads += getDownload("https://api.cfwidget.com/" + ID, true);
        }
        return downloads;
    }

    public static int getServerCount() {
        int onlineservers = 0;
        int[] IDs = {10205,10430,10431,10432,10433,10447,10448,10462,10531,10549};
        for (int ID : IDs) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://bstats.org/api/v1/plugins/" + ID + "/charts/servers/data/?maxElements=1")
                    .build();
            Response response;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
            String stringResponse;
            try {
                stringResponse = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
            onlineservers += Integer.parseInt(stringResponse.substring(16).replace("]]", ""));
        }
        return onlineservers;
    }
    public static void updateMemberCount(DiscordApi api) {
        int members = api.getServerById(811844634090537040l).get().getMemberCount() - 3;
        api.getServerById(811844634090537040l).get().getVoiceChannelById(815771540880752660l).get().updateName("Members: " + members);
        System.out.println("Updated members (" + members + ")");
    }
    public static void updateDownloadCount(DiscordApi api) {
        int downloads = getDownloads(new int[]{89567,89595,89470,89434,89441,88965,88219,89265,88887,89732}, new int[]{455364});
        api.getServerById(811844634090537040l).get().getVoiceChannelById(816056814343815198l).get().updateName("Total downloads: " + downloads);
        System.out.println("Updated downloads (" + downloads + ")");
    }

    public static void updateServersCount(DiscordApi api) {
        int servers = getServerCount();
        api.getServerById(811844634090537040l).get().getVoiceChannelById(816113904513187841l).get().updateName("Servers using: " + servers);
        System.out.println("Updated server count (" + servers + ")");
    }

    public static int getDownload(String url, boolean total) {
        System.out.println(url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        String stringResponse;
        try {
            stringResponse = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        

        int downloads = 0;
        if (total) {
          JsonObject object = new Gson().fromJson(stringResponse, JsonObject.class);
          JsonObject downloadObject = object.getAsJsonObject("downloads");
          downloads += downloadObject.get("total").getAsDouble();
        } else {
          Map jsonJavaRootObject = new Gson().fromJson(stringResponse, Map.class);
          downloads += (double) jsonJavaRootObject.get("downloads");
        }
        return downloads;
    }

}
