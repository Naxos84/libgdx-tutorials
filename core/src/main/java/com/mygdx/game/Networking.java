package com.mygdx.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Networking extends ApplicationAdapter {

    // Pick a resolution that is 16:9 but not unreadibly small
    public final static float VIRTUAL_SCREEN_HEIGHT = 960;
    public final static float VIRTUAL_SCREEN_WIDTH = 540;

    private Label labelDetails;
    private Label labelMessage;
    private TextButton button;
    private TextArea textIPAddress;
    private TextArea textMessage;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Skin skin;
    private Stage stage;

    private Viewport viewPort;

    @Override
    public void create() {
        camera = new OrthographicCamera(Gdx.graphics.getPrimaryMonitor().virtualX, Gdx.graphics.getPrimaryMonitor().virtualY);
        batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        stage = new Stage();

        // Wire the stage to receive input, as we are using Scene2d in this example
        Gdx.input.setInputProcessor(stage);

        // The following code loops through the available network interfaces
        // Keep in mind, there can be multiple interfaces per device, for example
        // one per NIC, one per active wireless and the loopback
        // In this case we only care about IPv4 address ( x.x.x.x format )
        final List<String> addresses = new ArrayList<String>();
        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (final NetworkInterface ni : Collections.list(interfaces)) {
                for (final InetAddress address : Collections.list(ni.getInetAddresses())) {
                    if (address instanceof Inet4Address) {
                        addresses.add(address.getHostAddress());
                    }
                }
            }
        } catch (final SocketException e) {
            Gdx.app.log("ERROR", "Failed to get the network interface", e);
        }

        final StringBuilder ipAddressesBuilder = new StringBuilder();
        for (final String address : addresses) {
            ipAddressesBuilder.append(address).append("\n");
        }

        // Now setup our scene UI

        final Table tableGroup = new Table(skin).pad(5);
        // tableGroup.setDebug(true);
        tableGroup.setFillParent(true);

        // Vertical group groups contents vertically. I suppose that was probably pretty obvious
        // final VerticalGroup verticalGroup = new VerticalGroup().space(3).pad(5).fill();// .space(2).pad(5).fill();//.space(3).reverse().fill();
        // Set the bounds of the group to the entire virtual display
        // verticalGroup.setBounds(0, 0, VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT);

        // Create our controls
        final Label detailsText = new Label("IP-Adressen", skin);
        labelDetails = new Label(ipAddressesBuilder.toString(), skin);
        final Label lastMessage = new Label("Last Message:", skin);
        labelMessage = new Label("None", skin);
        button = new TextButton("Send message", skin);
        final Label receiverText = new Label("Empfänger", skin);
        textIPAddress = new TextArea("", skin);
        final Label messageText = new Label("Message to send:", skin);
        textMessage = new TextArea("", skin);

        // Add them to scene
        tableGroup.add(detailsText);
        tableGroup.add(labelDetails);
        tableGroup.row();
        tableGroup.add(lastMessage);
        tableGroup.add(labelMessage);
        tableGroup.row();
        tableGroup.add(receiverText);
        tableGroup.add(textIPAddress);
        tableGroup.row();
        tableGroup.add(messageText);
        tableGroup.add(textMessage);
        tableGroup.row();
        tableGroup.add(button).colspan(2);

        // Add scene to stage
        stage.addActor(tableGroup);

        // Setup a viewport to map screen to a 480x640 virtual resolution
        // As otherwise this is way too tiny on my 1080p android phone.
        // stage.setViewport(viewPort);
        // stage.getCamera().viewportWidth = VIRTUAL_SCREEN_WIDTH;
        // stage.getCamera().viewportHeight = VIRTUAL_SCREEN_HEIGHT;
        viewPort = new StretchViewport(VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT, camera);
        viewPort.apply(true);
        stage.setViewport(viewPort);
        stage.getCamera().position.set(VIRTUAL_SCREEN_WIDTH / 2, VIRTUAL_SCREEN_HEIGHT / 2, 0);

        // Now we create a thread that will listen for incoming socket connections
        new Thread(new Runnable() {

            @Override
            public void run() {
                final ServerSocketHints serverSocketHint = new ServerSocketHints();
                // 0 means no timeout. Probably not the greatest idea in production!
                serverSocketHint.acceptTimeout = 0;

                // Create the socket server using TCP protocol and listening on 9021
                // Only one app can listen to a port at a time, keep in mind many ports are reserved
                // especially in the lower numbers ( like 21, 80, etc )
                final ServerSocket serverSocket = Gdx.net.newServerSocket(Protocol.TCP, 9021, serverSocketHint);

                // Loop forever
                while (true) {
                    // Create a socket
                    final Socket socket = serverSocket.accept(null);

                    // Read data from the socket into a BufferedReader
                    final BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    try {
                        // Read to the next newline (\n) and display that text on labelMessage
                        labelMessage.setText(buffer.readLine());
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start(); // And, start the thread running

        // Wire up a click listener to our button
        button.addListener(new ClickListener() {
            @Override
            public void clicked(final InputEvent event, final float x, final float y) {

                // When the button is clicked, get the message text or create a default string value
                final String textToSend;
                if (textMessage.getText().length() == 0) {
                    textToSend = "Doesn't say much but likes clicking buttons\n";
                } else {
                    textToSend = textMessage.getText() + "\n"; // Brute for a newline so readline gets a line
                }

                final SocketHints socketHints = new SocketHints();
                // Socket will time our in 4 seconds
                socketHints.connectTimeout = 4000;
                // create the socket and connect to the server entered in the text box ( x.x.x.x format ) on port 9021
                final Socket socket = Gdx.net.newClientSocket(Protocol.TCP, textIPAddress.getText(), 9021, socketHints);
                try {
                    // write our entered message to the stream
                    socket.getOutputStream().write(textToSend.getBytes());
                } catch (final IOException e) {
                    Gdx.app.log("ERROR", "Error writing message", e);
                }
            }
        });
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        stage.draw();
        batch.end();
    }

    @Override
    public void resize(final int width, final int height) {
        viewPort.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }
}
