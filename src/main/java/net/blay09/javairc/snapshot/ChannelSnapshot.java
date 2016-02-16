package net.blay09.javairc.snapshot;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class ChannelSnapshot {
    private final Set<UserSnapshot> users = new HashSet<>();
    private final String name;
    private String topic;
}
