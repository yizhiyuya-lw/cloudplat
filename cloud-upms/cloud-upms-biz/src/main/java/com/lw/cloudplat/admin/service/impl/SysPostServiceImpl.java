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
package com.lw.cloudplat.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lw.cloudplat.admin.api.entity.SysPost;
import com.lw.cloudplat.admin.api.vo.PostExcelVO;
import com.lw.cloudplat.admin.mapper.SysPostMapper;
import com.lw.cloudplat.admin.service.SysPostService;
import com.lw.cloudplat.common.core.exception.ErrorCodes;
import com.lw.cloudplat.common.core.util.MsgUtils;
import com.lw.cloudplat.common.core.util.R;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 岗位信息表服务实现类
 */
@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements SysPostService {

	/**
	 * 导入岗位
	 * @param excelVOList 岗位列表
	 * @param bindingResult 错误信息列表
	 * @return ok fail
	 */
	@Override
	public R importPost(List<PostExcelVO> excelVOList, BindingResult bindingResult) {
		// 通用校验获取失败的数据
		/*List<ErrorMessage> errorMessageList = (List<ErrorMessage>) bindingResult.getTarget();

		// 个性化校验逻辑
		List<SysPost> postList = this.list();

		// 执行数据插入操作 组装 PostDto
		for (PostExcelVO excel : excelVOList) {
			Set<String> errorMsg = new HashSet<>();
			// 检验岗位名称或者岗位编码是否存在
			boolean existPost = postList.stream()
				.anyMatch(post -> excel.getPostName().equals(post.getPostName())
						|| excel.getPostCode().equals(post.getPostCode()));

			if (existPost) {
				errorMsg.add(MsgUtils.getMessage(ErrorCodes.SYS_POST_NAMEORCODE_EXISTING, excel.getPostName(),
						excel.getPostCode()));
			}

			// 数据合法情况
			if (CollUtil.isEmpty(errorMsg)) {
				insertExcelPost(excel);
			}
			else {
				// 数据不合法
				errorMessageList.add(new ErrorMessage(excel.getLineNum(), errorMsg));
			}
		}
		if (CollUtil.isNotEmpty(errorMessageList)) {
			return R.failed(errorMessageList);
		}*/
		return R.ok();
	}

	/**
	 * 获取岗位列表并转换为Excel导出对象
	 * @return 岗位Excel导出对象列表
	 */
	@Override
	public List<PostExcelVO> listPosts() {
		List<SysPost> postList = this.list(Wrappers.emptyWrapper());
		// 转换成execl 对象输出
		return postList.stream().map(post -> {
			PostExcelVO postExcelVO = new PostExcelVO();
			BeanUtil.copyProperties(post, postExcelVO);
			return postExcelVO;
		}).toList();
	}

	/**
	 * 插入Excel中的岗位数据
	 * @param excel 包含岗位信息的Excel数据对象
	 */
	private void insertExcelPost(PostExcelVO excel) {
		SysPost sysPost = new SysPost();
		BeanUtil.copyProperties(excel, sysPost);
		this.save(sysPost);
	}

}
