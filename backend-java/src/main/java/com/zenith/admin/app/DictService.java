package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.domain.model.DictEntity;
import com.zenith.admin.domain.model.DictItemEntity;
import com.zenith.admin.dto.DictDTO;
import com.zenith.admin.dto.DictItemDTO;
import com.zenith.admin.dto.DictPageQuery;
import com.zenith.admin.dto.DictItemPageQuery;
import com.zenith.admin.infrastructure.convertor.DictConvertor;
import com.zenith.admin.infrastructure.dataobject.DictDO;
import com.zenith.admin.infrastructure.dataobject.DictItemDO;
import com.zenith.admin.infrastructure.mapper.DictItemMapper;
import com.zenith.admin.infrastructure.mapper.DictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictService {

    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;
    private final DictConvertor dictConvertor;

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

    public PageResponse<DictDTO> page(DictPageQuery query) {
        com.github.pagehelper.PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(DictDO::getName, query.getKeyword()).or().like(DictDO::getType, query.getKeyword());
            });
        }
        List<DictDO> dictDOS = dictMapper.selectList(queryWrapper);
        com.github.pagehelper.PageInfo<DictDO> pageInfo = new com.github.pagehelper.PageInfo<>(dictDOS);
        List<DictEntity> entities = dictConvertor.toEntityList(pageInfo.getList());
        List<DictDTO> dtos = dictConvertor.toDTOList(entities);
        return PageResponse.of(dtos, (int) pageInfo.getTotal(), query.getPageSize(), query.getPageIndex());
    }

    public void delete(Long id) {
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

    public MultiResponse<DictItemDTO> listItemsByType(String type) {
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, type).orderByAsc(DictItemDO::getSort);
        List<DictItemDO> dictItemDOS = dictItemMapper.selectList(queryWrapper);
        List<DictItemEntity> entities = dictConvertor.toItemEntityList(dictItemDOS);
        List<DictItemDTO> dtos = dictConvertor.toItemDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public PageResponse<DictItemDTO> pageItems(DictItemPageQuery query) {
        com.github.pagehelper.PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, query.getType());
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(DictItemDO::getLabel, query.getKeyword()).or().like(DictItemDO::getDictValue, query.getKeyword());
            });
        }
        queryWrapper.orderByAsc(DictItemDO::getSort);
        List<DictItemDO> dictItemDOS = dictItemMapper.selectList(queryWrapper);
        com.github.pagehelper.PageInfo<DictItemDO> pageInfo = new com.github.pagehelper.PageInfo<>(dictItemDOS);
        List<DictItemEntity> entities = dictConvertor.toItemEntityList(pageInfo.getList());
        List<DictItemDTO> dtos = dictConvertor.toItemDTOList(entities);
        return PageResponse.of(dtos, (int) pageInfo.getTotal(), query.getPageSize(), query.getPageIndex());
    }

    public void saveItem(DictItemDTO dictItemDTO) {
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
