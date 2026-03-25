package com.zenith.admin.infrastructure.gateway;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.UserGateway;
import com.zenith.admin.domain.model.UserEntity;
import com.zenith.admin.infrastructure.convertor.UserConvertor;
import com.zenith.admin.infrastructure.dataobject.UserDO;
import com.zenith.admin.infrastructure.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserGatewayImpl implements UserGateway {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserConvertor userConvertor;

    @Override
    public PageInfo<UserEntity> listByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserDO> userDOS = userMapper.selectList(null);
        PageInfo<UserDO> pageInfo = new PageInfo<>(userDOS);
        
        List<UserEntity> entities = userConvertor.toEntityList(userDOS);

        PageInfo<UserEntity> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);
        result.setList(entities);
        return result;
    }

    @Override
    public void save(UserEntity user) {
        UserDO userDO = userConvertor.toDataObject(user);
        if (userDO.getId() == null) {
            userMapper.insert(userDO);
        } else {
            userMapper.updateById(userDO);
        }
    }

    @Override
    public UserEntity getById(Long id) {
        UserDO userDO = userMapper.selectById(id);
        return userConvertor.toEntity(userDO);
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }
}
