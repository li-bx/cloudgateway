package com.hd.common;

import com.hd.common.model.QueryExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liwei
 * @Description:
 */
public class PageQueryExpressionList extends PageQuery<List<QueryExpression>>{

    public PageQueryExpressionList(){
        queryData=new ArrayList<>();
    }
}
