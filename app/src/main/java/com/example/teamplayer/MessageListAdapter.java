package com.example.teamplayer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MessageListAdapter  extends ArrayAdapter<message_item> {
    //the list values in the List of type hero
    List<message_item> messagetList;
    //private String email;
    private FirebaseAuth mAuth;

    private static final String TAG = "accept_request";
    private static final String ACTIVITIES_COLLECTION = "Activities/";

    //activity context
    Context context;
    int resource;

    //constructor initializing the values
    public MessageListAdapter(Context context, int resource, List<message_item> messagetList) {
        super(context, resource, messagetList);
        this.context = context;
        this.resource = resource;
        this.messagetList = messagetList;
    }

    /**
     * The function returns the ListView Item as a View
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        message_item messageItem = messagetList.get(position);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view;

        //Check if current user is the message sender
        if (messageItem.is_sender){

            //Set message to sender layout
            view = layoutInflater.inflate(R.layout.send_message, null, false);
            TextView message = view.findViewById(R.id.text_message_body);
            TextView time = view.findViewById(R.id.text_message_time);
            message.setText(messageItem.message);
            time.setText(messageItem.time);

        }else {
            //Set message to receive layout

            view = layoutInflater.inflate(resource, null, false);

            TextView Name = view.findViewById(R.id.text_message_name);
            TextView message = view.findViewById(R.id.text_message_body);
            TextView time = view.findViewById(R.id.text_message_time);

            Name.setText(messageItem.sender);
            message.setText(messageItem.message);
            time.setText(messageItem.time);
        }

        return view;
    }
}

