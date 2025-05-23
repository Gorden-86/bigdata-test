package com.lacus.admin.controller.system;

import com.lacus.common.core.base.BaseController;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.domain.common.command.BulkOperationCommand;
import com.lacus.domain.system.notice.command.NoticeAddCommand;
import com.lacus.domain.system.notice.dto.NoticeDTO;
import com.lacus.domain.system.notice.NoticeBusiness;
import com.lacus.domain.system.notice.query.NoticeQuery;
import com.lacus.domain.system.notice.command.NoticeUpdateCommand;
import com.lacus.core.annotations.AccessLog;
import com.lacus.enums.dictionary.BusinessTypeEnum;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公告 信息操作处理
 */
@RestController
@RequestMapping("/system/notice")
@Validated
public class SysNoticeController extends BaseController {

    @Autowired
    private NoticeBusiness noticeBusiness;

    /**
     * 获取通知公告列表
     */
    @PreAuthorize("@permission.has('system:notice:list')")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(NoticeQuery query) {
        PageDTO pageDTO = noticeBusiness.getNoticeList(query);
        return ResponseDTO.ok(pageDTO);
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @PreAuthorize("@permission.has('system:notice:query')")
    @GetMapping(value = "/{noticeId}")
    public ResponseDTO<NoticeDTO> getInfo(@PathVariable @NotNull @Positive Long noticeId) {
        return ResponseDTO.ok(noticeBusiness.getNoticeInfo(noticeId));
    }

    /**
     * 新增通知公告
     */
    @PreAuthorize("@permission.has('system:notice:add')")
    @AccessLog(title = "通知公告", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<?> add(@RequestBody NoticeAddCommand addCommand) {
        noticeBusiness.addNotice(addCommand);
        return ResponseDTO.ok();
    }

    /**
     * 修改通知公告
     */
    @PreAuthorize("@permission.has('system:notice:edit')")
    @AccessLog(title = "通知公告", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@RequestBody NoticeUpdateCommand updateCommand) {
        noticeBusiness.updateNotice(updateCommand);
        return ResponseDTO.ok();
    }

    /**
     * 删除通知公告
     */
    @PreAuthorize("@permission.has('system:notice:remove')")
    @AccessLog(title = "通知公告", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{noticeIds}")
    public ResponseDTO<?> remove(@PathVariable List<Long> noticeIds) {
        noticeBusiness.deleteNotice(new BulkOperationCommand<>(noticeIds));
        return ResponseDTO.ok();
    }



}
