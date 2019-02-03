package io.icode.concaregh.application.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.models.GroupChats;


public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mCtx;
    private List<GroupChats> mChats;
    private String imageUrl;

    // Global variable to handle OnItemClickListener
    public static OnItemClickListener mListener;

    public GroupMessageAdapter(Context mCtx, List<GroupChats> mChats, String imageUrl){
        this.mCtx= mCtx;
        this.mChats = mChats;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_chat_item_right,parent,false);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_chat_item_left,parent,false);
            return new ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GroupChats groupChats = mChats.get(position);

        holder.show_message.setText(groupChats.getMessage());

        // checks if imageUrl is empty or not
        if(imageUrl == null){
            holder.profile_image.setImageResource(R.drawable.ic_person_unknown);
        }
        else{
            Glide.with(mCtx).load(imageUrl).into(holder.profile_image);
        }

        // checks if chat is seen by user and sets the appropriate text
        if(position == mChats.size()-1){
            if(groupChats.isSeen()){
                holder.txt_seen.setText(R.string.text_seen);
            }
            else{
                holder.txt_seen.setText(R.string.text_delivered);
            }
        }
        else{
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        CircleImageView profile_image;
        TextView show_message;

        // Message Seen textView
        TextView txt_seen;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);

            // setting onClickListener on itemView
            itemView.setOnClickListener(this);
            // setting onCreateContextMenuListener on itemView
            itemView.setOnCreateContextMenuListener(this);

        }

        // handling normal Clicks
        @Override
        public void onClick(View view) {
            if(mListener != null){
                //get Adapter position
                int position = getAdapterPosition();
                 /*checks if position of item clicked is equal
                to the position of an item in recyclerView*/
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        // Handling Context Menu Item Clicks
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            // setting a title on ContextMenu
            contextMenu.setHeaderTitle(R.string.select_action);
            // menu items to delete message or cancel
            MenuItem delete = contextMenu.add(ContextMenu.NONE,1,1,R.string.text_delete);
            MenuItem cancel = contextMenu.add(ContextMenu.NONE,2,2,R.string.text_cancel);

            delete.setOnMenuItemClickListener(this);
            cancel.setOnMenuItemClickListener(this);

        }

        // Handling onItemClick(Actual item) in the ContextMenu
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(mListener != null){
                //get Adapter position
                int position = getAdapterPosition();
                /*checks if position of item clicked is equal
                to the position of an item in recyclerView*/
                if(position != RecyclerView.NO_POSITION){
                    switch (menuItem.getItemId()){
                        case 1:
                            mListener.onDeleteClick(position);
                            return true;
                        case 2:
                            mListener.onCancelClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public int getItemViewType(int position) {
        // getting the uid of the admin stored in shared preference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mCtx);
        final String admin_uid = preferences.getString("uid","");

        if(mChats.get(position).getSender().equals(admin_uid)){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }

    }

    public interface OnItemClickListener{
        void onItemClick(int position);

        void onDeleteClick(int position);

        void onCancelClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
}
