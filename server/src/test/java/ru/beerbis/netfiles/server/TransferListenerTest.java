package ru.beerbis.netfiles.server;

import io.netty.channel.ChannelFuture;
import org.junit.Test;
import ru.beerbis.netfiles.server.listener.TransferListener;

import java.net.BindException;

import static ru.beerbis.netfiles.server.settings.Settings.SETTINGS;

public class TransferListenerTest {

    @Test
    public void should_zeroActivityTest_success() throws InterruptedException {
        try (var transfers = new TransferListener("transfers")) {
        }
    }

    /**
     * Интересный случай. Как бы и работает норм, но вроде бы и некорректно как-то.
     * Как вариант: можно на `close` подождать завершения binding-фьючера, но зачем... Всё равно же закрываться.
     * Как правильно поступать - не совсем очевидно.
     * @throws InterruptedException ожидание фьючера закрытия(в {@link AutoCloseable} - sync) может быть прервано
     */
    @Test
    public void should_openSynclessAndImmediatelyClose_success() throws InterruptedException {
        try (var transfers = new TransferListener("transfers")) {
            transfers.open(SETTINGS.getTransfer());
        }
    }

    @Test
    public void should_openAndCloseOnce_success() throws InterruptedException {
        try (var transfers = new TransferListener("transfers")) {
            transfers.open(SETTINGS.getTransfer()).sync();
        }
    }

    @Test
    public void should_openAndCloseTwice_success() throws InterruptedException {
        try (var transfers = new TransferListener("transfers1")) {
            transfers.open(SETTINGS.getTransfer()).sync();
        }

        try (var transfers = new TransferListener("transfers2")) {
            transfers.open(SETTINGS.getTransfer()).sync();
        }
    }

    @Test(expected = BindException.class)
    public void should_openWithAlreadyOpened_bindingFail() throws InterruptedException {
        try (var transfers1 = new TransferListener("transfers1");
             var transfers2 = new TransferListener("transfers2")) {
            transfers1.open(SETTINGS.getTransfer()).sync();
            transfers2.open(SETTINGS.getTransfer()).sync();
        }
    }
}
