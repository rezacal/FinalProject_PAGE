package com.page.websocket.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.page.board.model.dto.fileDto;
import com.page.user.dto.UserVO;
import com.page.websocket.chat.biz.ChatBiz;
import com.page.websocket.chat.dto.ChatDto;
import com.page.websocket.file.biz.FileBiz;


public class ChatWebSocketHandler extends TextWebSocketHandler {

	private Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
	
	@Autowired
	private ChatBiz chatbiz;
	
	@Autowired
	private FileBiz fileBiz;
	
	private Map<String,WebSocketSession> users;
	private Map<WebSocketSession,String> pageUsers;
	
	/*
	 * 클라이언트가 연결되면, 클라이언트의 관련된 WebSocketSession을 users 맵에 저장한다.
	 * 이 users 맵은 채팅 메시지를 연결된 전체 클라이언트에 전달할 때 사용
	 */
	
	public ChatWebSocketHandler() {
		users = new ConcurrentHashMap<String,WebSocketSession>();
		pageUsers = new ConcurrentHashMap<WebSocketSession,String>();
    }
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		// 들어온 사람의 실제 로그인 아이디 정보를 가져온다.
    	Map<String, Object> map = session.getAttributes();
    	UserVO userVO = (UserVO)map.get("login");				
		
		logger.info(session.getId()+"(IP)"+session.getRemoteAddress().getHostName() + " 연결됨");			
		users.put(session.getId(), session);
		
		String username = userVO.getUser_name();
		
		// CHAT 테이블 값 중 pageno와 일치하는 채팅 List에 가져오기 / 미사용
		//pageUsers.put(session , pageno);
		//List<ChatDto> list = biz.ChatSelectPageList(Integer.parseInt(pageno));
		
		List<ChatDto> list = chatbiz.selectList();
		
		String sendmessage = "";
		// 가져온 값 종류별로 전달.
		for (ChatDto dto : list) {
			// 메세지
			if (dto.getChattype().equals("msg")){
				sendmessage = "{\"type\" :\"msg\","
							+ "\"nickname\" :\""+chatbiz.chatSelectID(dto.getUserno()).getUser_name()+"\","
							+ "\"message\" :\""+dto.getChatcontent()+"\","
							+ "\"profileimg\" :\""+chatbiz.chatSelectID(dto.getUserno()).getUser_img()+"\","
							+ "\"randomcolor\" :\""+dto.getChatcolor()+"\"}";
			// 동영상
			} else if (dto.getChattype().equals("video")){
				sendmessage = "{\"type\" :\"video\","
							+ "\"nickname\" :\""+chatbiz.chatSelectID(dto.getUserno()).getUser_name()+"\","
							+ "\"url\" :\""+dto.getVideourl()+"\","
							+ "\"profileimg\" :\""+chatbiz.chatSelectID(dto.getUserno()).getUser_img()+"\","
							+ "\"randomcolor\" :\""+dto.getChatcolor()+"\"}";
			// 파일
			} else if (dto.getChattype().equals("filedata")){
				fileDto selectFileDto = fileBiz.FileSelectOne(dto.getFileno());
				sendmessage = "{\"type\" :\"filedata\","
							+ "\"nickname\" :\""+chatbiz.chatSelectID(dto.getUserno()).getUser_name()+"\","
							+ "\"filename\" :\""+ selectFileDto.getFiletitle()+"\","
							+ "\"newFileName\" :\""+ selectFileDto.getFilestream()+"\","
							+ "\"profileimg\" :\""+chatbiz.chatSelectID(dto.getUserno()).getUser_img()+"\","
							+ "\"randomcolor\" :\""+dto.getChatcolor()+"\"}";
			}
			session.sendMessage(new TextMessage(sendmessage));
		}
		session.sendMessage(new TextMessage("{\"type\":\"inout\", \"message\":\"이전 글 불러오기 완료\"}"));
	}
	
	/*
	 * 클라이언트가 전송한 메시지를 users 맵에 보관한 전체 WebSocketSession에 다시 전달한다.
	 * 클라이언트는 메시지를 수신하면 채팅 영역에 보여주도록 구현.
	 * 특정 클라이언트가 채팅 메시지를 서버에 보내면 전체 클라이언트는 다시 그 메시지를 받아서 화면에 보여주게 된다.
	 */

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		//logger.info(session.getId()+"(IP)"+session.getRemoteAddress().getHostName() + "로부터 메시지 수신 : " + message.getPayload());
		
		// 들어온 사람의 실제 로그인 아이디 정보를 가져온다.
    	Map<String, Object> map = session.getAttributes();
    	UserVO userVO = (UserVO)map.get("login");
    	
		// 웹소켓을 통해 전달된 데이터값 jsonobject로 전환
		JSONParser jsonparser = new JSONParser();
		JSONObject jsonObj = null;
		jsonObj = (JSONObject)jsonparser.parse(message.getPayload());
		
		// 같은 페이지를 사용하는 사용자별 세션 분리 / 미사용
		//logger.info("pageno : "+(String)jsonObj.get("pageno"));
		//String pageno = (String)jsonObj.get("pageno");
		//pageUsers.put(session , pageno);		
		
		// DB CHAT 테이블에 저장(insert)
		if (!jsonObj.get("type").equals("inout")) {
			int fileno = 0;
			if (jsonObj.get("type").equals("filedata")) {
				fileno = Integer.parseInt((String)jsonObj.get("fileno"));
			}
			ChatDto dto = new ChatDto(0, userVO.getUser_no(), Integer.parseInt((String)jsonObj.get("pageno")) , (String)jsonObj.get("type"), (String)jsonObj.get("message"), (String)jsonObj.get("url"), fileno , (String)jsonObj.get("randomcolor"), new Date(), 0,0);
			int insert_res = chatbiz.insert(dto);
		}
		
		for(WebSocketSession s : users.values()) {
			s.sendMessage(message);
			logger.info(s.getId() + "에 메시지 발송 : " + message.getPayload());
		}
		
		// pageno 같은 사용자만 메시지전달 / 미사용 주석처리
		/*for(WebSocketSession s : users.values()) {
			//pageno 가 같은 사용자에게만  메시지 전송
			if (pageno.equals(pageUsers.get(s))) {
				s.sendMessage(message);
				logger.info(s.getId() + "에 메시지 발송 : " + message.getPayload());
			}
		}*/		
	}	

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		logger.info(session.getId() + " 익셉션 발생 : " + exception.getMessage());
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info(session.getId() + " 연결 종료됨");
		users.remove(session.getId());
	}
	
}
