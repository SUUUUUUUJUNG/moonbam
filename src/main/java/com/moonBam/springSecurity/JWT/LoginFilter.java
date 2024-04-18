package com.moonBam.springSecurity.JWT;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.moonBam.springSecurity.SpringSecurityUser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final Long expiredMs;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    	String userIdSave = request.getParameter("userIdSave");
    	String autoLogin = request.getParameter("autoLogin");
    	
    	System.out.println("아이디 저장: " + userIdSave);				//체크되면 on
    	
		//클라이언트 요청에서 username, password 추출
    	// String username = obtainUsername(request);
    	// String password = obtainPassword(request);
    	String username = request.getParameter("userId");
    	String password = request.getParameter("userPw");
        System.out.printf("Username: "+ username+" Password: "+ password);

		//스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
        
		//token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

	//로그인 성공시 실행하는 메소드 (여기서 JWT 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
				
		//인증객체에서 사용자 정보 추출(springSecurityUser - 아이디 / 패스워드 / 역할 / 활성화)
    	SpringSecurityUser springSecurityUser = (SpringSecurityUser) authentication.getPrincipal();

    	//사용자 정보 중 아이디 추출
        String username = springSecurityUser.getUsername();

        //사용자 정보 중 사용자 권한 목록 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        //사용자 권한 목록을 순회하는 iterator
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();

        //iterator 중 첫번째 권한을 auth에 저장
        GrantedAuthority auth = iterator.next();

        //role에 권한 객체에서 실제 권한을 추출한 것을 저장 
        String role = auth.getAuthority();

        //아이디와 역할을 통해 JWT토큰 생성(10시간 유지)
        String token = jwtUtil.createJwt(username, role, expiredMs);
        String cookieValue = "AuthToken=" + token + "; Path=/acorn; HttpOnly";
        if (request.isSecure()) { // HTTPS인 경우에만 Secure 플래그 추가
            cookieValue += "; Secure";
        }
        response.addHeader("Set-Cookie", cookieValue);
        	
        // 루트 주소로 리다이렉트
        response.sendRedirect("/acorn");
        
        //       response.sendRedirect("logining/");
    }

	//로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
    	response.sendRedirect("/login?error=true"); // 기본 로그인 페이지로 리디렉션
    }
}