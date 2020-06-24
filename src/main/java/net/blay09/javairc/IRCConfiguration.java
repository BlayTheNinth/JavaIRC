package net.blay09.javairc;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class IRCConfiguration {

    private String server;
    private int port;
    private String nick;
    private String username;
    private String realName;
    private int messageDelay;
    private String localAddress;
    private Charset encoding;
    private boolean isDebug;
    private String password;
    private boolean isKeepingSnapshots;
    private boolean isSecure;
    private boolean isTrustingSelfSigned;
    private boolean isDiffieHellmanDisabled;
    private final List<String> autoJoinChannels = new ArrayList<>();
    private final List<String> capabilities = new ArrayList<>();

    public static IRCConfiguration createDefault() {
        return new IRCConfiguration().setPort(6667).setMessageDelay(33).setEncoding(StandardCharsets.UTF_8);
    }

    public String getServer() {
        return server;
    }

    public IRCConfiguration setServer(String server) {
        this.server = server;
        return this;
    }

    public int getPort() {
        return port;
    }

    public IRCConfiguration setPort(int port) {
        this.port = port;
        return this;
    }

    public String getNick() {
        return nick;
    }

    public IRCConfiguration setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public IRCConfiguration setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public IRCConfiguration setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public int getMessageDelay() {
        return messageDelay;
    }

    public IRCConfiguration setMessageDelay(int messageDelay) {
        this.messageDelay = messageDelay;
        return this;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public IRCConfiguration setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
        return this;
    }

    public Charset getEncoding() {
        return encoding;
    }

    public IRCConfiguration setEncoding(Charset encoding) {
        this.encoding = encoding;
        return this;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public IRCConfiguration setDebug(boolean debug) {
        isDebug = debug;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public IRCConfiguration setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isKeepingSnapshots() {
        return isKeepingSnapshots;
    }

    public IRCConfiguration setKeepingSnapshots(boolean keepingSnapshots) {
        isKeepingSnapshots = keepingSnapshots;
        return this;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public IRCConfiguration setSecure(boolean secure) {
        isSecure = secure;
        return this;
    }

    public boolean isTrustingSelfSigned() {
        return isTrustingSelfSigned;
    }

    public IRCConfiguration setTrustingSelfSigned(boolean trustingSelfSigned) {
        isTrustingSelfSigned = trustingSelfSigned;
        return this;
    }

    public boolean isDiffieHellmanDisabled() {
        return isDiffieHellmanDisabled;
    }

    public IRCConfiguration setDiffieHellmanDisabled(boolean diffieHellmanDisabled) {
        isDiffieHellmanDisabled = diffieHellmanDisabled;
        return this;
    }

    public List<String> getAutoJoinChannels() {
        return autoJoinChannels;
    }

    public IRCConfiguration addAutoJoinChannel(String channel) {
        autoJoinChannels.add(channel);
        return this;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }
}
