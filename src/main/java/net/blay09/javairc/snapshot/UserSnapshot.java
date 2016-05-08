package net.blay09.javairc.snapshot;

import lombok.Data;
import net.blay09.javairc.IRCChannelUserMode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class UserSnapshot {
    private transient final Set<ChannelSnapshot> channels = new HashSet<ChannelSnapshot>();
    private transient final Map<String, IRCChannelUserMode> channelModes = new HashMap<String, IRCChannelUserMode>();
    private String nick;
    private String hostname;
    private String username;
    private String loginName;

    public UserSnapshot(String nick) {
        this.nick = nick;
    }
}
