package com.lacus.domain.rtc.job.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.domain.rtc.job.command.AddJobCommand;
import com.lacus.domain.rtc.job.command.UpdateJobCommand;

public class DataSyncJobModelFactory {

    public static DataSyncJobModel loadFromAddCommand(AddJobCommand addCommand, DataSyncJobModel model) {
        if (addCommand != null && model != null) {
            BeanUtil.copyProperties(addCommand, model);
        }
        return model;
    }

    public static DataSyncJobModel loadFromUpdateCommand(UpdateJobCommand updateJobCommand, DataSyncJobModel model) {
        if (updateJobCommand != null && model != null) {
            BeanUtil.copyProperties(updateJobCommand, model);
        }
        return model;
    }
}
