package fr.legabi.discordRPC;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    public static void main(String[] args) {
        DiscordRPC lib = DiscordRPC.INSTANCE;
        DiscordRichPresence presence = new DiscordRichPresence();
        String applicationId = "1002538267809423390";

        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");

        lib.Discord_Initialize(applicationId, handlers, true, null);

        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details   = "Nom du projet";
        presence.state = "Développement";
        presence.partySize = 1;
        presence.partyMax  = 100;
        presence.largeImageKey = "icon";
        presence.largeImageText = "Developped by le_gabi#1866";
        lib.Discord_UpdatePresence(presence);

        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    lib.Discord_Shutdown();
                    break;
                }
            }
        }, "RPC-Callback-Handler");
        t.start();

        JFrame frame = new JFrame("Unreal Engine RPC");
        GridLayout frameLayout = new GridLayout(2, 1);
        frame.setLayout(frameLayout);

        JPanel top = new JPanel();
        GridLayout topLayout = new GridLayout(2, 2);
        top.setLayout(topLayout);

        JPanel bottom = new JPanel();
        GridLayout botLayout = new GridLayout(2, 3);
        bottom.setLayout(botLayout);


        // Details
        JLabel detailsLabel = new JLabel("Details");
        detailsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JTextField detailsText = new JTextField(presence.details);

        JTextField partySizeText = new JTextField(String.valueOf(presence.partySize));
        JLabel partyLabel = new JLabel("of"); partyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JTextField partyMaxText = new JTextField(String.valueOf(presence.partyMax));

        JButton submit = new JButton("Update Presence");
        submit.addActionListener(e -> {
            presence.details = detailsText.getText();
            try {
                presence.partySize = Integer.parseInt(partySizeText.getText());
            } catch (Exception ignored) {}
            try {
                presence.partyMax  = Integer.parseInt(partyMaxText.getText());
            } catch (Exception ignored) {} // if text isn't a number, ignore it

            lib.Discord_UpdatePresence(presence);
        });

        top.add(detailsLabel);
        top.add(detailsText);

        bottom.add(partySizeText);
        bottom.add(partyLabel);
        bottom.add(partyMaxText);
        bottom.add(new JPanel());
        bottom.add(submit);
        bottom.add(new JPanel()); // dummy components to center the update button

        frame.add(top);
        frame.add(bottom);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().setVisible(false);
                t.interrupt();
            }
        });

        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
