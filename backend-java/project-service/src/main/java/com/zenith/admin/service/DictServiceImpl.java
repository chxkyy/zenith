package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.DictService;
import com.zenith.admin.dto.data.DictDTO;
import com.zenith.admin.dto.data.DictItemDTO;
import com.zenith.admin.dto.data.DictPageQuery;
import com.zenith.admin.dto.data.DictItemPageQuery;
import com.zenith.admin.DictConvertor;
import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.mapper.DictItemMapper;
import com.zenith.admin.mapper.DictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {

    private final DictMapper dictMapper;
    private final DictItemMapper dictItemMapper;
    private final DictConvertor dictConvertor;

    @Override
    public List<DictDTO> listAll() {
        List<DictDO> dictDOS = dictMapper.selectList(null);
        List<DictDTO> dtos = dictConvertor.toDTOList(dictDOS);
        return dtos;
    }

    @Override
    public List<DictDTO> listByType(String type) {
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictDO::getType, type);
        List<DictDO> dictDOS = dictMapper.selectList(queryWrapper);
        List<DictDTO> dtos = dictConvertor.toDTOList(dictDOS);
        return dtos;
    }

    @Override
    public void update(DictDTO dictDTO) {
        DictDO dictDO = dictConvertor.toDataObject(dictDTO);
        Long currentUserId = 1L;
        dictDO.setUpdateUserId(currentUserId);
        dictDO.setUpdateTime(java.time.LocalDateTime.now());
        dictMapper.updateById(dictDO);
    }

    @Override
    public DictDTO getById(Long id) {
        DictDO dictDO = dictMapper.selectById(id);
        return dictConvertor.toDTO(dictDO);
    }

    @Override
    public PageInfo<DictDTO> page(DictPageQuery query) {
        com.github.pagehelper.PageHelper.startPage(query.getPageIndex(), query.getPageSize());
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(DictDO::getName, query.getKeyword()).or().like(DictDO::getType, query.getKeyword());
            });
        }
        List<DictDO> dictDOS = dictMapper.selectList(queryWrapper);
        com.github.pagehelper.PageInfo<DictDO> pageInfo = new com.github.pagehelper.PageInfo<>(dictDOS);
        List<DictDTO> dtos = dictConvertor.toDTOList(pageInfo.getList());

        PageInfo<DictDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
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

    @Override
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
        DictDO dictDO = dictConvertor.toDataObject(dictDTO);
        Long currentUserId = 1L;
        if (dictDO.getId() == null) {
            dictDO.setCreateUserId(currentUserId);
            dictDO.setCreatedTime(java.time.LocalDateTime.now());
            dictMapper.insert(dictDO);
        } else {
            dictDO.setUpdateUserId(currentUserId);
            dictDO.setUpdateTime(java.time.LocalDateTime.now());
            dictMapper.updateById(dictDO);
        }
    }

    @Override
    public List<DictItemDTO> listItemsByType(String type) {
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, type).orderByAsc(DictItemDO::getSort);
        List<DictItemDO> dictItemDOS = dictItemMapper.selectList(queryWrapper);
        List<DictItemDTO> dtos = dictConvertor.toItemDTOList(dictItemDOS);
        return dtos;
    }

    @Override
    public PageInfo<DictItemDTO> pageItems(DictItemPageQuery query) {
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
        List<DictItemDTO> dtos = dictConvertor.toItemDTOList(pageInfo.getList());

        PageInfo<DictItemDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
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
        DictItemDO dictItemDO = dictConvertor.toItemDataObject(dictItemDTO);
        Long currentUserId = 1L;
        if (dictItemDO.getId() == null) {
            dictItemDO.setCreateUserId(currentUserId);
            dictItemDO.setCreatedTime(java.time.LocalDateTime.now());
            dictItemMapper.insert(dictItemDO);
        } else {
            dictItemDO.setUpdateUserId(currentUserId);
            dictItemDO.setUpdateTime(java.time.LocalDateTime.now());
            dictItemMapper.updateById(dictItemDO);
        }
    }

    @Override
    public void updateItem(DictItemDTO dictItemDTO) {
        DictItemDO dictItemDO = dictConvertor.toItemDataObject(dictItemDTO);
        Long currentUserId = 1L;
        dictItemDO.setUpdateUserId(currentUserId);
        dictItemDO.setUpdateTime(java.time.LocalDateTime.now());
        dictItemMapper.updateById(dictItemDO);
    }

    @Override
    public void deleteItem(Long id) {
        dictItemMapper.deleteById(id);
    }

    @Override
    public DictItemDTO getItemById(Long id) {
        DictItemDO dictItemDO = dictItemMapper.selectById(id);
        return dictConvertor.toItemDTO(dictItemDO);
    }
}
