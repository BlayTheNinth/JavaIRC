package net.blay09.javairc;


import net.blay09.javairc.snapshot.ChannelSnapshot;
import net.blay09.javairc.snapshot.SnapshotWrapper;
import net.blay09.javairc.snapshot.UserSnapshot;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class IRCConnection implements Runnable {

    private static final Logger log = Logger.getLogger(IRCConnection.class.getName());

    private static final String LINE_FEED = "\r\n";

    private final IRCListener listener;
    private final IRCConfiguration configuration;
    private final Thread thread;
    private final IRCParser parser;
    private final IRCSender sender;

    private final Map<String, ChannelSnapshot> channelSnapshots;
    private final Map<String, UserSnapshot> userSnapshots;

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private boolean running;
    private boolean connected;
    private String nick;

    private String serverType;
    private String channelTypes = "#&";
    private String channelUserModes = "ov";
    private String channelUserModePrefixes = "@+";

    public IRCConnection(final IRCConfiguration configuration, IRCListener listener) {
        if (configuration.isKeepingSnapshots()) {
            channelSnapshots = new HashMap<>();
            userSnapshots = new HashMap<>();
            this.listener = new SnapshotWrapper(listener, channelSnapshots, userSnapshots);
        } else {
            channelSnapshots = null;
            userSnapshots = null;
            this.listener = listener;
        }
        this.configuration = configuration;
        this.nick = configuration.getNick();
        this.parser = new IRCParser();
        this.sender = new IRCSender(this, configuration.getMessageDelay());
        this.thread = new Thread(this, "IRCConnection (" + configuration.getServer() + ")");

        log.setUseParentHandlers(false);
        ConsoleHandler logHandler = new ConsoleHandler();
        logHandler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord record) {
                return "[" + configuration.getServer() + "] " + record.getMessage() + "\n";
            }
        });
        log.addHandler(logHandler);
    }

    public void start() {
        running = true;
        thread.start();
    }

    private boolean connect() {
        try {
            if (configuration.isSecure()) {
                try {
                    SSLSocketFactory socketFactory;
                    if (configuration.isTrustingSelfSigned()) {
                        SSLContext context = SSLContext.getInstance("TLS");
                        context.init(null, new TrustManager[]{new NaiveTrustManager()}, null);
                        socketFactory = context.getSocketFactory();
                    } else {
                        socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    }
                    SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket(configuration.getServer(), configuration.getPort());
                    if (configuration.getLocalAddress() != null && !configuration.getLocalAddress().isEmpty()) {
                        sslSocket.bind(new InetSocketAddress(configuration.getLocalAddress(), configuration.getPort()));
                    }
                    if (configuration.isDiffieHellmanDisabled()) {
                        List<String> cipherSuites = new ArrayList<>();
                        for (String suite : sslSocket.getEnabledCipherSuites()) {
                            if (!suite.contains("_DHE_")) {
                                cipherSuites.add(suite);
                            }
                        }
                        sslSocket.setEnabledCipherSuites(cipherSuites.toArray(new String[0]));
                    }
                    sslSocket.startHandshake();
                    socket = sslSocket;
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    listener.onConnectionFailed(this, e);
                }
            } else {
                SocketAddress targetAddr = new InetSocketAddress(configuration.getServer(), configuration.getPort());
                socket = new Socket();
                if (configuration.getLocalAddress() != null && !configuration.getLocalAddress().isEmpty()) {
                    socket.bind(new InetSocketAddress(configuration.getLocalAddress(), configuration.getPort()));
                }
                socket.connect(targetAddr);
            }
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), configuration.getEncoding()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), configuration.getEncoding()));
            connected = true;
            return true;
        } catch (IOException e) {
            listener.onConnectionFailed(this, e);
            return false;
        }
    }

    @Override
    public void run() {
        if (!connect()) {
            running = false;
            return;
        }
        if (configuration.getPassword() != null) {
            sender.addToSendQueue("PASS " + configuration.getPassword());
            if (configuration.isDebug()) {
                log.info("> PASS **********");
            }
        }
        sendRaw("NICK " + nick);
        sendRaw("USER " + (configuration.getUsername() != null ? configuration.getUsername() : nick) + " \"\" \"\" " + (configuration.getRealName() != null
                ? configuration.getRealName()
                : nick));
        sender.start();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (configuration.isDebug()) {
                    log.info("< " + line);
                }
                if (!line.isEmpty()) {
                    handleMessage(parser.parse(line));
                }
            }
            stop();
        } catch (IOException e) {
            if (!e.getMessage().equals("Socket closed")) {
                unhandledException(e);
            } else {
                stop();
            }
        } catch (Exception e) {
            unhandledException(e);
        }
    }

    private void handleMessage(IRCMessage message) {
        if (!listener.onRawMessage(this, message)) {
            return;
        }
        int numeric = message.getNumericCommand();
        if (numeric > 0) {
            handleMessageNumeric(numeric, message);
            return;
        }
        String command = message.getCommand();
        switch (command) {
            case "PING":
                sendRaw("PONG " + message.arg(0));
                break;
            case "NOTICE":
                if (channelTypes.indexOf(message.arg(0).charAt(0)) != -1) {
                    listener.onChannelNotice(this, message, message.parseSender(), message.arg(0), message.arg(1));
                } else {
                    listener.onUserNotice(this, message, message.parseSender(), message.arg(1));
                }
                break;
            case "PRIVMSG":
                if (channelTypes.indexOf(message.arg(0).charAt(0)) != -1) {
                    listener.onChannelChat(this, message, message.parseSender(), message.arg(0), message.arg(1));
                } else {
                    listener.onUserChat(this, message, message.parseSender(), message.arg(1));
                }
                break;
            case "JOIN":
                listener.onUserJoin(this, message, message.parseSender(), message.arg(0));
                break;
            case "PART":
                listener.onUserPart(this, message, message.parseSender(), message.arg(0), message.arg(1));
                break;
            case "TOPIC":
                listener.onChannelTopicChange(this, message, message.parseSender(), message.arg(0), message.arg(1));
                break;
            case "NICK":
                listener.onUserNickChange(this, message, message.parseSender(), message.arg(0));
                break;
            case "QUIT":
                listener.onUserQuit(this, message, message.parseSender(), message.arg(0));
                break;
            case "MODE":
                if (channelTypes.indexOf(message.arg(0).charAt(0)) != -1) {
                    listener.onChannelMode(this, message, message.parseSender(), message.arg(0), message.arg(1), message.subargs(2));
                } else {
                    listener.onUserMode(this, message, message.parseSender(), message.arg(1), message.subargs(2));
                }
                break;
        }
    }

    private void handleMessageNumeric(int numeric, IRCMessage message) {
        switch (numeric) {
            case IRCNumerics.RPL_WELCOME:
                listener.onConnected(this);
                for (String capability : configuration.getCapabilities()) {
                    sendRaw("CAP REQ " + capability);
                }
                for (String channel : configuration.getAutoJoinChannels()) {
                    join(channel);
                }
                break;
            case IRCNumerics.RPL_MYINFO:
                serverType = message.arg(1);
                break;
            case IRCNumerics.RPL_ISUPPORT:
                for (int i = 0; i < message.argCount(); i++) {
                    if (message.arg(i).startsWith("CHANTYPES=")) {
                        channelTypes = message.arg(i).substring(10);
                    } else if (message.arg(i).startsWith("PREFIX=")) {
                        String value = message.arg(i).substring(7);
                        StringBuilder sb = new StringBuilder();
                        for (int j = 0; j < value.length(); j++) {
                            char c = value.charAt(j);
                            if (c == ')') {
                                channelUserModes = sb.toString();
                                sb = new StringBuilder();
                            } else if (c != '(') {
                                sb.append(c);
                            }
                        }
                        channelUserModePrefixes = sb.toString();
                    }
                }
                break;
            case IRCNumerics.RPL_TOPIC:
                listener.onChannelTopic(this, message, message.arg(1), message.arg(2));
                break;
        }
    }

    public void quit(String quitMessage) {
        try {
            sendRawNow("QUIT :" + quitMessage);
            stop();
        } catch (IOException ignored) {
        }
    }

    public void nick(String nick) {
        sendRaw("NICK " + nick);
    }

    public void join(String channel) {
        join(channel, null);
    }

    public void join(String channel, String password) {
        sendRaw("JOIN " + channel + (password != null ? " " + password : ""));
    }

    public void part(String channel) {
        sendRaw("PART " + channel);
    }

    public void message(String target, String message) {
        sendRaw("PRIVMSG " + target + " :" + message);
    }

    public void notice(String target, String message) {
        sendRaw("NOTICE " + target + " :" + message);
    }

    public void kick(String channel, String user, String reason) {
        sendRaw("KICK " + channel + " " + user + (reason != null ? ":" + reason : ""));
    }

    public void stop() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        if (sender != null) {
            sender.stop();
        }
        if (connected) {
            connected = false;
            listener.onDisconnected(this);
        }
        running = false;
    }

    public void sendRaw(String line) {
        if (configuration.isDebug()) {
            log.info("> " + line);
        }
        sender.addToSendQueue(line);
    }

    public void sendRawNow(String line) throws IOException {
        if (writer != null) {
            writer.write(line);
            writer.write(LINE_FEED);
            writer.flush();
        } else {
            throw new NotConnectedException("Attempted to send message while not connected to IRC server.");
        }
    }

    public void unhandledException(Exception e) {
        listener.onUnhandledException(this, e);
        stop();
    }

    public String getServer() {
        return configuration.getServer();
    }

    public String getNick() {
        return nick;
    }

    public String getChannelUserModePrefixes() {
        return channelUserModePrefixes;
    }

    public String getChannelUserModes() {
        return channelUserModes;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isRunning() {
        return running;
    }

    public String getServerType() {
        return serverType;
    }

    public UserSnapshot getUserSnapshot(String name) {
        return userSnapshots.get(name.toLowerCase());
    }

    public ChannelSnapshot getChannelSnapshot(String name) {
        return channelSnapshots.get(name.toLowerCase());
    }

}
