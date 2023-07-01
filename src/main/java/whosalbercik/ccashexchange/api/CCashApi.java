package whosalbercik.ccashexchange.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Base64;
import java.util.Optional;

import whosalbercik.ccashexchange.config.ServerConfig;
import whosalbercik.ccashexchange.object.Variant;

public final class CCashApi{

    public static final String SERVER = ServerConfig.SERVER_ADDRESS.get();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static boolean isOnline() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER + "api/properties"))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;

        } catch (Exception e) {
            return false;
        }
    }
    public static Optional<Long> getBalance(String name) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER + "api/v1/user/balance?name=" + name))
                .header("Accept", "application/json")
                .GET()
                .build();


        try {
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) return Optional.of(Long.parseLong(response.body()));

            return Optional.empty();

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static boolean containsAccount(String name) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER + "api/v1/user/exists?name=" + name))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            return response.statusCode() >= 200 && response.statusCode() <= 299;

        } catch (Exception e) {
            return false;
        }
    }

    public static Variant<Long, String> sendFunds(String sender, String pass, String receiver, Long amount) {

        String json = String.format("{\"name\":\"%s\", \"amount\":%s}", receiver, amount);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER + "api/v1/user/transfer"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", getBasicAuthenticationHeader(sender, pass))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            String temp = response.body();

            if (response.statusCode() == 200) {
                return Variant.ofT1(Long.parseLong(temp));
            }
            else {
                return Variant.ofT2(response.body());
            }

        } catch (Exception e) {
            return null;
        }

    }

    public static boolean addUser(String name, String pass) {

        if (containsAccount(name)) {
            return false;
        }

        String json = String.format("{\"name\":\"%s\", \"pass\":\"%s\"}", name, pass);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER + "api/v1/user/register"))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 204;

        } catch (Exception e) {
            return false;
        }


    }

    public static boolean verifyPassword(String username, String password) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER + "api/v1/user/verify_password"))
                .header("Accept", "application/json")
                .header("Authorization", getBasicAuthenticationHeader(username, password))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 204;

        } catch (Exception e) {
            return false;
        }
    }


    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

}
