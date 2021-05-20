/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.www.extensions.control.menu.page;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseJSPProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import java.io.IOException;

import java.util.Objects;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Allen Ziegenfus
 */
@Component(
	immediate = true,
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=150"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class PageProductNavigationControlMenuEntry
	extends BaseJSPProductNavigationControlMenuEntry
	implements ProductNavigationControlMenuEntry {

	public static final String LAYOUT_GUEST_PERMISSIONS =
		"LAYOUT_GUEST_PERMISSIONS";

	public static final String MANAGE_LAYOUT_PERMISSIONS_URL =
		"MANAGE_LAYOUT_PERMISSIONS_URL";

	public static final String MANAGE_LAYOUT_URL = "MANAGE_LAYOUT_URL";

	@Override
	public String getIconJspPath() {
		return "/control/menu/page.jsp";
	}

	@Override
	public boolean includeIcon(
			HttpServletRequest httpServletRequest, HttpServletResponse response)
		throws IOException {

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		if (layout != null) {
			httpServletRequest.setAttribute(
				MANAGE_LAYOUT_PERMISSIONS_URL,
				getManageLayoutPermissionsURL(httpServletRequest, layout));
			httpServletRequest.setAttribute(
				MANAGE_LAYOUT_URL,
				getManageLayoutURL(httpServletRequest, layout));

			httpServletRequest.setAttribute(
				LAYOUT_GUEST_PERMISSIONS,
				hasGuestPermissions(httpServletRequest, layout));
		}

		return super.includeIcon(httpServletRequest, response);
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		if (layout == null) {
			return false;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!(themeDisplay.isShowLayoutTemplatesIcon() ||
			  themeDisplay.isShowPageSettingsIcon())) {

			return false;
		}

		if (layout.isSystem()) {
			return false;
		}

		if (layout.isTypeControlPanel()) {
			return false;
		}

		return !Objects.equals(
			layout.getType(), LayoutConstants.TYPE_ASSET_DISPLAY);
	}

	@Override
	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.osb.www.extensions.control.menu.page)",
		unbind = "-"
	)
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
	}

	protected String getManageLayoutPermissionsURL(
		HttpServletRequest httpServletRequest, Layout layout) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			return PermissionsURLTag.doTag(
				StringPool.BLANK, Layout.class.getName(),
				HtmlUtil.escape(layout.getName(themeDisplay.getLocale())), null,
				String.valueOf(layout.getPlid()),
				LiferayWindowState.POP_UP.toString(), null,
				themeDisplay.getRequest());
		}
		catch (Exception e) {
			if (_log.isErrorEnabled()) {
				_log.error(e);
			}
		}

		return StringPool.BLANK;
	}

	protected String getManageLayoutURL(
		HttpServletRequest httpServletRequest, Layout layout) {

		PortletURL portletURL = _portal.getControlPanelPortletURL(
			httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter("selPlid", String.valueOf(layout.getPlid()));

		return portletURL.toString();
	}

	protected boolean hasGuestPermissions(
		HttpServletRequest httpServletRequest, Layout layout) {

		boolean hasGuestPermissions = false;

		try {
			User defaultUser = _userLocalService.getDefaultUser(
				_portal.getCompanyId(httpServletRequest));

			PermissionChecker permissionChecker =
				PermissionCheckerFactoryUtil.create(defaultUser);

			hasGuestPermissions = _layoutPermission.contains(
				permissionChecker, layout, ActionKeys.VIEW);
		}
		catch (PortalException pe) {
			if (_log.isErrorEnabled()) {
				_log.error(pe);
			}
		}

		return hasGuestPermissions;
	}

	private static Log _log = LogFactoryUtil.getLog(
		PageProductNavigationControlMenuEntry.class);

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Layout)"
	)
	private LayoutPermission _layoutPermission;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}