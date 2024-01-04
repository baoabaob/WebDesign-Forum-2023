package cc.ccake.forumApp.service.impl;

import cc.ccake.forumApp.mapper.PostsMapper;
import cc.ccake.forumApp.mapper.UserMapper;
import cc.ccake.forumApp.model.Post;
import cc.ccake.forumApp.model.ReplyIndex;
import cc.ccake.forumApp.model.User;
import cc.ccake.forumApp.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper; // 假设你有一个MyBatis的Mapper

    @Autowired
    private PostsMapper postsMapper;

    @Override
    public List<String> getMessagesByUsername(String username) {
        User user = findByUsername(username);
        return user != null ? user.getMessages() : null;
    }

    @Override
    public void updateMessagesByUsername(String username, List<String> messages) {
        User user = findByUsername(username);
        if (user != null) {
            user.setMessages(messages);
            userMapper.updateById(user);
        }
    }

    @Override
    public List<Integer> getPostsByUsername(String username) {
        User user = findByUsername(username);
        return user != null ? user.getPosts() : null;
    }

    @Override
    public void updatePostsByUsername(String username, List<Integer> posts) {
        User user = findByUsername(username);
        if (user != null) {
            user.setPosts(posts);
            userMapper.updateById(user);
        }
    }

    @Override
    public List<ReplyIndex> getRepliesByUsername(String username) {
        User user = findByUsername(username);
        List<ReplyIndex> validReplies = new ArrayList<>();
        List<ReplyIndex> userReplies = user.getReplies();
        if (userReplies != null) {
            userReplies.forEach(replyIndex -> {
                Post post = postsMapper.selectById(replyIndex.getPostId());
                if (post != null) {
                    validReplies.add(replyIndex);
                }
            });
        }
        user.setReplies(validReplies);
        return user != null ? validReplies : null;
    }

    @Override
    public void updateRepliesByUsername(String username, List<ReplyIndex> replies) {
        User user = findByUsername(username);
        if (user != null) {
            user.setReplies(replies);
            userMapper.updateById(user);
        }
    }

    private User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }
}
