package cc.ccake.forumApp.model;

import lombok.Data;

import java.util.List;

@Data
public class UserPostsDTO {
    private List<Integer> postIds;
    private List<ReplyIndex> replyIndices;
    private List<String> messages;

    public UserPostsDTO(List<Integer> posts, List<ReplyIndex> replies, List<String> messages) {
        this.postIds = posts;
        this.replyIndices = replies;
        this.messages = messages;
    }

}