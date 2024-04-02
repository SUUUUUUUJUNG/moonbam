package com.moonBam.controller.member;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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
import org.springframework.web.servlet.ModelAndView;

import com.moonBam.dto.MemberDTO;
import com.moonBam.service.member.LoginService;
import com.moonBam.service.member.OpenApiService;


@Controller
public class LoginController {

	@Autowired
	LoginService serv;
	
	@Autowired
	MailController mc;
	
	@Autowired
	SecurityController sc;
	
	@Autowired
	OpenApiService openApiService;
	
	@RequestMapping("/Login")   
	public String Login() {
		return "member/Login/loginMain";
	}
	
	@RequestMapping("/FindInfo")   
	public String FindInfo() {
		return "member/Find_Info/searchMemberData";
	}
	
	@RequestMapping("/FindAllPW")   
	public String FindAllPW() {
		return "member/Find_Info/childFindAllPW";
	}
	
	//로그인
	@PostMapping("/Logined")
	public String LoginToMypage(String userId, String userPw, HttpSession session, boolean userIdSave,  HttpServletResponse response, boolean autoLogin) throws NoSuchAlgorithmException, UnsupportedEncodingException, GeneralSecurityException {
		String realUserPw = sc.encrypt(userPw);
		System.out.println("아이디 저장: " + userIdSave);				//체크되면 true
		System.out.println("자동 로그인: " + autoLogin);					//체크 안 되면 false
		MemberDTO dto = serv.login(userId, realUserPw);

		if (dto != null) {
			session.setAttribute("loginUser", dto);

			if(autoLogin) {
				
				Cookie autoId= new Cookie("userId", userId);
				Cookie autoPW= new Cookie("userPw", userPw);
				autoId.setMaxAge(60*60*24);
				autoPW.setMaxAge(60*60*24);
				response.addCookie(autoId);
				response.addCookie(autoPW);
				
//				System.out.println("등록 오토 아이디"+ autoId);
//				System.out.println("등록 오토 패스"+ autoPW);

			} else {
				
				Cookie autoId= new Cookie("userId", null);
				Cookie autoPW= new Cookie("userPw", null);
				autoId.setMaxAge(0);
				autoPW.setMaxAge(0);
				response.addCookie(autoId);
				response.addCookie(autoPW);

//				System.out.println("삭제 오토 아이디"+ autoId);
//				System.out.println("삭제 오토 패스"+ autoPW);
				
				if(userIdSave) {
					Cookie id= new Cookie("userId", userId);
					id.setMaxAge(60*60*24);
					response.addCookie(id);
					
//					System.out.println("등록 저장 아이디"+ id);
				} else {
					Cookie id= new Cookie("userId", null);
					id.setMaxAge(0);
					response.addCookie(id);
					
//					System.out.println("삭제 저장 아이디"+ id);
				}
				
			}
			
			return "main";
		} else {
			return "member/Find_Info/cantFindUserdata";
		}
	}

	//로그아웃
	@GetMapping("/Logout")
	public String Logout(HttpSession session) {
		MemberDTO dto = (MemberDTO) session.getAttribute("loginUser");
		if (dto != null) {
			session.removeAttribute("loginUser");
			return "main";
		} else {
			return "member/Find_Info/cantFindUserdata";
		}

	}
	
	//아이디 찾기
	@PostMapping("/SearchID")
	public String SearchID(Model model, String restoreEmail) {
		String[] emailParts = restoreEmail.split("@");
		Map<String, String> map = new HashMap<>();
			map.put("restoreUserEmailId", emailParts[0]);
			map.put("restoreUserEmailDomain", emailParts[1]);
		MemberDTO dto = serv.findUserId(map);
		if (dto != null) {
			model.addAttribute("dto", dto);
			return "member/Find_Info/viewID";
		} else {
			return "member/Find_Info/cantFindUserdata";
		}

	}
	
	//비밀번호 찾기
	@PostMapping("/SearchPartPW")
	public String SearchPartPW(Model model, HttpServletResponse response, String userId) throws NoSuchAlgorithmException, UnsupportedEncodingException, GeneralSecurityException {
		System.out.println(userId);
		MemberDTO dto = openApiService.selectOneAPIMember(userId);
		System.out.println(dto);
		if (dto != null) {
			String password = sc.decrypt(dto.getUserPw());
			int visible = (int) Math.ceil(password.length() / 2);
			String masked = "*".repeat(password.length() - visible);
			String maskedPW = password.substring(0, visible) + masked;
			
			Cookie userIdCookie = new Cookie("findPW_userId",userId);
			userIdCookie.setMaxAge(30*60);
			response.addCookie(userIdCookie);
			
			model.addAttribute("dto", dto);
			model.addAttribute("maskedPW", maskedPW);
			return "member/Find_Info/viewPartPW";
		} else {
			return "member/Find_Info/cantFindUserdata";
		}
	}
	
	//비밀번호 변경을 위한 메일 송신
		@GetMapping("/SearchAllPW")
		public String SearchAllPW(Model model, HttpSession session, HttpServletResponse response, HttpServletRequest request) throws Exception {
			
			String userId = "";
			Cookie[] cookies = request.getCookies();
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("findPW_userId")) {
					userId = cookie.getValue();
				}
			}
			
			MemberDTO dto = openApiService.selectOneAPIMember(userId);
			
			if (dto != null) {

				Cookie userIdCookie = new Cookie("findPW_userId",null);
				userIdCookie.setMaxAge(0);
				
				String[] emailparts = dto.getUserId().split("@");
				
				model.addAttribute("dto", dto);
				model.addAttribute("emailDomain", emailparts[1]);
				mc.sendEmail(dto.getUserId(), dto);

				response.addCookie(userIdCookie);
				
				return "member/Find_Info/viewAllPW";
			} else {
				return "member/Find_Info/emailErrorPage";
			}

		}
		
		//이메일 하이퍼링크를 통해 들어오는 비밀번호 변경 페이지
		@GetMapping("/UpdatePasswordPage")
		public ModelAndView UpdatePasswordPage(String userId) {
			ModelAndView mav = new ModelAndView();
				mav.addObject("userId", userId);
				mav.setViewName("member/Find_Info/updatePassword");
			return mav;
		}
		
		//비밀번호 변경
		@PostMapping("/UpdatePassword")
		public String UpdatePassword(String userId, String userPw) {
			
			String realPassword = sc.encrypt(userPw);
			
			Map<String, String> map = new HashMap<>();
				map.put("userId", userId);
				map.put("userPw", realPassword);
				serv.updatePassword(map);
			return "redirect:/UpdateComplete";
		}
		
		//비밀번호 변경 완료
		@GetMapping("UpdateComplete")
		public String UpdateComplete() {
			return "member/Find_Info/updateComplete";
		}
		
	
	
}
