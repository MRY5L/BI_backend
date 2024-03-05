package com.mry5l.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mry5l.springbootinit.model.entity.Userinfo;
import com.mry5l.springbootinit.service.UserinfoService;
import com.mry5l.springbootinit.mapper.UserinfoMapper;
import org.springframework.stereotype.Service;

/**
* @author YJL
* @description 针对表【userinfo】的数据库操作Service实现
* @createDate 2024-03-05 12:24:23
*/
@Service
public class UserinfoServiceImpl extends ServiceImpl<UserinfoMapper, Userinfo>
    implements UserinfoService{

}




