<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<jsp:include page="head.jsp"></jsp:include>
<script type="text/javascript">	
$(document).ready(function() {
	const Object_test = {
	    key1: "value1",
	    key2: "value2"
	};

	const $test = {
	    ajax: {
	        url: '/test/list', // 실제 데이터를 요청할 서버의 URL
	        method: 'GET', // 또는 'POST', 서버와의 요청 방식에 따라
	        data: function(param) {
	            return $.extend({}, Object_test, param);
	        }
	    }
	};


	    $('#clickMe').click(function(e) {
	        console.log($test.ajax.data({}));
	        alert("클릭");
	    });

	});
</script>
<title>Insert title here</title>
</head>
<body>
<button id="clickMe">Click me</button>
</body>
</html>