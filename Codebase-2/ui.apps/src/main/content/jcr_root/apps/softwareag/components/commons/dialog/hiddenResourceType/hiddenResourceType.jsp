<%--
  ADOBE CONFIDENTIAL
  ___________________

  Copyright 2015 Adobe
  All Rights Reserved.

  NOTICE: All information contained herein is, and remains
  the property of Adobe and its suppliers, if any. The intellectual
  and technical concepts contained herein are proprietary to Adobe
  and its suppliers and are protected by all applicable intellectual
  property laws, including trade secret and copyright laws.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe.
--%>
<%@ include file="/libs/granite/ui/global.jsp" %>
<%@ page session="false"
           import="com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Tag" %>
<%@ page session="false"
         import="com.adobe.granite.xss.XSSAPI,
                 com.softwareag.core.util.DialogUtil,
                 java.util.List" %>
<%--###
Hidden
======

.. granite:servercomponent:: /libs/granite/ui/components/coral/foundation/form/hidden
   :supertype: /libs/granite/ui/components/coral/foundation/form/field

   A hidden field component.

   It extends :granite:servercomponent:`Field </libs/granite/ui/components/coral/foundation/form/field>` component.

   It has the following content structure:

   .. gnd:gnd::

      [granite:FormHidden] > granite:commonAttrs, granite:renderCondition

      /**
       * The name that identifies the field when submitting the form.
       */
      - name (String)

      /**
       * Indicates if the field is in disabled state.
       */
      - disabled (Boolean)

      /**
       * The value of the field.
       */
      - value (StringEL)
###--%>
<%

    if (!cmp.getRenderCondition(resource, false).check()) {
        return;
    }

    Config cfg = cmp.getConfig();
    String name = cfg.get("name", String.class);
    String defaultValue = cmp.getExpressionHelper().getString(cfg.get("value", ""));

    // A special logic to calculate the value.
    // There may be the case where hidden field is used with `name` that is not part of the form values but `ignoreData` is not set.
    // In that case, to maintain compatibility, we still use the value from `value`, which otherwise will be `null`.
    // TBD how many such cases in reality.
    // If we never have such case, we can just follow the standard procedure:
    // String value = cmp.getValue().val(cmp.getExpressionHelper().getString(cfg.get("value", "")));
    String value;
    if (cfg.get("ignoreData", false)) {
        value = defaultValue;
    } else {
        value = cmp.getValue().getContentValue(name, defaultValue);
    }

    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    cmp.populateCommonAttrs(attrs);

    attrs.add("type", "hidden");
    attrs.add("name", name);
    attrs.add("value", value);
    attrs.addDisabled(cfg.get("disabled", false));

    final List<String> hiddenFields = DialogUtil.getHiddenFields(request);
%>
<input <%= attrs.build() %>>

<% for (final String fieldAttrs : hiddenFields) { %>
<input <%= fieldAttrs %>>
<% } %>
