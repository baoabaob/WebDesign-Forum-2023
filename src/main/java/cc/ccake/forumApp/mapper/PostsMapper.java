package cc.ccake.forumApp.mapper;

import cc.ccake.forumApp.model.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PostsMapper extends BaseMapper<Post> {
}
