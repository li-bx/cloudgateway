package com.hd.auserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hd.auserver.entity.AccountEntity;
import com.hd.auserver.service.AccountService;
import com.hd.common.MyPage;
import com.hd.common.RetResponse;
import com.hd.common.RetResult;
import com.hd.common.model.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.sql.Wrapper;
import java.util.Date;
import java.util.List;

/**
 * @Author: liwei
 * @Description:
 */
@RestController
public class AccountController {

    @Autowired
    AccountService accountService;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/account")
    public RetResult list(@RequestParam("account") String account,String enterprise,int pageNum,int pageSize) {
        QueryWrapper qw = new QueryWrapper();
        qw.like("account",account);
        qw.eq("enterprise",enterprise);
        //qw1.orderByDesc("id","account");
        MyPage<AccountEntity> accountEntityPage = accountService.selectAccounts(pageNum, pageSize,qw);
        //        accountEntities.get(1).setAccount(null);
        return RetResponse.makeRsp(accountEntityPage);
    }
    @PostMapping("/account/{account}")
    public RetResult Add(@PathVariable ("account")  String account,@RequestParam("enterprise") String enterprise,@RequestParam("password") String password) {
        accountService.save(new AccountEntity(null,enterprise,account,passwordEncoder.encode(password),new Date()));
        return RetResponse.makeRsp("添加账号成功.");
    }

    @GetMapping("/account/{account}")
    public RetResult get(@PathVariable ("account")  String account,@RequestParam("enterprise") String enterprise) throws Exception {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("account",account);
        qw.eq("enterprise",enterprise);
        List list = accountService.list(qw);
        if(list.size()<=0){
            throw  new Exception("账号不存在!");
        }
        return RetResponse.makeRsp(list);
    }

    @DeleteMapping("/account/{account}")
    public RetResult delete(@PathVariable("account")  String account,@RequestParam("enterprise") String enterprise) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("account",account);
        qw.eq("enterprise",enterprise);
        accountService.remove(qw);
        return RetResponse.makeRsp("删除账号成功.");
    }
}