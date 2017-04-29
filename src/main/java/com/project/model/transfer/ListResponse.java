package com.project.model.transfer;

import java.util.List;

/**
 * Created by JORGE-HP on 29/4/2017.
 */
public class ListResponse<T> extends BaseResponse{
    List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
