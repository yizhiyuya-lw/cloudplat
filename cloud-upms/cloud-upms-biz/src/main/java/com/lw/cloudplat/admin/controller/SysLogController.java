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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lw.cloudplat.admin.api.dto.SysLogDTO;
import com.lw.cloudplat.admin.api.entity.SysLog;
import com.lw.cloudplat.admin.service.SysLogService;
import com.lw.cloudplat.common.core.util.R;
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
 * 系统日志前端控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("/log")
@Tag(description = "log", name = "日志管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysLogController {

	private final SysLogService sysLogService;

	/**
	 * 分页查询系统日志
	 * @param page 分页参数对象
	 * @param sysLog 系统日志查询条件
	 * @return 包含分页结果的响应对象
	 */
	@GetMapping("/page")
	public R getLogPage(@ParameterObject Page page, @ParameterObject SysLogDTO sysLog) {
		return R.ok(sysLogService.getLogPage(page, sysLog));
	}

	/**
	 * 批量删除日志
	 * @param ids 要删除的日志ID数组
	 * @return 操作结果，成功返回success，失败返回false
	 */
	@DeleteMapping
	@HasPermission("sys_log_del")
	public R removeByIds(@RequestBody Long[] ids) {
		return R.ok(sysLogService.removeBatchByIds(CollUtil.toList(ids)));
	}

	/**
	 * 保存日志
	 * @param sysLog 日志实体
	 * @return 操作结果，成功返回success，失败返回false
	 */
	@Inner
	@PostMapping("/save")
	public R saveLog(@Valid @RequestBody SysLog sysLog) {
		return R.ok(sysLogService.saveLog(sysLog));
	}

	/**
	 * 导出系统日志到Excel表格
	 * @param sysLog 系统日志查询条件DTO
	 * @return 符合查询条件的系统日志列表
	 */
	@GetMapping("/export")
	@HasPermission("sys_log_export")
	public List<SysLog> exportLogs(SysLogDTO sysLog) {
		return sysLogService.listLogs(sysLog);
	}

}
