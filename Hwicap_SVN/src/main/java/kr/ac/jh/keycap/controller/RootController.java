package kr.ac.jh.keycap.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import kr.ac.jh.keycap.model.AdminVo;
import kr.ac.jh.keycap.model.KeycapVo;
import kr.ac.jh.keycap.model.ReviewVo;
import kr.ac.jh.keycap.model.UserVo;
import kr.ac.jh.keycap.service.AdminService;
import kr.ac.jh.keycap.service.AuthenticationService;
import kr.ac.jh.keycap.service.KeycapService;
import kr.ac.jh.keycap.service.ReviewService;
import kr.ac.jh.keycap.service.UserService;

import kr.ac.jh.keycap.util.Pager;
import kr.ac.jh.keycap.util.PasswordUtil;

@Controller
public class RootController {

	@Autowired
	UserService service;

	@Autowired
	AdminService serviceAdmin;

	@Autowired
	KeycapService serviceKeycap;

	@Autowired
	ReviewService serviceReview;
	
	@Autowired
	AuthenticationService authenticationService;
		
	@GetMapping("/")
	String index(Model model, Pager pager) {
		pager.setPerPage(4); //한 페이지 당 4개씩 보는걸로 세팅
		List<KeycapVo> keycap = serviceKeycap.list(pager);
		List<KeycapVo> keycapBest = serviceKeycap.listBest(pager);
		List<KeycapVo> keycapReviewDesc= serviceKeycap.listReviewDesc(pager);
		
		// 각 상품(신상품,베스트상품)에 대한 리뷰 수와 별점 평균을 저장할 맵
	    Map<Integer, Integer> reviewTotals = new HashMap<>();
	    Map<Integer, Double> averageStars = new HashMap<>();
	    Map<Integer, Integer> reviewTotalsBest = new HashMap<>();
	    Map<Integer, Double> averageStarsBest = new HashMap<>();
	    
	    for (KeycapVo keycapVo : keycap) {
	    	// pager 객체를 새로 생성하여 각 키캡에 대한 리뷰를 별도로 조회
			Pager reviewPager = new Pager();
	    	List<ReviewVo> reviews = serviceReview.list(keycapVo.getKeycapNum(), reviewPager);
	        double totalStars = 0;
	        for (ReviewVo review : reviews) {
	            totalStars += review.getReviewStar();
	        }
	        
	        //별점 평균
	        double averageStar = (reviews.size() > 0) ? totalStars / reviews.size() : 0;
	        
	        // 평균 별점을 소수점 첫째 자리까지만 표시하도록 반올림
	        averageStar = Math.round(averageStar * 10) / 10.0;
	        
	        reviewTotals.put(keycapVo.getKeycapNum(), reviews.size());
	        averageStars.put(keycapVo.getKeycapNum(), averageStar);
	    }
	    
	    for (KeycapVo keycapVo : keycapBest) {
	    	// pager 객체를 새로 생성하여 각 키캡에 대한 리뷰를 별도로 조회
			Pager reviewPager = new Pager();
	    	List<ReviewVo> reviews = serviceReview.list(keycapVo.getKeycapNum(), reviewPager);
	        double totalStars = 0;
	        for (ReviewVo review : reviews) {
	            totalStars += review.getReviewStar();
	        }
	        
	        //별점 평균
	        double averageStar = (reviews.size() > 0) ? totalStars / reviews.size() : 0;
	        
	        // 평균 별점을 소수점 첫째 자리까지만 표시하도록 반올림
	        averageStar = Math.round(averageStar * 10) / 10.0;
	        
	        reviewTotalsBest.put(keycapVo.getKeycapNum(), reviews.size());
	        averageStarsBest.put(keycapVo.getKeycapNum(), averageStar);
	    }
	    
	    for (KeycapVo keycapVo : keycapReviewDesc) {
	    	// pager 객체를 새로 생성하여 각 키캡에 대한 리뷰를 별도로 조회
			Pager reviewPager = new Pager();
	    	List<ReviewVo> reviews = serviceReview.list(keycapVo.getKeycapNum(), reviewPager);
	        double totalStars = 0;
	        for (ReviewVo review : reviews) {
	            totalStars += review.getReviewStar();
	        }
	        
	        //별점 평균
	        double averageStar = (reviews.size() > 0) ? totalStars / reviews.size() : 0;
	        
	        // 평균 별점을 소수점 첫째 자리까지만 표시하도록 반올림
	        averageStar = Math.round(averageStar * 10) / 10.0;
	        
	        reviewTotalsBest.put(keycapVo.getKeycapNum(), reviews.size());
	        averageStarsBest.put(keycapVo.getKeycapNum(), averageStar);
	    }
	    
	    model.addAttribute("keycap", keycap);
	    model.addAttribute("keycapBest", keycapBest);
	    model.addAttribute("keycapReviewDesc", keycapReviewDesc);
	    model.addAttribute("reviewTotals", reviewTotals); // 신상품에 따른 리뷰 수 를 모델에 추가.
	    model.addAttribute("averageStars", averageStars); // 신상품에 따른 별점 평균을 모델에 추가.
	    model.addAttribute("reviewTotalsBest", reviewTotalsBest); // 베스트상품에 따른 리뷰 수 를 모델에 추가.
	    model.addAttribute("averageStarsBest", averageStarsBest); // 베스트품에 따른 별점 평균을 모델에 추가.
	    
		return "index";
	}

