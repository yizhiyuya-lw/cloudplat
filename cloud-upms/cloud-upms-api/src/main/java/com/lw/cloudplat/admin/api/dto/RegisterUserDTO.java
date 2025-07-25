package com.lw.cloudplat.admin.api.dto;

import lombok.Data;

/**
 * 注册用户 DTO
 *
 */
@Data
public class RegisterUserDTO {

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 新密码
	 */
	private String password;

	/**
	 * 电话
	 */
	private String phone;

}
