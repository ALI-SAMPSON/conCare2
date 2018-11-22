package io.icode.concaregh.application.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.models.Chats;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context mCTx;
    private List<Chats> mChats;
    private String imageUrl;

    FirebaseUser user;

    public MessageAdapter(){}

    public MessageAdapter(Context mCTx, List<Chats> mChats, String imageUrl){
        this.mCTx = mCTx;
        this.mChats = mChats;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_item_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_item_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chats chats = mChats.get(position);

        holder.show_message.setText(chats.getMessage());

        // checks if imageUrl is empty or not
        if(imageUrl == null){
            holder.profile_image.setImageResource(R.drawable.avatar_placeholder);
        }
        else{
            Glide.with(mCTx).load(imageUrl).into(holder.profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profile_image;
        TextView show_message;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);


        }
    }

    @Override
    public int getItemViewType(int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(mChats.get(position).getSender().equals(user.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }

    }
}
