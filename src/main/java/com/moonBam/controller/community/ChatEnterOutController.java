




//ChatRoomController로 합쳐져서 현재는 사용 안 해도 되는 파일 04/15-미지









//package com.moonBam.controller.community;
//
//
//
//import java.util.HashMap;
//import java.util.Map;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.web.servlet.support.RequestContextUtils;
//
//import com.moonBam.dto.ChatMemberDTO;
//import com.moonBam.dto.ChatRoomDTO;
//import com.moonBam.dto.MemberDTO;
//import com.moonBam.service.CommunityEnterOutService;
//
//
//@Controller
//public class ChatEnterOutController {
//	
//	@Autowired
//	CommunityEnterOutService comEnterOutService;
//		
//	////////////[재사용할 함수fn]
//	
//	//1.(chatNum 으로 채팅방 정보 가져오기)
//	public ChatRoomDTO chatRoomSelectBychatNum(int chatNum) {
//	
//	return comEnterOutService.chatRoomSelectById(chatNum);
//	
//	}
//	
//	//2. chatNum과 userId를 map에 넣어서 chatmember select하기
//	public ChatMemberDTO chatmemberSelectFn(Map<String, Object> chatMemberselectMap ) {
//		
//		return comEnterOutService.chatMemberEnterSelect(chatMemberselectMap);
//		
//	}
//	
//
////////////////////////////////////////////////
//	
//	
//	@RequestMapping("/chatRoom")
//	public String chatMemberInsert(@Param("chatNum") int chatNum, HttpSession session, RedirectAttributes redirectAttributes) {
//		//chatRoom enter(insert) 후 권한 일치 검사 후 채팅방 입장 시키기
//		//insert 시 필요한 자료는 chatNum (쿼리스트링으로 딸려옴) 그리고 userId(session에 있음)가 필요
//		
//		MemberDTO memberDto = (MemberDTO)session.getAttribute("loginUser");
//		String userIdInSession = memberDto.getUserId(); //현재 나의 Id
//		
//		//enter를 위한 2개의 data가 들어갈 것임
//		Map<String, Object> chatMemberInsertMap = new HashMap<>();
//		//select조건 2개 map에 저장
//		chatMemberInsertMap.put("userId", userIdInSession);
//		chatMemberInsertMap.put("chatNum", chatNum);
//		System.out.println("chatMemberInsertMap 확인 chatRoomInsert "+chatMemberInsertMap);
//		
//		String nextjsp = "redirect:/chatRoom/enter"; //정상 진행jsp
//		
//		//일단 중복된 데이터가 없는지 확인 후 insert 진행하기 (중복 저장 방지를 위해~)
//		ChatMemberDTO chatMemberDto = comEnterOutService.chatMemberEnterSelect(chatMemberInsertMap);
//		int recordNum = 0;
//		if(chatMemberDto == null) {//null일 때만 insert
//		
//			//Insert
//			try {	
//				recordNum = comEnterOutService.chatMemberEnterInsert(chatMemberInsertMap);
//				
//				if(recordNum==0) { //인서트를 했는데! 값이 0일경우는 error임
//					nextjsp = "redirect:/?cg=community"; //커뮤니티홈으로 이동
//				}
//			
//			}catch(Exception e){
//				//모종의 이유로 Insert 실패 시 error임
//				System.out.println("chatMember insert 실패");
//				nextjsp = "redirect:/?cg=community"; //커뮤니티홈으로 이동
//			}
//			
//		}//if(chatMemberDto ==  null) end
//		
//	
//		//chatRoomSelect 컨트롤러로 data를 넘기기 위해 저장
//		redirectAttributes.addFlashAttribute("chatMemberInsertMap",chatMemberInsertMap);
//		
//		
//		return nextjsp;
//		
//	}
//	
//	@RequestMapping("/chatRoom/enter")
//	public String chatMemberSelect(HttpServletRequest request) {
//	
//		//DB check 입장 승인할지 말지 결정 (chatNum, UserId 일치여부 확인)
//		//chatNum과 userIdInSession을 조건으로 가진 select 결과가 있는지 없는지 ChatMemberDTO가져와서 null이 아닐 때만 링크 접속하게 하기
//		//나중에 여기에 "강퇴"칼럼의 Y,N 값을 확인해야함 (N만 입장 가능)
//		Map<String, ?> flashMap =RequestContextUtils.getInputFlashMap(request);
//		Map<String, Object> chatMemberselectMap = (Map<String, Object>) flashMap.get("chatMemberInsertMap");
//		System.out.println("chatMemberInsertMap 확인 chatRoomSelect "+chatMemberselectMap);
//		
//		//정상진행 시 chatRoom.jsp로 진입
//		String returnWhere = "community/chatRoom"; //chatRoom.jsp
//	
//		try {
//			
//			ChatMemberDTO chatMemberDto = comEnterOutService.chatMemberEnterSelect(chatMemberselectMap);
//			System.out.println("chatRoomSelect  "+chatMemberDto);
//			
//			
//			if(chatMemberDto == null ) {
//				returnWhere = "redirect:/?cg=community"; //커뮤니티목록으로 다시 리턴
//			}
//			
//			
//			
//		}catch(Exception e){
//			
//			System.out.println("chatMember select 실패");
//			returnWhere = "redirect:/?cg=community"; //커뮤니티목록으로 다시 리턴
//		}
//		
//		
//		/////////request에 저장하여 jsp로 chatNum 전달함 (더보기에서 사용할 예정)
//		request.setAttribute("ChatRoomDTO", this.chatRoomSelectBychatNum( (int) chatMemberselectMap.get("chatNum")));
//		
//		
//		return returnWhere;
//	}
//	
//	////////////////////방 나가기 눌렀을 때///////////////////////////////
//	@RequestMapping("/chatRoom/out")
//	public String chatRoomOut(@RequestParam("userId") String userId, @RequestParam("chatNum") int chatNum ) {
//		//form data로 받아온 값 2개로 chatMember table에서 delete 진행
//		
//		Map<String, Object> chatMemberDeleteMap = new HashMap<>();
//		chatMemberDeleteMap.put("userId", userId);
//		chatMemberDeleteMap.put("chatNum", chatNum);
//		
//		//정상진행 시 커뮤니티 홈으로 진입 -> 그런데 잘 delete처리 돼서 목록에 없는지 확인하기 위해 /chatRoom/enter로 리다이렉트
//		//만약 잘 지워졌다면 검수 기능으로 인해 커뮤니티 홈으로 가게 될 것임
//		String returnWhere = "redirect:/chatRoom/enter"; 
//		
//		try {
//			
//			int deletNum = comEnterOutService.chatMemberDeleteBychatNumAndUserId(chatMemberDeleteMap);
//			
//			
//			//delete를 했는데 0이다? 그러면 문제 있는 것임. 그때는 그 자리에 그대로 있어야함
//			if(deletNum==0) {
//				returnWhere = "redirect:/Chatmore";
//			
//		}
//		
//		}catch(Exception e) {
//			
//			//에러 발생 시
//			System.out.println("chatRoomOut delete 실패");
//			returnWhere = "redirect:/Chatmore";
//		}
//		
//	
//		return returnWhere;
//	}
//	
//	
//}
