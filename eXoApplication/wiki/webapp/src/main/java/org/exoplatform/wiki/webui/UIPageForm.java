/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.wiki.webui;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormTextAreaInput;
import org.exoplatform.wiki.commons.Utils;
import org.exoplatform.wiki.mow.api.Page;
import org.exoplatform.wiki.rendering.RenderingService;
import org.exoplatform.wiki.resolver.PageResolver;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com Nov
 * 5, 2009
 */

@ComponentConfig(lifecycle = UIFormLifecycle.class,
                 template = "app:/templates/wiki/webui/UIPageForm.gtmpl",
                 events = {
                   @EventConfig(listeners = UIPageForm.RenderActionListener.class)
                   }
                 )
public class UIPageForm extends UIForm {

  public UIPageForm() {
    UIFormTextAreaInput markupInput = new UIFormTextAreaInput("Markup",
                                                              "Markup",
                                                              "This is **bold**");
    addUIFormInput(markupInput).setRendered(true);
    setActions(new String[] { "Render" });
  }

  static public class RenderActionListener extends EventListener<UIPageForm> {

    public void execute(Event<UIPageForm> event) throws Exception {
      UIPageForm thisForm = event.getSource();

      UIFormTextAreaInput markupInput = thisForm.findComponentById("Markup");
      String markup = markupInput.getValue();

      String requestURL = Utils.getCurrentRequestURL();
      PageResolver pageResolver = (PageResolver) PortalContainer.getComponent(PageResolver.class);
      Page page = pageResolver.resolve(requestURL);
      page.getContent().setText(markup);
      
      String output = thisForm.renderWikiMarkup(markup, null);

      UIWikiPageContentArea pageContent = ((UIWikiPageArea)thisForm.getParent()).getChild(UIWikiPageContentArea.class);
      pageContent.setHtmlOutput(output);
      event.getRequestContext().addUIComponentToUpdateByAjax(pageContent);

    }

  }

  /*
   * public String getWikiPage() { String link = null; PortalRequestContext
   * portalRequestContext = Util.getPortalRequestContext();
   * PortletRequestContext portletRequestContext =
   * WebuiRequestContext.getCurrentInstance(); String portalURI =
   * portalRequestContext.getPortalURI(); portalRequestContext.
   * PortletPreferences portletPreferences = getPortletPreferences(); String
   * repository = portletPreferences.getValue(UICLVPortlet.REPOSITORY, null);
   * String workspace = portletPreferences.getValue(UICLVPortlet.WORKSPACE,
   * null); String baseURI = portletRequestContext.getRequest().getScheme() +
   * "://" + portletRequestContext.getRequest().getServerName() + ":" +
   * String.format("%s", portletRequestContext.getRequest().getServerPort());
   * String basePath = portletPreferences.getValue(UICLVPortlet.BASE_PATH,
   * null); link = baseURI + portalURI + basePath + "/" + repository + "/" +
   * workspace + node.getPath(); return link; }
   */

  public String renderWikiMarkup(String markup, String syntaxId) throws Exception {
    RenderingService renderingService = (RenderingService) PortalContainer.getComponent(RenderingService.class);
    String output = renderingService.render(markup, null);
    return output;
  }

}