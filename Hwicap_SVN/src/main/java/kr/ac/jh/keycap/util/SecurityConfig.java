package kr.ac.jh.keycap.util;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;


/** Spring Security 설정 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
    private JwtTokenValidator jwtTokenValidator;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http
	        .authorizeRequests()
	            .antMatchers("/login", "/token").permitAll()  // 인증 없이 접근할 수 있는 경로
	            .anyRequest().authenticated()  // 나머지 요청은 인증 필요
	        .and()
	        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenValidator), UsernamePasswordAuthenticationFilter.class);  // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
	}

	
	// JWT 인증 필터
    public class JwtAuthenticationFilter extends OncePerRequestFilter {
    	
    	private final JwtTokenValidator jwtTokenValidator;  // JwtTokenValidator 객체, JWT 검증을 담당
    	
    	// JwtAuthenticationFilter 생성자에서 JwtTokenValidator를 주입받음.
        public JwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator) {
            this.jwtTokenValidator = jwtTokenValidator;
        }
    	
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            // 1. 쿠키에서 JWT 추출
            Cookie jwtCookie = WebUtils.getCookie(request, "JWT");
            if (jwtCookie != null) {
            	String jwt = jwtCookie.getValue();
            	
            	// 2. JWT 검증 (디코딩 및 유효성 검사)
                if (jwt != null && !jwt.isEmpty()) {
                	// 요청에서 사용자 ID를 추출.
                	String accountIdFromRequest = (String) request.getAttribute("accountId");  // accountId를 요청 속성(Attribute)에서 가져옴
                	
                	// 3. JWT 검증 : 유효한 토큰인 경우, 인증 객체를 생성하고  인증 상태로 설정.
                	if (jwtTokenValidator.verifyJwt(jwt, accountIdFromRequest)) {
                        // JWT가 유효하다면 인증 설정.
                        // 사용자 인증 정보를 담은 Authentication 객체 생성
                        Authentication authentication = new UsernamePasswordAuthenticationToken(accountIdFromRequest, null, new ArrayList<>());
                        
                        // 인증 정보를 SecurityContextHolder에 설정하여 이후 요청에서 인증 정보를 사용할 수 있도록 합니다.
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            
            // 4. 필터 체인을 계속 진행
            // 필터 체인을 계속해서 실행. 즉, 인증 후 요청 처리.
            filterChain.doFilter(request, response); // IOException이 발생할 수 있으므로, throws로 예외 처리
            
        }
    }
	 
}
