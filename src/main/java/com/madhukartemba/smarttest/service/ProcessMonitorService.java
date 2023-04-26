package com.madhukartemba.smarttest.service;

import java.util.List;

import com.madhukartemba.smarttest.entity.ProcessBuilderWrapper;

public class ProcessMonitorService extends RefreshableDisplayService {

    List<ProcessBuilderWrapper> wrapperList;

    public ProcessMonitorService(List<ProcessBuilderWrapper> list) {
        this.wrapperList = list;
    }

}
