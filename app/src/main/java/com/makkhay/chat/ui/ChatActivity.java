package com.makkhay.chat.ui;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.auth.FirebaseAuth;
import com.makkhay.chat.R;
import com.makkhay.chat.model.Message;
import com.makkhay.chat.util.Config;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;



public class ChatActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private String name="";

    private ImageButton btnSend, btnPick;
    private EditText inputMsg;
    private ListView listViewMessages;

    private static int RESULT_LOAD_IMAGE = 1;
    Realm realm;
    Context context;
    RequestQueue queue;
    LayoutInflater inflater;
    private MessagesListAdapter mAdapter;
    private ArrayList<Message> messageList;
    public static final String TAG = "MainActivity";
    ImageView imageView;
    private LottieAnimationView animationView;
    private TextView chatTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        animationView = (LottieAnimationView) findViewById(R.id.lottieClear);
        chatTV = (TextView) findViewById(R.id.chat_title);

        showTutorial();

        int unicode = 0x1F4AC;

        chatTV.setText("AI Chat  "+ getEmojiByUnicode(unicode));



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnPick = (ImageButton) findViewById(R.id.pick);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view);


        context = getApplicationContext();
        queue = Volley.newRequestQueue(context);
        inflater = getLayoutInflater();
        messageList = new ArrayList<>();

        name ="You";
        realm = Realm.getInstance(context);

        final RealmResults<Message> results = realm.where(Message.class).findAll();
        if(results.size() > 0)
        {
            realm.beginTransaction();
            for (int i = 0; i < results.size(); i++) {
                messageList.add(results.get(i));

            }
            realm.commitTransaction();
        }


        mAdapter = new MessagesListAdapter(this, messageList);
        listViewMessages.setAdapter(mAdapter);
        listViewMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Message mssg = messageList.get(i);
                String clipboardText = mssg.getMessage();
                Log.d(TAG, "onItemClick: " + mssg.getMessage());
                Toast.makeText(getApplicationContext(),"Copied Text to Clipboard ",Toast.LENGTH_LONG).show();

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("My Clipboard", clipboardText);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ChatActivity.this, btnPick);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        int id = item.getItemId();

                        if(id == R.id.send_Pic){

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select a Picture"), RESULT_LOAD_IMAGE);

                        } else if(id == R.id.send_video){
                            Intent intent = new Intent();
                            intent.setType("video/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select a Video"), RESULT_LOAD_IMAGE);
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu

            }
        });


    }

    private void sendMessage()
    {
        String msg = inputMsg.getText().toString().trim();

        if( msg.length()==0) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!networkIsAvailable(context))
        {
            Toast.makeText(getApplicationContext(), "Connect to internet", Toast.LENGTH_SHORT).show();
            return;
        }
        inputMsg.setText("");

        realm.beginTransaction();
        Message message = realm.createObject(Message.class);
        message.setSuccess(1);
        message.setChatBotName(name);
        message.setChatBotID(Integer.parseInt(Config.chatBotID));
        message.setMessage(msg);
        message.setEmotion(null);
        message.setSelf(true);
        realm.commitTransaction();

        messageList.add(message);
        mAdapter.notifyDataSetChanged();
        msg = msg.replace(" ","+");
        sendMessageToServer(msg);
    }

    private void sendMessageToServer(final String msg)
    {
        String url = Config.URL+"?apiKey="+Config.apiKey+"&message="+msg+"&chatBotID="+Config.chatBotID+"&externalID="+Config.externalID;
        JsonObjectRequest jsonObjectRequest= new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null && response.length() > 0) {
                            try {
                                if(response.getInt("success")==1)
                                {
                                    realm.beginTransaction();
                                    Message message = realm.createObject(Message.class);
                                    JSONObject m = response.getJSONObject("message");
                                    message.setChatBotName(m.optString("chatBotName", ""));
                                    message.setChatBotID(m.optInt("chatBotID"));
                                    message.setMessage(m.optString("message", ""));
                                    message.setEmotion(m.optString("emotions",null));
                                    message.setSelf(false);
                                    realm.commitTransaction();
                                    appendMessage(message);
                                }
                                else
                                {

                                    String error=response.getString("errorMessage");
                                    Toast.makeText(getApplicationContext(), "Error: "+error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // errors
                            Toast.makeText(getApplicationContext(), "Retry Later ", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                        Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        );

        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(policy);
        jsonObjectRequest.setTag(TAG);
        queue.add(jsonObjectRequest);
    }


    public class MessagesListAdapter extends BaseAdapter {

        private Context context;
        private List<Message> messagesItems;

        public MessagesListAdapter(Context context, List<Message> navDrawerItems) {
            this.context = context;
            this.messagesItems = navDrawerItems;
        }

        @Override
        public int getCount() {
            return messagesItems.size();
        }

        @Override
        public Object getItem(int position) {
            return messagesItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Message m = messagesItems.get(position);

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (messagesItems.get(position).isSelf()) {
                convertView = mInflater.inflate(R.layout.list_item_message_right,
                        null);
            } else {
                convertView = mInflater.inflate(R.layout.list_item_message_left,
                        null);
            }

            TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
            TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);

            txtMsg.setText(m.getMessage());
            lblFrom.setText(m.getChatBotName());

            imageView = (ImageView) convertView.findViewById(R.id.pictureView);
//            imageView.setImageResource(R.mipmap.ic_email);

            return convertView;
        }
    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
                displayAnimation("loading.json");

                Toast.makeText(getApplicationContext(),"URI is: "+ imageUri.toString(),Toast.LENGTH_SHORT).show();





            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getApplicationContext(), "You haven't picked anything",Toast.LENGTH_LONG).show();
        }
    }


    private void appendMessage(final Message m) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                messageList.add(m);
                mAdapter.notifyDataSetChanged();
                // Playing device's notification
                playBeep();
            }
        });
    }


    private boolean networkIsAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void playBeep() {

        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    @Override
    public void onDestroy() {
        animationView.cancelAnimation();
        animationView = null;
        super.onDestroy();
        realm.close();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),"Signed out", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {

            Intent a = new Intent(this, DashboardActivity.class);
            startActivity(a);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showTutorial(){

        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.inputMsg), "Get Started", "Type your message here and the AI bot will reply you ")
                        // All options below are optional
                        .outerCircleColor(R.color.lime)      // Specify a color for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(24)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(14)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        displayAnimation("welcome.json");
                    }
                });



    }

    private void displayAnimation(final String animFileName){
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                LottieComposition.Factory.fromAssetFileName(getApplicationContext(), animFileName, new OnCompositionLoadedListener() {
                    @Override
                    public void onCompositionLoaded(@Nullable LottieComposition composition) {
                        animationView.setComposition(composition);
                        animationView.playAnimation();
                    }
                });

                animationView.addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animationView.setVisibility(View.GONE);

                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        animationView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }






}
