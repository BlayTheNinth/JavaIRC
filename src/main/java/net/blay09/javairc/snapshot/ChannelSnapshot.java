package net.blay09.javairc.snapshot;


import java.util.HashSet;
import java.util.Set;


public class ChannelSnapshot {

    private transient final Set<UserSnapshot> users = new HashSet<>();

    private final String name;
    private String topic;

    public ChannelSnapshot(String name) {
        this.name = name;
    }

    public Set<UserSnapshot> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
