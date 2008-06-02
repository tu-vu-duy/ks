/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.forum.webui.popup;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.PathNotFoundException;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.forum.ForumSessionUtils;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.forum.service.Post;
import org.exoplatform.forum.service.Topic;
import org.exoplatform.forum.service.UserProfile;
import org.exoplatform.forum.service.Utils;
import org.exoplatform.forum.webui.UIForumPortlet;
import org.exoplatform.forum.webui.UITopicDetail;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.exception.MessageException;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormStringInput;

/**
 * Created by The eXo Platform SAS
 * Author : Vu Duy Tu
 *          tu.duy@exoplatform.com
 * 11-03-2008, 09:13:50
 */

@ComponentConfig(
		lifecycle = UIFormLifecycle.class,
		template = "app:/templates/forum/webui/popup/UISplitTopicForm.gtmpl",
		events = {
			@EventConfig(listeners = UISplitTopicForm.SaveActionListener.class), 
			@EventConfig(listeners = UISplitTopicForm.CancelActionListener.class,phase = Phase.DECODE)
		}
)
public class UISplitTopicForm extends UIForm implements UIPopupComponent {
	private List<Post> posts = new ArrayList<Post>() ;
	private Topic topic = new Topic() ;
	private UserProfile userProfile = null;
	public static final String FIELD_SPLITTHREAD_INPUT = "SplitThread" ;
	public UISplitTopicForm() {
		addUIFormInput(new UIFormStringInput(FIELD_SPLITTHREAD_INPUT,FIELD_SPLITTHREAD_INPUT, null));
  }
	public void activate() throws Exception {}
	public void deActivate() throws Exception {}
	
	private Post getPostById(String postId) throws Exception {
		for (Post post : this.posts) {
	    if(post.getId().equals(postId)) return post ;
    }
		return new Post() ;
	}
	@SuppressWarnings({ "unused", "unchecked" })
  private List<Post> getListPost() throws Exception {
		String postId = this.topic.getId().replaceFirst(Utils.TOPIC, Utils.POST) ;
		this.posts.remove(this.getPostById(postId));
		for (Post post : this.posts) {
			if(getUIFormCheckBoxInput(post.getId()) != null) {
				getUIFormCheckBoxInput(post.getId()).setChecked(false) ;
			}else {
				addUIFormInput(new UIFormCheckBoxInput(post.getId(), post.getId(), false) );
			}
    }
		return this.posts ; 
	}
	public void setListPost(List<Post> posts) {this.posts = posts ;}
	@SuppressWarnings("unused")
  private Topic getTopic() {return this.topic ;}
	public void setTopic(Topic topic) { this.topic = topic; }
	@SuppressWarnings("unused")
  private UserProfile getUserProfile() {return this.userProfile ;}
	public void setUserProfile(UserProfile userProfile) { this.userProfile = userProfile; }
	
	static	public class SaveActionListener extends EventListener<UISplitTopicForm> {
    @SuppressWarnings("unchecked")
    public void execute(Event<UISplitTopicForm> event) throws Exception {
    	UISplitTopicForm uiForm = event.getSource() ;
      String newTopicTitle = uiForm.getUIStringInput(FIELD_SPLITTHREAD_INPUT).getValue() ;
      if(newTopicTitle != null && newTopicTitle.length() > 0) {
	    	List<UIComponent> children = uiForm.getChildren() ;
				List<Post> posts = new ArrayList<Post>() ;
				for(UIComponent child : children) {
					if(child instanceof UIFormCheckBoxInput) {
						if(((UIFormCheckBoxInput)child).isChecked()) {
							posts.add(uiForm.getPostById(((UIFormCheckBoxInput)child).getName()));
						}
					}
				}
				if(posts.size() > 0) {
					Topic topic = new Topic() ;
					Post post = posts.get(0) ;
					String owner = ForumSessionUtils.getCurrentUser() ;
					String topicId = post.getId().replaceFirst(Utils.POST, Utils.TOPIC);
					topic.setId(topicId) ;
					topic.setTopicName(newTopicTitle) ;
					topic.setOwner(owner) ;
					topic.setModifiedBy(owner) ;
					topic.setDescription(post.getMessage());
					topic.setIcon(post.getIcon());
					topic.setAttachments(post.getAttachments());
					topic.setLastPostBy(posts.get(posts.size() - 1).getOwner()) ;
					String path = uiForm.topic.getPath() ;
					String []string = path.split("/") ;
					String categoryId = string[string.length - 3] ;
					String forumId = string[string.length - 2] ;
					ForumService forumService = (ForumService)PortalContainer.getInstance().getComponentInstanceOfType(ForumService.class) ;
					try {
            forumService.saveTopic(ForumSessionUtils.getSystemProvider(), categoryId, forumId, topic, true, true) ;
          } catch (PathNotFoundException e) {
            
            // hung.hoang add
            UIApplication uiApp = uiForm.getAncestorOfType(UIApplication.class) ;
            uiApp.addMessage(new ApplicationMessage("UISplitTopicForm.msg.forum-deleted", null, ApplicationMessage.WARNING)) ;
            event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
            return ;            
          }          
					String destTopicPath = path.substring(0, path.lastIndexOf("/"))+ "/" + topicId ;
					forumService.movePost(ForumSessionUtils.getSystemProvider(), posts, destTopicPath);
				}else {
					Object[] args = { };
					throw new MessageException(new ApplicationMessage("UITopicDetail.msg.notCheck", args, ApplicationMessage.WARNING)) ;
				}
      } else {
      	Object[] args = {FIELD_SPLITTHREAD_INPUT };
				throw new MessageException(new ApplicationMessage("NameValidator.msg.ShortText", args, ApplicationMessage.WARNING)) ;
      }
			UIForumPortlet forumPortlet = uiForm.getAncestorOfType(UIForumPortlet.class) ;
			forumPortlet.cancelAction() ;
			UITopicDetail topicDetail = forumPortlet.findFirstComponentOfType(UITopicDetail.class) ;
			event.getRequestContext().addUIComponentToUpdateByAjax(topicDetail) ;
		}
	}

	static	public class CancelActionListener extends EventListener<UISplitTopicForm> {
		public void execute(Event<UISplitTopicForm> event) throws Exception {
			UISplitTopicForm uiForm = event.getSource() ;
			UIForumPortlet forumPortlet = uiForm.getAncestorOfType(UIForumPortlet.class) ;
			forumPortlet.cancelAction() ;
		}
	}
}
