package kr.ac.jh.keycap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import kr.ac.jh.keycap.dao.UserDao;
import kr.ac.jh.keycap.model.UserVo;
import kr.ac.jh.keycap.util.Pager;
import kr.ac.jh.keycap.util.PasswordUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao dao;
	
	@Value("${NODE_TOKEN_API_URL}")  // Spring 환경설정 값 직접 주입
    private String nodeTokenApiUrl;
	
	@Override
	public List<UserVo> list(Pager pager) {
		int total = dao.total(pager);
		
		pager.setTotal(total);
		
		return dao.list(pager);
	}

	@Override
	public void add(UserVo item) {
		// Node.js API 호출을 위한 RestTemplate 생성 (특별한 SSL 설정 없이 기본 RestTemplate 사용)
		// TODO - AWS SSL 인증서 할때 다시 해야함
		RestTemplate restTemplate = new RestTemplate();
		
		// 요청 본문에 전달할 데이터
        String jsonBody = "{"
                + "\"userId\": \"" + item.getUserId() + "\", "
                + "\"name\": \"" + item.getUserName() + "\", "
                + "\"tel\": \"" + item.getUserTel() + "\", "
                + "\"email\": \"" + item.getUserAddress() + "\""
                + "}";
        
        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        
        // HTTP 요청 엔티티 생성
        // 요청 본문에 jsonBody를 포함시켜 Node.js 서버로 요청 - HttpEntity 부분
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        
        // POST 요청 보내기
        // ResponseEntity로 Node.js 반환(응답) 값 받기
        ResponseEntity<String> response = restTemplate.exchange(nodeTokenApiUrl + "/sendEmail", HttpMethod.POST, entity, String.class);
        
        // 응답 처리
        if (response.getStatusCode().is2xxSuccessful()) { // 이메일 전송 성공
        	dao.add(item);
        } else {
        	// 이메일 전송 실패 처리
            System.out.println("이메일 전송 실패: " + response.getStatusCode()); // 상태 코드 출력
            System.out.println("응답 본문: " + response.getBody()); // 응답 본문 출력
            // 실패 시 추가적인 로깅을 남기거나 예외를 던지는 방식으로 처리 가능
            throw new RuntimeException("이메일 전송 실패: " + response.getStatusCode() + " - " + response.getBody());
        }
	}

	@Override
	public UserVo item(String userId) {
		return dao.item(userId);
	}

	@Override
	public void update(UserVo item) {
		dao.update(item);
	}

	@Override
	public void delete(String userId) {
		dao.delete(userId);
	}

	@Override
	public boolean login(UserVo item) {
//		UserVo user = dao.login(item);
		UserVo user = dao.item(item.getUserId());
		
		if(user != null) {
			
			// 입력된 비밀번호와 DB의 암호화된 비밀번호 비교
	        boolean passwordCheck = PasswordUtil.checkPassword(item.getUserPw(), user.getUserPw());
	        
	        // 비밀번호 일치 시 로그인 성공 처리
	        if(passwordCheck) {
	        	item.setUserPw(null);
				item.setUserName(user.getUserName() );
				item.setUserAddress(user.getUserAddress() );
				item.setUserTel(user.getUserTel());
				
				return true;
	        }
	        
		}
		
		return false;
	}

	@Override
	public void oauthAdd(UserVo item) {
		dao.oauthAdd(item);
	}

	@Override
	public boolean oauthLogin(UserVo item) {
		UserVo user = dao.oauthLogin(item);
		
		if(user != null) {
						
			return true;
		}
		
		return false;
	}
}