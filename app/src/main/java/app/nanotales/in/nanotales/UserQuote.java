package app.nanotales.in.nanotales;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UserQuote extends AppCompatActivity {

     private ImageView userQuotePic;
     private TextView userQuote;
     private Button btnChooseGallery;
     Uri imageUri;
     private static final int  PICK_IMAGE = 100;

        @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_quote);
        setupUI();

        //taking text from previous activity
            Bundle extras = getIntent().getExtras();
            String compose_text = extras.getString("string");
            userQuote.setText(compose_text);

            btnChooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              openGallery();
            }

        });
    }
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode==PICK_IMAGE){
            imageUri = data.getData();

            userQuotePic.setImageURI(imageUri);

        }
    }

    private void setupUI() {
        userQuotePic = (ImageView) findViewById(R.id.user_quote_pic);
        btnChooseGallery = (Button)findViewById(R.id.btn_pic_gallery);
        userQuote = (TextView)findViewById(R.id.user_quote);
    }
}
