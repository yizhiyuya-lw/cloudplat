package com.lw.cloudplat.admin.api.feign;

import com.lw.cloudplat.admin.api.entity.SysDictItem;
import com.lw.cloudplat.common.core.constant.ServiceNameConstants;
import com.lw.cloudplat.common.core.util.R;
import com.lw.cloudplat.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 远程字典服务接口
 */
@FeignClient(contextId = "remoteDictService", value = ServiceNameConstants.UPMS_SERVICE)
public interface RemoteDictService {

	/**
	 * 通过字典类型查找字典
	 * @param type 字典类型
	 * @return 同类型字典
	 */
	@NoToken
	@GetMapping("/dict/remote/type/{type}")
	R<List<SysDictItem>> getDictByType(@PathVariable("type") String type);

}
