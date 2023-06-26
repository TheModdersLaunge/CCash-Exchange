package whosalbercik.ccashexchange.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Base64;
import java.util.Optional;

import whosalbercik.ccashexchange.config.ServerConfig;
import whosalbercik.ccashexchange.object.Variant;

public final class CCashApi extends Thread{

    public static final String SERVER = ServerConfig.SERVER_ADDRESS.get();
    private static final HttpClient client = HttpClient.newHttpClient();


    public static Optional<Long> getBalance(String name) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER + "api/v1/user/balance?name=" + name))
                .header("Accept", "application/json")
                .GET()
                .build();


        try {
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() > 299) return Optional.empty();

            return Optional.of(Long.parseLong(response.body()));

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

    public static Variant<Long, String> sendFunds(String a_name, String pass, String b_name, Long amount) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER + "api/v1/user/transfer?name=" + b_name + "¶amount=" + String.valueOf(amount)))
                .header("Accept", "application/json")
                .header("Authorization", getBasicAuthenticationHeader(a_name, pass))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            String temp = response.body();

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER + "api/v1/user/register"))
                .header("Accept", "application/json")
                .header("Authorization", getBasicAuthenticationHeader(name, pass))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            return response.statusCode() >= 200 && response.statusCode() < 300;

        } catch (Exception e) {
            return false;
        }


    }


    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

}
