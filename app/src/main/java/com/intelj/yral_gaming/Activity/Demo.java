package com.intelj.yral_gaming.Activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.intelj.yral_gaming.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Demo extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPackages();
    }

    private ArrayList<PInfo> getPackages() {
        ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
        final int max = apps.size();
        for (int i=0; i<max; i++) {
            apps.get(i).prettyPrint();
        }
        return apps;
    }

    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue ;
            }
            PInfo newInfo = new PInfo();
            newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.path = p.applicationInfo.sourceDir;
            newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());

            File file = new File(newInfo.path);
            long sizeinByte = file.length();
            long sizeinMb = sizeinByte / (1024*1024);

            Toast.makeText(this,newInfo.appname + "\t"/*"\t" + newInfo.pname + "\t" + newInfo.versionName + "\t" + newInfo.versionCode + "\t" */+sizeinMb+"Mb",Toast.LENGTH_SHORT).show();


            res.add(newInfo);
        }
        return res;
    }
    private class PInfo {
        private String appname = "";
        private String pname = "";
        private String versionName = "";
        private int versionCode = 0;
        private String path = "";
        private Drawable icon;

        private void prettyPrint() {
            Log.i("Here",appname + "\t" + pname + "\t" + versionName + "\t" + versionCode + "\t" + path );
        }
    }

}
