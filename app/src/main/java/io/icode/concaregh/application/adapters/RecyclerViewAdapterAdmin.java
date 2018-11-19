package io.icode.concaregh.application.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.icode.concaregh.application.R;
import io.icode.concaregh.application.chatApp.MessageActivity;
import io.icode.concaregh.application.models.Admin;

public class RecyclerViewAdapterAdmin extends RecyclerView.Adapter<RecyclerViewAdapterAdmin.ViewHolder> {

    private Context mCtx;
    private List<Admin> mAdmin;

    public RecyclerViewAdapterAdmin(Context mCtx, List<Admin> mAdmin){
        this.mCtx = mCtx;
        this.mAdmin = mAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items_admin,parent, false);

        return new RecyclerViewAdapterAdmin.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // gets the positions of the all users
        final Admin admin = mAdmin.get(position);

        // sets username to the text of the textView
        holder.username.setText(admin.getUsername());

        if(admin.getImageUrl() == null){
            // loads the default placeholder into ImageView if ImageUrl is null
            holder.profile_pic.setImageResource(R.drawable.avatar_placeholder);
        }
        else{
            // loads user image into the ImageView
            Glide.with(mCtx).load(admin.getImageUrl()).into(holder.profile_pic);
        }

        // onClickListener for view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // passing adminUid as a string to the MessageActivity
                Intent intent = new Intent(mCtx,MessageActivity.class);
                intent.putExtra("uid",admin.getAdminUid());
                mCtx.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mAdmin.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profile_pic;
        TextView username;

        public ViewHolder(View itemView) {
            super(itemView);

            profile_pic = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
        }
    }

}
