package com.example.optpro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    FloatingActionButton button;
    String code = "[FansSwitcher]\n" +
            "+CVars=r.PUBGMaxSupportQualityLevel=3\n" +
            "+CVars=r.PUBGDeviceFPSLow=6\n" +
            "+CVars=r.PUBGDeviceFPSMid=6\n" +
            "+CVars=r.PUBGDeviceFPSHigh=6\n" +
            "+CVars=r.PUBGDeviceFPSHDR=6\n" +
            "+CVars=r.PUBGDeviceFPSDef=6\n" +
            "+CVars=r.PUBGDeviceFPSUltralHigh=6\n" +
            "+CVars=r.PUBGMSAASupport=4\n" +
            "+CVars=r.PUBGLDR=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.floatingActionButton);
        opt(new OptCallback() {
            @Override
            public void onGrant(String uriString) {
                textView.setText("当前未授予必要权限，请点击按钮授权");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Grant(uriString);
                    }
                });
            }

            @Override
            public void onCreate(Uri uri) {
                textView.setText("授权成功，请点击按钮优化");
                loadList(uri);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CreateFile(uri);
                        init();
                    }
                });
            }
        });
    }

    public void Grant(String uriString) {
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        }
        this.startActivityForResult(intent, 1);
    }

    public void isGranted() {
        File file = Environment.getExternalStorageDirectory();
        String path = file.getPath() + "/Android/data/com.tencent.tmgp.pubgmhd/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android";
        String uriCheck = fileUriUtils.grantCheckUri(path);
        if (!fileUriUtils.isGrant(this, uriCheck)) {
            Toast.makeText(this, "权限未正确授予，由于和平精英未安装或未登录，请尝试在排除问题后重新授权", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "权限未正确授予，由于和平精英未安装或未登录，请尝试在排除问题后重新授权", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "权限未正确授予，由于和平精英未安装或未登录，请尝试在排除问题后重新授权", Toast.LENGTH_LONG).show();
        }
    }

    public void opt(OptCallback callback) {
        File file = Environment.getExternalStorageDirectory();
        String path = file.getPath() + "/Android/data/com.tencent.tmgp.pubgmhd/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android";
        String uriString = fileUriUtils.permissionUri(path);
        String uriCheck = fileUriUtils.grantCheckUri(path);
        Uri uri = Uri.parse(uriCheck);
        if (!fileUriUtils.isGrant(this, uriCheck)) {
            callback.onGrant(uriString);
        } else {
            callback.onCreate(uri);
        }
    }

    public interface OptCallback {
        void onGrant(String uriString);
        void onCreate(Uri uri);
    }

    public void CreateFile(Uri uri) {
        DocumentFile documentFile = DocumentFile.fromTreeUri(this, uri);
        assert documentFile != null;
        DocumentFile documentFile1 = documentFile.findFile("EnjoyCJZC.ini");
        if (documentFile1 == null) {
            DocumentFile file = documentFile.createFile("none", "EnjoyCJZC.ini");
            try {
                assert file != null;
                OutputStream fileOutputStream = getContentResolver().openOutputStream(file.getUri());
                try {
                    fileOutputStream.write(code.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "基本优化已完成，请启动游戏后回到这里以解锁高级功能", Toast.LENGTH_LONG).show();
        } else {
            updateFile(uri);
        }
    }

    public void updateFile(Uri uri) {
        StringBuilder userCode = new StringBuilder();
        StringBuilder encodeCode = new StringBuilder();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            TextView textView = recyclerView.getChildAt(i).findViewById(R.id.info);
            EditText editText = recyclerView.getChildAt(i).findViewById(R.id.value);
            String textLine = textView.getText().toString() + "=" + editText.getText().toString();
            ConverterUtils converter = new ConverterUtils();
            String encodeTextLine = converter.encoder(textLine);
            String encodeLine = "+CVars=" + encodeTextLine;
            userCode.append(encodeLine).append("\n");
        }
        String flagText = "\n\n[FansCustom]";
        encodeCode.append(code).append(flagText).append("\n").append(userCode);
        DocumentFile documentFile = DocumentFile.fromTreeUri(this, uri);
        assert documentFile != null;
        DocumentFile documentFile1 = documentFile.findFile("EnjoyCJZC.ini");
        if (documentFile1 != null) {
            documentFile1.delete();
        }
        DocumentFile file = documentFile.createFile("none", "EnjoyCJZC.ini");
        try {
            assert file != null;
            OutputStream fileOutputStream = getContentResolver().openOutputStream(file.getUri());
            try {
                fileOutputStream.write(encodeCode.toString().getBytes(StandardCharsets.UTF_8));
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "优化完成：用户自定义代码已生效", Toast.LENGTH_SHORT).show();
    }

    public void loadList(Uri uri) {
        DocumentFile documentFile = DocumentFile.fromTreeUri(this, uri);
        assert documentFile != null;
        DocumentFile documentFile1 = documentFile.findFile("EnjoyCJZC.ini");
        if (documentFile1 == null) {
            return;
        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(documentFile1.getUri());
            if (!new Scanner(inputStream).useDelimiter("\\Z").hasNext()) {
                documentFile1.delete();
                CreateFile(uri);
                Toast.makeText(this, "已为空白配置文件重新初始化，请启动游戏后回到这里以解锁高级功能", Toast.LENGTH_SHORT).show();
                return;
            }
            inputStream = getContentResolver().openInputStream(documentFile1.getUri());
            try {
                String fileText = new Scanner(inputStream).useDelimiter("\\Z").next();
                String flagText = "\n\n[FansCustom]";
                if (fileText.startsWith(code + flagText)) {
                    String test = fileText.substring(code.length() + flagText.length() + 1);
                    String[] dataSet = test.split("\n");
                    MyAdapter adapter = new MyAdapter(dataSet);
                    RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(manager);
                } else if (fileText.startsWith(code)) {
                    textView.setText("配置文件为基本优化，请启动游戏后回到这里以解锁高级功能");
                } else {
                    documentFile1.delete();
                    CreateFile(uri);
                    Toast.makeText(this, "已为错误配置文件重新初始化，请启动游戏后回到这里以解锁高级功能", Toast.LENGTH_SHORT).show();
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;
        if (data == null) {
            return;
        }
        if (requestCode == 1 && (uri = data.getData()) != null) {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            isGranted();
        }
    }
}