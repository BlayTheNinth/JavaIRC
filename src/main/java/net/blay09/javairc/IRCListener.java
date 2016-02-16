package net.blay09.javairc;

public interface IRCListener {
    boolean onRawMessage(IRCConnection connection, IRCMessage message);
    void onUnhandledException(IRCConnection connection, Exception e);
    void onConnectionFailed(IRCConnection connection, Exception e);
    void onConnected(IRCConnection connection);
    void onDisconnected(IRCConnection connection);
    void onUserJoin(IRCConnection connection, IRCMessage message, IRCUser user, String channel);
    void onUserPart(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String quitMessage);
    void onUserQuit(IRCConnection connection, IRCMessage message, IRCUser user, String quitMessage);
    void onUserNickChange(IRCConnection connection, IRCMessage message, IRCUser user, String nick);
    void onChannelTopic(IRCConnection connection, IRCMessage message, String channel, String topic);
    void onChannelTopicChange(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String topic);
    void onChannelNotice(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String text);
    void onUserNotice(IRCConnection connection, IRCMessage message, IRCUser user, String text);
    void onChannelChat(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String text);
    void onUserChat(IRCConnection connection, IRCMessage message, IRCUser user, String text);
    void onChannelMode(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String flags, String[] args);
    void onUserMode(IRCConnection connection, IRCMessage message, IRCUser user, String flags, String[] args);
}
