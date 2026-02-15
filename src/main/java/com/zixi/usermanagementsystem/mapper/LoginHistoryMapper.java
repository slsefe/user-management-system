package com.zixi.usermanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zixi.usermanagementsystem.model.domain.LoginHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录历史记录 Mapper
 */
@Mapper
public interface LoginHistoryMapper extends BaseMapper<LoginHistory> {
}
