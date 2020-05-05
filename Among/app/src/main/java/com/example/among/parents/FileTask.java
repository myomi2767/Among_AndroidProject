package com.example.among.parents;


import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

class FileTask extends AsyncTask<Map<String, String>, Integer, String> {
    private String result = "";
    private String method;
    public static String ip = "70.12.230.200"; // 자신의 IP주소를 쓰시면 됩니다.

    @Override
    protected String doInBackground(Map<String, String>... maps) { // 내가 전송하고 싶은 파라미터
        method = maps[0].get("method");
        String result = "";

        switch (method) {
            case "fileUpload":
                result = fileUpload(maps[0]);
                break;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) { //서블릿으로부터 값을 받을 함수

    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String fileUpload(Map<String, String> maps) {

        try {
            File file = new File("/sdcard/" + maps.get("filename")); // recorded.mp4
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            StringBuffer sb = new StringBuffer("");
            for (int i = 0; i < buffer.length; i++) {
                inputStream.read(buffer);
            }
            String b = fileUpload.byteArrayToBinaryString(buffer);
            Log.d("fileUpload", b);
            maps.put("file", b);
            inputStream.close();
        } catch (Exception e) {
        }
        //HTTP 요청 준비
        HttpClient.Builder http = new HttpClient.Builder("GET", "http://" + ip + ":8088/among/fileUpload");

        //Parameter 전송
        http.addAllParameters(maps);
        //HTTP 요청 전송
        HttpClient post = http.create();
        post.request();
        //응답 상태 코드
        int statusCode = post.getHttpStatusCode();
        //응답 본문
        String body = post.getBody(); //Spring의 Controller에서 반환한 값. JSON 형식
        return body;
    }
}
