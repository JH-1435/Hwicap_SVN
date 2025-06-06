package kr.ac.jh.keycap.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.ac.jh.keycap.dao.UserDao;

@Service
public class GoogleLoginServiceImpl implements GoogleLoginService {	
	
	@Autowired
	UserDao dao;
	
	@Override
	public String getAccessToken(String authorize_code) {
		String access_Token = "";
//		String refresh_Token = "";
		String reqURL = "https://www.googleapis.com/oauth2/v4/token";
		
		try {
			URL url = new URL(reqURL);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// POST 요청을 위해 기본값이 false인 setDoOutput을 true로
			
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			// POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			StringBuilder sb = new StringBuilder();
			sb.append("grant_type=authorization_code");
			
//			sb.append("&client_id="); // Git에 commit 하기 위해 비우고 주석처리 ,본인이 발급받은 클라이언트key 아이디 넣어주기
//			sb.append("&client_secret="); // Git에 commit 하기 위해 비우고 주석처리 , 본인이 발급받은 클라이언트key 비밀번호 넣어주기
			sb.append("&redirect_uri=http://13.125.249.221:8090/login/oauth2/googleLogin/google"); // REDIRECT_URI 본인이 설정한 주소 넣어주기

			sb.append("&code=" + authorize_code);
			bw.write(sb.toString());
			bw.flush();
			
			// 결과 코드가 200이라면 성공
			int responseCode = conn.getResponseCode();
			System.out.println("google_responseCode : " + responseCode);
			
			// 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("google_response body : " + result);

			// jackson objectmapper 객체 생성
			ObjectMapper objectMapper = new ObjectMapper();
			
			// JSON String -> Map
			Map<String, Object> jsonMap = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
			});

			access_Token = jsonMap.get("access_token").toString();

			System.out.println("google_access_token : " + access_Token);
 
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return access_Token;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, Object> getUserInfo(String access_Token) {
		// 요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
		HashMap<String, Object> userInfo = new HashMap<String, Object>();
		String reqURL = "https://www.googleapis.com/userinfo/v2/me?access_token";
				
		try {
			URL url = new URL(reqURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			// 요청에 필요한 Header에 포함될 내용
			conn.setRequestProperty("Authorization", "Bearer " + access_Token);

			int responseCode = conn.getResponseCode();
			System.out.println("responseCode : " + responseCode);

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("response body : " + result);
			System.out.println("result type" + result.getClass().getName()); // java.lang.String

			try {
				// jackson objectmapper 객체 생성
				ObjectMapper objectMapper = new ObjectMapper();
				
				// JSON String -> Map
				Map<String, Object> jsonMap = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
				});
				
				System.out.println("jsonMap : " + jsonMap);
								
				//사용자 정보 추출 (email,profile 말고 다른 정보는 Google 확인 프로세스를 거쳐야함.)				
				String id = "Google_" + jsonMap.get("id").toString();
		        String name = jsonMap.get("name").toString();
		        String email = "Google(email)_" + jsonMap.get("email").toString();
		        
		        //userInfo에 넣기
		        userInfo.put("id", id);
		        userInfo.put("name", name);
		        userInfo.put("email", email);
		        		        
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return userInfo;
	}

}
