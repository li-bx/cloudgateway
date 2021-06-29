package com.hd.gateway.conf;

/**
 * @Author: liwei
 */

import com.alibaba.fastjson.JSON;
import com.hd.gateway.GatewayApplication;
import com.hd.gateway.model.RetResult;
import com.hd.gateway.model.TokenInfo;
import com.hd.gateway.utils.HttpUtil;
import com.hd.gateway.utils.JwtUtils;
import com.hd.gateway.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


//全局过滤器，实现GlobalFilter接口，和Ordered接口即可。
@Component
@Slf4j
public class CheckTokenGlobalGatewayFilter implements GlobalFilter, Ordered {
    @Autowired
    JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("check token" + Thread.currentThread().getId());
        String kk = exchange.getRequest().getHeaders().get("Authorization").get(0);
        //TODO:  判断token 登录
        TokenInfo tokenInfo;
        try {
            tokenInfo = jwtUtils.decodeToken(kk.replace("Bearer ", ""));
            String uri=exchange.getRequest().getPath().value();
            //url第一个分段一遍用作服务名识别，此处去掉
            tokenInfo.setUri(uri.substring(uri.indexOf("/",1)));
            tokenInfo.setMethod(exchange.getRequest().getMethodValue());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.makeJsonResponse(exchange.getResponse(),
                    new RetResult(HttpStatus.UNAUTHORIZED.value(), "登录校验失败!", false));
        }
        //TODO: 根据应用，判断token超时
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Long mSec = System.currentTimeMillis() - sdf.parse(tokenInfo.getLoginTime()).getTime();
            if (mSec > (60000 * 20)) {
                return ResponseUtil.makeJsonResponse(exchange.getResponse(),
                        new RetResult(HttpStatus.UNAUTHORIZED.value(), "登录校验失败!", false));
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseUtil.makeJsonResponse(exchange.getResponse(),
                    new RetResult(HttpStatus.UNAUTHORIZED.value(), "登录校验失败!", false));
        }

        ServerHttpRequest request = exchange.getRequest().mutate().header("token-info", JSON.toJSONString(tokenInfo)).build();
        ServerWebExchange buildExchange = exchange.mutate().request(request).build();

        //TODO: 记录访问日志
        return chain.filter(buildExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 300;
    }
}