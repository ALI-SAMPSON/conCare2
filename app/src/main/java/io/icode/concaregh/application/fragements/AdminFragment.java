package io.icode.concaregh.application.fragements;

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
import io.icode.concaregh.application.adapters.RecyclerViewAdapterAdmin;
import io.icode.concaregh.application.models.Admin;


public class AdminFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapterAdmin adapterUser;
    private List<Admin> mAdmin;

    ConstraintLayout mLayout;

    FirebaseAuth mAuth;

    DatabaseReference userRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin,container,false);

        mLayout = view.findViewById(R.id.mLayout);

        recyclerView =  view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdmin = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        userRef = FirebaseDatabase.getInstance().getReference("Admin");

        readAdmin();

        // return view
        return view;
    }

    // message to read the admin from the database
    public  void readAdmin(){

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clears list
                mAdmin.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Admin admin = snapshot.getValue(Admin.class);

                    assert admin != null;

                    assert currentUser != null;

                    mAdmin.add(admin);

                }

                // adapter initialization and RecyclerView set up
                adapterUser = new RecyclerViewAdapterAdmin(getContext(),mAdmin);
                recyclerView.setAdapter(adapterUser);
                adapterUser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                Snackbar.make(mLayout,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

}
