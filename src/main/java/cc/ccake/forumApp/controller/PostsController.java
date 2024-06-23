package cc.ccake.forumApp.controller;

import cc.ccake.forumApp.model.*;
import cc.ccake.forumApp.service.AuthService;
import cc.ccake.forumApp.service.PostsService;
import cc.ccake.forumApp.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Posts Controller", description = "API for managing posts")
public class PostsController {
    @Autowired
    PostsService postsService;

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(PostsController.class);

    @Operation(summary = "Get all posts with optional pagination and summary")
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

    @Operation(summary = "Get the number of posts")
    @GetMapping("/num")
    ResponseEntity<?> getPostNum() {
        logger.info("Processing GET /posts/num request");
        Map<String, Object> response = new HashMap<>();
        response.put("num", postsService.count());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get current user's posts, replies and messages")
    @GetMapping("/my")
    public ResponseEntity<UserPostsDTO> getCurrentUserPosts() {
        logger.info("Processing GET /posts/my request");
        String username = authService.getCurrentUsername();
        List<Integer> posts = userService.getPostsByUsername(username);
        List<ReplyIndex> replies = userService.getRepliesByUsername(username);
        List<String> messages = userService.getMessagesByUsername(username);
        return ResponseEntity.ok(new UserPostsDTO(posts, replies, messages));
    }

    @Operation(summary = "Add a message to a user")
    @PostMapping("/messages")
    public ResponseEntity<Void> addMessage(@RequestBody UserMessageDTO msgDTO) {
        logger.info("Processing POST /posts/messages request");
        List<String> messages = userService.getMessagesByUsername(msgDTO.getUsername());
        if (messages == null) messages = new ArrayList<>();
        messages.add(msgDTO.getMessage());
        userService.updateMessagesByUsername(msgDTO.getUsername(), messages);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Search posts by title")
    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPostsByTitle(@RequestParam String title) {
        logger.info("Processing GET /posts/search request");
        return ResponseEntity.ok(postsService.searchPostsByTitle(title));
    }

    @Operation(summary = "Get post by ID")
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

    @Operation(summary = "Create a new post")
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        logger.info("Processing POST /posts request");
        String username = authService.getCurrentUsername();
        post.setCreated_date(LocalDateTime.now());
        post.setUsername(username);
        Post createdPost = postsService.createPost(post);
        var userPostIds = userService.getPostsByUsername(username);
        if (userPostIds == null) userPostIds = new ArrayList<>();
        userPostIds.add(post.getId());
        userService.updatePostsByUsername(username, userPostIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @Operation(summary = "Reply to a post")
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

    @Operation(summary = "Delete a post")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId) {
        logger.info("Processing DELETE /posts/" + postId + " request");
        String username = authService.getCurrentUsername();
        Post post = postsService.getPostById(postId);
        if (post != null && post.getUsername().equals(username)) {
            postsService.deletePost(postId);
            var postIds = userService.getPostsByUsername(username);
            postIds.remove(postId);
            userService.updatePostsByUsername(username, postIds);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
