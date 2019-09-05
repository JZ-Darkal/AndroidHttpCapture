package cn.darkal.networkdiagnosis;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by xuzhou on 2016/7/31.
 * VpnService方案暂时搁置
 */
public class MyVpnService extends VpnService {
    private String mServerAddress;
    private String mServerPort;
    private byte[] mSharedSecret;
    private PendingIntent mConfigureIntent;

    private Handler mHandler;
    private Thread mThread;

    private ParcelFileDescriptor mInterface;
    private String mParameters;

    public MyVpnService() {
        this.mHandler = new Handler();
    }

    private void setupVpn() {
        ParcelFileDescriptor parcelFileDescriptor;
        Log.i("~~~", "VpnService: try to setup VPN.");
        Builder builder = new Builder();
        builder.setSession("firewall");
        builder.addAddress("10.0.8.1", 32);
        builder.addRoute("0.0.0.0", 0);
        try {
            parcelFileDescriptor = builder.establish();
            if (parcelFileDescriptor != null) {
                Log.i("~~~", "VpnService: call ProxyWorker.start()");
                // Packets to be sent are queued in this input stream.
                FileInputStream in = new FileInputStream(parcelFileDescriptor.getFileDescriptor());

                // Packets received need to be written to this output stream.
                FileOutputStream out = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());

                // Allocate the buffer for a single packet.
                ByteBuffer packet = ByteBuffer.allocate(32767);
//                ...
                // Read packets sending to this interface
//                int length = in.read(packet.array());
//                ...
                // Write response packets back
//                out.write(packet.array(), 0, length);
                //                ProxyWorker.getInstance().start(((VpnService) this), v2);
                int timer = 0;

                // We keep forwarding packets till something goes wrong.
                while (true) {
                    // Assume that we did not make any progress in this iteration.
                    boolean idle = true;

                    // Read the outgoing packet from the input stream.
                    int length = in.read(packet.array());
                    if (length > 0) {
                        // Write the outgoing packet to the tunnel.
                        packet.limit(length);
//                        tunnel.write(packet);
                        packet.clear();

                        // There might be more outgoing packets.
                        idle = false;

                        // If we were receiving, switch to sending.
                        if (timer < 1) {
                            timer = 1;
                        }
                    }

                    // Read the incoming packet from the tunnel.
//                    length = tunnel.read(packet);
                    if (length > 0) {
                        // Ignore control messages, which start with zero.
                        if (packet.get(0) != 0) {
                            // Write the incoming packet to the output stream.
                            out.write(packet.array(), 0, length);
                        }
                        packet.clear();

                        // There might be more incoming packets.
                        idle = false;

                        // If we were sending, switch to receiving.
                        if (timer > 0) {
                            timer = 0;
                        }
                    }

                    // If we are idle or waiting for the network, sleep for a
                    // fraction of time to avoid busy looping.
                    if (idle) {
                        Thread.sleep(100);

                        // Increase the timer. This is inaccurate but good enough,
                        // since everything is operated in non-blocking mode.
                        timer += (timer > 0) ? 100 : -100;

                        // We are receiving for a long time but not sending.
                        if (timer < -15000) {
                            // Send empty control messages.
                            packet.put((byte) 0).limit(1);
                            for (int i = 0; i < 3; ++i) {
                                packet.position(0);
//                                tunnel.write(packet);
                            }
                            packet.clear();

                            // Switch to sending.
                            timer = 1;
                        }

                        // We are sending for a long time but not receiving.
                        if (timer > 20000) {
                            throw new IllegalStateException("Timed out");
                        }
                    }
                }

            }
        } catch (final IllegalStateException e) {
            Log.i("~~~", "VpnService: builder.establish() failed.");
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyVpnService.this, "Cannot establish VPN (" + e.toString() + ")",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopVpn() {
        Log.i("~~~", "VpnService: call ProxyWorker.stop()");
//        ProxyWorker.getInstance().stop();
    }

    @Override
    public void onRevoke() {
        stopVpn();
        super.onRevoke();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                setupVpn();
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean run(InetSocketAddress server) throws Exception {
        DatagramChannel tunnel = null;
        boolean connected = false;
        try {
            // Create a DatagramChannel as the VPN tunnel.
            tunnel = DatagramChannel.open();

            // Protect the tunnel before connecting to avoid loopback.
            if (!protect(tunnel.socket())) {
                throw new IllegalStateException("Cannot protect the tunnel");
            }

            // Connect to the server.
            tunnel.connect(server);

            // For simplicity, we use the same thread for both reading and
            // writing. Here we put the tunnel into non-blocking mode.
            tunnel.configureBlocking(false);

            // Authenticate and configure the virtual network interface.
            handshake(tunnel);

            // Now we are connected. Set the flag and show the message.
            connected = true;
            mHandler.sendEmptyMessage(R.string.connected);

            // Packets to be sent are queued in this input stream.
            FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());

            // Packets received need to be written to this output stream.
            FileOutputStream out = new FileOutputStream(mInterface.getFileDescriptor());

            // Allocate the buffer for a single packet.
            ByteBuffer packet = ByteBuffer.allocate(32767);

            // We use a timer to determine the status of the tunnel. It
            // works on both sides. A positive value means sending, and
            // any other means receiving. We start with receiving.
            int timer = 0;

            // We keep forwarding packets till something goes wrong.
            while (true) {
                // Assume that we did not make any progress in this iteration.
                boolean idle = true;

                // Read the outgoing packet from the input stream.
                int length = in.read(packet.array());
                if (length > 0) {
                    // Write the outgoing packet to the tunnel.
                    packet.limit(length);
                    tunnel.write(packet);
                    packet.clear();

                    // There might be more outgoing packets.
                    idle = false;

                    // If we were receiving, switch to sending.
                    if (timer < 1) {
                        timer = 1;
                    }
                }

                // Read the incoming packet from the tunnel.
                length = tunnel.read(packet);
                if (length > 0) {
                    // Ignore control messages, which start with zero.
                    if (packet.get(0) != 0) {
                        // Write the incoming packet to the output stream.
                        out.write(packet.array(), 0, length);
                    }
                    packet.clear();

                    // There might be more incoming packets.
                    idle = false;

                    // If we were sending, switch to receiving.
                    if (timer > 0) {
                        timer = 0;
                    }
                }

                // If we are idle or waiting for the network, sleep for a
                // fraction of time to avoid busy looping.
                if (idle) {
                    Thread.sleep(100);

                    // Increase the timer. This is inaccurate but good enough,
                    // since everything is operated in non-blocking mode.
                    timer += (timer > 0) ? 100 : -100;

                    // We are receiving for a long time but not sending.
                    if (timer < -15000) {
                        // Send empty control messages.
                        packet.put((byte) 0).limit(1);
                        for (int i = 0; i < 3; ++i) {
                            packet.position(0);
                            tunnel.write(packet);
                        }
                        packet.clear();

                        // Switch to sending.
                        timer = 1;
                    }

                    // We are sending for a long time but not receiving.
                    if (timer > 20000) {
                        throw new IllegalStateException("Timed out");
                    }
                }
            }
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            Log.e("~~~", "Got " + e.toString());
        } finally {
            try {
                tunnel.close();
            } catch (Exception e) {
                // ignore
            }
        }
        return connected;
    }

    private void handshake(DatagramChannel tunnel) throws Exception {
        // To build a secured tunnel, we should perform mutual authentication
        // and exchange session keys for encryption. To keep things simple in
        // this demo, we just send the shared secret in plaintext and wait
        // for the server to send the parameters.

        // Allocate the buffer for handshaking.
        ByteBuffer packet = ByteBuffer.allocate(1024);

        // Control messages always start with zero.
        packet.put((byte) 0).put(mSharedSecret).flip();

        // Send the secret several times in case of packet loss.
        for (int i = 0; i < 3; ++i) {
            packet.position(0);
            tunnel.write(packet);
        }
        packet.clear();

        // Wait for the parameters within a limited time.
        for (int i = 0; i < 50; ++i) {
            Thread.sleep(100);

            // Normally we should not receive random packets.
            int length = tunnel.read(packet);
            if (length > 0 && packet.get(0) == 0) {
                configure(new String(packet.array(), 1, length - 1).trim());
                return;
            }
        }
        throw new IllegalStateException("Timed out");
    }

    private void configure(String parameters) throws Exception {
        // If the old interface has exactly the same parameters, use it!
        if (mInterface != null && parameters.equals(mParameters)) {
            Log.i("~~~~", "Using the previous interface");
            return;
        }

        // Configure a builder while parsing the parameters.
        Builder builder = new Builder();
        for (String parameter : parameters.split(" ")) {
            String[] fields = parameter.split(",");
            try {
                switch (fields[0].charAt(0)) {
                    case 'm':
                        builder.setMtu(Short.parseShort(fields[1]));
                        break;
                    case 'a':
                        builder.addAddress(fields[1], Integer.parseInt(fields[2]));
                        break;
                    case 'r':
                        builder.addRoute(fields[1], Integer.parseInt(fields[2]));
                        break;
                    case 'd':
                        builder.addDnsServer(fields[1]);
                        break;
                    case 's':
                        builder.addSearchDomain(fields[1]);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Bad parameter: " + parameter);
            }
        }

        // Close the old interface since the parameters have been changed.
        try {
            mInterface.close();
        } catch (Exception e) {
            // ignore
        }

        // Create a new interface using the builder and save the parameters.
        mInterface = builder.setSession(mServerAddress)
                .setConfigureIntent(mConfigureIntent)
                .establish();
        mParameters = parameters;
        Log.i("~~~", "New interface: " + parameters);
    }
}
