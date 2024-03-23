package com.moonBam.dto.board;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Alias("PostDTO")
public class PostDTO implements IPost {

    private Long postId;

	@NotBlank
    private String postBoard;

	@NotBlank
    private String userId;

    private Long contId;

	@NotBlank
	@Size(max = 15)
    private String postTitle;

    private Date postDate;

    private Date postEditDate;

	@NotBlank
    private String postText;

	@NotBlank
    private String nickname;

	@NotBlank
    private Long categoryId;
    
	public PostDTO() {
	}

	public PostDTO(Long postId, String postBoard, String userId, Long contId, String postTitle, Date postDate,
			Date postEditDate, String postText, String nickname, Long categoryId) {
		this.postId = postId;
		this.postBoard = postBoard;
		this.userId = userId;
		this.contId = contId;
		this.postTitle = postTitle;
		this.postDate = postDate;
		this.postEditDate = postEditDate;
		this.postText = postText;
		this.nickname = nickname;
		this.categoryId = categoryId;
	}
}

