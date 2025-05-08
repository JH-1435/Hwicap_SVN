package kr.ac.jh.keycap.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.ac.jh.keycap.model.UserAccountVo;

public interface AuthenticationService {

	boolean authenticateUser(UserAccountVo item, HttpServletResponse response, HttpServletRequest request);
	
}
