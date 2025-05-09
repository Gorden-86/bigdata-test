package com.lacus.domain.rtc.instance.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.rtc.entity.DataSyncJobInstanceEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DataSyncJobInstanceModel extends DataSyncJobInstanceEntity {
    private static final long serialVersionUID = -621202053228278357L;
    private String jobName;
    private String trackingUrl;

    public DataSyncJobInstanceModel(DataSyncJobInstanceEntity entity) {
        BeanUtil.copyProperties(entity, this);
    }
}
