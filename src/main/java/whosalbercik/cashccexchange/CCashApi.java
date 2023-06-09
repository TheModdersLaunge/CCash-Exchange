package ccash;

import variant.Variant;
import java.util.UUID;
import java.util.Optional;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;

public final class CCashApi {
    public static Optional<Long> getBalance(String name) {
        final HttpResponse<String> resp = Unirest.get("v1/user/balance?name=" + name)
            .header("Accept", "application/json")
            .asString();
    
        String temp = resp.getBody();
        if (resp.isSuccess()) { return Optional.of(Long.parseLong(temp)); }
        else { return Optional.empty(); }
    }
    
    public static boolean containsAccount(String name) {
        return Unirest.get("v1/user/exists?name=" + name).header("Accept", "application/json").asEmpty().isSuccess();
    }

    public static Variant<Long, String> sendFunds(String a_name, String password, String b_name, Long amount) {
        final HttpResponse<String> resp = Unirest.post("v1/user/transfer")
            .header("Accept", "application/json")
            .field("name", b_name)
            .field("amount", String.valueOf(amount))
            .basicAuth(a_name, password)
            .asString();

        String temp = resp.getBody();
        if (resp.isSuccess()) { return Variant.ofT1(Long.parseLong(temp)); }
        else { return Variant.ofT2(resp.getBody()); }
    }
}