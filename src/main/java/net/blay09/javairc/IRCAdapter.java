package net.blay09.javairc;

public abstract class IRCAdapter implements IRCListener {
    @Override
    public boolean onRawMessage(IRCConnection connection, IRCMessage message) {
        return true;
    }

    @Override
    public void onUnhandledException(IRCConnection connection, Exception e) {

    }

    @Override
    public void onConnectionFailed(IRCConnection connection, Exception e) {

    }

    @Override
    public void onConnected(IRCConnection connection) {

    }

    @Override
    public void onDisconnected(IRCConnection connection) {

    }

    @Override
    public void onUserJoin(IRCConnection connection, IRCMessage message, IRCUser user, String channel) {

    }

    @Override
    public void onUserPart(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String quitMessage) {

    }

    @Override
    public void onUserQuit(IRCConnection connection, IRCMessage message, IRCUser user, String quitMessage) {

    }

    @Override
    public void onUserNickChange(IRCConnection connection, IRCMessage message, IRCUser user, String nick) {

    }

    @Override
    public void onChannelTopic(IRCConnection connection, IRCMessage message, String channel, String topic) {

    }

    @Override
    public void onChannelTopicChange(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String topic) {

    }

    @Override
    public void onChannelNotice(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String text) {

    }

    @Override
    public void onUserNotice(IRCConnection connection, IRCMessage message, IRCUser user, String text) {

    }

    @Override
    public void onChannelChat(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String text) {

    }

    @Override
    public void onUserChat(IRCConnection connection, IRCMessage message, IRCUser user, String text) {

    }

    @Override
    public void onChannelMode(IRCConnection connection, IRCMessage message, IRCUser user, String channel, String flags, String[] args) {

    }

    @Override
    public void onUserMode(IRCConnection connection, IRCMessage message, IRCUser user, String flags, String[] args) {

    }
}
