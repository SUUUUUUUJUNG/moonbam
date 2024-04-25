package com.moonBam.dto;

import java.util.List;

import org.apache.ibatis.type.Alias;

import com.moonBam.dto.board.PostDTO;

@Alias("MyPageDTO")
public class MyPageDTO {
	/////
	private List<PostDTO> list;   // 현재페이지에 들어갈 레코드를 perPage만큼만 저장 
	private int curPage;    //현재 볼 페이지 번호 
	private int perPage=8;  //한페이지에 보여질 목록 수 
	private int totalCount; //전체 레코드 갯수 
	
	private int perBlock=5;
	
	public List<PostDTO> getList() {
		return list;
	}
	public void setList(List<PostDTO> list) {
		this.list = list;
	}
	public int getCurPage() {
		return curPage;
	}
	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}
	public int getPerPage() {
		return perPage;
	}
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getPerBlock() {
		return perBlock;
	}
	public void setPerBlock(int perBlock) {
		this.perBlock = perBlock;
	}
}
