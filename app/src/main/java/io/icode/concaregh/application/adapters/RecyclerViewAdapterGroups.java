package io.icode.concaregh.application.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.chatApp.GroupMessageActivity;
import io.icode.concaregh.application.models.Admin;
import io.icode.concaregh.application.models.GroupChats;
import io.icode.concaregh.application.models.Groups;


public class RecyclerViewAdapterGroups extends RecyclerView.Adapter<RecyclerViewAdapterGroups.ViewHolder> {

    Admin admin;

    // variable to store admin uid from sharePreference
    //String admin_uid;

    private Context mCtx;
    private List<Groups> mGroups;
    private boolean isChat;

    // string variable to contain lastMessage from user
    private String theLastMessage;

    public RecyclerViewAdapterGroups(Context mCtx, List<Groups> mGroups, boolean isChat){
        this.mCtx = mCtx;
        this.mGroups = mGroups;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_items_groups,parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // gets the positions of the all users
        final Groups groups = mGroups.get(position);

        // sets username to the text of the textView
        holder.groupName.setText(groups.getGroupName());

        if(groups.getGroupIcon() == null){
            // loads the default placeholder into ImageView if ImageUrl is null
            holder.groupIcon.setImageResource(R.mipmap.group_icon);
        }
        else{
            // loads users image into the ImageView
            Glide.with(mCtx).load(groups.getGroupIcon()).into(holder.groupIcon);
        }


        // calling the lastMessage method
        if(isChat){
            //lastMessage(users.getUid(),holder.last_msg);
        }
        else {
            holder.last_msg.setVisibility(View.GONE);
        }

        // code to check if user is online
        if(isChat){

        }
        else{

        }

        // onClickListener for view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // passing adminUid as a string to the MessageActivity
                Intent intent = new Intent(mCtx,GroupMessageActivity.class);
                intent.putExtra("group_name", groups.getGroupName());
                intent.putExtra("group_icon", groups.getGroupIcon());
                intent.putExtra("date_created", groups.getDateCreated());
                intent.putExtra("time_created", groups.getTimeCreated());
                // passing an array of the ids of the group members
                intent.putStringArrayListExtra("usersIds",(ArrayList<String>)groups.getGroupMembersIds());
                mCtx.startActivity(intent);

                // storing string in sharePreference

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mCtx).edit();
                editor.putString("group_name",groups.getGroupName());
                editor.putString("group_icon", groups.getGroupIcon());
                //editor.putStringSet("icons",(Set<String>)groups.getGroupMembersIds());
                editor.apply();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        Admin admin;

        CircleImageView groupIcon;
        TextView groupName;
        TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            admin = new Admin();

            groupIcon = itemView.findViewById(R.id.ci_group_icon);
            groupName = itemView.findViewById(R.id.tv_group_name);
            last_msg = itemView.findViewById(R.id.tv_last_msg);

        }
    }

    // checks for last message
    private void lastMessage(final String user_id, final TextView last_msg){

        theLastMessage = "default";

        // getting the uid of the admin stored in shared preference
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mCtx);
        final String admin_uid = preferences.getString("uid","");

        DatabaseReference lastMsgRef = FirebaseDatabase.getInstance().getReference("Chats");
        lastMsgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupChats groupChats = snapshot.getValue(GroupChats.class);

                    assert groupChats != null;

                    // compares the uid of the admin and user and return the last message
                    if(groupChats.getReceivers().contains(admin_uid) && groupChats.getSender().equals(user_id)
                            || groupChats.getReceivers().contains(user_id) && groupChats.getSender().equals(admin_uid)){
                        theLastMessage = groupChats.getMessage();
                    }

                }

                // switch case for theLastMessage
                switch (theLastMessage){
                    case "default":
                        last_msg.setText(R.string.no_message);
                        break;

                        default:
                            last_msg.setText(theLastMessage);
                            break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display error message if one should occur
                Toast.makeText(mCtx, databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
