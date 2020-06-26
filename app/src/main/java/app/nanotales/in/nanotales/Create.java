package app.nanotales.in.nanotales;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Create#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Create extends Fragment implements View.OnClickListener{

    View view;
    EditText composeText;
    ImageView drafts,close,next;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Create() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Create.
     */
    // TODO: Rename and change types and number of parameters
    public static Create newInstance(String param1, String param2) {
        Create fragment = new Create();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create, container, false);
        close = (ImageView) view.findViewById(R.id.imageView_close);
        drafts = (ImageView) view.findViewById(R.id.imageView_drafts);
        next = (ImageView) view.findViewById(R.id.imageView_next);
        composeText = (EditText) view.findViewById(R.id.user_text_compose);
        close.setOnClickListener(this);
        //drafts.setOnClickListener(this);
        next.setOnClickListener(this);
        //composeText.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.imageView_close :
                startActivity(new Intent(getActivity(),HomeScreen.class));


            case  R.id.imageView_next :
                if(validate()){
                    Intent i = new Intent(getActivity(),UserQuote.class);
                    i.putExtra("string",composeText.getText());
                    startActivity(i);

                }
                else{
                    Toast.makeText(getActivity(), "Please write a quote to compose", Toast.LENGTH_SHORT).show();
                }


        }
    }

    private boolean validate() {
        if(composeText.getText().toString().isEmpty()){
            return false;
        }
        else{
            return true;
        }
    }
}