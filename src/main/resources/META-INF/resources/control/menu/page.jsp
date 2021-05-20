<%--
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
--%>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.osb.www.extensions.control.menu.page.PageProductNavigationControlMenuEntry" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %>

<%
boolean hasGuestPermissions = (boolean)GetterUtil.getBoolean(request.getAttribute(PageProductNavigationControlMenuEntry.LAYOUT_GUEST_PERMISSIONS));
String manageLayoutURL = (String)request.getAttribute(PageProductNavigationControlMenuEntry.MANAGE_LAYOUT_URL);
String manageLayoutPermissionsURL = (String)request.getAttribute(PageProductNavigationControlMenuEntry.MANAGE_LAYOUT_PERMISSIONS_URL);
%>

<li class="control-menu-nav-item">
	<liferay-ui:icon
		cssClass="control-menu-icon"
		icon="control-panel"
		markupView="lexicon"
		message="manage-page"
		method="get"
		url="<%= manageLayoutURL %>"
		useDialog="<%= false %>"
	/>
</li>
<li class="control-menu-nav-item">
	<liferay-ui:icon
		cssClass="control-menu-icon"
		icon='<%= hasGuestPermissions ? "live" : "lock" %>'
		iconCssClass='<%= hasGuestPermissions ? "color-accent-6" : "color-accent-3" %>'
		markupView="lexicon"
		message="permissions"
		method="get"
		url="<%= manageLayoutPermissionsURL %>"
		useDialog="<%= true %>"
	/>
</li>