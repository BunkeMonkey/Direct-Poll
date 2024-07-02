package bunke.DirectPoll.Networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkUtils {

    public interface IpCallback {
        void onIpReceived(String ip);
    }

    public static void getExternalIpAddress(IpCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL url = new URL("https://api.ipify.org");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String ip = in.readLine();
                in.close();
                callback.onIpReceived(ip);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onIpReceived(null);
            }
        });
    }
}