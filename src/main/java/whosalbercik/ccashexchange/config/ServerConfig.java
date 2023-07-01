package whosalbercik.ccashexchange.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> SERVER_ADDRESS;

    public static final ForgeConfigSpec.ConfigValue<String> MARKET_PASS;
    public static final ForgeConfigSpec.ConfigValue<String> MARKET_ACCOUNT;

    public static final ForgeConfigSpec.ConfigValue<Integer> TRANSACTIONS_PER_PLAYER;
    public static final ForgeConfigSpec.ConfigValue<Float> TAXATION;




    static {

        BUILDER.comment("Data used by the mod to access the market account");

        SERVER_ADDRESS = BUILDER.define("server_address", "http://127.0.0.1:80/");

        MARKET_ACCOUNT = BUILDER.define("market_account", "market");
        MARKET_PASS = BUILDER.define("market_password", "password");

        TRANSACTIONS_PER_PLAYER = BUILDER.comment("-1 = UNLIMITED, maximum active transactions for a player").define("max_transactions", 5);

        TAXATION = BUILDER.define("taxation", 0.01f);


        SPEC = BUILDER.build();
    }

}
