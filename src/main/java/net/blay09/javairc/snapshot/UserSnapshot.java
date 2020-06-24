package net.blay09.javairc.snapshot;

import net.blay09.javairc.IRCChannelUserMode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserSnapshot {
    private transient final Set<ChannelSnapshot> channels = new HashSet<>();
    private transient final Map<String, IRCChannelUserMode> channelModes = new HashMap<>();
    private String nick;
    private String hostname;
    private String username;
    private String loginName;

    public UserSnapshot(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Set<ChannelSnapshot> getChannels() {
        return channels;
    }

    public Map<String, IRCChannelUserMode> getChannelModes() {
        return channelModes;
    }
}
