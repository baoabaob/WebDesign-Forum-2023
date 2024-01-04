package cc.ccake.forumApp.service.impl;

import cc.ccake.forumApp.mapper.PostsMapper;
import cc.ccake.forumApp.model.Post;
import cc.ccake.forumApp.model.Reply;
import cc.ccake.forumApp.service.PostsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostsServiceImpl extends ServiceImpl<PostsMapper, Post> implements PostsService {

    @Autowired
    private PostsMapper postsMapper;

    @Override
    public Post createPost(Post post) {
        postsMapper.insert(post); // 插入帖子
        return post; // 返回帖子，可能包含生成的ID
    }

    @Override
    public Post getPostById(Integer id) {
        return postsMapper.selectById(id); // 根据ID获取帖子
    }

    @Override
    public List<Post> getAllPosts() {
        return postsMapper.selectList(null); // 获取所有帖子
    }

    @Override
    public Post updatePost(Integer id, Post post) {
        post.setId(id); // 确保帖子的ID是正确的
        postsMapper.updateById(post); // 更新帖子
        return post; // 返回更新后的帖子
    }

    @Override
    public void deletePost(Integer id) {
        postsMapper.deleteById(id); // 根据ID删除帖子
    }

    @Override
    public List<Post> searchPostsByTitle(String title) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title", title); // 使用标题进行模糊查询
        return postsMapper.selectList(queryWrapper);
    }

    @Override
    public Post addReplyToPost(Integer postId, Reply reply) {
        Post post = postsMapper.selectById(postId);
        if (post != null) {
            List<Reply> replies = post.getReplies();
            if (replies == null) {
                replies = new ArrayList<>();
            }
            replies.add(reply); // 添加新回复
            post.setReplies(replies);
            postsMapper.updateById(post); // 更新帖子
        }
        return post;
    }

    @Override
    public Post updateReplies(Integer postId, List<Reply> replies) {
        Post post = postsMapper.selectById(postId);
        if (post != null) {
            post.setReplies(replies); // 更新整个回复列表
            postsMapper.updateById(post); // 更新帖子
        }
        return post;
    }
}
