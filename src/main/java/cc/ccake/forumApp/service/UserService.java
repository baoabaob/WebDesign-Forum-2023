package cc.ccake.forumApp.service;

import cc.ccake.forumApp.model.ReplyIndex;
import cc.ccake.forumApp.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserService extends IService<User> {
    List<String> getMessagesByUsername(String username);

    void updateMessagesByUsername(String username, List<String> messages);

    List<Integer> getPostsByUsername(String username);

    void updatePostsByUsername(String username, List<Integer> posts);

    List<ReplyIndex> getRepliesByUsername(String username);

    void updateRepliesByUsername(String username, List<ReplyIndex> replies);

}
