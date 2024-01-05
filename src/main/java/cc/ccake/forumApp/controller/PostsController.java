package cc.ccake.forumApp.controller;

import cc.ccake.forumApp.model.*;
import cc.ccake.forumApp.service.AuthService;
import cc.ccake.forumApp.service.PostsService;
import cc.ccake.forumApp.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PostsController {
    @Autowired
    PostsService postsService;

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(PostsController.class);

    // 获取所有帖子，通过可选参数返回帖子摘要。带分页功能。
    @GetMapping
    public ResponseEntity<?> getPosts(
            @RequestParam(required = false, defaultValue = "false") boolean isPrecis,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        logger.info("Processing GET /posts request");
        // 检查是否需要分页
        if (page != null && size != null) {
            Page<Post> postPage = new Page<>(page, size);
            QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("created_date");

            if (isPrecis) {
                IPage<Post> paginatedResult = postsService.page(postPage, queryWrapper);
                List<PostPrecisDTO> postPrecisDTOs = paginatedResult.getRecords().stream().map(post -> new PostPrecisDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getUsername(),
                        post.getCreated_date(),
                        post.getContent())
                ).collect(Collectors.toList());
                return ResponseEntity.ok(postPrecisDTOs);
            } else {
                IPage<Post> paginatedResult = postsService.page(postPage, queryWrapper);
                return ResponseEntity.ok(paginatedResult.getRecords());
            }
        } else {
            if (isPrecis) {
                List<PostPrecisDTO> postPrecisDTOs = postsService.getSortedPostPrecis();
                return ResponseEntity.ok(postPrecisDTOs);
            } else {
                QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
                queryWrapper.orderByDesc("created_date");
                List<Post> posts = postsService.list(queryWrapper);
                return ResponseEntity.ok(posts);
            }
        }
    }

    @GetMapping("/num")
    ResponseEntity<?> getPostNum() {
        logger.info("Processing GET /posts/num request");
        Map<String, Object> response = new HashMap<>();
        response.put("num", postsService.count());
        return ResponseEntity.ok(response);
    }

    // 获取当前用户发帖、回复列表和messages
    @GetMapping("/my")
    public ResponseEntity<UserPostsDTO> getCurrentUserPosts() {
        logger.info("Processing GET /posts/my request");
        String username = authService.getCurrentUsername();
        List<Integer> posts = userService.getPostsByUsername(username);
        List<ReplyIndex> replies = userService.getRepliesByUsername(username);
        List<String> messages = userService.getMessagesByUsername(username);
        return ResponseEntity.ok(new UserPostsDTO(posts, replies, messages));
    }

    // 给某用户添加一条message
    @PostMapping("/messages")
    public ResponseEntity<Void> addMessage(@RequestBody UserMessageDTO msgDTO) {
        logger.info("Processing POST /posts/messages request");
        List<String> messages = userService.getMessagesByUsername(msgDTO.getMessage());
        if (messages == null) messages = new ArrayList<String>();
        messages.add(msgDTO.getMessage());
        userService.updateMessagesByUsername(msgDTO.getUsername(), messages);
        return ResponseEntity.ok().build();
    }

    // 根据title查询帖子
    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPostsByTitle(@RequestParam String title) {
        logger.info("Processing GET /posts/search request");
        return ResponseEntity.ok(postsService.searchPostsByTitle(title));
    }

    // 根据id获取帖子
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@RequestParam(required = false, defaultValue = "false") boolean isPrecis, @PathVariable("postId") Integer id) {
        logger.info("Processing GET /posts/" + id + " request");
        var post = postsService.getPostById(id);
        if (isPrecis) {
            PostPrecisDTO postPrecisDTO = new PostPrecisDTO(
                    post.getId(),
                    post.getTitle(),
                    post.getUsername(),
                    post.getCreated_date(),
                    post.getContent()
            );
            return ResponseEntity.ok(postPrecisDTO);
        }
        return ResponseEntity.ok(post);
    }

    // 用户发帖
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        logger.info("Processing POST /posts request");
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
        logger.info("Processing POST /posts/" + postId + "/replies request");
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
        logger.info("Processing DELETE /posts/" + postId + " request");
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
