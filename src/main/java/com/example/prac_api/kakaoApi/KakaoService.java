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

    // 인가 코드로 토큰을 얻는 과정
    public String getToken(String code) throws IOException {
        String host = "https://kauth.kakao.com/oauth/token";
        /*
        host의 url은 그냥 문자열임. 우리가 원하는건 문자열이 아니라 url임
        그래서 URL를 활용해서 해당 문자열을 url로 바꿔줌
        URL클래스를 이용하면 url을 Protocol, Host Name, File로 나눌 수 있음
        */
        URL url = new URL(host);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String token = " ";
        try {
            // URL 요청 방법
            // GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE 중 하나를 선택하면 됨
            urlConnection.setRequestMethod("POST");

            // 출력(OutputStream)으로 POST 데이터를 넘겨주는 함수 -> 좀 더 알아봐야함, 나중에 이거 빼보기
            // 반대는 setDoInput()
            // server와 통신을 하려면 위 메소드들은 true로 해야함
            urlConnection.setDoOutput(true);

            // OutputStreamWriter은 문자 단위 출력을 위한 하위 스트림
            // getOutputStream()은 출력(POST로 받는 값)을 할 수 있게 해주는 함수
            // BufferedWriter는 그냥 출력을 해주는 클래스임 -> System.out.println(); 과 비슷
            // .write() -> 버퍼에 있는 값 출력
            // .flush() -> 남아있는 데이터 모두 출력
            // .close() -> 스트림 닫음
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));

            // StringBuilder은 문자열 끼리 더할 수 있게 해주는 클래스
            // 기존 String 클래스도 더할 수 있는데, 이는 시간이 노래걸림
            // StringBuilder 클래스 String 클래스보다 훨씬 빠름름
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=2aad40910868e3c5fa9594f8de34a07b");
            sb.append("&redirect_uri=http://localhost:8080");
            sb.append("&code = " + code);

            // 그냥 출력을 보는 것임
            bw.write(sb.toString());
            bw.flush();

            int responseCode = urlConnection.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            // 위에 적혀있는 writer와 반대의 개념이라고 생각하면 됨
            // .readLine() 입력을 받는 것 -> 리턴값이 String이고, 예외처리를 해서 사용해야함
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JSONParser parser = new JSONParser();

            // JSON 데이터를 JSON 객체로 만들어 줌
            // .parse() 를 사용하려면 ParseException을 예외처리 해줘야함
            JSONObject elem = (JSONObject) parser.parse(result);

            String accessToken = elem.get("access_token").toString();
            String refreshToken = elem.get("refresh_token").toString();
            System.out.println("access_token : " + accessToken);
            System.out.println("refresh_token : " + refreshToken);

            token = accessToken;

            br.close();
            bw.close();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return token;
    }

    // 발급 받은 토큰으로 사용자 정보 조회
    public Map<String, Object> getUserInfo(String accessToken) throws IOException {
        // host에 어떤 URL이 들어가는지 확인해봐야함
        String host = "";
        Map<String, Object> result = new HashMap<>();
        try {
            URL url = new URL(host);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // 요청(Response Data)의 데이터 타입을 정하는 것
            // 첫번째 파라미터 - the keyword by which the request is known
            // 두번째 파라미터 - the value associated with it.
            urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            urlConnection.setRequestMethod("GET"); // 조회라서 GET임

            int responseCode = urlConnection.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            String res = "";
            while ((line = br.readLine()) != null) {
                res += line;
            }

            System.out.println("res : " + res);

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(res);
            // .get() 는 HashMap 클래스의 메소드 => JSONObject 클래스는 HashMap의 자식 클래스
            // .get()은 HashMap에서 원하는 키의 값을 출력해주는 메소드
            JSONObject kakaoAccount = (JSONObject) obj.get("kakao_account");
            JSONObject properties = (JSONObject) obj.get("properties");

            // 아마도 미리 정한 값들만 가져올 수 있을 듯
            // 이 부분에 대해서 좀 더 알아야함
            String id = obj.get("id").toString();
            String nickname = properties.get("nickname").toString();
            String ageRange = kakaoAccount.get("age_range").toString();

            // result에 key - value 쌍으로 값을 넣어줌
            result.put("id", id);
            result.put("nickname", nickname);
            result.put("age_range", ageRange);

            br.close();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return result;
    }
}
