package net.blay09.javairc;

public class IRCUser {
    private final String nick;
    private final String username;
    private final String hostname;

    public IRCUser(String nick, String username, String hostname) {
        this.nick = nick;
        this.username = username;
        this.hostname = hostname;
    }

    public String getNick() {
        return nick;
    }

    public String getUsername() {
        return username;
    }

    public String getHostname() {
        return hostname;
    }
}
