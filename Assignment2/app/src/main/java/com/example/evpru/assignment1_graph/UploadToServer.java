package com.example.evpru.assignment1_graph;
/*
/**
 * Created by Praneeth on 3/6/2017.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UploadToServer {

    private int serverResponseCode = 0;
    private String upLoadServerUri = null;
    private String uploadFilePath = "/data/data/com.example.evpru.assignment1_graph/databases/";
    private String uploadFileName;
    private Context context;
    private String downloadDir = "/data/data/com.example.evpru.assignment1_graph/databases/download/";

    public boolean isUploadInProgress = false;

    private boolean isDownloaded;

    public static UploadToServer uploadToServer;


    public UploadToServer(String filename, MainActivity context) {
        this.uploadFileName = filename;
        this.context = context;
    }



    public static UploadToServer getInstance(String dbName, MainActivity context)
    {
        if(null == uploadToServer)
        {
            uploadToServer = new UploadToServer(dbName,context);
        }

        return uploadToServer;
    }


    public void upload() {
        upLoadServerUri = "https://impact.asu.edu/CSE535Spring17Folder/UploadToServer.php";
        AsyncTask uploading = new AsyncTask() {

            @Override
            protected void onPreExecute() {
                isUploadInProgress = true;
            }

            @Override
            protected Object doInBackground(Object[] params) {
                uploadFile(uploadFilePath + uploadFileName);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                isUploadInProgress = false;
                if(serverResponseCode == 200)
                {
                    Toast.makeText(context, "File upload Complete.",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, "Could not upload file!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        uploading.execute();

    }
    public void download(final String filename) {
        AsyncTask downloading = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                setDownloaded(false);
            }

            @Override
            protected Object doInBackground(Object[] params) {
                downloadFile(filename);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                File sourceFile = new File(downloadDir+filename);
                if (!sourceFile.isFile())
                    Toast.makeText(context, "File not downloaded!",Toast.LENGTH_SHORT).show();
                else
                {
                    setDownloaded(true);
                    Toast.makeText(context, "File download Complete!",Toast.LENGTH_SHORT).show();
                }
            }
        };
        downloading.execute();
    }

    public boolean isDownloaded()
    {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {

        isDownloaded = downloaded;
    }

    public void uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            System.out.println("File Not Found");
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, new TrustManager[]{
                        new X509TrustManager() {
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            }

                            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            }

                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[]{};
                            }
                        }
                }, null);
                HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                if (serverResponseCode == 200) {
                    System.out.print("Uploaded \n");
                }
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void downloadFile(String filename) {
        int count;
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            }, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            URL url = new URL("https://impact.asu.edu/CSE535Spring17Folder/" + filename);
            URLConnection conection = url.openConnection();
            conection.connect();
            File dir = new File(downloadDir);
            if(dir.exists() == false){
                dir.mkdirs();
            }
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);
            OutputStream output = new FileOutputStream(downloadDir + filename);

            byte data[] = new byte[1024];
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}