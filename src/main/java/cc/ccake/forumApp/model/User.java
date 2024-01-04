package cc.ccake.forumApp.model;

import cc.ccake.forumApp.config.ReplyIndexListTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.List;

@TableName(value = "User", autoResultMap = true)
@Data
public class User {
    @TableId
    private Integer id;
    private String username;
    private String password;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> messages;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> posts;
    @TableField(typeHandler = ReplyIndexListTypeHandler.class)
    private List<ReplyIndex> replies;
}