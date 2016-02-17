package net.blay09.javairc.snapshot;

import net.blay09.javairc.*;

import java.util.Map;

public class SnapshotWrapper implements IRCListener {

    private final IRCListener parent;
    private final Map<String, ChannelSnapshot> channels;
    private final Map<String, UserSnapshot> users;

    public SnapshotWrapper(IRCListener parent, Map<String, ChannelSnapshot> channels, Map<String, UserSnapshot> users) {
        this.parent = parent;
        this.channels = channels;
        this.users = users;
    }

    private UserSnapshot getUserSnapshot(String nick) {
        UserSnapshot user = users.get(nick.toLowerCase());
        if(user == null) {
            user = new UserSnapshot(nick);
            users.put(nick.toLowerCase(), user);
        }
        return user;
    }

    private ChannelSnapshot getChannelSnapshot(String name) {
        ChannelSnapshot channel = channels.get(name.toLowerCase());
        if(channel == null) {
            channel = new ChannelSnapshot(name);
            channels.put(name.toLowerCase(), channel);
        }
        return channel;
    }

    @Override
    public boolean onRawMessage(IRCConnection connection, IRCMessage message) {
        int numeric = message.getNumericCommand();
        switch(numeric) {
            case IRCNumerics.RPL_IDENTIFIED:
            case IRCNumerics.RPL_WHOISLOGIN2: // ?, nick
                getUserSnapshot(message.arg(1)).setLoginName(message.arg(1));
                break;
            case IRCNumerics.RPL_WHOISLOGIN: // nick, loginName
                getUserSnapshot(message.arg(1)).setLoginName(message.arg(2));
                break;
            case IRCNumerics.RPL_NAMREPLY: // channel, names
                ChannelSnapshot channel = getChannelSnapshot(message.arg(0));
                String[] names = message.arg(2).split(" ");
                for (String name : names) {
                    char firstChar = name.charAt(0);
                    int idx = connection.getChannelUserModePrefixes().indexOf(firstChar);
                    IRCChannelUserMode mode = null;
                    if (idx != -1) {
                        mode = IRCChannelUserMode.fromChar(connection.getChannelUserModes().charAt(idx));
                        name = name.substring(1);
                    }
                    UserSnapshot user = getUserSnapshot(name);
                    if (mode != null) {
                        user.getChannelModes().put(channel.getName(), mode);
                    }
                    user.getChannels().add(channel);
                    channel.getUsers().add(user);
                }
                break;
        }
        return parent.onRawMessage(connection, message);
    }

    @Override
    public void onUnhandledException(IRCConnection connection, Exception e) {
        parent.onUnhandledException(connection, e);
    }

    @Override
    public void onConnectionFailed(IRCConnection connection, Exception e) {
        parent.onConnectionFailed(connection, e);
    }

    @Override
    public void onConnected(IRCConnection connection) {
        parent.onConnected(connection);
    }

    @Override
    public void onDisconnected(IRCConnection connection) {
        parent.onDisconnected(connection);
    }

    @Override
    public void onUserJoin(IRCConnection connection, IRCMessage message, IRCUser user, String channel) {
        ChannelSnapshot channelSnapshot = getChannelSnapshot(channel);
        UserSnapshot userSnapshot = getUserSnapshot(user.getNick());
        channelSnapshot.getUsers().add(userSnapshot);
        userSnapshot.getChannels().add(channelSnapshot);
        parent.onUserJoin(connection, message, user, channel);
    }

    @Override
    public void onUserPart(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String quitMessage) {
        ChannelSnapshot channelSnapshot = getChannelSnapshot(channel);
        UserSnapshot userSnapshot = getUserSnapshot(user.getNick());
        channelSnapshot.getUsers().remove(userSnapshot);
        userSnapshot.getChannels().remove(channelSnapshot);
        parent.onUserPart(connection, message, user, channel, quitMessage);
    }

    @Override
    public void onUserQuit(IRCConnection connection, IRCMessage message, IRCUser user, String quitMessage) {
        UserSnapshot userSnapshot = getUserSnapshot(user.getNick());
        for(ChannelSnapshot channelSnapshot : userSnapshot.getChannels()) {
            channelSnapshot.getUsers().remove(userSnapshot);
        }
        userSnapshot.getChannels().clear();
        parent.onUserQuit(connection, message, user, quitMessage);
    }

    @Override
    public void onUserNickChange(IRCConnection connection, IRCMessage message, IRCUser user, String nick) {
        UserSnapshot userSnapshot = getUserSnapshot(user.getNick());
        users.remove(user.getNick());
        userSnapshot.setNick(nick);
        users.put(nick, userSnapshot);
        parent.onUserNickChange(connection, message, user, nick);
    }

    @Override
    public void onChannelTopic(IRCConnection connection, IRCMessage message, String channel, String topic) {
        getChannelSnapshot(channel).setTopic(topic);
        parent.onChannelTopic(connection, message, channel, topic);
    }

    @Override
    public void onChannelTopicChange(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String topic) {
        getChannelSnapshot(channel).setTopic(topic);
        parent.onChannelTopicChange(connection, message, user, channel, topic);
    }

    @Override
    public void onChannelNotice(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String text) {
        parent.onChannelNotice(connection, message, user, channel, text);
    }

    @Override
    public void onUserNotice(IRCConnection connection, IRCMessage message, IRCUser user, String text) {
        parent.onUserNotice(connection, message, user, text);
    }

    @Override
    public void onChannelChat(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String text) {
        parent.onChannelChat(connection, message, user, channel, text);
    }

    @Override
    public void onUserChat(IRCConnection connection, IRCMessage message, IRCUser user, String text) {
        parent.onUserChat(connection, message, user, text);
    }

    @Override
    public void onChannelMode(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String flags, String[] args) {
        parent.onChannelMode(connection, message, user, channel, flags, args);
    }

    @Override
    public void onUserMode(IRCConnection connection, IRCMessage message, IRCUser user, String flags, String[] args) {
        parent.onUserMode(connection, message, user, flags, args);
    }
}
