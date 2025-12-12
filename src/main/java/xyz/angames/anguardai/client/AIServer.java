package xyz.angames.anguardai.client;

import xyz.angames.anguardai.Anguardai;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class AIServer {

    private final Anguardai plugin;
    private final HttpClient client;
    private final String apiUrl;
    private final String secretKey;
    private final String serverPort;

    public AIServer(Anguardai plugin) {
        this.plugin = plugin;
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        String ip = plugin.getConfig().getString("vds.ip");
        int port = plugin.getConfig().getInt("vds.port");
        this.secretKey = plugin.getConfig().getString("vds.secret_key");
        this.apiUrl = "http://" + ip + ":" + port + "/predict_binary";

        int mcPort = plugin.getServer().getPort();
        this.serverPort = String.valueOf(mcPort);

        plugin.getLogger().info("§e[Debug] AI Client Configured: " + apiUrl);
    }

    public CompletableFuture<String> sendRequest(byte[] flatBufferData) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/octet-stream")
                .header("X-Secret-Key", secretKey)
                .header("X-Server-Port", serverPort)
                .POST(HttpRequest.BodyPublishers.ofByteArray(flatBufferData))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        plugin.getLogger().warning("§c[Debug] API Error: " + response.statusCode());
                        if (response.statusCode() == 401) plugin.getLogger().warning("-> Invalid Secret Key!");
                        if (response.statusCode() == 403) plugin.getLogger().warning("-> License Limit Reached!");
                        return null;
                    }
                    return response.body();
                })
                .exceptionally(e -> {
                    plugin.getLogger().severe("§c[Debug] Connection Failed: " + e.getMessage());
                    if (!e.getMessage().contains("timed out")) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }
}