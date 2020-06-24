package net.blay09.javairc;

import java.util.LinkedList;

public class IRCSender implements Runnable {

    private final LinkedList<String> queue = new LinkedList<>();
    private final Thread thread;
    private final IRCConnection connection;
    private int messageDelay;
    private boolean running;

    public IRCSender(IRCConnection connection, int messageDelay) {
        this.connection = connection;
        this.thread = new Thread(this, "IRCSender (" + connection.getServer() + ")");
        this.messageDelay = messageDelay;
    }

    public boolean addToSendQueue(String message) {
        synchronized (queue) {
            queue.addLast(message);
        }
        return true;
    }

    public void start() {
        this.running = true;
        this.thread.start();
    }

    @Override
    public void run() {
        try {
            while (running) {
                boolean messageSent = false;
                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        connection.sendRawNow(queue.pop());
                        messageSent = true;
                    }
                }
                try {
                    Thread.sleep(messageSent ? messageDelay : 100);
                } catch (InterruptedException ignored) {
                }
            }
        } catch (Exception e) {
            connection.unhandledException(e);
        }
        running = false;
    }

    public void stop() {
        try {
            running = false;
            thread.join();
        } catch (InterruptedException ignored) {
        }
    }
}