	// @ResponseBody를 하면 뷰리졸버가 jsp를 찾지않고, 후처리를 하지않고 그대로 클라이언트에게 보내주는 지시를 함
	@ResponseBody
	// id가 null 이면 OK, 아니면 FAIL => 중복검사를 했을때 아이디가 없으면 OK, 있으면 FAIL
	@GetMapping("/confirmId")
	String confirmId(String userId) {
		if (service.item(userId) == null) {
			return "OK";
		}

		return "FAIL";
	}

	@GetMapping("/signup")
	String signup() {
		return "signup";
	}

	@PostMapping("/signup")
	String signup(UserVo item, 
			@RequestParam String userAddress1, @RequestParam String userAddress2,
			@RequestParam String userTel1, @RequestParam String userTel2, @RequestParam String userTel3,
			RedirectAttributes redirectAttributes) {
		// 회원가입한 사용자 비밀번호 암호화 처리
        String passwordHash = PasswordUtil.hashPassword(item.getUserPw());
        
        // 비밀번호 암호화 처리에 성공하면 회원가입 처리
        if(!passwordHash.isEmpty()) {
        	item.setUserPw(passwordHash);
        	item.setUserAddress(userAddress1 + "@" + userAddress2);
    		item.setUserTel(userTel1 + userTel2 + userTel3);
    		service.add(item);
    		
    		// 회원가입 성공 메시지 전달
    	    redirectAttributes.addFlashAttribute("signupMessage", "회원가입이 완료되었습니다!");
        }
		return "redirect:.";
	}
	
	@GetMapping("/login")
	String login(Model model) {
		
		//kakao 로그인 url 설정(scope에서 email 말고 profile로도 할수 있음)
		String kakaoUrl = "https://kauth.kakao.com/oauth/authorize?" 
		        + "client_id=57fa5c05405bb7a6a1ba8cb814b8a07e" 
		        + "&redirect_uri=http://13.125.249.221:8090/kakaoLogin" 
		        + "&response_type=code";
		
		model.addAttribute("kakaoUrl", kakaoUrl);      
		
		//google 로그인 url 설정(scope에서 email 말고 profile로도 할수 있음)
		String googleUrl = "https://accounts.google.com/o/oauth2/v2/auth?" 
                + "scope=email profile" 
                + "&response_type=code" 
                + "&state=security_token%3D138r5719ru3e1%26url%3Dhttps://oauth2.example.com/token" 
                + "&client_id=" + "422521090205-gtff0vs1pd429k6q7thrm33cdgqs0jim.apps.googleusercontent.com"
                + "&redirect_uri=" + "http://13.125.249.221:8090/login/oauth2/googleLogin/google"
                + "&access_type=offline";
                
        model.addAttribute("googleUrl", googleUrl);
        
      //naver 로그인 url 설정(scope에서 email 말고 profile로도 할수 있음)
      String naverUrl = "https://nid.naver.com/oauth2.0/authorize?" 
              + "&response_type=code" 
              + "&client_id=" + "RPoUC7EHDQYGWDzMhYWh"
              + "&redirect_uri=" + "http://13.125.249.221:8090/naver/login";
      	model.addAttribute("naverUrl", naverUrl);
      	
		return "login";
	}

