package com.lacus.core.cache.map;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import com.lacus.enums.DatasourceStatusEnum;
import com.lacus.enums.DatasourceTypeEnum;
import com.lacus.enums.dictionary.*;
import com.lacus.enums.interfaces.DictionaryEnum;
import com.lacus.dao.system.result.DictionaryData;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 本地一级缓存  使用Map
 */
@Slf4j
public class MapCache {

    private final static Map<String, List<DictionaryData>> DICTIONARY_CACHE = MapUtil.newHashMap(128);

    static {
        initDictionaryCache();
    }

    private static void initDictionaryCache() {

        DICTIONARY_CACHE.put(BusinessTypeEnum.getDictName(), arrayToList(BusinessTypeEnum.values()));
        DICTIONARY_CACHE.put(YesOrNoEnum.getDictName(), arrayToList(YesOrNoEnum.values()));
        DICTIONARY_CACHE.put(StatusEnum.getDictName(), arrayToList(StatusEnum.values()));
        DICTIONARY_CACHE.put(GenderEnum.getDictName(), arrayToList(GenderEnum.values()));
        DICTIONARY_CACHE.put(NoticeStatusEnum.getDictName(), arrayToList(NoticeStatusEnum.values()));
        DICTIONARY_CACHE.put(NoticeTypeEnum.getDictName(), arrayToList(NoticeTypeEnum.values()));
        DICTIONARY_CACHE.put(OperationStatusEnum.getDictName(), arrayToList(OperationStatusEnum.values()));
        DICTIONARY_CACHE.put(VisibleStatusEnum.getDictName(), arrayToList(VisibleStatusEnum.values()));
        DICTIONARY_CACHE.put(DatasourceTypeEnum.getDictName(), arrayToList(DatasourceTypeEnum.values()));
        DICTIONARY_CACHE.put(DatasourceStatusEnum.getDictName(), arrayToList(DatasourceStatusEnum.values()));
    }

    public static Map<String, List<DictionaryData>> dictionaryCache() {
        return DICTIONARY_CACHE;
    }

    @SuppressWarnings("rawtypes")
    private static List<DictionaryData> arrayToList(DictionaryEnum[] dictionaryEnums) {
        if(ArrayUtil.isEmpty(dictionaryEnums)) {
            return ListUtil.empty();
        }
        return Arrays.stream(dictionaryEnums).map(DictionaryData::new).collect(Collectors.toList());
    }


}
