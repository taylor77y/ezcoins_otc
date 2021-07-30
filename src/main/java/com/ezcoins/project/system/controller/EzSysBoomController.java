package com.ezcoins.project.system.controller;


import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Log;
import com.ezcoins.constant.enums.OperatorType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.common.service.mapper.SearchModel;
import com.ezcoins.project.system.entity.EzSysBoom;
import com.ezcoins.project.system.entity.EzSysLogininfor;
import com.ezcoins.project.system.entity.req.SYSBoomReqDto;
import com.ezcoins.project.system.service.EzSysBoomService;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponsePageList;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 首页轮播图 前端控制器
 * </p>
 *
 * @author wanglei
 * @since 2021-07-29
 */
@Api(tags = "Admin-系统模块")
@RestController
@RequestMapping("/admin/system/ez-sys-boom")
public class EzSysBoomController {
    @Autowired
    private EzSysBoomService boomService;


    @PostMapping("getBoomList")
    @ApiOperation(value = "轮播图列表")
    @AuthToken
    public ResponsePageList<EzSysBoom> getBoomList(@RequestBody SearchModel<EzSysBoom> searchModel){
        return ResponsePageList.success(boomService.page(searchModel.getPage(), searchModel.getQueryModel()));
    }

    @PostMapping("addOrUpdateBoom")
    @ApiOperation(value = "增加/修改轮播图")
    @AuthToken
    @Log(title = "系统模块", logInfo ="增加/修改轮播图", operatorType = OperatorType.MANAGE)
    public Response addOrUpdateBoom(@RequestBody @Validated SYSBoomReqDto sysBoomReqDto){
        Integer id = sysBoomReqDto.getId();
        EzSysBoom ezSysBoom = new EzSysBoom();
        BeanUtils.copyBeanProp(ezSysBoom, sysBoomReqDto);
        if (StringUtils.isNotNull(id)){
            ezSysBoom.setUpdateBy(ContextHandler.getUserName());
            boomService.updateById(ezSysBoom);

        }else {
            ezSysBoom.setCreateBy(ContextHandler.getUserName());
            boomService.save(ezSysBoom);
        }
        return Response.success();
    }


    @DeleteMapping("delBoom/{id}")
    @ApiOperation(value = "删除轮播图")
    @AuthToken
    @Log(title = "系统模块", logInfo ="删除轮播图", operatorType = OperatorType.MANAGE)
    public Response delBoom(@PathVariable Integer id){
        boomService.removeById(id);
        return Response.success();
    }


}

