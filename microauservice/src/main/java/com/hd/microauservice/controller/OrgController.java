package com.hd.microauservice.controller;

import com.hd.common.RetResponse;
import com.hd.common.RetResult;
import com.hd.common.model.RequiresPermissions;
import com.hd.common.model.TokenInfo;
import com.hd.common.vo.SyOrgVo;
import com.hd.microauservice.conf.SecurityContext;
import com.hd.microauservice.entity.SyOrgEntity;
import com.hd.microauservice.service.SyOrgService;
import com.hd.microauservice.utils.EnterpriseVerifyUtil;
import com.hd.microauservice.utils.VoConvertUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liwei
 */
@Api(tags = "部门Controller")
@RestController
@Slf4j
public class OrgController  {
    @Autowired
    SyOrgService syOrgService;

    @ApiOperation(value = "创建部门")
    @RequiresPermissions("org:create")
    @PostMapping("/org")
    public RetResult createOrg(@RequestBody @Validated SyOrgVo syOrgVo) throws Exception {
        EnterpriseVerifyUtil.verifyEnterId(syOrgVo.getEnterpriseId());
        TokenInfo tokenInfo = SecurityContext.GetCurTokenInfo();
        if(syOrgVo.getParentId()==null){
            //每个企业只能创建一个顶级
            if(syOrgService.haveTopOrg(syOrgVo.getEnterpriseId())){
                throw new Exception("顶级部门已存在!");
            }
        }
        SyOrgEntity syOrgEntity=new SyOrgEntity();
        VoConvertUtils.convertObject(syOrgVo,syOrgEntity);
        syOrgService.save(syOrgEntity);
        return RetResponse.makeRsp("创建部门成功.");
    }
    @ApiOperation(value = "获取部门tree")
    @RequiresPermissions("org:tree")
    @GetMapping("/org/tree")
    public RetResult getOrgTree(String enterId) {
        EnterpriseVerifyUtil.verifyEnterId(enterId);
        List<SyOrgVo> listVo = syOrgService.getOrgTree(enterId);
        return RetResponse.makeRsp(listVo);
    }

    @ApiOperation(value = "获取自己有权限的部门tree")
    @RequiresPermissions(value = "org:tree",note ="获取自己有权限的部门tree" )
    @GetMapping("/org/my/tree")
    public RetResult getOrgMyTree() {
        List<SyOrgVo> listVo = syOrgService.getMyOrgTree();
        return RetResponse.makeRsp(listVo);
    }
    @ApiOperation(value = "获取自己有权限的部门tree带人员")
    @RequiresPermissions(value = "org:treemen",note ="获取自己有权限的部门tree带人员" )
    @GetMapping("/org/my/treemen")
    public RetResult getOrgMyTreemen() {
        List<SyOrgVo> listVo = syOrgService.getMyOrgTreeWithMen();
        return RetResponse.makeRsp(listVo);
    }
    @ApiOperation(value = "编辑部门")
    @RequiresPermissions("org:edit")
    @PutMapping("/org/{id}")
    public RetResult editOrg(@PathVariable("id") Long orgId,@RequestBody @Validated SyOrgVo syOrgVo) throws Exception {
        EnterpriseVerifyUtil.verifyEnterId(syOrgVo.getEnterpriseId());
        //syOrgVo.setEnterpriseId(SecurityContext.GetCurTokenInfo().getenterpriseId());
        SyOrgEntity syOrgEntity=new SyOrgEntity();
        VoConvertUtils.convertObject(syOrgVo,syOrgEntity);
        syOrgService.updateById(syOrgEntity);
        return RetResponse.makeRsp("修改部门成功");
    }
    @ApiOperation(value = "删除部门")
    @RequiresPermissions("org:delete")
    @DeleteMapping("/org/{id}")
    public RetResult delOrg(@PathVariable("id") Long orgId) {
        EnterpriseVerifyUtil.verifyEnterId(syOrgService.getById(orgId).getEnterpriseId());
        syOrgService.removeById(orgId);
        return RetResponse.makeRsp("删除部门成功");
    }
}