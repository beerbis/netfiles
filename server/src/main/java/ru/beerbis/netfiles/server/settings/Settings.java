package ru.beerbis.netfiles.server.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class Settings {
    private static final ObjectMapper MAPPER = new JavaPropsMapper();
    public static final Settings SETTINGS = readProperties("server.properties", Settings.class);

    private final ListenerSettings transfer;

    public Settings(@JsonProperty(value = "transfer", required = true) @Nonnull ListenerSettings transfer) {
        this.transfer = requireNonNull(transfer, "transfer");
    }

    static private <T> T readProperties(String name, Class<T> clazz) {
        var url = Settings.class.getClassLoader().getResource(name);
        try {
            return requireNonNull(MAPPER.readValue(url, clazz), "file does not exist, or is empty");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Settings loading failed for " + clazz.getName()
                    + System.lineSeparator() + url, e);
        }
    }

    @Nonnull
    public ListenerSettings getTransfer() {
        return transfer;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "transfer=" + transfer +
                '}';
    }
}
