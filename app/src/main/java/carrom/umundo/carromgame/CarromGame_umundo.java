package carrom.umundo.carromgame;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import org.umundo.core.Discovery;
import org.umundo.core.Message;
import org.umundo.core.Node;
import org.umundo.core.Publisher;
import org.umundo.core.RTPSubscriberConfig;
import org.umundo.core.Receiver;
import org.umundo.core.Subscriber;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import carrom.umundo.renderer.RenderThread;


public class CarromGame_umundo extends Activity {
    static Context context;
    private static final String TAG = CarromGame_umundo.class.getSimpleName();
    Discovery disc;
    Node node;
    public static Publisher gamePublisher;
    Subscriber gameSubscriber;
    Thread testPublishing;
    Boolean contentView;
    RenderThread renderThread;

    public class TestPublishing extends Application implements Runnable {

        @Override
        public void run() {
            String message = "Playing";
            while (gamePublisher != null) {
                Log.v("CarromGame: umundo", "run");
                //gamePublisher.send(message.getBytes());
                try {
                    Thread.sleep(1000);
                    Log.v("CarromGame:", "sleep");
                } catch (InterruptedException e) {
                    Log.v("CarromGame: exception", "in run");
                    e.printStackTrace();
                }
                CarromGame_umundo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //tv.setText(tv.getText() + "o");
                        Log.v("CarromGame: umundo", "context view o");
                        //contentView = true;

                        renderThread = new RenderThread(new displayComponents().getHolder(), new MainGamePanel(CarromGame_umundo.this));
                        renderThread.run();
                    }
                });
            }
        }
    }

    public class TestReceiver extends Receiver {
        byte[] msgb;
        String type = null;

        public void receive(Message msg) {
            msgb = msg.getData();
            type = msg.getMeta("CLASS");
            Log.v("CarromGame:umundo value", "TYPE = " + type);
            for (String key : msg.getMeta().keySet()) {
                Log.v("CarromGame: umundo", key + ": " + msg.getMeta(key) + " value for class" + msg.getMeta("CLASS"));
            }

            CarromGame_umundo.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tv.setText(tv.getText() + "i");
                    Log.v("CarromGame: umundo", "context view i before");


                    ObjectInputStream is = null;
                    if ((type != null) && (type != "")) {
                        try {
                            type = null;
                            ByteArrayInputStream in = new ByteArrayInputStream(msgb);
                            is = new ObjectInputStream(in);
                            is.readObject();
                            Log.v("CarromGame: umundo", "inside try block " + is.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.v("CarromGame: umundo", "context view i after" + is.toString());
                        renderThread = new RenderThread(new displayComponents().getHolder(), new MainGamePanel(CarromGame_umundo.this));
                        renderThread.run();
                    }
                }
            });
        }
    }

    public class displayComponents extends SurfaceView implements SurfaceHolder.Callback {
        public displayComponents() {
            super(getApplicationContext());
            this.getHolder().addCallback(this);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            renderThread.running = true;
            renderThread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            while (true) {
                try {
                    renderThread.join();
                    break;
                } catch (InterruptedException e) {
                }
            }

        }
    }

    //Called when activity is created for the first time
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // To check if there is any active Wifi connection
        if (!wifi.isWifiEnabled()) {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
        //Allow the application to receive Wifi Multicast packets.
        WifiManager.MulticastLock mcLock = wifi.createMulticastLock("gameLock");
        mcLock.acquire();
        System.loadLibrary("umundoNativeJava");
        disc = new Discovery(Discovery.DiscoveryType.MDNS);
        node = new Node();
        disc.add(node);

        Log.v("CarromGame:", "on create");
        gamePublisher = new Publisher("Carrom"); //Carrom: channel Name
        node.addPublisher(gamePublisher);

        //gamePublisher.send();

        gameSubscriber = new Subscriber("Carrom", new TestReceiver());
        node.addSubscriber(gameSubscriber);

        testPublishing = new Thread(new TestPublishing());
        //contentView();
        testPublishing.start();

        Log.v("CarromGame: umundo", "inside context view");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setTitle(title);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        MainGamePanel.PANEL_HEIGHT = this.getWindowManager().getDefaultDisplay().getHeight();
        MainGamePanel.PANEL_WIDTH = this.getWindowManager().getDefaultDisplay().getWidth();
        setContentView(new MainGamePanel(this));

        //mcLock.release(); //TODO: Look into this in more detail
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_carrom_game_umundo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
