package com.lacus.domain.rtc.job.dto;

import lombok.Data;

import java.util.LinkedList;

@Data
public class MappedTableDTO {
    private Long jobId;
    private LinkedList<TableDTO> mappedSourceTables;
    private LinkedList<TableDTO> mappedSinkTables;
}
