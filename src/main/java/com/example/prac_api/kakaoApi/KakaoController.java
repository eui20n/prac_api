package com.example.prac_api.kakaoApi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class KakaoController {
    static KakaoService kakaoService;
    @RequestMapping(value = "/kakaoLogin", method = RequestMethod.GET)
    public ResponseEntity createUser(
            @RequestParam(value = "code", required = false) String code
    ) throws Exception {
        String accessToken = kakaoService.getAccessToken(code);
        // kakao log Api를 user정보를 받아옴
        HashMap<String, Object> userInfo = kakaoService.getUserInfo(accessToken);

    }
}
