package cc.ccake.forumApp.mapper;

import cc.ccake.forumApp.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
    // 这里可以根据需要添加自定义的数据库操作方法
}
