package cc.ccake.forumApp.controller;

import cc.ccake.forumApp.model.*;
import cc.ccake.forumApp.service.AuthService;
import cc.ccake.forumApp.service.PostsService;
import cc.ccake.forumApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class postsController {
    @Autowired
    PostsService postsService;

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    // 获取所有帖子
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postsService.getAllPosts());
    }

    // 获取当前用户发帖、回复列表和messages
    @GetMapping("/my")
    public ResponseEntity<UserPostsDTO> getCurrentUserPosts() {
        String username = authService.getCurrentUsername();
        List<Integer> posts = userService.getPostsByUsername(username);
        List<ReplyIndex> replies = userService.getRepliesByUsername(username);
        List<String> messages = userService.getMessagesByUsername(username);
        return ResponseEntity.ok(new UserPostsDTO(posts, replies, messages));
    }

    // 给某用户添加一条message
    @PostMapping("/messages")
    public ResponseEntity<Void> addMessage(@RequestBody UserMessageDTO msgDTO) {
        List<String> messages = userService.getMessagesByUsername(msgDTO.getMessage());
        if (messages == null) messages = new ArrayList<String>();
        messages.add(msgDTO.getMessage());
        userService.updateMessagesByUsername(msgDTO.getUsername(), messages);
        return ResponseEntity.ok().build();
    }

    // 根据title查询帖子
    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPostsByTitle(@RequestParam String title) {
        return ResponseEntity.ok(postsService.searchPostsByTitle(title));
    }

    // 用户发帖
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        String username = authService.getCurrentUsername();
        post.setCreated_date(LocalDateTime.now());
        post.setUsername(username);
        Post createdPost = postsService.createPost(post);
        var userPostIds = userService.getPostsByUsername(username);
        if (userPostIds == null) userPostIds = new ArrayList<Integer>();
        userPostIds.add(post.getId());
        userService.updatePostsByUsername(username, userPostIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    // 用户回复某贴
    @PostMapping("/{postId}/replies")
    public ResponseEntity<Void> replyToPost(@PathVariable Integer postId, @RequestBody Reply reply) {
        String username = authService.getCurrentUsername();
        reply.setCreatedDate(LocalDateTime.now());
        reply.setUsername(username);
        postsService.addReplyToPost(postId, reply);
        var userRelyIndices = userService.getRepliesByUsername(username);
        var replyNum = postsService.getPostById(postId).getReplies().size();
        userRelyIndices.add(new ReplyIndex(postId, replyNum));
        userService.updateRepliesByUsername(username, userRelyIndices);
        return ResponseEntity.ok().build();
    }

    // 用户删除某贴，调用该方法后需要重新获取用户messages
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId) {
        String username = authService.getCurrentUsername();
        Post post = postsService.getPostById(postId);
        if (post != null && post.getUsername().equals(username)) {
            postsService.deletePost(postId);
            var postIds = userService.getPostsByUsername(username);
            postIds.remove(postId);//这里可能有bug，不确定是怎么删的
            userService.updatePostsByUsername(username, postIds);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
