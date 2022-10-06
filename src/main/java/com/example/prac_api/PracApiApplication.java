package com.example.prac_api;

import com.example.prac_api.kakaoApi.KakaoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class PracApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PracApiApplication.class, args);
		try{
			KakaoService test = new KakaoService();
			System.out.println(test.getToken("b0bee9c4437f02eeebc3eb5784d659e5"));
		} catch(IOException e){
			e.printStackTrace();
		}
	}

}
