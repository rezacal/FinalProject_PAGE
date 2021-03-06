package com.page.board.model.biz;

import java.util.List;

import com.page.board.model.dto.Criteria;
import com.page.board.model.dto.SearchCriteria;
import com.page.board.model.dto.boardDto;
import com.page.board.model.dto.fileDto;

public interface boardBiz {
	
	public List<boardDto> selectList();
	public List<boardDto> listPaging(Criteria cri);
	
	
	public List<boardDto> searchTitle(SearchCriteria scri);
	public List<boardDto> searchContent(SearchCriteria scri);
	public List<boardDto> searchId(SearchCriteria scri);

	public boardDto selectOne(int boardseq);
	public int insertBoard(boardDto dto);
	public int updateBoard(boardDto dto);
	public int deleteBoard(int boardseq);
	
	public int countBoardListTotal();
	public int countTitleList(String keyword);
	public int countContentList(String keyword);
	public int countIdList(String keyword);
	
	public int insertFile(fileDto fdto);
	

}
