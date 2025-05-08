package kr.ac.jh.keycap.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import kr.ac.jh.keycap.model.UserAccountVo;
import kr.ac.jh.keycap.util.JwtTokenValidator;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	@Value("${NODE_TOKEN_API_URL}")  // Spring 환경설정 값 직접 주입
    private String nodeTokenApiUrl;
	
	@Autowired
    private JwtTokenValidator jwtTokenValidator;
	    
	@Override
	public boolean authenticateUser(UserAccountVo item, HttpServletResponse response, HttpServletRequest request) {
		
		// Node.js API 호출을 위한 RestTemplate 생성 (특별한 SSL 설정 없이 기본 RestTemplate 사용)
		// TODO - AWS SSL 인증서 할때 다시 해야함
	    RestTemplate restTemplate = new RestTemplate();
	    
	    // 요청 본문에 사용자 ID를 포함시켜 Node.js 서버로 요청 - HttpEntity 부분
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);	// 헤더에 Content-Type을 application/json으로 설정+
	    HttpEntity<String> entity = new HttpEntity<>("{\"accountId\": \"" + item.getAccountId() + "\"}", headers);  // JSON 형식의 body를 만들어 HttpEntity에 포함
	    
	    // Node.js HTTPS URL (Node.js 서버가 HTTPS를 지원하는 경우) - AWS SSL 인증서 필요-후추
//	    String httpsNodeTokenApiUrl = "https://localhost:3000/token"; // HTTPS로 변경
	    
	    // ResponseEntity로 Node.js 반환(응답) 값 받기
	    ResponseEntity<String> responseNode = restTemplate.exchange(nodeTokenApiUrl + "/token", HttpMethod.POST, entity, String.class);
	    
	    // Node.js API 호출 if문
	    if (responseNode.getStatusCode() == HttpStatus.OK) {
	        // Node.js에서 받은 JWT을 파싱
	        String responseNodeBody = responseNode.getBody();
	        String jwt = jwtTokenValidator.extractJwtFromResponse(responseNodeBody); // JWT 추출

	        // JWT 검증
	        if (jwtTokenValidator.verifyJwt(jwt, item.getAccountId())) {
	        	// JWT가 유효하면 세션에 사용자 정보 저장
	            // 로그인 성공 시 JWT을 쿠키에 저장
	            Cookie jwtTokenCookie = new Cookie("token", jwt);
	            //jwtTokenCookie.setHttpOnly(false);  // HTTP와 HTTPS 모두에 쿠키 전송, Servlet 3.0 이상 만 가능
//	            jwtTokenCookie.setSecure(true);     // (운영서버, AWS SSL 인증서 필요-후추)HTTPS 연결에서만 사용
	            jwtTokenCookie.setSecure(false);    // (개발서버)HTTP 연결에서만 사용
	            jwtTokenCookie.setPath("/"); 	    // 애플리케이션 전체에서 유효
	            //(운영서버, AWS SSL 인증서 필요-후추)HTTPS 연결에서만 사용 addHeader
	            //response.addHeader("Set-Cookie", "token=" + jwt + "; HttpOnly; Secure; Path=/;"); // 응답 헤더에 Set-Cookie 헤더 추가 (HttpOnly 설정 JavaScript에서 접근할 수 없도록 해줌)
	            response.addHeader("Set-Cookie", "token=" + jwt + "; Secure; Path=/;");
	            response.addCookie(jwtTokenCookie); // JwtToken 쿠키 추가
	            
	            // 인증 성공
	            return true;
	            
	        }
	    } 
	    // 인증 실패
	    return false;
	}
}
