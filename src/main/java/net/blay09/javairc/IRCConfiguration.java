package net.blay09.javairc;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Builder
@Value
public class IRCConfiguration {
    private String server;
    private int port;
    private String nick;
    private String username;
    private String realName;
    private int messageDelay;
    private String localAddress;
    private Charset encoding;
    private boolean debug;
    private String password;
    private boolean snapshots;
    private boolean secure;
    private boolean selfSigned;
    private boolean disableDiffieHellman;
    @Singular
    private List<String> autoJoinChannels;
    @Singular
    private List<String> capabilities;

    public static IRCConfigurationBuilder builder() {
        // This does not really error, it's just a bug in the Lombok plugin for IntelliJ.
        // It'll compile and run just fine.
        // https://github.com/rzwitserloot/lombok/issues/1006
        return new IRCConfigurationBuilder().port(6667).messageDelay(33).encoding(StandardCharsets.UTF_8);
    }
}
