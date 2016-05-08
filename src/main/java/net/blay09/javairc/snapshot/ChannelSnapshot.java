package net.blay09.javairc.snapshot;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class ChannelSnapshot {
    private transient final Set<UserSnapshot> users = new HashSet<UserSnapshot>();
    private final String name;
    private String topic;
}
