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

package com.lw.cloudplat.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lw.cloudplat.admin.api.entity.SysPost;
import com.lw.cloudplat.admin.api.vo.PostExcelVO;
import com.lw.cloudplat.common.core.util.R;
import org.springframework.validation.BindingResult;

import java.util.List;

/**
 * 岗位信息表
 */
public interface SysPostService extends IService<SysPost> {

	/**
	 * 获取岗位列表用于导出Excel
	 * @return 岗位Excel数据列表
	 */
	List<PostExcelVO> listPosts();

	/**
	 * 导入岗位信息
	 * @param excelVOList 岗位Excel数据列表
	 * @param bindingResult 数据校验结果
	 * @return 导入结果(R对象)
	 */
	R importPost(List<PostExcelVO> excelVOList, BindingResult bindingResult);

}
