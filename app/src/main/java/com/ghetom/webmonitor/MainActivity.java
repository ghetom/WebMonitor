package com.ghetom.webmonitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static Long intervalInMinutes = 15L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize(){
        sharedPreferences = getApplicationContext().getSharedPreferences(String.valueOf(R.string.shared_preferences), MODE_PRIVATE);
        EditText intervalInput = findViewById(R.id.editInterval);
        intervalInput.setText(intervalInMinutes.toString());

        EditText input = findViewById(R.id.editText);
        Set<String> keys = sharedPreferences.getAll().keySet();
        String[] urls = keys.toArray(new String[0]);
        String delimiter = urls.length > 0 ? "\n" : "";
        String output = String.join(delimiter,urls);
        input.setText(output);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(saveButtonListener);

        createWorkRequest("Monitor web changes",intervalInMinutes);
    }

    private View.OnClickListener saveButtonListener = view -> {
        //TODO: implement interval save

        clear();
        EditText urlInput = findViewById(R.id.editText);
        String [] urls = urlInput.getText().toString().split("\n");

        for (String url : urls) {
            add(url);
        }

        Toast toast = Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT);
        toast.show();
    };

    private void createWorkRequest(String message, Long intervalInMinutes){
        PeriodicWorkRequest monitorRequest = new PeriodicWorkRequest.Builder(MonitorWorker.class,
                intervalInMinutes,
                TimeUnit.MINUTES)
                .setConstraints(Constraints.NONE)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(monitorRequest.getClass().getSimpleName(), ExistingPeriodicWorkPolicy.KEEP,monitorRequest);
    }

    public void add(String url) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String html = getContents(url);
                    editor.putString(url, html);
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private String getContents(String url) {
        WebUtils webUtils = WebUtils.getInstance();
        return webUtils.getContents(url);
    }
}