	@PostMapping("/login")
	public RedirectView login(UserVo item, HttpSession session, HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes) {
	    if (service.login(item)) {
	        session.setAttribute("user", item);
	        
	        // 사용자 인증 JWT 토큰 처리
	        boolean isAuthenticated = authenticationService.authenticateUser(item, response, request);
	        
	        // 인증 실패 시 로그인 화면으로 리다이렉션
	        if (!isAuthenticated) {
	        	redirectAttributes.addFlashAttribute("error", "사용자 인증 처리가 실패하였습니다.");
	            return new RedirectView("/login");
	        } else {
	        	// 인증 성공 시 홈 화면으로 리다이렉션
	            return new RedirectView("/");
	        }
	        
	    }
	    
	    // 로그인 실패 시 다시 로그인 화면으로 리다이렉션
	    redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 틀립니다.");
	    return new RedirectView("/login");
	    	
	    	// * TODO - 예전코드
//	        // 토큰 생성
//	        SecureRandom random = new SecureRandom();
//	        byte[] keyBytes = new byte[32]; // 32 bytes = 256 bits
//	        random.nextBytes(keyBytes);
//	        String secretKey = Base64.getEncoder().encodeToString(keyBytes); //랜덤하게 생성된 크기의 바이트를 임시 시크릿키로 사용
//	        
//	        byte[] key = Base64.getDecoder().decode(secretKey);
//	        
//	        String jws = Jwts.builder()
//	                .claim("sub", item.getUserId())
//	                .signWith(Keys.hmacShaKeyFor(key))
//	                .compact();
//	        
//	    	// 토큰을 쿠키에 저장
//	        /* Cookie도 LocalStorage랑 마찬가지로 XSS에 탈취당할 가능성이있음. 하지만 Cookie에는 HttpOnly라는 
//	          	옵션이 존재하는데 이 옵션을 지정하면 Script에서 Cookie를 읽어올 수 없게한다. 
//	          	이로인해 악의적인 Script에서 Cookie를 가져올 수 없기 때문에 XSS공격에 방어가 된다. */
//	        /* localhost 즉 나의 localhost8090 에서 쓰일것이며 모든경로(Path=/)에서 쓰일 것인 jwt 토큰을 쿠키에 저장한다.
//	         * 쿠키에 'HttpOnly' 속성을 설정하면 JavaScript에서 쿠키에 접근할 수 없게 됨
//	         * 이 속성은 XSS 공격을 방지하는 데 도움이 되지만, 로컬 개발 환경에서는 문제(jwt 토큰이 쿠키에 저장 되지않는 문제 등)를 일으킬 수 있다.
//	         */
//	        response.setHeader("Set-Cookie", "token=" + jws + "; Domain=localhost; Path=/");
//	        
//	        /*"token"이라는 이름의 쿠키를 생성하고, 그 값으로 jws를 설정하며, 이 쿠키는 HTTPS 연결에서만 전송되고 클라이언트 측 스크립트에 의한 접근이 차단 하여 XSS를 방어함
//	         * 근데 localhost는 http 이므로 Secure; 즉 Secure 가 true 면 HTTPS 전용 쿠키가 설정되기에 쓸 수 없다.(이게 보안에 더 좋음 보통 사이트는 https니까..)
//	         
//	        	response.setHeader("Set-Cookie", "token=" + jws + "; Secure; HttpOnly");
//	        */
//	        
//	        // CSRF 토큰 생성 및 세션에 저장
//	        //클라이언트에서 요청을 보낼 때 'X-CSRF-TOKEN' 헤더에 이 값을 설정하면, 서버에서는 세션에 저장된 CSRF 토큰과 요청에 담긴 CSRF 토큰이 일치하는지 확인하여 요청의 유효성을 검증할 수 있다.
//	        String csrfToken = UUID.randomUUID().toString(); // UUID 클래스를 이용하여 랜덤한 CSRF 토큰을 생성
//	        session.setAttribute("CSRF-TOKEN", csrfToken);
	} 
	
	@GetMapping("/logout")
	String logout(HttpSession session, HttpServletResponse response) {
		// 세션을 무효화(invalidate)
		session.invalidate();
		
		//쿠키에 저장된 jwt 토큰 을 만료함(jwt를 쿠키에 저장해 둬야 이렇게 로그아웃 시 만료 시킬 수 있음. 다른곳에 저장하면 만료시간을 따로 정해줘야함..)
		Cookie cookie = new Cookie("token", null); // 쿠키의 이름을 "token"으로, 값을 null로 설정
		cookie.setMaxAge(0); // 쿠키의 만료 시간을 0으로 설정하여 쿠키를 즉시 만료
		response.addCookie(cookie); // 응답에 쿠키를 추가하여 클라이언트에게 전달

		return "redirect:.";
	}

	// Admin 관련
	@RequestMapping("/indexAdmin")
	String indexAdmin() {
		return "indexAdmin";
	}

	@GetMapping("/loginAdmin")
	String loginAdmin() {
		return "loginAdmin";
	}
	
