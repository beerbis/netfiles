package ru.beerbis.netfiles.server;

import ru.beerbis.netfiles.server.listener.TransferListener;
import ru.beerbis.netfiles.server.settings.Settings;

import java.util.Scanner;

public class ServerApplication {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        try(var transfer = new TransferListener("transfer")) {
            transfer.open(Settings.SETTINGS.getTransfer());
            while (true) {
                var cmd = SCANNER.nextLine().toUpperCase();
                if (cmd.equals("STOP")) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
