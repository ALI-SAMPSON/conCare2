package io.icode.concaregh.application.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.icode.concaregh.application.R;
import io.icode.concaregh.application.adapters.RecyclerViewAdapterGroups;
import io.icode.concaregh.application.chatApp.ChatActivity;
import io.icode.concaregh.application.models.Groups;

public class GroupsFragment extends Fragment {

    View view;

    ConstraintLayout constraintLayout;

    TextView tv_no_group;

    RecyclerView recyclerView;

    RecyclerViewAdapterGroups adapterGroups;

    Groups groups;

    FirebaseUser currentUser;

    List<Groups> groupList;

    ProgressBar progressBar;

    DatabaseReference groupRef;

    public GroupsFragment() {
        // Required empty public constructor
    }

    ChatActivity applicationContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        applicationContext = (ChatActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_groups, container, false);

        // getting reference to ids
        constraintLayout = view.findViewById(R.id.constraintLayout);

        tv_no_group = view.findViewById(R.id.tv_no_group);

        groups = new Groups();

        groupList = new ArrayList<>();

        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        currentUser =FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        // setting layout of the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(applicationContext));

        adapterGroups = new RecyclerViewAdapterGroups(applicationContext,groupList,false);

        recyclerView.setAdapter(adapterGroups);

        progressBar = view.findViewById(R.id.progressBar);

        // method call
        displayGroups();

        return view;

    }

    // method to display groups on this fragment layout file
    private void displayGroups(){

        // display progressBar
        progressBar.setVisibility(View.VISIBLE);

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){

                    // display text to display message to user
                    tv_no_group.setVisibility(View.VISIBLE);
                }
                else {
                    // clear list
                    groupList.clear();

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Groups groups = snapshot.getValue(Groups.class);

                        assert groups != null;

                        // checks if user id exist in the group and add the group to user's groups
                        if(groups.getGroupMembersIds().contains(currentUser.getUid())){

                            // hides text ot display message to user
                            tv_no_group.setVisibility(View.GONE);

                            // add all groups to list
                            groupList.add(groups);
                        }

                    }
                }

                adapterGroups.notifyDataSetChanged();

                // hides progressBar
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // hides progressBar
                progressBar.setVisibility(View.GONE);

                // display message if exception occurs
                Snackbar.make(constraintLayout,databaseError.getMessage(),Snackbar.LENGTH_SHORT).show();

            }
        });

    }

}
