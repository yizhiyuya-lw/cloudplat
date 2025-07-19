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

import com.lw.cloudplat.admin.api.entity.SysDept;
import com.lw.cloudplat.admin.api.vo.DeptExcelVo;
import com.lw.cloudplat.admin.service.SysDeptService;
import com.lw.cloudplat.common.core.util.R;
import com.lw.cloudplat.common.log.annotation.SysLog;
import com.lw.cloudplat.common.security.annotation.HasPermission;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门管理前端控制器
 *
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dept")
@Tag(description = "dept", name = "部门管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysDeptController {

	private final SysDeptService sysDeptService;

	/**
	 * 通过ID查询部门信息
	 * @param id 部门ID
	 * @return 包含部门信息的响应对象
	 */
	@GetMapping("/{id}")
	public R getById(@PathVariable Long id) {
		return R.ok(sysDeptService.getById(id));
	}

	/**
	 * 查询全部部门列表
	 * @return 包含全部部门列表的响应结果
	 */
	@GetMapping("/list")
	public R listDepts() {
		return R.ok(sysDeptService.list());
	}

	/**
	 * 获取树形菜单
	 * @param deptName 部门名称
	 * @return 包含树形菜单的响应结果
	 */
	@GetMapping(value = "/tree")
	public R getDeptTree(String deptName) {
		return R.ok(sysDeptService.getDeptTree(deptName));
	}

	/**
	 * 保存部门信息
	 * @param sysDept 部门实体
	 * @return 操作结果
	 */
	@SysLog("添加部门")
	@PostMapping
	@HasPermission("sys_dept_add")
	public R saveDept(@Valid @RequestBody SysDept sysDept) {
		return R.ok(sysDeptService.save(sysDept));
	}

	/**
	 * 根据ID删除部门
	 * @param id 部门ID
	 * @return 操作结果，成功返回true，失败返回false
	 */
	@SysLog("删除部门")
	@DeleteMapping("/{id}")
	@HasPermission("sys_dept_del")
	public R removeById(@PathVariable Long id) {
		return R.ok(sysDeptService.removeDeptById(id));
	}

	/**
	 * 编辑部门信息
	 * @param sysDept 部门实体对象
	 * @return 操作结果，成功返回success，失败返回false
	 */
	@SysLog("编辑部门")
	@PutMapping
	@HasPermission("sys_dept_edit")
	public R updateDept(@Valid @RequestBody SysDept sysDept) {
		sysDept.setUpdateTime(LocalDateTime.now());
		return R.ok(sysDeptService.updateById(sysDept));
	}

	/**
	 * 获取部门子级列表
	 * @param deptId 部门ID
	 * @return 包含子级部门列表的响应结果
	 */
	@GetMapping(value = "/getDescendantList/{deptId}")
	public R getDescendantList(@PathVariable Long deptId) {
		return R.ok(sysDeptService.listDescendants(deptId));
	}

	/**
	 * 导出部门数据
	 * @return 部门数据列表
	 */
	@GetMapping("/export")
	public List<DeptExcelVo> exportDepts() {
		return sysDeptService.exportDepts();
	}

	/**
	 * 导入部门信息
	 * @param excelVOList 部门Excel数据列表
	 * @param bindingResult 数据校验结果
	 * @return 导入结果
	 */
	@PostMapping("import")
	public R importDept(List<DeptExcelVo> excelVOList, BindingResult bindingResult) {

		return sysDeptService.importDept(excelVOList, bindingResult);
	}

}
