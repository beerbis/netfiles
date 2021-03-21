package ru.beerbis.netfiles.server.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

public class ListenerSettings {

    private final Integer port;

    @JsonCreator
    public ListenerSettings(@JsonProperty(value = "port", required = true) @Nonnull Integer port) {
        this.port = requireNonNull(port);
    }

    @Nonnull
    public Integer getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "ListenerSettings{" +
                "port=" + port +
                '}';
    }
}
