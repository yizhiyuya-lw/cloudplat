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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lw.cloudplat.admin.api.entity.SysRoleMenu;
import com.lw.cloudplat.admin.mapper.SysRoleMenuMapper;
import com.lw.cloudplat.admin.service.SysRoleMenuService;
import com.lw.cloudplat.common.core.constant.CacheConstants;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 角色菜单表服务实现类
 */
@Service
@AllArgsConstructor
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

	private final CacheManager cacheManager;

	/**
	 * @param roleId 角色
	 * @param menuIds 菜单ID拼成的字符串，每个id之间根据逗号分隔
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.MENU_DETAILS, key = "#roleId")
	public Boolean saveRoleMenus(Long roleId, String menuIds) {
		this.remove(Wrappers.<SysRoleMenu>query().lambda().eq(SysRoleMenu::getRoleId, roleId));

		if (StrUtil.isBlank(menuIds)) {
			return Boolean.TRUE;
		}
		List<SysRoleMenu> roleMenuList = Arrays.stream(menuIds.split(StrUtil.COMMA)).map(menuId -> {
			SysRoleMenu roleMenu = new SysRoleMenu();
			roleMenu.setRoleId(roleId);
			roleMenu.setMenuId(Long.valueOf(menuId));
			return roleMenu;
		}).toList();

		// 清空userinfo
		cacheManager.getCache(CacheConstants.USER_DETAILS).clear();
		this.saveBatch(roleMenuList);
		return Boolean.TRUE;
	}

}
