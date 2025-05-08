package kr.ac.jh.keycap.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@PropertySource("classpath:config/application.properties")  // 파일 경로 지정
public class WebConfig extends WebMvcConfigurerAdapter {
	
	@Value("${NODE_TOKEN_API_URL}")
    private String nodeTokenApiUrl;
	
	@Value("${JWT_SECRET_KEY}")
    private String jwtSecretKey;

//    @Bean
//    public WebConfig webConfig() {
//        return new WebConfig(); // Bean으로 등록
//    }

    public String getNodeTokenApiUrl() {
        return nodeTokenApiUrl;
    }
    
    public String getjwtSecretKey() {
        return jwtSecretKey;
    }
	
	//이 설정을 통해 '/images/'로 시작하는 URL 요청은 '/app/resources/img/HwicapUpload/userImgF/' 디렉토리로 매핑 즉 공유 폴더가 됨.
	// 근데 제 3자가 이 폴더가 없어도 공유 폴더이기에 그대로 사진이 보임. 보안적인 문제(제 3자가 나의 폴더를 보는 등)를 해결함.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        	registry.addResourceHandler("/images/**")
        			.addResourceLocations("file:/app/resources/img/HwicapUpload/userImgF/");
        	registry.addResourceHandler("/keycapImages/**")
			.addResourceLocations("file:/app/resources/img/HwicapUpload/keycapImgF/");
    }
    
}
