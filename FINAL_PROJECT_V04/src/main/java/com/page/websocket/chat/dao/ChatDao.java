package com.page.websocket.chat.dao;

import java.util.List;

import com.page.user.dto.UserVO;
import com.page.websocket.chat.dto.ChatDto;

public interface ChatDao {
	
	String NAMESPACE = "com.page.websocket.chat.Mapper.";
	
	public List<ChatDto> ChatSelectList();
	public List<ChatDto> ChatSelectPageList(int pageno);
	public int ChatInsert(ChatDto dto);
	
	public UserVO chatSelectID(int user_no);
	
	public int CheckSaveChat(int user_no);
	public int CheckSavePaint(int user_no);

}
