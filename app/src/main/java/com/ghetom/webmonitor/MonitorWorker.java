package com.ghetom.webmonitor;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Map;

public class MonitorWorker extends Worker {

    private NotificationHelper notificationHelper;
    private SharedPreferences sharedPreferences;

    private MonitorWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        notificationHelper = new NotificationHelper(context);
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.shared_preferences), MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Result doWork() {
        scan();
        return Result.success();
    }

    private String getContents(String url) {
        WebUtils webUtils = WebUtils.getInstance();
        return webUtils.getContents(url);
    }

    private void scan() {
        Map<String, String> archivedUrls = (Map<String, String>) sharedPreferences.getAll();
        for (Map.Entry<String, String> entry : archivedUrls.entrySet()) {
            String archivedContent = entry.getValue().trim();
            String currentContent = getContents(entry.getKey()).trim();

            if(!currentContent.isEmpty()) {
                if (!archivedContent.equalsIgnoreCase(currentContent)) {
                    entry.setValue(currentContent);

                    String title = "Web changes found";
                    String message = entry.getKey();
                    notificationHelper.createNotification(title, message);
                }
            }
        }
    }
}
