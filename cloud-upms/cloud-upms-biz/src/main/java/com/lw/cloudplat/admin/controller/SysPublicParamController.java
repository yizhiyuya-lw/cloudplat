/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.lw.cloudplat.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lw.cloudplat.admin.api.entity.SysPublicParam;
import com.lw.cloudplat.admin.service.SysPublicParamService;
import com.lw.cloudplat.common.core.util.R;
import com.lw.cloudplat.common.log.annotation.SysLog;
import com.lw.cloudplat.common.security.annotation.HasPermission;
import com.lw.cloudplat.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公共参数控制器：提供公共参数的增删改查及同步功能
 */
@RestController
@AllArgsConstructor
@RequestMapping("/param")
@Tag(description = "param", name = "公共参数配置")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysPublicParamController {

	private final SysPublicParamService sysPublicParamService;

	/**
	 * 根据key查询公共参数值
	 * @param publicKey 公共参数key
	 * @return 公共参数值
	 */
	@Inner(value = false)
	@Operation(description = "查询公共参数值", summary = "根据key查询公共参数值")
	@GetMapping("/publicValue/{publicKey}")
	public R publicKey(@PathVariable("publicKey") String publicKey) {
		return R.ok(sysPublicParamService.getParamValue(publicKey));
	}

	/**
	 * 分页查询系统公共参数
	 * @param page 分页对象
	 * @param sysPublicParam 公共参数查询条件
	 * @return 分页查询结果
	 */
	@Operation(description = "分页查询", summary = "分页查询")
	@GetMapping("/page")
	public R getParamPage(@ParameterObject Page page, @ParameterObject SysPublicParam sysPublicParam) {
		LambdaUpdateWrapper<SysPublicParam> wrapper = Wrappers.<SysPublicParam>lambdaUpdate()
			.like(StrUtil.isNotBlank(sysPublicParam.getPublicName()), SysPublicParam::getPublicName,
					sysPublicParam.getPublicName())
			.like(StrUtil.isNotBlank(sysPublicParam.getPublicKey()), SysPublicParam::getPublicKey,
					sysPublicParam.getPublicKey())
			.eq(StrUtil.isNotBlank(sysPublicParam.getSystemFlag()), SysPublicParam::getSystemFlag,
					sysPublicParam.getSystemFlag());

		return R.ok(sysPublicParamService.page(page, wrapper));
	}

	/**
	 * 通过id查询公共参数
	 * @param publicId 公共参数id
	 * @return 包含查询结果的响应对象
	 */
	@Operation(description = "通过id查询公共参数", summary = "通过id查询公共参数")
	@GetMapping("/details/{publicId}")
	public R getById(@PathVariable("publicId") Long publicId) {
		return R.ok(sysPublicParamService.getById(publicId));
	}

	/**
	 * 获取系统公共参数详情
	 * @param param 系统公共参数查询对象
	 * @return 包含查询结果的响应对象
	 */
	@GetMapping("/details")
	public R getDetail(@ParameterObject SysPublicParam param) {
		return R.ok(sysPublicParamService.getOne(Wrappers.query(param), false));
	}

	/**
	 * 新增公共参数
	 * @param sysPublicParam 公共参数对象
	 * @return 操作结果
	 */
	@Operation(description = "新增公共参数", summary = "新增公共参数")
	@SysLog("新增公共参数")
	@PostMapping
	@HasPermission("sys_syspublicparam_add")
	public R saveParam(@RequestBody SysPublicParam sysPublicParam) {
		return R.ok(sysPublicParamService.save(sysPublicParam));
	}

	/**
	 * 修改公共参数
	 * @param sysPublicParam 公共参数对象
	 * @return 操作结果
	 */
	@Operation(description = "修改公共参数", summary = "修改公共参数")
	@SysLog("修改公共参数")
	@PutMapping
	@HasPermission("sys_syspublicparam_edit")
	public R updateParam(@RequestBody SysPublicParam sysPublicParam) {
		return sysPublicParamService.updateParam(sysPublicParam);
	}

	/**
	 * 通过id数组删除公共参数
	 * @param ids 要删除的公共参数id数组
	 * @return 操作结果
	 */
	@Operation(description = "删除公共参数", summary = "删除公共参数")
	@SysLog("删除公共参数")
	@DeleteMapping
	@HasPermission("sys_syspublicparam_del")
	public R removeById(@RequestBody Long[] ids) {
		return R.ok(sysPublicParamService.removeParamByIds(ids));
	}

	/**
	 * 导出excel 表格
	 * @return
	 */
	@GetMapping("/export")
	@HasPermission("sys_syspublicparam_edit")
	public List<SysPublicParam> exportParams() {
		return sysPublicParamService.list();
	}

	/**
	 * 同步参数到缓存
	 * @return 操作结果
	 */
	@SysLog("同步参数")
	@PutMapping("/sync")
	@HasPermission("sys_syspublicparam_edit")
	public R syncParam() {
		return sysPublicParamService.syncParamCache();
	}

}
