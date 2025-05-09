package com.lacus.domain.system.menu.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.lacus.common.constant.Constants;
import com.lacus.domain.system.menu.dto.MetaDTO;
import com.lacus.domain.system.menu.dto.RouterDTO;
import com.lacus.dao.system.entity.SysMenuEntity;
import com.lacus.enums.MenuComponentEnum;
import com.lacus.enums.MenuTypeEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RouterModel extends SysMenuEntity {


    public RouterDTO produceDirectoryRouterVO(List<RouterDTO> children) {
        RouterDTO router = produceDefaultRouterVO();

        if (CollUtil.isNotEmpty(children) && Objects.equals(MenuTypeEnum.DIRECTORY.getValue(), getMenuType())) {
            router.setAlwaysShow(true);
            router.setRedirect("noRedirect");
            router.setChildren(children);
        }

        return router;
    }


    public RouterDTO produceMenuFrameRouterVO() {
        RouterDTO router = new RouterDTO();

        router.setMeta(null);
        List<RouterDTO> childrenList = new ArrayList<>();
        RouterDTO children = new RouterDTO();
        children.setPath(getPath());
        children.setComponent(getComponent());
        children.setName(StrUtil.upperFirst(getPath()));
        children.setMeta(new MetaDTO(getMenuName(), getIcon(), !getIsCache(), getPath()));
        children.setQuery(getQuery());
        childrenList.add(children);
        router.setChildren(childrenList);

        return router;
    }


    public RouterDTO produceInnerLinkRouterVO() {

        RouterDTO router = new RouterDTO();

        router.setMeta(new MetaDTO(getMenuName(), getIcon()));
        router.setPath("/");
        List<RouterDTO> childrenList = new ArrayList<>();
        RouterDTO children = new RouterDTO();
        String routerPath = trimHttpPrefixForInnerLink(getPath());
        children.setPath(routerPath);
        children.setComponent(MenuComponentEnum.INNER_LINK.description());
        children.setName(StrUtil.upperFirst(routerPath));
        children.setMeta(new MetaDTO(getMenuName(), getIcon(), getPath()));
        childrenList.add(children);
        router.setChildren(childrenList);

        return router;
    }

    public RouterDTO produceDefaultRouterVO() {
        RouterDTO router = new RouterDTO();
        router.setHidden(!getIsVisible());
        router.setName(getRouteName());
        router.setPath(getRouterPath());
        router.setComponent(getComponentTypeForFrontEnd());
        router.setQuery(getQuery());
        router.setMeta(new MetaDTO(getMenuName(), getIcon(), !getIsCache(), getPath()));
        return router;
    }


    /**
     * 获取路由名称
     * @return 路由名称
     */
    public String getRouteName() {
        String routerName = StrUtil.upperFirst(getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isSingleLevelMenu()) {
            routerName = StrUtil.EMPTY;
        }
        return routerName;
    }


    /**
     * 是否为单个一级菜单
     *
     * @return 结果
     */
    public boolean isSingleLevelMenu() {
        return getParentId().intValue() == 0
            && MenuTypeEnum.MENU.getValue().equals(getMenuType())
            && !getIsExternal();
    }


    /**
     * 是否为菜单内部跳转
     *
     * @return 结果
     */
    public boolean isMultipleLevelMenu(Tree<Long> tree) {
        return MenuTypeEnum.DIRECTORY.getValue().equals(getMenuType()) && tree.hasChild();
    }


    /**
     * 获取路由地址
     * @return 路由地址
     */
    public String getRouterPath() {
        String routerPath = getPath();
        // 内链打开外网方式
        if (getParentId().intValue() != 0 && isInnerLink()) {
            routerPath = trimHttpPrefixForInnerLink(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0L == getParentId() && Objects.equals(MenuTypeEnum.DIRECTORY.getValue(), getMenuType()) && !getIsExternal()) {
            routerPath = "/" + getPath();
        // 非外链并且是一级目录（类型为菜单）
        } else if (isSingleLevelMenu()) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 是否为内链组件
     *
     * @return 结果
     */
    public boolean isInnerLink() {
        return !getIsExternal() && (HttpUtil.isHttp(getPath()) || HttpUtil.isHttps(getPath()));
    }


    /**
     * 内链域名特殊字符替换
     */
    public String trimHttpPrefixForInnerLink(String path) {
        if (HttpUtil.isHttp(path)) {
            return StrUtil.stripIgnoreCase(path, Constants.HTTP, "");
        }
        if (HttpUtil.isHttps(path)) {
            return StrUtil.stripIgnoreCase(path, Constants.HTTPS, "");
        }
        return path;
    }

    /**
     * 获取组件信息
     *
     * @return 组件信息
     */
    public String getComponentTypeForFrontEnd() {
        String component = MenuComponentEnum.LAYOUT.description();
        if (StrUtil.isNotEmpty(getComponent()) && !isSingleLevelMenu()) {
            component = getComponent();
        } else if (isInnerLinkView()) {
            component = MenuComponentEnum.INNER_LINK.description();
        } else if (isParentView()) {
            component = MenuComponentEnum.PARENT_VIEW.description();
        }
        return component;
    }

    /**
     * 是否为inner_link_view组件
     *
     * @return 结果
     */
    public boolean isInnerLinkView() {
        return StrUtil.isEmpty(getComponent()) && getParentId().intValue() != 0 && isInnerLink();
    }


    /**
     * 是否为parent_view组件
     *
     * @return 结果
     */
    public boolean isParentView() {
        return StrUtil.isEmpty(getComponent())
            && getParentId().intValue() != 0
            && MenuTypeEnum.DIRECTORY.getValue().equals(getMenuType());
    }


}
