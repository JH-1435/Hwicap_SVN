<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<jsp:include page="head.jsp"></jsp:include>	
<script>
document.addEventListener('DOMContentLoaded', (event) => {		
	//이메일 선택
	document.getElementById('find_email').addEventListener('change', function() {
	    var selectedEmail = this.value;
	    var emailInput = document.getElementsByName('userAddress2')[0];
	    
	    emailInput.value = selectedEmail;
	    
	    if (selectedEmail === "") {
	    	// 직접입력을 선택한 경우, 입력 필드를 활성화.
	    	emailInput.removeAttribute('readonly');
	    } else {
	    	// 직접입력 외 이메일을 선택한 경우, 입력 필드를 읽기 전용으로 설정.
	    	emailInput.setAttribute('readonly', true);
	    }
	});
	
	
	//엔테키 누를 시 submit 함수 실행
	document.getElementById('signupForm').addEventListener('keydown', function(event) {
        var keyCode = event.keyCode;
        if (keyCode == 13) {
            event.preventDefault(); // 기본 이벤트를 취소합니다.
            submit();
        }
    });
});
//벨리데이션(검증)하는 코드들
function submit() {
	const form = document.signupForm;
	const phoneNum = form.userTel1.value + form.userTel2.value + form.userTel3.value;
	
	if(form.userId.value != form.id_confirm.value) {
		alert('아이디 중복확인을 하세요');
		return;
	}
	else if(form.userId.value == "") {
		alert('아이디를 입력 해 주세요');
		form.userId.focus();
		return;
	}
	else if(form.userPw.value == "") {
		alert('비밀번호를 입력 해 주세요');
		form.userPw.focus();
		return;
	}
	else if(form.userPw_confirm.value == "") {
		alert('비밀번호를 입력 해 주세요');
		form.userPw_confirm.focus();
		return;
	}
	else if(form.userPw.value != form.userPw_confirm.value) {
		alert('비밀번호가 일치하지 않습니다');
		form.userPw_confirm.value = "";
		form.userPw_confirm.focus();
		return;
	}
	else if(form.userName.value == "") {
		alert('이름을 입력 해 주세요');
		form.userName.focus();
		return;
	}
	else if(form.userTel1.value == "" || form.userTel2.value == "" || form.userTel3.value == "") {
		alert('번호를 입력 해 주세요');
		if(form.userTel1.value == "") {
			form.userTel1.focus();
		}
		else if(form.userTel2.value == "") {
			form.userTel2.focus();
		}
		else if(form.userTel3.value == "") {
			form.userTel3.focus();
		}
		return;
	}
	else if(phoneNum.length < 10 || phoneNum.length > 11) {
        alert("번호를 제대로 입력해 주세요.");
        form.userTel1.focus();
        return; 
    }
	else if(form.userAddress1.value == "") {
		alert('이메일을 입력 해 주세요');
		form.userAddress1.focus();
		return;
	}	
	else if(form.userAddress2.value == "") {
		alert('이메일을 입력 해 주세요');
		form.userAddress2.focus();
		return;
	}
	form.submit();
}
//1 == '1' => value(값)이 같으면 true, |  1 === '1' => type과 value 가 같아야 하므로 false
// confirmId 에서 사용한 코드들 = 에이젝, 즉 에이젝을 사용함
function confirmId(){
	// 에이젝을 사용하기 위한 객체생성
	const form = document.signupForm;
	
	const xhr = new XMLHttpRequest();
	
	// 비동기방식(true),에이젝의 제대로된 성능을 내려면 비동기 방식 채용
	xhr.open("GET", "confirmId?userId=" + form.userId.value, true);  
	
	xhr.onreadystatechange = function() {
		// DONE(처리가 됨)인 상태가 된다면..
		if(xhr.readyState == XMLHttpRequest.DONE) {
							
			// AJAX 요청이 정상적으로 처리되었는지 아닌지만을 검사하기 위해 응답 코드가 200 인지 확인
			if(xhr.status == 200) {
				if(xhr.responseText == 'OK') { 
					if(form.userId.value != "")
					{
						alert("사용할수 있는 아이디 입니다");
						form.id_confirm.value = form.userId.value;
					}
					else if(form.userId.value == "")
					{
						alert('아이디를 입력 해 주세요');
						form.userId.focus();
					}
				} else {
					alert('다른 사용자가 이미 사용하고 있는 아이디 입니다');
				}
			}
		}	
	};
	
	xhr.send();
	
}
</script>
</head>
<body>
<div class="wrap">
<header id="header-wrap">
<div class="status-bar">
	<div class="nav" style="padding-top: 46.67px;">
        <div class="logo-container">
            <a href="/"><img src="img/logo-main.png" class="logo"/></a>
        </div>
        <div class="nav-list-container">
            <ul class="nav-list">
            	<li><a href="/">HOME</a></li>
                <li><a href="/keycap/list?sort=null">NEW</a></li>
                <li><a href="/keycap/list?sort=best">BEST</a></li>
                <c:if test="${sessionScope.user == null}">
                	<li><a href="/login">CONTACT</a></li>
                </c:if>
                <c:if test="${sessionScope.user != null}">
                	<li><a href="/board/${sessionScope.user.userId}/addCs">CONTACT</a></li>
                </c:if>
            </ul>
        </div>
    </div>
