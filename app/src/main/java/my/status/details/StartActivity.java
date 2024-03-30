package my.status.details;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import com.jmedeisis.draglinearlayout.DragLinearLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class StartActivity extends AppCompatActivity {
    private TextView tv_Speech_to_text;
    private long pressedTime;
    Intent google_intent;
    private ActivityResultLauncher<Intent> google_launcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        google_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    Intent data=result.getData();
                    if(data!=null){
                        ArrayList<String> resultString = data.getStringArrayListExtra(
                                RecognizerIntent.EXTRA_RESULTS);
                        tv_Speech_to_text.setText(
                                Objects.requireNonNull(resultString).get(0));
                    }


                }
        );
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            new OnBackInvokedDispatcher() {
                @Override
                public void registerOnBackInvokedCallback(int priority, @NonNull OnBackInvokedCallback callback) {
                    if (pressedTime + 2000 > System.currentTimeMillis()) {
                    finish();
                    } else {
                        Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
                    }
                    pressedTime = System.currentTimeMillis();
                }

                @Override
                public void unregisterOnBackInvokedCallback(@NonNull OnBackInvokedCallback callback) {

                }

            };
        }
        else{
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    if (pressedTime + 2000 > System.currentTimeMillis()) {
                    finish();
                    } else {
                        Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
                    }
                    pressedTime = System.currentTimeMillis();
                }
            });
        }


        //Draglinearlayout start
        DragLinearLayout dragLayout = findViewById(R.id.container);

        // we are creating for loop for dragging
        // and dropping of child items.
        for (int i = 0; i < dragLayout.getChildCount(); i++) {

            // below is the child inside dragger layout
            View sourceElem = dragLayout.getChildAt(i);
            View destElem;
            if(i==0){
                destElem=dragLayout.getChildAt(dragLayout.getChildCount()-1);
            }
            else{
                destElem=dragLayout.getChildAt(i-1);
            }
            // below line will set all children draggable
            // except the header layout.
            // the child is its own drag handle.
            dragLayout.setViewDraggable(destElem, sourceElem);
        }
        //swiperefreshlayout start
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshLayout);
        TextView textView = (TextView)findViewById(R.id.tv1);

        // Refresh  the layout
        swipeRefreshLayout.setOnRefreshListener(
                () -> {

                    // Your code goes here
                    // In this code, we are just
                    // changing the text in the textbox
                    textView.setText(R.string.refreshed);

                    // This line is important as it explicitly
                    // refreshes only once
                    // If "true" it implicitly refreshes forever
                    swipeRefreshLayout.setRefreshing(false);
                }
        );

        //speech to text start
        ImageView iv_mic = findViewById(R.id.iv_mic);
        tv_Speech_to_text = findViewById(R.id.tv_speech_to_text);

        iv_mic.setOnClickListener(v -> {
            google_intent
                    = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            google_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            google_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault());
            google_intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

            try {
//                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);


                google_launcher.launch(google_intent);
            }
            catch (Exception e) {
                Toast
                        .makeText(StartActivity.this, " " + e.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

}