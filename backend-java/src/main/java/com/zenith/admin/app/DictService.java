package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zenith.admin.domain.model.DictEntity;
import com.zenith.admin.domain.model.DictItemEntity;
import com.zenith.admin.dto.DictDTO;
import com.zenith.admin.dto.DictItemDTO;
import com.zenith.admin.infrastructure.convertor.DictConvertor;
import com.zenith.admin.infrastructure.dataobject.DictDO;
import com.zenith.admin.infrastructure.dataobject.DictItemDO;
import com.zenith.admin.infrastructure.mapper.DictMapper;
import com.zenith.admin.infrastructure.mapper.DictItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictService {

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private DictItemMapper dictItemMapper;

    @Autowired
    private DictConvertor dictConvertor;

    public MultiResponse<DictDTO> listAll() {
        List<DictDO> dictDOS = dictMapper.selectList(null);
        List<DictEntity> entities = dictConvertor.toEntityList(dictDOS);
        List<DictDTO> dtos = dictConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public MultiResponse<DictDTO> listByType(String type) {
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictDO::getType, type);
        List<DictDO> dictDOS = dictMapper.selectList(queryWrapper);
        List<DictEntity> entities = dictConvertor.toEntityList(dictDOS);
        List<DictDTO> dtos = dictConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public void update(DictDTO dictDTO) {
        DictEntity entity = dictConvertor.toEntity(dictDTO);
        DictDO dictDO = dictConvertor.toDataObject(entity);
        dictMapper.updateById(dictDO);
    }

    public DictDTO getById(Long id) {
        DictDO dictDO = dictMapper.selectById(id);
        DictEntity entity = dictConvertor.toEntity(dictDO);
        return dictConvertor.toDTO(entity);
    }

    public PageResponse<DictDTO> page(Integer pageIndex, Integer pageSize, String keyword) {
        Page<DictDO> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(DictDO::getName, keyword).or().like(DictDO::getType, keyword);
            });
        }
        Page<DictDO> result = dictMapper.selectPage(page, queryWrapper);
        List<DictEntity> entities = dictConvertor.toEntityList(result.getRecords());
        List<DictDTO> dtos = dictConvertor.toDTOList(entities);
        return PageResponse.of(dtos, (int) result.getTotal(), pageSize, pageIndex);
    }

    public void delete(Long id) {
        // 检查是否有字典项
        DictDO dictDO = dictMapper.selectById(id);
        if (dictDO != null) {
            LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DictItemDO::getType, dictDO.getType());
            Long count = dictItemMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new com.alibaba.cola.exception.BizException("DICT_HAS_ITEMS", "该字典类型下存在字典项，请先删除所有字典项后再删除类型");
            }
        }
        dictMapper.deleteById(id);
    }

    public void save(DictDTO dictDTO) {
        // 检查编码唯一性
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictDO::getType, dictDTO.getType());
        if (dictDTO.getId() != null) {
            queryWrapper.ne(DictDO::getId, dictDTO.getId());
        }
        Long count = dictMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new com.alibaba.cola.exception.BizException("DICT_TYPE_EXIST", "字典类型编码已存在");
        }
        DictEntity entity = dictConvertor.toEntity(dictDTO);
        DictDO dictDO = dictConvertor.toDataObject(entity);
        if (dictDO.getId() == null) {
            dictMapper.insert(dictDO);
        } else {
            dictMapper.updateById(dictDO);
        }
    }

    // 字典项相关方法
    public MultiResponse<DictItemDTO> listItemsByType(String type) {
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, type).orderByAsc(DictItemDO::getSort);
        List<DictItemDO> dictItemDOS = dictItemMapper.selectList(queryWrapper);
        List<DictItemEntity> entities = dictConvertor.toItemEntityList(dictItemDOS);
        List<DictItemDTO> dtos = dictConvertor.toItemDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public PageResponse<DictItemDTO> pageItems(String type, Integer pageIndex, Integer pageSize, String keyword) {
        Page<DictItemDO> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, type);
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(DictItemDO::getLabel, keyword).or().like(DictItemDO::getDictValue, keyword);
            });
        }
        queryWrapper.orderByAsc(DictItemDO::getSort);
        Page<DictItemDO> result = dictItemMapper.selectPage(page, queryWrapper);
        List<DictItemEntity> entities = dictConvertor.toItemEntityList(result.getRecords());
        List<DictItemDTO> dtos = dictConvertor.toItemDTOList(entities);
        return PageResponse.of(dtos, (int) result.getTotal(), pageSize, pageIndex);
    }

    public void saveItem(DictItemDTO dictItemDTO) {
        // 检查同类型下值的唯一性
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, dictItemDTO.getType());
        queryWrapper.eq(DictItemDO::getDictValue, dictItemDTO.getDictValue());
        if (dictItemDTO.getId() != null) {
            queryWrapper.ne(DictItemDO::getId, dictItemDTO.getId());
        }
        Long count = dictItemMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new com.alibaba.cola.exception.BizException("DICT_ITEM_VALUE_EXIST", "同类型下字典项值已存在");
        }
        DictItemEntity entity = dictConvertor.toItemEntity(dictItemDTO);
        DictItemDO dictItemDO = dictConvertor.toItemDataObject(entity);
        if (dictItemDO.getId() == null) {
            dictItemMapper.insert(dictItemDO);
        } else {
            dictItemMapper.updateById(dictItemDO);
        }
    }

    public void updateItem(DictItemDTO dictItemDTO) {
        DictItemEntity entity = dictConvertor.toItemEntity(dictItemDTO);
        DictItemDO dictItemDO = dictConvertor.toItemDataObject(entity);
        dictItemMapper.updateById(dictItemDO);
    }

    public void deleteItem(Long id) {
        dictItemMapper.deleteById(id);
    }

    public DictItemDTO getItemById(Long id) {
        DictItemDO dictItemDO = dictItemMapper.selectById(id);
        DictItemEntity entity = dictConvertor.toItemEntity(dictItemDO);
        return dictConvertor.toItemDTO(entity);
    }
}
