package com.page.websocket.handler;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class PaintHandler extends TextWebSocketHandler{
	 
    private Logger logger = LoggerFactory.getLogger(PaintHandler.class);
 
    /**
     * 서버에 연결한 사용자들을 저장하는 리스트.
     */
    private List<WebSocketSession> connectedUsers;
     
    public PaintHandler() {
        connectedUsers = new ArrayList<WebSocketSession>();
    }
     
    /**
     * 접속과 관련되어 있는 Event Method
     * @param WebSocketSession 접속한 사용자
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {         
        logger.info(session.getId()+"(IP)"+session.getRemoteAddress().getHostName()+ "님이 접속(paint)");        
        connectedUsers.add(session);
        //logger.info(connectedUsers.size() + ""); 
              
        if(connectedUsers.size() != 1) {
        	connectedUsers.get(0).sendMessage( new TextMessage( "!@#REQUESTCANVAS" ) );
        }
        
        for (WebSocketSession webSocketSession : connectedUsers) {          
            webSocketSession.sendMessage( new TextMessage( "!@#OK" ) );                     
        }         
    }
     
    /**
     * 두 가지 이벤트를 처리함.
     * 1. Send : 클라이언트가 서버에게 메시지를 보냄
     * 2. Emit : 서버에 연결되어 있는 클라이언트들에게 메시지를 보냄
     *
     * @param WebSocketSession 메시지를 보낸 클라이언트
     * @param TextMessage 메시지의 내용
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {      
       
    	//logger.info(session.getId()+"(IP)"+session.getRemoteAddress().getHostName() + "로부터 메시지 수신 : " + message.getPayload());
        for (WebSocketSession webSocketSession : connectedUsers) {
            if ( !webSocketSession.getId().equals(session.getId()) ) {
                webSocketSession.sendMessage(new TextMessage(message.getPayload()));
            }
        }         
    }
     
    /**
     * 클라이언트가 서버와 연결을 끊음.
     * @param WebSocketSession 연결을 끊은 클라이언트
     * @param CloseStatus 연결 상태(확인 필요)
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info(session.getId() + "님이 퇴장했습니다.(paint)");
        connectedUsers.remove(session);         
    }
     
}