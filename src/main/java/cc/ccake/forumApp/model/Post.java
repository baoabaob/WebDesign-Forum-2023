package cc.ccake.forumApp.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@TableName(value = "Post", autoResultMap = true)
@Data
public class Post {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String username;
    private LocalDateTime created_date;
    private String content;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Reply> replies;
}
