package carrom.umundo.carromgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import org.umundo.core.Discovery;
import org.umundo.core.Message;
import org.umundo.core.Node;
import org.umundo.core.Publisher;
import org.umundo.core.RTPSubscriberConfig;
import org.umundo.core.Receiver;
import org.umundo.core.Subscriber;


public class CarromGame_umundo extends Activity {

    private static final String TAG = CarromGame_umundo.class.getSimpleName();
    Discovery disc;
    Node node;
    Publisher gamePublisher;
    Subscriber gameSubscriber;
    Thread testPublishing;
    Boolean contentView;

    public class TestPublishing implements Runnable {

        @Override
        public void run() {
            String message = "Playing";
            if (gamePublisher != null) {
                Log.v("CarromGame: umundo", "run" );
                gamePublisher.send(message.getBytes());
                try {
                    Thread.sleep(1000);
                    Log.v("CarromGame:","sleep");
                } catch (InterruptedException e) {
                    Log.v("CarromGame: exception","in run");
                    e.printStackTrace();
                }
                CarromGame_umundo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //tv.setText(tv.getText() + "o");
                        Log.v("CarromGame: umundo","context view");
                        contentView = true;
                    }
                });
            }
        }
    }

    public class TestReceiver extends Receiver {
        public void receive(Message msg) {
            Log.v("CarromGame: umundo", msg.toString());
            for (String key : msg.getMeta().keySet()) {
                Log.v("CarromGame: umundo", key + ": " + msg.getMeta(key));
            }
            CarromGame_umundo.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tv.setText(tv.getText() + "i");
                    contentView = true;
                }
            });
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
        gamePublisher = new Publisher("CarromPub");
        node.addPublisher(gamePublisher);

        gameSubscriber = new Subscriber("CarromPub", new TestReceiver());
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
