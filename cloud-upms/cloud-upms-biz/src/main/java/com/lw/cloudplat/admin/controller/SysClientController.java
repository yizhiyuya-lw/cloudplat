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

package com.lw.cloudplat.admin.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lw.cloudplat.admin.api.entity.SysOauthClientDetails;
import com.lw.cloudplat.admin.service.SysOauthClientDetailsService;
import com.lw.cloudplat.common.core.util.R;
import com.lw.cloudplat.common.log.annotation.SysLog;
import com.lw.cloudplat.common.security.annotation.HasPermission;
import com.lw.cloudplat.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户端管理模块前端控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("/client")
@Tag(description = "client", name = "客户端管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysClientController {

	private final SysOauthClientDetailsService clientDetailsService;

	/**
	 * 通过客户端ID查询客户端详情
	 * @param clientId 客户端ID
	 * @return 包含客户端详情的响应对象
	 */
	@GetMapping("/{clientId}")
	public R getByClientId(@PathVariable String clientId) {
		SysOauthClientDetails details = clientDetailsService
			.getOne(Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId));
		return R.ok(details);
	}

	/**
	 * 分页查询系统终端信息
	 * @param page 分页参数对象
	 * @param sysOauthClientDetails 系统终端查询条件
	 * @return 分页查询结果
	 */
	@GetMapping("/page")
	public R getClientPage(@ParameterObject Page page, @ParameterObject SysOauthClientDetails sysOauthClientDetails) {
		LambdaQueryWrapper<SysOauthClientDetails> wrapper = Wrappers.<SysOauthClientDetails>lambdaQuery()
			.like(StrUtil.isNotBlank(sysOauthClientDetails.getClientId()), SysOauthClientDetails::getClientId,
					sysOauthClientDetails.getClientId())
			.like(StrUtil.isNotBlank(sysOauthClientDetails.getClientSecret()), SysOauthClientDetails::getClientSecret,
					sysOauthClientDetails.getClientSecret());
		return R.ok(clientDetailsService.page(page, wrapper));
	}

	/**
	 * 添加客户端终端
	 * @param clientDetails 客户端详情实体
	 * @return 操作结果，成功返回success，失败返回false
	 */
	@SysLog("添加终端")
	@PostMapping
	@HasPermission("sys_client_add")
	public R saveClient(@Valid @RequestBody SysOauthClientDetails clientDetails) {
		return R.ok(clientDetailsService.saveClient(clientDetails));
	}

	/**
	 * 根据ID列表批量删除终端
	 * @param ids 要删除的终端ID数组
	 * @return 操作结果，成功返回success
	 */
	@SysLog("删除终端")
	@DeleteMapping
	@HasPermission("sys_client_del")
	public R removeById(@RequestBody Long[] ids) {
		clientDetailsService.removeBatchByIds(CollUtil.toList(ids));
		return R.ok();
	}

	/**
	 * 编辑终端信息
	 * @param clientDetails 终端实体信息
	 * @return 操作结果
	 */
	@SysLog("编辑终端")
	@PutMapping
	@HasPermission("sys_client_edit")
	public R updateClient(@Valid @RequestBody SysOauthClientDetails clientDetails) {
		return R.ok(clientDetailsService.updateClientById(clientDetails));
	}

	/**
	 * 根据客户端ID获取客户端详情
	 * @param clientId 客户端ID
	 * @return 包含客户端详情的响应结果
	 */
	@Inner
	@GetMapping("/getClientDetailsById/{clientId}")
	public R getClientDetailsById(@PathVariable String clientId) {
		return R.ok(clientDetailsService.getOne(
				Wrappers.<SysOauthClientDetails>lambdaQuery().eq(SysOauthClientDetails::getClientId, clientId), false));
	}

	/**
	 * 同步缓存字典
	 * @return 操作结果
	 */
	@SysLog("同步终端")
	@PutMapping("/sync")
	public R syncClient() {
		return clientDetailsService.syncClientCache();
	}

	/**
	 * 导出客户端信息到Excel
	 * @param sysOauthClientDetails 客户端查询条件
	 * @return 符合条件的客户端列表
	 */
	@SysLog("导出excel")
	@GetMapping("/export")
	public List<SysOauthClientDetails> exportClients(SysOauthClientDetails sysOauthClientDetails) {
		return clientDetailsService.list(Wrappers.query(sysOauthClientDetails));
	}

}