</div>
</header>
<!-- <button>을 쓰면 내 의도와 상관없이 submit 되어버림,고로 form태그 바깥으로 놔야함 -->
	<div class="sign-wrap">
	<!-- Title  -->
		<div class="login-text">
			<h2>회원가입</h2>
		</div>
		<div class="signup-text">
			<i class="fas fa-check"></i>
			<span>항목은 <span style="color: #333;">필수 입력</span> 항목입니다.</span>
		</div>
		<!-- Form 입력 -->
		<div class="signup-form">
			<form id="signupForm" name="signupForm" method="post">
				<input name="id_confirm" type="hidden" value="" >
				<div class="login-entry">
				<fieldset>
				<div class="signup-list">
				<ul class="text">
					<li class="th"><p>아이디</p><i class="fas fa-check"></i></li>
					<li class="td">
					<!-- 특수문자, 띄어쓰기,한국어 등 사용불가 replace(/[^a-zA-Z0-9]/g, '') -->
						<input name="userId" type="text" oninput="this.value = this.value.replace(/[^a-zA-Z0-9]/g, '');">
						<button type="button" onclick="location.href='javascript:confirmId()';" style="cursor: pointer;">중복검사</button>
					</li>
				</ul>
				<ul class="text">	
					<li class="th"><p>비밀번호</p><i class="fas fa-check"></i></li>
					<li class="td"><input name="userPw" type="password" oninput="this.value = this.value.replace(/[^a-zA-Z0-9]/g, '');"></li>
				</ul>
				<ul class="text">
					<li class="th"><p>비밀번호 재확인</p><i class="fas fa-check"></i></li>
					<li class="td"><input name="userPw_confirm" type="password" oninput="this.value = this.value.replace(/[^a-zA-Z0-9]/g, '');"></li>
				</ul>
				<ul class="text">
					<li class="th"><p>이름</p><i class="fas fa-check"></i></li>
					<!-- 입력 값이 변경될 때마다 oninput 이벤트 핸들러가 실행되어 한글 입력 중인 문자까지 제거되기 때문에 발생하기에.. oniput을 않씀 -->
					<li class="td"><input name="userName" type="text"></li>
				</ul>
				<ul class="text">
					<li class="th"><p>전화번호</p><i class="fas fa-check"></i></li>
					<li class="td">
						<input class="userTel" name="userTel1" type="tel" oninput="this.value = this.value.replace(/\D/g, '');" maxlength="4">
						<em>-</em>
						<input class="userTel" name="userTel2" type="tel" oninput="this.value = this.value.replace(/\D/g, '');" maxlength="4">
						<em>-</em>
						<input class="userTel" name="userTel3" type="tel" oninput="this.value = this.value.replace(/\D/g, '');" maxlength="4">
					</li>
				</ul>
				<ul class="text">
					<li class="th"><p>이메일</p><i class="fas fa-check"></i></li>
					<li class="td">
						<input class="userAddress1" name="userAddress1" type="text" oninput="this.value = this.value.replace(/[^a-zA-Z0-9]/g, '');" required>
						<em>@</em>
						<input class="userAddress2" name="userAddress2" type="text"  oninput="this.value = this.value.replace(/[^a-zA-Z0-9.]/g, '');" required >
						<select name="find_email" id="find_email">
							<option value="">직접입력</option>
								<option value="naver.com">naver.com</option>
								<option value="nate.com">nate.com</option>
								<option value="dreamwiz.com">dreamwiz.com</option>
								<option value="yahoo.co.kr">yahoo.co.kr</option>
								<option value="empal.com">empal.com</option>
								<option value="unitel.co.kr">unitel.co.kr</option>
								<option value="gmail.com">gmail.com</option>
								<option value="korea.com">korea.com</option>
								<option value="chol.com">chol.com</option>
								<option value="paran.com">paran.com</option>
								<option value="freechal.com">freechal.com</option>
								<option value="hanmail.net">hanmail.net</option>
								<option value="hotmail.com">hotmail.com</option>
						</select>
					</li>
				</ul>
				</div>
					</fieldset>
				</div>
				</form>
			<!-- form태그 바깥에 버튼을 두면 엔터키가 안먹힘 -->
			<div id="signup02">
				<button onclick="submit()" class="signup-btn">입력 완료</button>
			</div>
		</div>
	</div>
	<div class="footer">
			<ul class="footer_contents">
				<li class="footer-a">[34503] 대전광역시 동구 우암로 352-21  [대표전화] 042-670-0600  
					COPYRIGHT 2010 BY KOREA POLYTECHNICS. ALL RIGHTS RESERVED.</li>
					
					<li class="footer-b">CUSTOMER CENTER
					<em style="color: #333; font-size: x-large;">042-637-0000</em>
					MON-FRI : 10 : 00 ~ 18 : 00
					LUNCH   : 12 : 00 ~ 13 : 00  /  SAT, SUN, HOLIDAY OFF
					</li>
					
				<li class="footer-c"><img src="/resources/img/logo-footer.png"></li>
			</ul>
		</div>
</div>
</body>
</html>