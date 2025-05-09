package com.lacus.domain.metadata.column;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lacus.dao.metadata.entity.MetaColumnEntity;
import com.lacus.dao.metadata.entity.MetaDbEntity;
import com.lacus.dao.metadata.entity.MetaTableEntity;
import com.lacus.service.metadata.IMetaColumnService;
import com.lacus.service.metadata.IMetaDbService;
import com.lacus.service.metadata.IMetaTableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class ColumnBusiness {

    @Autowired
    private IMetaColumnService metaColumnService;

    @Autowired
    private IMetaDbService metaDbService;

    @Autowired
    private IMetaTableService metaTableService;

    public List<MetaColumnEntity> getColumnsBytTableId(Long tableId) {
        return metaColumnService.getColumnsByTableId(tableId);
    }

    public List<MetaColumnEntity> getColumnsBytTableName(Long datasourceId, String dbName, String tableName) {
        MetaDbEntity metaDb = metaDbService.getMetaDb(datasourceId, dbName);
        if (ObjectUtils.isNotEmpty(metaDb)) {
            Long dbId = metaDb.getDbId();
            MetaTableEntity metaTable = metaTableService.getMetaTable(dbId, tableName);
            if (ObjectUtils.isNotEmpty(metaTable)) {
                LambdaQueryWrapper<MetaColumnEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(MetaColumnEntity::getTableId, metaTable.getTableId());
                List<MetaColumnEntity> list = metaColumnService.list(wrapper);
                for (MetaColumnEntity column : list) {
                    column.setDbName(dbName);
                    column.setTableName(tableName);
                }
                return list;
            }
        }
        return null;
    }
}
