package com.example.sydney.aifiletransfer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final String FTPHost = "files.000webhost.com";
    final String user = "attendancemonitor";
    final String pass = "darksalad12";
    final int PORT = 21;

    Button btn_upload, btn_download;
    final int PICK_FILE = 1;
    String filename, filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_download = (Button) findViewById(R.id.btn_download);

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("file/*");
                startActivityForResult(i, PICK_FILE);
            }
        });
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent downloadIntent = new Intent(getApplicationContext(), DownloadActivity.class);
                startActivity(downloadIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE && data.getData() != null) {
            filepath = data.getData().getPath();
            filename = data.getData().getLastPathSegment();
            new UploadFile().execute(filepath, FTPHost, user, pass);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class UploadFile extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            FTPClient client = new FTPClient();
            try {
                client.connect(params[1], PORT);
                client.login(params[2], params[3]);
                client.enterLocalPassiveMode();
                client.changeWorkingDirectory("/public_html/CSVFiles");
                client.setFileType(FTP.BINARY_FILE_TYPE);
                return client.storeFile(filename, new FileInputStream(new File(params[0])));
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("FTP", e.toString());
                return false;
            } finally {
                try {
                    client.logout();
                    client.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}