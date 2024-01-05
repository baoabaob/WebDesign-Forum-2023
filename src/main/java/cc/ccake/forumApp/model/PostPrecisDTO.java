package cc.ccake.forumApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostPrecisDTO {
    private Integer id;
    private String title;
    private String username;
    private LocalDateTime created_date;
    private String content;
}
