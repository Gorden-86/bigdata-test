package com.lacus.domain.rtc.jobCatalog.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateJobCatalogCommand extends AddJobCatalogCommand {

    @NotBlank
    private String catalogId;
}
