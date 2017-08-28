package com.rh.core.wfe.util;

import com.rh.core.base.Bean;
import com.rh.core.wfe.WfAct;

public class TestLineEvent extends AbstractLineEvent {

    @Override
    public void forward(WfAct preWfAct, WfAct nextWfAct, Bean lineDef) {
        // TODO Auto-generated method stub
        System.out.println("#############forward@@@@@@@@@@@@@@@@@" );
    }

    @Override
    public void backward(WfAct preWfAct, WfAct nextWfAct, Bean lineDef) {
        // TODO Auto-generated method stub
        System.out.println("@@@@@@@@@@@@forward###########" );
    }

}
