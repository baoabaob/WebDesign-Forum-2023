package cc.ccake.forumApp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReplyIndex {
    private Integer postId;
    private Integer replyIndex;

    public ReplyIndex(Integer postId, Integer replyIndex) {
        this.postId = postId;
        this.replyIndex = replyIndex;
    }
}