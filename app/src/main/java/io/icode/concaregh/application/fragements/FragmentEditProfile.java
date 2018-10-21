package io.icode.concaregh.application.fragements;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentEditProfile extends Fragment {

    public FragmentEditProfile(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

         View view = inflater.inflate(io.icode.concaregh.application.R.layout.fragment_edit_profile,container,false);

         // getting references to the views of the fragment layout
        CircleImageView circleImageView = view.findViewById(io.icode.concaregh.application.R.id.circularImageView);
        EditText editTextUsername = view.findViewById(io.icode.concaregh.application.R.id.editTextUsername);
        AppCompatButton saveButton = view.findViewById(io.icode.concaregh.application.R.id.appCompatButtonSave);

        //circleImageView.setImageResource("");
        //editTextUsername.setText("");

         return view;
    }
}
