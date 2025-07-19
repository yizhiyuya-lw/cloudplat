/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package com.lw.cloudplat.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lw.cloudplat.admin.api.entity.SysRole;
import com.lw.cloudplat.admin.api.entity.SysRoleMenu;
import com.lw.cloudplat.admin.api.vo.RoleExcelVO;
import com.lw.cloudplat.admin.api.vo.RoleVO;
import com.lw.cloudplat.admin.mapper.SysRoleMapper;
import com.lw.cloudplat.admin.service.SysRoleMenuService;
import com.lw.cloudplat.admin.service.SysRoleService;
import com.lw.cloudplat.common.core.constant.CacheConstants;
import com.lw.cloudplat.common.core.exception.ErrorCodes;
import com.lw.cloudplat.common.core.util.MsgUtils;
import com.lw.cloudplat.common.core.util.R;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 系统角色服务实现类
 */
@Service
@AllArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

	private SysRoleMenuService roleMenuService;

	/**
	 * 通过用户ID查询角色信息
	 * @param userId 用户ID
	 * @return 角色信息列表
	 */
	@Override
	public List listRolesByUserId(Long userId) {
		return baseMapper.listRolesByUserId(userId);
	}

	/**
	 * 根据角色ID查询角色列表
	 * @param roleIdList 角色ID列表
	 * @param key 缓存key
	 * @return 角色列表
	 */
	@Override
	@Cacheable(value = CacheConstants.ROLE_DETAILS, key = "#key", unless = "#result.isEmpty()")
	public List<SysRole> listRolesByRoleIds(List<Long> roleIdList, String key) {
		return baseMapper.selectByIds(roleIdList);
	}

	/**
	 * 通过角色ID删除角色并清空角色菜单缓存
	 * @param ids 角色ID数组
	 * @return 删除是否成功
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean removeRoleByIds(Long[] ids) {
		roleMenuService
			.remove(Wrappers.<SysRoleMenu>update().lambda().in(SysRoleMenu::getRoleId, CollUtil.toList(ids)));
		return this.removeBatchByIds(CollUtil.toList(ids));
	}

	/**
	 * 更新角色菜单列表
	 * @param roleVo 包含角色ID和菜单ID列表的角色对象
	 * @return 更新是否成功
	 */
	@Override
	public Boolean updateRoleMenus(RoleVO roleVo) {
		return roleMenuService.saveRoleMenus(roleVo.getRoleId(), roleVo.getMenuIds());
	}

	/**
	 * 导入角色
	 * @param excelVOList 角色列表
	 * @param bindingResult 错误信息列表
	 * @return ok fail
	 */
	@Override
	public R importRole(List<RoleExcelVO> excelVOList, BindingResult bindingResult) {
		// 通用校验获取失败的数据
		/*List<ErrorMessage> errorMessageList = (List<ErrorMessage>) bindingResult.getTarget();

		// 个性化校验逻辑
		List<SysRole> roleList = this.list();

		// 执行数据插入操作 组装 RoleDto
		for (RoleExcelVO excel : excelVOList) {
			Set<String> errorMsg = new HashSet<>();
			// 检验角色名称或者角色编码是否存在
			boolean existRole = roleList.stream()
				.anyMatch(sysRole -> excel.getRoleName().equals(sysRole.getRoleName())
						|| excel.getRoleCode().equals(sysRole.getRoleCode()));

			if (existRole) {
				errorMsg.add(MsgUtils.getMessage(ErrorCodes.SYS_ROLE_NAMEORCODE_EXISTING, excel.getRoleName(),
						excel.getRoleCode()));
			}

			// 数据合法情况
			if (CollUtil.isEmpty(errorMsg)) {
				insertExcelRole(excel);
			}
			else {
				// 数据不合法情况
				errorMessageList.add(new ErrorMessage(excel.getLineNum(), errorMsg));
			}
		}
		if (CollUtil.isNotEmpty(errorMessageList)) {
			return R.failed(errorMessageList);
		}*/
		return R.ok();
	}

	/**
	 * 查询全部角色列表并转换为Excel视图对象
	 * @return 角色Excel视图对象列表
	 */
	@Override
	public List<RoleExcelVO> listRoles() {
		List<SysRole> roleList = this.list(Wrappers.emptyWrapper());
		// 转换成execl 对象输出
		return roleList.stream().map(role -> {
			RoleExcelVO roleExcelVO = new RoleExcelVO();
			BeanUtil.copyProperties(role, roleExcelVO);
			return roleExcelVO;
		}).toList();
	}

	/**
	 * 插入Excel中的角色数据
	 * @param excel 包含角色信息的Excel数据对象
	 */
	private void insertExcelRole(RoleExcelVO excel) {
		SysRole sysRole = new SysRole();
		sysRole.setRoleName(excel.getRoleName());
		sysRole.setRoleDesc(excel.getRoleDesc());
		sysRole.setRoleCode(excel.getRoleCode());
		this.save(sysRole);
	}

}