	@PostMapping("/loginAdmin")
	public RedirectView loginAdmin(AdminVo item, HttpSession session, HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		if (serviceAdmin.loginAdmin(item)) {
			//adminVo 에서 값 가져올시 adminServiceImpl에서 login 항목참조
			session.setAttribute("admin", item);
			
			// 사용자 인증 JWT 토큰 처리
	        boolean isAuthenticated = authenticationService.authenticateUser(item, response, request);
	        
	        // 인증 실패 시 관리자 로그인 화면으로 리다이렉션
	        if (!isAuthenticated) {
	        	redirectAttributes.addFlashAttribute("error", "관리자 인증 처리가 실패하였습니다.");
	            return new RedirectView("/loginAdmin");
	        } else {
	        	// 로그인 성공 시  관리자 화면으로 리다이렉션
		        return new RedirectView("/indexAdmin");
	        }
	        
			// * TODO - 예전코드
//			// 토큰 생성
//	        SecureRandom random = new SecureRandom();
//	        byte[] keyBytes = new byte[32]; // 32 bytes = 256 bits
//	        random.nextBytes(keyBytes);
//	        String secretKey = Base64.getEncoder().encodeToString(keyBytes); //랜덤하게 생성된 크기의 바이트를 임시 시크릿키로 사용
//	        
//	        byte[] key = Base64.getDecoder().decode(secretKey);
//	        
//	        String jws = Jwts.builder()
//	                .claim("sub", item.getAdminId())
//	                .signWith(Keys.hmacShaKeyFor(key))
//	                .compact();
//	        
//	    	// 토큰을 쿠키에 저장
//	        /* Cookie도 LocalStorage랑 마찬가지로 XSS에 탈취당할 가능성이있음. 하지만 Cookie에는 HttpOnly라는 
//	          	옵션이 존재하는데 이 옵션을 지정하면 Script에서 Cookie를 읽어올 수 없게한다. 
//	          	이로인해 악의적인 Script에서 Cookie를 가져올 수 없기 때문에 XSS공격에 방어가 된다. */
//	        /* localhost 즉 나의 localhost8090 에서 쓰일것이며 모든경로(Path=/)에서 쓰일 것인 jwt 토큰을 쿠키에 저장한다.
//	         * 쿠키에 'HttpOnly' 속성을 설정하면 JavaScript에서 쿠키에 접근할 수 없게 됨
//	         * 이 속성은 XSS 공격을 방지하는 데 도움이 되지만, 로컬 개발 환경에서는 문제(jwt 토큰이 쿠키에 저장 되지않는 문제 등)를 일으킬 수 있다.
//	         */
//	        response.setHeader("Set-Cookie", "token=" + jws + "; Domain=localhost; Path=/");
//	        
//	        /*"token"이라는 이름의 쿠키를 생성하고, 그 값으로 jws를 설정하며, 이 쿠키는 HTTPS 연결에서만 전송되고 클라이언트 측 스크립트에 의한 접근이 차단 하여 XSS를 방어함
//	         * 근데 localhost는 http 이므로 Secure; 즉 Secure 가 true 면 HTTPS 전용 쿠키가 설정되기에 쓸 수 없다.(이게 보안에 더 좋음 보통 사이트는 https니까..)
//	         
//	        	response.setHeader("Set-Cookie", "token=" + jws + "; Secure; HttpOnly");
//	        */
//	        
//	        // CSRF 토큰 생성 및 세션에 저장
//	        //클라이언트에서 요청을 보낼 때 'X-CSRF-TOKEN' 헤더에 이 값을 설정하면, 서버에서는 세션에 저장된 CSRF 토큰과 요청에 담긴 CSRF 토큰이 일치하는지 확인하여 요청의 유효성을 검증할 수 있다.
//	        String csrfToken = UUID.randomUUID().toString(); // UUID 클래스를 이용하여 랜덤한 CSRF 토큰을 생성
//	        session.setAttribute("CSRF-TOKEN", csrfToken);
	    }
	    
	    // 로그인 실패 시 다시 관리자 로그인 화면으로 리다이렉션
		redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 틀립니다.");
	    return new RedirectView("/loginAdmin");
	}

	@GetMapping("/logoutAdmin")
	String logoutAdmin(HttpSession session, HttpServletResponse response) {
		// 세션을 무효화(invalidate)
		session.invalidate();
				
		//쿠키에 저장된 jwt 토큰 을 만료함(jwt를 쿠키에 저장해 둬야 이렇게 로그아웃 시 만료 시킬 수 있음. 다른곳에 저장하면 만료시간을 따로 정해줘야함..)
		Cookie cookie = new Cookie("token", null); // 쿠키의 이름을 "token"으로, 값을 null로 설정
		cookie.setMaxAge(0); // 쿠키의 만료 시간을 0으로 설정하여 쿠키를 즉시 만료
		response.addCookie(cookie); // 응답에 쿠키를 추가하여 클라이언트에게 전달

		return "redirect:.";
	}
	@GetMapping("/test_list")
	public Map<String, String> testList(@RequestParam Map<String, String> params) {
        // 기본 데이터 생성
        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");
        
        // 클라이언트로부터 받은 파라미터와 병합
        data.putAll(params);
        
        return data;
	}
}
