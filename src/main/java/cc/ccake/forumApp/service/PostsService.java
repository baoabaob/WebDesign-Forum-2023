package cc.ccake.forumApp.service;

import cc.ccake.forumApp.model.Post;
import cc.ccake.forumApp.model.Reply;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PostsService extends IService<Post> {
    Post createPost(Post post);

    Post getPostById(Integer id);

    List<Post> getAllPosts();

    Post updatePost(Integer id, Post post);

    void deletePost(Integer id);

    List<Post> searchPostsByTitle(String title);

    Post addReplyToPost(Integer postId, Reply reply);

    Post updateReplies(Integer postId, List<Reply> replies);
}
