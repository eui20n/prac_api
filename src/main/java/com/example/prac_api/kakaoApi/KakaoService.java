package com.example.prac_api.kakaoApi;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoService {

    public String getAccessToken (String authorize_code) {
        String reqURL = "https://kauth.kakao.com/oauth/token";
        String token = "";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            // 발급 받은 값 저장하기
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=--");
            sb.append("&redirect_uri=--");
            sb.append("&code=" + authorize_code);
            sb.append("&client_secret=--");

            bw.write(sb.toString());
            bw.flush();

            // 200번이 나와야함
//            int responseCode = conn.getResponseCode();
//            System.out.println("responseCode : " + responseCode);

            // 200 이외 예외 처리하기

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

//            System.out.println("response body : " + result);

            JSONParser parser = new JSONParser();
            JSONObject element = (JSONObject) parser.parse(result);

            String accessToken = element.get("access_token").toString();
            String refreshToken = element.get("refresh_token").toString();
//            System.out.println("refresh_token = " + refreshToken);
//            System.out.println("accessToken = " + accessToken);

            token = accessToken;

            br.close();
            bw.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return token;
    }

    public HashMap<String, Object> getUserInfo(String accessToken) {

        HashMap<String, Object> userInfo = new HashMap<String, Object>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

//            int responseCode = conn.getResponseCode();
//            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JSONParser parser = new JSONParser();
            JSONObject element = (JSONObject) parser.parse(result);
            JSONObject kakaoAccount = (JSONObject) element.get("kakao_account");
            JSONObject properties = (JSONObject) element.get("properties");

            // 원하는 정보 가져오기
            String id = element.get("id").toString();
            Long longId = Long.parseLong(id);
            String nickname = properties.get("nickname").toString();
            String ageRange = kakaoAccount.get("age_range").toString();
            String birthday = kakaoAccount.get("birthday").toString();
            String gender = kakaoAccount.get("gender").toString();
            String email = kakaoAccount.get("email").toString();

            userInfo.put("id", longId);
            userInfo.put("nickname", nickname);
            userInfo.put("age_range", ageRange);
            userInfo.put("birthday", birthday);
            userInfo.put("gender", gender);
            userInfo.put("email", email);

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }
}
