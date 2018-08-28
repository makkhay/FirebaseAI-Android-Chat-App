package com.makkhay.chat.ui;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
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
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.auth.FirebaseAuth;
import com.makkhay.chat.R;
import com.makkhay.chat.model.Message;
import com.makkhay.chat.util.Config;
import com.makkhay.chat.util.ExpandableListAdapter;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
    private MessageListAdapter mAdapter;
    private ArrayList<Message> messageList;
    public static final String TAG = "MainActivity";
    ImageView imageView;
    private LottieAnimationView animationView;
    private TextView chatTV;
    private  Intent changeActivity;
    View view_Group;
    private DrawerLayout mDrawerLayout;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    int unicode = 0x1F4AC;
    //Icons for expandable menu
    public static int[] icon = { R.drawable.baseline_dashboard_black_18dp, R.drawable.baseline_chat_black_18dp};

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableList.setIndicatorBounds(expandableList.getRight()- 80, expandableList.getWidth());
        } else {
            expandableList.setIndicatorBoundsRelative(expandableList.getRight()- 80, expandableList.getWidth());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // initialize lottie
        animationView = (LottieAnimationView) findViewById(R.id.lottieClear);
        // will show targetview tutorial
        showTutorial();
        chatTV = (TextView) findViewById(R.id.chat_title);
        chatTV.setText("AI chat  "+ getEmojiByUnicode(unicode));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setItemIconTintList(null);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // populate data to expandable ListView
        prepareListData();
        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);
        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView,
                                        View view,
                                        int groupPosition,
                                        int childPosition, long id) {
                // Menu 1; do something on click
                if(childPosition==0 && groupPosition == 0){
                    changeActivity = new Intent(ChatActivity.this, DashboardActivity.class);
                    startActivity(changeActivity);
                } else if(childPosition == 1 && groupPosition == 0 ){

                    changeActivity = new Intent(ChatActivity.this, PieChartActivity.class);
                    startActivity(changeActivity);
                } else if(childPosition == 2 && groupPosition == 0 ){

                    changeActivity = new Intent(ChatActivity.this, BarChartActivity.class);
                    startActivity(changeActivity);
                }

                //Menu 2; do something on click
                if(childPosition==0 && groupPosition == 1){
                    // Show a dialog when the button is pressed
                    String[] items = getResources().getStringArray(R.array.food);
                    new LovelyChoiceDialog(ChatActivity.this)
                            .setTopColorRes(R.color.colorPrimaryDark)
                            .setTitle("Send money to ")
                            .setIcon(R.drawable.ic_add_friend)
                            .setItemsMultiChoice(items, new LovelyChoiceDialog.OnItemsSelectedListener<String>() {
                                @Override
                                public void onItemsSelected(List<Integer> positions, List<String> items) {

                                }
                            })
                            .setConfirmButtonText("Confirm")
                            .show();
                } else if(childPosition == 1 && groupPosition == 1 ){
                    // Show a dialog when the button is pressed
                    new LovelyInfoDialog(ChatActivity.this)
                            .setTopColorRes(R.color.colorPrimaryDark)
                            .setIcon(R.drawable.ic_menu_send)
                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                            .setTitle("Mute Chat ?")
                            .setMessage("By doing so all notification will be muted. ")
                            .show();

                }
                //Set background color when an item is selected
                view.setSelected(true);
                view_Group = view;
                view_Group.setBackgroundColor(Color.parseColor("#d4dce6"));
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //Log.d("DEBUG", "heading clicked");
                return false;
            }
        });

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

        mAdapter = new MessageListAdapter(this, messageList);
        listViewMessages.setAdapter(mAdapter);
        // Do something when an item from chat bubbles is pressed
        listViewMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Message mssg = messageList.get(i);
                String clipboardText = mssg.getMessage();
                Log.d(TAG, "onItemClick: " + mssg.getMessage());
                Toast.makeText(getApplicationContext(),"Copied Text to Clipboard ",Toast.LENGTH_LONG).show();

                // Save the clicked text to clipboard for copy pasting
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("My Clipboard", clipboardText);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
            }
        });

        // Send message when button is clicked
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

    /**
     * A method to send message to server.
     * @param msg is a string, which is sent to chatbot api for the messaging purposes.
     */
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

    /**
     * This is a method to check if the request code sent while sending image and video is met.
     * If the code matches then do something
     * @param reqCode
     * @param resultCode
     * @param data
     */
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
                Toast.makeText(getApplicationContext(),"Success!! ",Toast.LENGTH_SHORT).show();

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

    /**
     * Util adapter class to display the data.
     * It's an interface between the data source and the layout.
     */
    public class MessageListAdapter extends BaseAdapter {

        private Context context;
        private List<Message> messagesItems;

        public MessageListAdapter(Context context, List<Message> navDrawerItems) {
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
            return convertView;
        }
    }

    /**
     * Checks if internet is connected
     * @param context
     * @return
     */
    private boolean networkIsAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /**
     * Play a sound when message is recieved.
     */
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * This method will display the targetview tutorial.
     */
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

    /**
     * This a method to display the lottie animation
     * @param animFileName is passed on to this method so that users can select their desired file
     */
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

    /**
     * Method to display the emoji in TextView
     * @param unicode
     * @return
     */
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    /**
     * This is a method to populate the list data.
     * The Listview is populated with a static list to display menu
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        // Adding data header
        listDataHeader.add("Dashboard");
        listDataHeader.add("Chat");

        // Adding child data
        List<String> heading1 = new ArrayList<String>();
        heading1.add("Show all chart");
        heading1.add("Show pie chart");
        heading1.add("Show bar chart");
        List<String> heading2 = new ArrayList<String>();
        heading2.add("Transfer money");
        heading2.add("Mute chat");

        listDataChild.put(listDataHeader.get(0), heading1);// Header, Child data
        listDataChild.put(listDataHeader.get(1), heading2);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

}
