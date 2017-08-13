package com.zhangpeng.share;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button bt;
    String path= Environment.getExternalStorageDirectory().getPath()+ "/file_name.pdf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bt = (Button) findViewById(R.id.share);
    }
    public void share(View view){
        sendFileByOtherApp(this,path);
    }
    public static void sendFileByOtherApp(Context context, String path) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file:///"+path));//文本类型
        //shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//启动新的activity
        //shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
        shareIntent.setType(getMimeType(path));//添加MIME类型
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(shareIntent, 0);
        //获取可以用来发送该类型文件的ResolveInfo列表，也就是可以发送这种文件的应用列表信息
        Log.d("size",""+resInfo.size());
        if (!resInfo.isEmpty()) {
            ArrayList<Intent> targetIntents = new ArrayList<Intent>();
            for (ResolveInfo info : resInfo) {
                ActivityInfo activityInfo = info.activityInfo;
                if (activityInfo.packageName.contains("com.tencent.mobileqq")) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setPackage(activityInfo.packageName);
                    intent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file:///"+path));
                    intent.setClassName(activityInfo.packageName, activityInfo.name);
                    targetIntents.add(intent);
                    }
            }
            Intent chooser = Intent.createChooser(targetIntents.remove(0), "Send mail...");
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[]{}));
            context.startActivity(chooser);

        }
    }


    /*public void initShare(){
        String type = getMimeType(path);
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///"+path));
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType(type);
    }*/

    public static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "text/plain";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }
}
