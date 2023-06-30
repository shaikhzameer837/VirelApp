package com.intelj.y_ral_gaming.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

public class AmazonUrlOpener {
    private static final String AMAZON_PACKAGE_NAME = "com.amazon.mShop.android.shopping";

    public static void openAmazonUrl(Context context, String url) {
        PackageManager packageManager = context.getPackageManager();

        // Check if Amazon app is installed
        try {
            packageManager.getPackageInfo(AMAZON_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);

            // Amazon app is installed, open URL in the app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setPackage(AMAZON_PACKAGE_NAME);
            context.startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            // Amazon app is not installed, open URL in browser
            openUrlInBrowser(context, url);
        }
    }

    private static void openUrlInBrowser(Context context, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
}

