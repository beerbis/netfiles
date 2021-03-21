package ru.beerbis.netfiles.server;

import io.netty.channel.ChannelFuture;
import org.junit.Test;
import ru.beerbis.netfiles.server.listener.TransferListener;

import java.net.BindException;

import static ru.beerbis.netfiles.server.settings.Settings.SETTINGS;

public class TransferListenerTest {

    @Test
    public void should_openAndCloseOnce_success() throws InterruptedException {
        var future = new TransferListener().open(SETTINGS.getTransfer());
        future.sync();
        future.channel().close().sync();
    }

    @Test
    public void should_openAndCloseTwice_success() throws InterruptedException {
        var future1 = new TransferListener().open(SETTINGS.getTransfer());
        future1.sync();
        future1.channel().close().sync();

        var future2 = new TransferListener().open(SETTINGS.getTransfer());
        future2.sync();
        future2.channel().close().sync();
    }

    @Test(expected = BindException.class)
    public void should_openWithAlreadyOpened_bindingFail() throws InterruptedException {
        ChannelFuture future1 = null;
        ChannelFuture future2;
        try {
            future1 = new TransferListener().open(SETTINGS.getTransfer());
            future1.sync();

            future2 = new TransferListener().open(SETTINGS.getTransfer());
            future2.sync();
            future2.channel().close().sync();
        } finally {
            future1.channel().close().sync();
        }
    }
}
