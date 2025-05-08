(function() {
    const originalOpen = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function(method, url, async, user, password) {
        
        const jwtToken = getCookie('JWT');  // JWT 토큰을 쿠키에서 가져옴.
        
        // 로그인 상태인지 확인하는 조건
        if (jwtToken) {
            // 요청 헤더에 JWT 토큰을 추가
            this.setRequestHeader('Authorization', 'Bearer ' + jwtToken);
        }

        originalOpen.apply(this, arguments);  // 원래의 open 함수 호출
    };

    // 쿠키 값을 가져오는 함수
    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
    }
})();
