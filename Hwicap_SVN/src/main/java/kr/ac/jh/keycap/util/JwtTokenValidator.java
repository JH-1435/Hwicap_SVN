package kr.ac.jh.keycap.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

/** JWT Token 추출 및 검증  */
@Component  // @Component나 @Service 같은 어노테이션을 사용해 Spring 관리 빈으로 등록  즉 @Autowired 등 으로 다른 곳에서 객체를 쓰기위해 Spring 관리 Bean으로 등록해야함, 의존성 관리.
public class JwtTokenValidator {
	
	@Value("${JWT_SECRET_KEY}")  // Spring 환경설정 값 직접 주입
    private String jwtSecretKey;
	
	private ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper
	
	 // JWT 검증 메소드
    @SuppressWarnings("deprecation")
	public boolean verifyJwt(String token, String getAccountId) {
        try {
        	
        	Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)  // 서명 검증 및 클레임 파싱
                    .getBody();
        	
        	// 1. accountId 검증: 토큰에 포함된 'sub'가 사용자 ID와 일치하는지 확인
            String accountId = claims.getSubject();
            if (accountId == null || accountId.isEmpty()) {
                throw new Exception("Invalid userId in JWT token");
            }
            
            
            // 2. 추가 검증: 예를 들어, 요청한 사용자가 실제로 인증된 사용자인지 확인
            if (accountId == getAccountId) {
                throw new Exception("User not found");
            }

            // 3. 만료된 토큰 검증
            if (claims.getExpiration().before(new Date())) {
                throw new ExpiredJwtException(null, claims, "Token is expired");
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
	
    // Node.js에서 반환된 응답에서 JWT 추출 (Jackson 사용)
    public String extractJwtFromResponse(String responseBody) {
    	try {
            // JSON 응답을 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            // "jwtToken" 필드에서 JWT 값을 추출
            return rootNode.path("jwtToken").asText();
            
        } catch (Exception e) {
            // 예외 발생 시 null 반환
            return null;
        }
    }
	
}
