package com.zixi.usermanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zixi.usermanagementsystem.model.domain.VerificationCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验证码 Mapper
 */
@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
}
