package com.moonBam.controller.community;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.moonBam.dto.ChatMemberDTO;
import com.moonBam.dto.ChatRoomDTO;
import com.moonBam.service.CommunityEnterOutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ChatRoomController {
	
	
	@Autowired
	CommunityEnterOutService comEnterOutService;
		
	////////////[재사용할 함수fn]
	
	//1.(chatNum 으로 채팅방 정보 가져오기)
	public ChatRoomDTO chatRoomSelectBychatNum(int chatNum) {
	
	return comEnterOutService.chatRoomSelectById(chatNum);
	
	}
	
	//2. chatNum과 userId를 map에 넣어서 chatmember select하기
	public ChatMemberDTO chatmemberSelectFn(Map<String, Object> chatMemberselectMap ) {
		
		return comEnterOutService.chatMemberEnterSelect(chatMemberselectMap);
		
	}
	
	
	/////방 입장을 위해 방 클릭하면 이렇게 먼저 중복제외하고 insert 진행
	@RequestMapping(value="/chatRoom",  method = RequestMethod.GET)
	public String chatRoomGet(@Param("chatNum") int chatNum, HttpSession session, Principal principal) {

		String userIdInSession = principal.getName();//현재 나의 Id
		
		//enter를 위한 2개의 data가 들어갈 것임
		Map<String, Object> chatMemberInsertMap = new HashMap<>();
		//select조건 2개 map에 저장
		chatMemberInsertMap.put("userId", userIdInSession);
		chatMemberInsertMap.put("chatNum", chatNum);
		System.out.println("chatMemberInsertMap 확인 chatRoomInsert "+chatMemberInsertMap);
		
		String nextWhere = "redirect:/chatRoom/enter?chatNum="+chatNum; //정상 진행jsp
		
		//일단 중복된 데이터가 없는지 확인 후 insert 진행하기 (중복 저장 방지를 위해~)
		ChatMemberDTO chatMemberDto = comEnterOutService.chatMemberEnterSelect(chatMemberInsertMap);
		
		///////////////////////////////////////////////
		if(chatMemberDto != null) {
			//만약 중복 data가 있으면 인원 수 뭐 어쩌고 상관없이 그냥 입장하면 됨.
			//이미 여기 안에 있는 멤버이라는 뜻임!!!!!!!!!!!!!!
			return nextWhere;
			
		}else {//중복 data가 없으면 서버 작업 진행해주기 
			
			//이 채팅방의 현재, 최대 인원 수 체크하기. 현재인원 수 currentNow , 최대인원 수 amount
			ChatRoomDTO cdto = chatRoomSelectBychatNum(chatNum);
			int currntNow = cdto.getCurrentNow();
			int amount = cdto.getAmount();
			
			if(currntNow < amount ) { //제한 인원수에 초과되지 않았을 경우 (입장 가능)
			
				int chatMemberANDroomRecordNum = 0;
				
					//Insert
					try {	
						
						Map<String, Integer> chatRoomRecordNumMap = new HashMap<>();
						chatRoomRecordNumMap.put("currntNow", (currntNow+1));
						chatRoomRecordNumMap.put("chatNum", chatNum);
						
						//chatMember에 insert 하고 chatRoom의 currntNow(현재 인원 수)에 +1 업데이트 tx묶여있음
					
						chatMemberANDroomRecordNum = comEnterOutService.chatMemberEnterInsert(chatMemberInsertMap, chatRoomRecordNumMap);
						
						
						if(chatMemberANDroomRecordNum == 0 ) { //tx 에서 무언가 꼬인 것임
							session.setAttribute("mesg", "문제가 발생하였습니다.");
							nextWhere = "redirect:/?cg=community"; //커뮤니티홈으로 이동
						}
						
					}catch(Exception e){
						//모종의 이유로 Insert 실패 시 error임
						session.setAttribute("mesg", "문제가 발생하였습니다.");
						System.out.println("chatMember insert 실패");
						nextWhere = "redirect:/?cg=community"; //커뮤니티홈으로 이동
					}
					
			
			}else { //제한 인원수에 초과 됨. (입장 불가, 커뮤니티 홈으로 이동)
				
				session.setAttribute("mesg", "입장 가능한 인원이 초과하였습니다.");
				nextWhere = "redirect:/?cg=community"; //커뮤니티홈으로 이동
			}
			
			
			
			return nextWhere; //chatMemberDto가 null일경우의 retrun 
			 
		}
		
		
	}
	
	//DB check 입장 승인할지 말지 결정 (chatNum, UserId 일치여부 확인)
	@RequestMapping("/chatRoom/enter")
	public String chatMemberSelect(HttpServletRequest request, Principal principal, @Param("chatNum") int chatNum, HttpSession session, Model model ) {
		String str = (String) session.getAttribute("userIdInSession");
		//chatNum과 userIdInSession을 조건으로 가진 select 결과가 있는지 없는지 ChatMemberDTO가져와서 null이 아닐 때만 링크 접속하게 하기
		//나중에 여기에 "강퇴"칼럼의 Y,N 값을 확인해야함 (N만 입장 가능)
		
		String userIdInSession = principal.getName();//현재 나의 Id
		request.setAttribute("userIdInSession", userIdInSession); //////////////형이 필요해서 저장해둔거
		
		Map<String, Object> chatMemberselectMap = new HashMap<>();
		chatMemberselectMap.put("userId", userIdInSession);
		chatMemberselectMap.put("chatNum", chatNum);
		System.out.println("chatMemberInsertMap 확인 chatRoomSelect "+chatMemberselectMap);
		
		
		
		//정상진행 시 chatRoom.jsp로 진입
		String returnWhere = "community/chatRoom/chatRoom"; //chatRoom.jsp
	
		try {
			
			ChatMemberDTO chatMemberDto = comEnterOutService.chatMemberEnterSelect(chatMemberselectMap);
			System.out.println("chatRoomSelect  "+chatMemberDto);
			
			
			if(chatMemberDto == null ) {
				returnWhere = "redirect:/?cg=community"; //커뮤니티목록으로 다시 리턴
			}
			
			
			
		}catch(Exception e){
			
			System.out.println("chatMember select 실패");
			returnWhere = "redirect:/?cg=community"; //커뮤니티목록으로 다시 리턴
		}
		
		
		/////////request에 저장하여 jsp로 chatNum 전달함 (더보기에서 사용할 예정)
		request.setAttribute("ChatRoomDTO", this.chatRoomSelectBychatNum( (int) chatMemberselectMap.get("chatNum")));
		
		
		return returnWhere;
	}
	
	
	
	
	////////////////////방 나가기 눌렀을 때///////////////////////////////
	@RequestMapping("/chatRoom/out")
	public String chatRoomOut(@RequestParam("userId") String userId, @RequestParam("chatNum") int chatNum, HttpSession session) {
	//form data로 받아온 값 2개로 chatMember table에서 delete 진행
	
	Map<String, Object> chatMemberDeleteMap = new HashMap<>();
	chatMemberDeleteMap.put("userId", userId);
	chatMemberDeleteMap.put("chatNum", chatNum);
	System.out.println("chatMemberDeleteMap   "+chatMemberDeleteMap);
	
	
	String returnWhere = "";
	
	ChatRoomDTO cdto = chatRoomSelectBychatNum(chatNum);
	System.out.println("리더id = "+cdto.getLeaderId());
	
	if(cdto.getLeaderId().equals(userId)) { //나가려는 사람의 id와 방의 리더id가 동일할경우 나가기 금지
		 
		session.setAttribute("mesg", "방장은 방을 나갈 수 없습니다. 방장 위임 후 다시 시도해주세요.");
		returnWhere = "redirect:/Chatmore?chatNum="+chatNum;		
		
	}else { //리더가 아닌 사람이 나갈 경우
		
		
		
		try {
			
			int currntNow = cdto.getCurrentNow(); //현재 인원수 table에서 가져옴 
			
			Map<String, Integer> chatRoomRecordNumMap = new HashMap<>();
			chatRoomRecordNumMap.put("currntNow", (currntNow-1)); //현재 인원 수에서 -1 한 것을 map에 저장
			chatRoomRecordNumMap.put("chatNum", chatNum);
		
			//chatMember에 delete 하고 chatRoom의 currntNow(현재 인원 수)에 -1 업데이트 tx묶여있음
			int deletNum = comEnterOutService.chatMemberDeleteBychatNumAndUserId(chatMemberDeleteMap,chatRoomRecordNumMap);
			
			
			//정상진행 시 커뮤니티 홈으로 진입 -> 그런데 잘 delete처리 돼서 목록에 없는지 확인하기 위해 /chatRoom/enter로 리다이렉트
			//만약 잘 지워졌다면 검수 기능으로 인해 커뮤니티 홈으로 가게 될 것임	
			session.setAttribute("mesg", "방을 나갔습니다.");
			returnWhere = "redirect:/chatRoom/enter?chatNum="+chatNum; 
			
		
		//delete를 했는데 0이다? 그러면 chatmember table에 일치하는 정보가 없는거라, 커뮤니티 홈으로 이동시켜야함 
		//혹시 모르니 이것도 enter를 먼저 거쳐서 검수 후 커뮤니티 홈으로 이동하게끔 함
		if(deletNum==0) {
			
			returnWhere = "redirect:/chatRoom/enter?chatNum="+chatNum;
			session.setAttribute("mesg", "방을 나갈 수 없습니다. 본인의 방이 맞는지 확인해주세요.");
		
		}
		
		}catch(Exception e) {
		
			//뭔가 알 수 없는 에러 발생 시
			System.out.println("chatRoomOut delete 실패");
			returnWhere = "redirect:/Chatmore?chatNum="+chatNum;
			session.setAttribute("mesg", "문제가 발생하였습니다.");
			
		}
	
	}//else종료
	
	return returnWhere;
	
	}
	
	
	
	@RequestMapping("/reportWindow")
	public String reportWindow() {
		System.out.println("reportWindow");
		return "community/chatRoom/report";
	}
	
	// 전달 받을 데이터 수정 필요, 신고 처리 필요함
	@RequestMapping(value="/chatReport", method = RequestMethod.POST)
	@ResponseBody
	public void chatReport() {
		System.out.println("chatReport");
	}
	
	@RequestMapping("/memberWindow")
	public String memberWindow() {
		System.out.println("memberWindow");
		return "community/chatRoom/member";
	}
	
	// 전달 받을 데이터 수정 필요, 신고 처리 필요함
		@RequestMapping(value="/chatMember", method = RequestMethod.POST)
		@ResponseBody
		public void chatMember() {
			System.out.println("chatMember");
		}
		
		// 전달 받을 데이터 수정 필요, 신고 처리 필요함
		@RequestMapping(value="/newLeader", method = RequestMethod.POST)
		@ResponseBody
		public void newLeader() {
			System.out.println("newLeader");
		}
		
		// 전달 받을 데이터 수정 필요, 신고 처리 필요함
		@RequestMapping(value="/memberRemove", method = RequestMethod.POST)
		@ResponseBody
		public void memberRemove() {
			System.out.println("memberRemove");
		}
		
}
