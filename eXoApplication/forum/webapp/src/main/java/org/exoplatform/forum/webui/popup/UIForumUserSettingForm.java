/***************************************************************************
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 ***************************************************************************/
package org.exoplatform.forum.webui.popup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.download.DownloadService;
import org.exoplatform.forum.ForumSessionUtils;
import org.exoplatform.forum.ForumTransformHTML;
import org.exoplatform.forum.ForumUtils;
import org.exoplatform.forum.service.Category;
import org.exoplatform.forum.service.Forum;
import org.exoplatform.forum.service.ForumPageList;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.forum.service.ForumServiceUtils;
import org.exoplatform.forum.service.ForumSubscription;
import org.exoplatform.forum.service.JCRPageList;
import org.exoplatform.forum.service.Post;
import org.exoplatform.forum.service.Topic;
import org.exoplatform.forum.service.UserProfile;
import org.exoplatform.forum.service.Utils;
import org.exoplatform.forum.service.Watch;
import org.exoplatform.forum.webui.UIBreadcumbs;
import org.exoplatform.forum.webui.UICategory;
import org.exoplatform.forum.webui.UICategoryContainer;
import org.exoplatform.forum.webui.UIFormSelectBoxForum;
import org.exoplatform.forum.webui.UIForumContainer;
import org.exoplatform.forum.webui.UIForumDescription;
import org.exoplatform.forum.webui.UIForumLinks;
import org.exoplatform.forum.webui.UIForumPageIterator;
import org.exoplatform.forum.webui.UIForumPortlet;
import org.exoplatform.forum.webui.UITopicContainer;
import org.exoplatform.forum.webui.UITopicDetail;
import org.exoplatform.forum.webui.UITopicDetailContainer;
import org.exoplatform.forum.webui.UITopicPoll;
import org.exoplatform.forum.webui.UITopicsTag;
import org.exoplatform.ks.rss.RSS;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormCheckBoxInput;
import org.exoplatform.webui.form.UIFormInputWithActions;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;

/**
 * Created by The eXo Platform SARL
 * Author : Hung Nguyen
 *					hung.nguyen@exoplatform.com
 * Aus 01, 2007 2:48:18 PM 
 */
@ComponentConfig(
		lifecycle = UIFormLifecycle.class,
		template = "app:/templates/forum/webui/popup/UIForumUserSettingForm.gtmpl",
		events = {
			@EventConfig(listeners = UIForumUserSettingForm.AttachmentActionListener.class), 
			@EventConfig(listeners = UIForumUserSettingForm.SetDefaultAvatarActionListener.class), 
			@EventConfig(listeners = UIForumUserSettingForm.SaveActionListener.class), 
			@EventConfig(listeners = UIForumUserSettingForm.OpenTabActionListener.class), 
			@EventConfig(listeners = UIForumUserSettingForm.OpentContentActionListener.class), 
			@EventConfig(listeners = UIForumUserSettingForm.DeleteEmailWatchActionListener.class), 
			@EventConfig(listeners = UIForumUserSettingForm.ResetRSSActionListener.class), 
			@EventConfig(listeners = UIForumUserSettingForm.UpdateEmailActionListener.class), 
			@EventConfig(listeners = UIForumUserSettingForm.CancelActionListener.class, phase=Phase.DECODE)
		}
)
public class UIForumUserSettingForm extends UIForm implements UIPopupComponent {
	public static final String FIELD_USERPROFILE_FORM = "ForumUserProfile" ;
	public static final String FIELD_USEROPTION_FORM = "ForumUserOption" ;
	public static final String FIELD_USERWATCHMANGER_FORM = "ForumUserWatches" ;
	
	public static final String FIELD_TIMEZONE_SELECTBOX = "TimeZone" ;
	public static final String FIELD_SHORTDATEFORMAT_SELECTBOX = "ShortDateformat" ;
	public static final String FIELD_LONGDATEFORMAT_SELECTBOX = "LongDateformat" ;
	public static final String FIELD_TIMEFORMAT_SELECTBOX = "Timeformat" ;
	public static final String FIELD_MAXTOPICS_SELECTBOX = "MaximumThreads" ;
	public static final String FIELD_MAXPOSTS_SELECTBOX = "MaximumPosts" ;
	public static final String FIELD_FORUMJUMP_CHECKBOX = "ShowForumJump" ;
	public static final String FIELD_AUTOWATCHMYTOPICS_CHECKBOX = "AutoWatchMyTopics" ;
	public static final String FIELD_AUTOWATCHTOPICIPOST_CHECKBOX = "AutoWatchTopicIPost" ;
	public static final String FIELD_TIMEZONE = "timeZone" ;
	
	public static final String FIELD_USERID_INPUT = "ForumUserName" ;
	public static final String FIELD_SCREENNAME_INPUT = "ScreenName" ;
	public static final String FIELD_USERTITLE_INPUT = "ForumUserTitle" ;
	public static final String FIELD_SIGNATURE_TEXTAREA = "Signature" ;
	public static final String FIELD_ISDISPLAYSIGNATURE_CHECKBOX = "IsDisplaySignature" ;
	public static final String FIELD_ISDISPLAYAVATAR_CHECKBOX = "IsDisplayAvatar" ;
	public static final String RSS_LINK = "RSSLink";
	public static final String EMAIL_ADD = "EmailAddress";
	
	public final String WATCHES_ITERATOR = "WatchChesPageIterator";
	
	@SuppressWarnings("unused")
  private String tabId = "ForumUserProfile";
	private ForumService forumService ;
	private UserProfile userProfile = null ;
	private String[] permissionUser = null;
	private List<Watch> listWatches = new ArrayList<Watch>();
	private JCRPageList pageList ;
	UIForumPageIterator pageIterator ;
	private final String defaultAvatar = "/forum/skin/DefaultSkin/webui/background/Avatar1.gif";
	
	public UIForumUserSettingForm() throws Exception {
		forumService = (ForumService)PortalContainer.getInstance().getComponentInstanceOfType(ForumService.class) ;
		WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
		ResourceBundle res = context.getApplicationResourceBundle() ;
		permissionUser = new String[]{res.getString("UIForumPortlet.label.PermissionAdmin").toLowerCase(), 
																	res.getString("UIForumPortlet.label.PermissionModerator").toLowerCase(),
																	res.getString("UIForumPortlet.label.PermissionGuest").toLowerCase(),
																	res.getString("UIForumPortlet.label.PermissionUser").toLowerCase()};
		setActions(new String[]{"Save", "Cancel"});
	}
	
	public String getPortalName() {
		PortalContainer pcontainer =  PortalContainer.getInstance() ;
		return pcontainer.getPortalContainerInfo().getContainerName() ;  
	}
	
	@SuppressWarnings({ "unchecked" })
	private void initForumOption() throws Exception {
		try {
			this.userProfile = forumService.getUserSettingProfile(ForumSessionUtils.getCurrentUser()) ;
		} catch (Exception e) {			
			e.printStackTrace() ;
		}
		
		List<SelectItemOption<String>> list ;
		String []timeZone1 = getLabel(FIELD_TIMEZONE).split("/") ;
		list = new ArrayList<SelectItemOption<String>>() ;
		for(String string : timeZone1) {
			list.add(new SelectItemOption<String>(string + "/timeZone", ForumUtils.getTimeZoneNumberInString(string))) ;
		}
		UIFormSelectBoxForum timeZone = new UIFormSelectBoxForum(FIELD_TIMEZONE_SELECTBOX, FIELD_TIMEZONE_SELECTBOX, list) ;
		double timeZoneOld = -userProfile.getTimeZone() ;
		Date date = getNewDate(timeZoneOld) ;
		String mark = "-";
		if(timeZoneOld < 0) {
			timeZoneOld = -timeZoneOld ;
		} else if(timeZoneOld > 0){
			mark = "+" ;
		} else {
			timeZoneOld = 0.0 ;
			mark = "";
		}
		timeZone.setValue(mark + timeZoneOld + "0");
		list = new ArrayList<SelectItemOption<String>>() ;
		String []format = new String[] {"M-d-yyyy", "M-d-yy", "MM-dd-yy", "MM-dd-yyyy","yyyy-MM-dd", "yy-MM-dd", "dd-MM-yyyy", "dd-MM-yy",
				"M/d/yyyy", "M/d/yy", "MM/dd/yy", "MM/dd/yyyy","yyyy/MM/dd", "yy/MM/dd", "dd/MM/yyyy", "dd/MM/yy"} ;
		for (String frm : format) {
			list.add(new SelectItemOption<String>((frm.toLowerCase() +" ("	+ ForumUtils.getFormatDate(frm, date)+")"), frm)) ;
		}

		UIFormSelectBox shortdateFormat = new UIFormSelectBox(FIELD_SHORTDATEFORMAT_SELECTBOX, FIELD_SHORTDATEFORMAT_SELECTBOX, list) ;
		shortdateFormat.setValue(userProfile.getShortDateFormat());
		list = new ArrayList<SelectItemOption<String>>() ;
		format = new String[] {"DDD, MMMM dd, yyyy", "DDDD, MMMM dd, yyyy", "DDDD, dd MMMM, yyyy", "DDD, MMM dd, yyyy", "DDDD, MMM dd, yyyy", "DDDD, dd MMM, yyyy",
								"MMMM dd, yyyy", "dd MMMM, yyyy","MMM dd, yyyy", "dd MMM, yyyy"} ;
		for (String idFrm : format) {
			list.add(new SelectItemOption<String>((idFrm.toLowerCase() +" (" + ForumUtils.getFormatDate(idFrm, date)+")"), idFrm.replaceFirst(" ", "="))) ;
		}
	
		UIFormSelectBox longDateFormat = new UIFormSelectBox(FIELD_LONGDATEFORMAT_SELECTBOX, FIELD_LONGDATEFORMAT_SELECTBOX, list) ;
		longDateFormat.setValue(userProfile.getLongDateFormat().replaceFirst(" ", "="));
		list = new ArrayList<SelectItemOption<String>>() ;
		list.add(new SelectItemOption<String>("12-hour","hh:mm=a")) ;
		list.add(new SelectItemOption<String>("24-hour","HH:mm")) ;
		UIFormSelectBox timeFormat = new UIFormSelectBox(FIELD_TIMEFORMAT_SELECTBOX, FIELD_TIMEFORMAT_SELECTBOX, list) ;
		timeFormat.setValue(userProfile.getTimeFormat().replace(' ', '='));
		list = new ArrayList<SelectItemOption<String>>() ;
		for(int i=5; i <= 45; i = i + 5) {
			list.add(new SelectItemOption<String>(String.valueOf(i),("id" + i))) ;
		}
		UIFormSelectBox maximumThreads = new UIFormSelectBox(FIELD_MAXTOPICS_SELECTBOX, FIELD_MAXTOPICS_SELECTBOX, list) ;
		maximumThreads.setValue("id" + userProfile.getMaxTopicInPage());
		list = new ArrayList<SelectItemOption<String>>() ;
		for(int i=5; i <= 35; i = i + 5) {
			list.add(new SelectItemOption<String>(String.valueOf(i), ("id" + i))) ;
		}
	
		UIFormSelectBox maximumPosts = new UIFormSelectBox(FIELD_MAXPOSTS_SELECTBOX, FIELD_MAXPOSTS_SELECTBOX, list) ;
		maximumPosts.setValue("id" + userProfile.getMaxPostInPage());
		boolean isJump = userProfile.getIsShowForumJump() ;
		UIFormCheckBoxInput isShowForumJump = new UIFormCheckBoxInput<Boolean>(FIELD_FORUMJUMP_CHECKBOX, FIELD_FORUMJUMP_CHECKBOX, isJump);
		isShowForumJump.setChecked(isJump) ;

		UIFormStringInput userId = new UIFormStringInput(FIELD_USERID_INPUT, FIELD_USERID_INPUT, null);
		userId.setValue(this.userProfile.getUserId());
		userId.setEditable(false) ;
		userId.setEnable(false);
		UIFormStringInput screenName = new UIFormStringInput(FIELD_SCREENNAME_INPUT, FIELD_SCREENNAME_INPUT, null);
		String screenN = userProfile.getScreenName();
		if(ForumUtils.isEmpty(screenN)) screenN = userProfile.getUserId();
		screenName.setValue(screenN);
		UIFormStringInput userTitle = new UIFormStringInput(FIELD_USERTITLE_INPUT, FIELD_USERTITLE_INPUT, null);
		userTitle.setValue(this.userProfile.getUserTitle());
		if(this.userProfile.getUserRole() > 0) {
			userTitle.setEditable(false) ;
			userTitle.setEnable(false);
		}
		UIFormTextAreaInput signature = new UIFormTextAreaInput(FIELD_SIGNATURE_TEXTAREA, FIELD_SIGNATURE_TEXTAREA, null);
		String strSignature = this.userProfile.getSignature();
		if(ForumUtils.isEmpty(strSignature)) strSignature = "";
		signature.setValue(strSignature);
		UIFormCheckBoxInput isDisplaySignature = new UIFormCheckBoxInput<Boolean>(FIELD_ISDISPLAYSIGNATURE_CHECKBOX, FIELD_ISDISPLAYSIGNATURE_CHECKBOX, false);
		isDisplaySignature.setChecked(this.userProfile.getIsDisplaySignature()) ;

		UIFormCheckBoxInput isAutoWatchMyTopics = new UIFormCheckBoxInput<Boolean>(FIELD_AUTOWATCHMYTOPICS_CHECKBOX, FIELD_AUTOWATCHMYTOPICS_CHECKBOX, false);
		isAutoWatchMyTopics.setChecked(userProfile.getIsAutoWatchMyTopics()) ;
		UIFormCheckBoxInput isAutoWatchTopicIPost = new UIFormCheckBoxInput<Boolean>(FIELD_AUTOWATCHTOPICIPOST_CHECKBOX, FIELD_AUTOWATCHTOPICIPOST_CHECKBOX, false);
		isAutoWatchTopicIPost.setChecked(userProfile.getIsAutoWatchTopicIPost()) ;
		
		UIFormCheckBoxInput isDisplayAvatar = new UIFormCheckBoxInput<Boolean>(FIELD_ISDISPLAYAVATAR_CHECKBOX, FIELD_ISDISPLAYAVATAR_CHECKBOX, false);
		isDisplayAvatar.setChecked(this.userProfile.getIsDisplayAvatar()) ;
		
		UIFormInputWithActions inputSetProfile = new UIFormInputWithActions(FIELD_USERPROFILE_FORM);
		inputSetProfile.addUIFormInput(userId) ;
		inputSetProfile.addUIFormInput(screenName) ;
		inputSetProfile.addUIFormInput(userTitle) ;
		inputSetProfile.addUIFormInput(signature) ;
		inputSetProfile.addUIFormInput(isDisplaySignature) ;
		inputSetProfile.addUIFormInput(isDisplayAvatar) ;
		inputSetProfile.addUIFormInput(isAutoWatchMyTopics) ;
		inputSetProfile.addUIFormInput(isAutoWatchTopicIPost) ;
		
		UIFormInputWithActions inputSetOption = new UIFormInputWithActions(FIELD_USEROPTION_FORM); 
		inputSetOption.addUIFormInput(timeZone) ;
		inputSetOption.addUIFormInput(shortdateFormat) ;
		inputSetOption.addUIFormInput(longDateFormat) ;
		inputSetOption.addUIFormInput(timeFormat) ;
		inputSetOption.addUIFormInput(maximumThreads) ;
		inputSetOption.addUIFormInput(maximumPosts) ;
		inputSetOption.addUIFormInput(isShowForumJump) ;
		
		UIFormInputWithActions inputUserWatchManger = new UIFormInputWithActions(FIELD_USERWATCHMANGER_FORM);
		listWatches = forumService.getWatchByUser(this.userProfile.getUserId());
		
		UIFormCheckBoxInput formCheckBoxRSS = null;
		UIFormCheckBoxInput formCheckBoxEMAIL = null;
		String listObjectId = "", watchId;
		List<String> listId = new ArrayList<String>();
		ForumSubscription forumSubscription = forumService.getForumSubscription(userProfile.getUserId());
		listId.addAll(Arrays.asList(forumSubscription.getCategoryIds()));
		listId.addAll(Arrays.asList(forumSubscription.getForumIds()));
		listId.addAll(Arrays.asList(forumSubscription.getTopicIds()));
		boolean isAddWatchRSS;
		for(Watch watch : listWatches){
			if(listObjectId.trim().length() > 0) listObjectId += "/";
			watchId = watch.getId();
			listObjectId += watchId;
			formCheckBoxRSS = new UIFormCheckBoxInput<Boolean>("RSS" + watch.getId(), "RSS" + watch.getId(), false);
			isAddWatchRSS = watch.isAddWatchByRS();
			formCheckBoxRSS.setEnable(isAddWatchRSS);
			if(isAddWatchRSS){
				if(listId.contains(watchId))
					formCheckBoxRSS.setChecked(true);
				else 
					formCheckBoxRSS.setChecked(false);
			}
			inputUserWatchManger.addChild(formCheckBoxRSS);
			
			formCheckBoxEMAIL = new UIFormCheckBoxInput<Boolean>("EMAIL" + watch.getId(), "EMAIL" + watch.getId(), watch.isAddWatchByEmail());
			formCheckBoxEMAIL.setChecked(watch.isAddWatchByEmail());
			formCheckBoxEMAIL.setEnable(watch.isAddWatchByEmail());
			inputUserWatchManger.addChild(formCheckBoxEMAIL);
		}
		
		UIFormStringInput formStringInput = null;
		formStringInput = new UIFormStringInput(RSS_LINK, null);
		
		
		String rssLink = "";
		PortalRequestContext portalContext = Util.getPortalRequestContext();
		String url = portalContext.getRequest().getRequestURL().toString();
		url = url.substring(0, url.indexOf("/", 8)) ;
		rssLink = url + RSS.getUserRSSLink(userProfile.getUserId());
		formStringInput.setValue(rssLink);
		formStringInput.setEditable(false);
		
		inputUserWatchManger.addChild(formStringInput);
		
		
		formStringInput = new UIFormStringInput(EMAIL_ADD, ForumSessionUtils.getEmailUser(this.userProfile.getUserId()));
		formStringInput.setValue(ForumSessionUtils.getEmailUser(this.userProfile.getUserId()));
		inputUserWatchManger.addChild(formStringInput);
		
		addUIFormInput(inputSetProfile);
		addUIFormInput(inputSetOption);
		addUIFormInput(inputUserWatchManger);
		
		
		pageIterator = addChild(UIForumPageIterator.class, null, WATCHES_ITERATOR);
		pageList = new ForumPageList(7, listWatches.size());
		pageIterator.updatePageList(pageList);
		try {
			if(pageIterator.getInfoPage().get(3) <= 1) pageIterator.setRendered(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
  private void saveForumSubscription() throws Exception {
		List<String> cateIds = new ArrayList<String>();
		List<String> forumIds = new ArrayList<String>();
		List<String> topicIds = new ArrayList<String>();
		String watchId;
		UIFormCheckBoxInput formCheckBoxRSS = null;
		UIFormInputWithActions inputUserWatchManger = this.getChildById(FIELD_USERWATCHMANGER_FORM);
		for(Watch watch : listWatches){
			formCheckBoxRSS = inputUserWatchManger.getChildById("RSS" + watch.getId());
			watchId = watch.getId();
			if(formCheckBoxRSS.isChecked()){
				if(watchId.indexOf(Utils.CATEGORY)==0) {
					cateIds.add(watchId);
				}else if(watchId.indexOf(Utils.FORUM)==0) {
					forumIds.add(watchId);
				} else if(watchId.indexOf(Utils.TOPIC)==0) {
					topicIds.add(watchId);
				}
			}
		}
		ForumSubscription forumSubscription = new ForumSubscription();
		forumSubscription.setCategoryIds(cateIds.toArray(new String[]{}));
		forumSubscription.setForumIds(forumIds.toArray(new String[]{}));
		forumSubscription.setTopicIds(topicIds.toArray(new String[]{}));
		forumService.saveForumSubscription(forumSubscription, userProfile.getUserId());
	}
	
	@SuppressWarnings("unchecked")
  public List<Watch> getListWatch() throws Exception {
		long pageSelect = pageIterator.getPageSelected() ;
		List<Watch>list = new ArrayList<Watch>();
		try {
			list.addAll(this.pageList.getPageWatch(pageSelect, this.listWatches)) ;
			if(list.isEmpty()){
				while(list.isEmpty() && pageSelect > 1) {
					list.addAll(this.pageList.getPageWatch(--pageSelect, this.listWatches)) ;
					pageIterator.setSelectPage(pageSelect) ;
				}
			}
		} catch (Exception e) {
		}
		return list ;
	}
	
	private Date getNewDate(double timeZoneOld) {
		Calendar	calendar = GregorianCalendar.getInstance() ;
		calendar.setLenient(false) ;
		int gmtoffset = calendar.get(Calendar.DST_OFFSET) + calendar.get(Calendar.ZONE_OFFSET);
		calendar.setTimeInMillis(System.currentTimeMillis() - gmtoffset + (long)(timeZoneOld*3600000)) ; 
		return calendar.getTime() ;
	}
	
	@SuppressWarnings("unused")
  private String getAvatarUrl(){
		String url = defaultAvatar;
		try {
			DownloadService dservice = getApplicationComponent(DownloadService.class) ;
			url = ForumSessionUtils.getUserAvatarURL(ForumSessionUtils.getCurrentUser(), this.forumService, dservice);
		} catch (Exception e) {
			url = defaultAvatar;
		}
		if(url == null || url.trim().length() < 1) url = defaultAvatar;
		return url;
	}
	
	public UIFormSelectBoxForum getUIFormSelectBoxForum(String name) {
		return	findComponentById(name) ;
	}
	
	public void activate() throws Exception {
		initForumOption() ;
	}
	public void deActivate() throws Exception {
	}
	
	private boolean canView(Category category, Forum forum, Topic topic, Post post, UserProfile userProfile) throws Exception{
		if(userProfile.getUserRole() == 0) return true;
		boolean canView = true;
		boolean isModerator = false;
		if(category == null) return false;
		String[] listUsers = category.getUserPrivate();
		//check category is private:
		if(listUsers.length > 0 && listUsers[0].trim().length() > 0 && !ForumServiceUtils.hasPermission(listUsers, userProfile.getUserId())) 
			return false;
		else
			canView = true;
		
		// check forum
		if(forum != null){
			listUsers = forum.getModerators();
			if(userProfile.getUserRole() == 1 && (listUsers.length > 0 && listUsers[0].trim().length() > 0 && 
					ForumServiceUtils.hasPermission(listUsers, userProfile.getUserId()))) {
				isModerator = true;
				canView = true;
			} else if(forum.getIsClosed()) return false;
			else canView = true;
			
			// ckeck Topic:
			if(topic != null){
				if(isModerator) canView = true;
				else if(!topic.getIsClosed() && topic.getIsActive() && topic.getIsActiveByForum() && topic.getIsApproved() && 
								!topic.getIsWaiting() &&((topic.getCanView().length == 1 && topic.getCanView()[0].trim().length() < 1) ||
								ForumServiceUtils.hasPermission(topic.getCanView(), userProfile.getUserId()) ||
								ForumServiceUtils.hasPermission(forum.getViewer(), userProfile.getUserId()) ||
								ForumServiceUtils.hasPermission(forum.getPoster(), userProfile.getUserId()) )) canView = true;
				else canView = false;
			}
		}
		
		return canView;
	}
	
	static	public class SaveActionListener extends EventListener<UIForumUserSettingForm> {
		public void execute(Event<UIForumUserSettingForm> event) throws Exception {
			UIForumUserSettingForm uiForm = event.getSource() ;
			UIFormInputWithActions inputSetProfile = uiForm.getChildById(FIELD_USERPROFILE_FORM) ;
			String userTitle = inputSetProfile.getUIStringInput(FIELD_USERTITLE_INPUT).getValue() ;
			String screenName = inputSetProfile.getUIStringInput(FIELD_SCREENNAME_INPUT).getValue() ;
			UserProfile userProfile = uiForm.userProfile ;
			if(userTitle == null || userTitle.trim().length() < 1){
    		userTitle = userProfile.getUserTitle();
    	} else {
    		int newPos = Arrays.asList(uiForm.permissionUser).indexOf(userTitle.toLowerCase());
    		if(newPos >= 0 && newPos < userProfile.getUserRole()){
    			userTitle = userProfile.getUserTitle();
    		}
    	}
			int maxText = ForumUtils.MAXSIGNATURE ;
			String signature = inputSetProfile.getUIFormTextAreaInput(FIELD_SIGNATURE_TEXTAREA).getValue() ;
			if (ForumUtils.isEmpty(signature)) {
				signature = "" ;
			} else if (signature.trim().length() > maxText) {
				UIApplication uiApp = uiForm.getAncestorOfType(UIApplication.class) ;
				Object[] args = { uiForm.getLabel(FIELD_SIGNATURE_TEXTAREA), String.valueOf(maxText) };
				uiApp.addMessage(new ApplicationMessage("NameValidator.msg.warning-long-text", args, ApplicationMessage.WARNING)) ;
				event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages()) ;
				return ;
			}
			
			signature = ForumTransformHTML.enCodeHTML(signature);
			boolean isDisplaySignature = (Boolean)inputSetProfile.getUIFormCheckBoxInput(FIELD_ISDISPLAYSIGNATURE_CHECKBOX).getValue() ;
			Boolean isDisplayAvatar = (Boolean)inputSetProfile.getUIFormCheckBoxInput(FIELD_ISDISPLAYAVATAR_CHECKBOX).getValue() ;
			boolean isAutoWatchMyTopics = (Boolean)inputSetProfile.getUIFormCheckBoxInput(FIELD_AUTOWATCHMYTOPICS_CHECKBOX).getValue() ;
			boolean isAutoWatchTopicIPost = (Boolean)inputSetProfile.getUIFormCheckBoxInput(FIELD_AUTOWATCHTOPICIPOST_CHECKBOX).getValue() ;
			
			UIFormInputWithActions inputSetOption = uiForm.getChildById(FIELD_USEROPTION_FORM) ;
			long maxTopic = Long.parseLong(inputSetOption.getUIFormSelectBox(FIELD_MAXTOPICS_SELECTBOX).getValue().substring(2)) ;
			long maxPost = Long.parseLong(inputSetOption.getUIFormSelectBox(FIELD_MAXPOSTS_SELECTBOX).getValue().substring(2)) ;
			double timeZone = Double.parseDouble(uiForm.getUIFormSelectBoxForum(FIELD_TIMEZONE_SELECTBOX).getValue());
			String shortDateFormat = inputSetOption.getUIFormSelectBox(FIELD_SHORTDATEFORMAT_SELECTBOX).getValue();
			String longDateFormat = inputSetOption.getUIFormSelectBox(FIELD_LONGDATEFORMAT_SELECTBOX).getValue();
			String timeFormat = inputSetOption.getUIFormSelectBox(FIELD_TIMEFORMAT_SELECTBOX).getValue();
			boolean isJump = (Boolean)inputSetOption.getUIFormCheckBoxInput(FIELD_FORUMJUMP_CHECKBOX).getValue() ;
			UIForumPortlet forumPortlet = uiForm.getAncestorOfType(UIForumPortlet.class) ;
			userProfile.setUserTitle(userTitle);
			userProfile.setScreenName(screenName);
			userProfile.setSignature(signature);
			userProfile.setIsDisplaySignature(isDisplaySignature);
			userProfile.setIsDisplayAvatar(isDisplayAvatar);
			userProfile.setTimeZone(-timeZone) ;
			userProfile.setTimeFormat(timeFormat.replace('=', ' '));
			userProfile.setShortDateFormat(shortDateFormat);
			userProfile.setLongDateFormat(longDateFormat.replace('=', ' '));
			userProfile.setMaxPostInPage(maxPost);
			userProfile.setMaxTopicInPage(maxTopic);
			userProfile.setIsShowForumJump(isJump);
			userProfile.setIsAutoWatchMyTopics(isAutoWatchMyTopics);
			userProfile.setIsAutoWatchTopicIPost(isAutoWatchTopicIPost);
			
			uiForm.forumService.saveUserSettingProfile(userProfile);
			try {
	      uiForm.saveForumSubscription();
      } catch (Exception e) {
      	e.printStackTrace();
      }
			forumPortlet.updateUserProfileInfo() ;
			userProfile = forumPortlet.getUserProfile();
			forumPortlet.setRenderForumLink();
			UICategoryContainer categoryContainer = forumPortlet.findFirstComponentOfType(UICategoryContainer.class) ;
			if(categoryContainer.isRendered()) categoryContainer.renderJump();
			forumPortlet.findFirstComponentOfType(UITopicDetail.class).setUserProfile(userProfile) ;
			forumPortlet.findFirstComponentOfType(UITopicContainer.class).setUserProfile(userProfile) ;
			forumPortlet.findFirstComponentOfType(UITopicsTag.class).setUserProfile(userProfile) ;
			forumPortlet.cancelAction() ;
			event.getRequestContext().addUIComponentToUpdateByAjax(forumPortlet);
		}
	}
	
	static	public class CancelActionListener extends EventListener<UIForumUserSettingForm> {
		public void execute(Event<UIForumUserSettingForm> event) throws Exception {
			UIForumUserSettingForm uiForm = event.getSource() ;
			UIForumPortlet forumPortlet = uiForm.getAncestorOfType(UIForumPortlet.class) ;
			forumPortlet.cancelAction() ;
		}
	}
	
	static	public class OpenTabActionListener extends EventListener<UIForumUserSettingForm> {
		public void execute(Event<UIForumUserSettingForm> event) throws Exception {
			UIForumUserSettingForm uiForm = event.getSource() ;
			uiForm.tabId = event.getRequestContext().getRequestParameter(OBJECTID);
//			UIForumPortlet forumPortlet = uiForm.getAncestorOfType(UIForumPortlet.class) ;
			event.getRequestContext().addUIComponentToUpdateByAjax(uiForm) ;
		}
	}
	

	static public class AttachmentActionListener extends EventListener<UIForumUserSettingForm> {
		public void execute(Event<UIForumUserSettingForm> event) throws Exception {
			UIForumUserSettingForm uiForm = event.getSource() ;
			UIPopupContainer popupContainer = uiForm.getAncestorOfType(UIPopupContainer.class) ;
			UIPopupAction uiChildPopup = popupContainer.getChild(UIPopupAction.class).setRendered(true) ;
			UIAttachFileForm attachFileForm = uiChildPopup.activate(UIAttachFileForm.class, 500) ;
			attachFileForm.updateIsTopicForm(false) ;
			attachFileForm.setIsChangeAvatar(true);
			attachFileForm.setMaxField(1);
			event.getRequestContext().addUIComponentToUpdateByAjax(popupContainer) ;
		}
	}
	
	static public class SetDefaultAvatarActionListener extends EventListener<UIForumUserSettingForm> {
		public void execute(Event<UIForumUserSettingForm> event) throws Exception {
			UIForumUserSettingForm uiForm = event.getSource() ;
			uiForm.forumService.setDefaultAvatar(uiForm.userProfile.getUserId());
			event.getRequestContext().addUIComponentToUpdateByAjax(uiForm) ;
		}
	}
	
	static public class DeleteEmailWatchActionListener extends EventListener<UIForumUserSettingForm> {
		public void execute(Event<UIForumUserSettingForm> event) throws Exception {
			UIForumUserSettingForm uiForm = event.getSource() ;
			String input =  event.getRequestContext().getRequestParameter(OBJECTID) ;
			String userId = input.substring(0, input.indexOf("/"));
			String email = input.substring(input.lastIndexOf("/"));
			String path = (input.substring(0, input.lastIndexOf("/"))).replace(userId + "/", "");
			UIPopupContainer popupContainer = uiForm.getAncestorOfType(UIPopupContainer.class) ;
			String emails = userId + "/" + email;
			try {
				uiForm.forumService.removeWatch(1, path, emails) ;
				for(int i = 0; i < uiForm.listWatches.size(); i ++){
					if(uiForm.listWatches.get(i).getNodePath().equals(path) && uiForm.listWatches.get(i).getUserId().equals(userId)){
						uiForm.listWatches.remove(i);
						break;
					}
				}
				uiForm.pageList = new ForumPageList(7, uiForm.listWatches.size());
				uiForm.pageIterator.updatePageList(uiForm.pageList);
			} catch (Exception e) {
			}
			event.getRequestContext().addUIComponentToUpdateByAjax(popupContainer) ;
		}
	}
	
	static public class ResetRSSActionListener extends EventListener<UIForumUserSettingForm> {
		public void execute(Event<UIForumUserSettingForm> event) throws Exception {
			UIForumUserSettingForm uiForm = event.getSource() ;
			UIPopupContainer popupContainer = uiForm.getAncestorOfType(UIPopupContainer.class) ;
			UIFormInputWithActions inputUserWatchManger = uiForm.getChildById(FIELD_USERWATCHMANGER_FORM);
			UIFormCheckBoxInput<Boolean> formCheckBoxRSS = null;
			String listObjectId = "";
			for(int i = 0; i < uiForm.listWatches.size(); i ++){
				formCheckBoxRSS = inputUserWatchManger.getChildById("RSS" + uiForm.listWatches.get(i).getId());
				if(formCheckBoxRSS.isChecked()) {
					if(listObjectId.trim().length() > 0) listObjectId += "/";
					listObjectId += uiForm.listWatches.get(i).getId();
				}
			}
			if(listObjectId.trim().length() > 0){
				String rssLink = "";
				PortalRequestContext portalContext = Util.getPortalRequestContext();
				String url = portalContext.getRequest().getRequestURL().toString();
				url = url.replaceFirst("http://", "") ;
				url = url.substring(0, url.indexOf("/")) ;
				url = "http://" + url;
				rssLink = url + RSS.getRSSLink("forum", uiForm.getPortalName(), listObjectId);
				((UIFormStringInput)inputUserWatchManger.getChildById(RSS_LINK)).setValue(rssLink);
			}
			event.getRequestContext().addUIComponentToUpdateByAjax(uiForm) ;
		}
	}
	
	static public class UpdateEmailActionListener extends EventListener<UIForumUserSettingForm> {
		public void execute(Event<UIForumUserSettingForm> event) throws Exception {
			UIForumUserSettingForm uiForm = event.getSource() ;
			UIPopupContainer popupContainer = uiForm.getAncestorOfType(UIPopupContainer.class) ;
			UIFormInputWithActions inputUserWatchManger = uiForm.getChildById(FIELD_USERWATCHMANGER_FORM);
			String newEmailAdd = ((UIFormStringInput)inputUserWatchManger.getChildById(EMAIL_ADD)).getValue();
			if(newEmailAdd == null || newEmailAdd.trim().length() < 1 || !ForumUtils.isValidEmailAddresses(newEmailAdd)){
				Object[] args = { };
				UIApplication uiApp = uiForm.getAncestorOfType(UIApplication.class) ;
				uiApp.addMessage(new ApplicationMessage("UIForumUserSettingForm.msg.Email-inValid", args, ApplicationMessage.WARNING)) ;
				return;
			}
			UIFormCheckBoxInput<Boolean> formCheckBoxEMAIL = null;
			List<String> listObjectId = new ArrayList<String>();
			for(Watch watch : uiForm.listWatches){
				formCheckBoxEMAIL = inputUserWatchManger.getChildById("EMAIL" + watch.getId());
				if(formCheckBoxEMAIL.isChecked()) {
					listObjectId.add(watch.getId());
					watch.setEmail(newEmailAdd);
				}
			}
			if(listObjectId.size() > 0){
				uiForm.forumService.updateEmailWatch(listObjectId, newEmailAdd, uiForm.userProfile.getUserId());
			}
			event.getRequestContext().addUIComponentToUpdateByAjax(popupContainer) ;
		}
	}
	
	static	public class OpentContentActionListener extends EventListener<UIForumUserSettingForm> {
		public void execute(Event<UIForumUserSettingForm> event) throws Exception {
			UIForumUserSettingForm uiForm = event.getSource() ;
			String path =  event.getRequestContext().getRequestParameter(OBJECTID) ;
			boolean isErro = false ;
			UIForumPortlet forumPortlet = uiForm.getAncestorOfType(UIForumPortlet.class) ;
			UserProfile userProfile = forumPortlet.getUserProfile();
			boolean isRead = true;
			ForumService forumService = (ForumService)PortalContainer.getInstance().getComponentInstanceOfType(ForumService.class) ;
			
			String []id = path.split("/") ;
			String cateId="", forumId="", topicId="";
			for (int i = 0; i < id.length; i++) {
	      if(id[i].indexOf(Utils.CATEGORY) >= 0) cateId = id[i];
	      else if(id[i].indexOf(Utils.FORUM) >= 0) forumId = id[i];
	      else if(id[i].indexOf(Utils.TOPIC) >= 0) topicId = id[i];
      }
			Category category = null;
			Forum forum = null;
			Topic topic = null;
			try{
				category = forumService.getCategory(cateId) ;
				forum = forumService.getForum(cateId , forumId ) ;
				topic = forumService.getTopic(cateId, forumId, topicId, userProfile.getUserId());
			} catch (Exception e) { 
			}
			isRead = uiForm.canView(category, forum, topic, null, userProfile);
			
			if(ForumUtils.isEmpty(forumId)) {
				if(category != null) {
					if(isRead){
						UICategoryContainer categoryContainer = forumPortlet.getChild(UICategoryContainer.class) ;
						categoryContainer.getChild(UICategory.class).update(category, null);
						categoryContainer.updateIsRender(false) ;
						forumPortlet.getChild(UIBreadcumbs.class).setUpdataPath(cateId);
						forumPortlet.updateIsRendered(ForumUtils.CATEGORIES);
						event.getRequestContext().addUIComponentToUpdateByAjax(forumPortlet) ;
					}
				} else isErro = true ;
			} else if(ForumUtils.isEmpty(topicId)) {
				if(forum != null) {
					if(isRead) {
						forumPortlet.updateIsRendered(ForumUtils.FORUM);
						UIForumContainer uiForumContainer = forumPortlet.getChild(UIForumContainer.class) ;
						uiForumContainer.setIsRenderChild(true) ;
						uiForumContainer.getChild(UIForumDescription.class).setForum(forum);
						UITopicContainer uiTopicContainer = uiForumContainer.getChild(UITopicContainer.class) ;
						uiTopicContainer.setUpdateForum(cateId, forum, 0) ;
						forumPortlet.getChild(UIForumLinks.class).setValueOption((cateId+"/"+forumId));
						event.getRequestContext().addUIComponentToUpdateByAjax(forumPortlet) ;
					}
				} else isErro = true ;
			} else {
				if(topic != null) {
					if(isRead){
						forumPortlet.updateIsRendered(ForumUtils.FORUM);
						UIForumContainer uiForumContainer = forumPortlet.getChild(UIForumContainer.class) ;
						UITopicDetailContainer uiTopicDetailContainer = uiForumContainer.getChild(UITopicDetailContainer.class) ;
						uiForumContainer.setIsRenderChild(false) ;
						uiForumContainer.getChild(UIForumDescription.class).setForum(forum);
						UITopicDetail uiTopicDetail = uiTopicDetailContainer.getChild(UITopicDetail.class) ;
						uiTopicDetail.setUpdateForum(forum) ;
						uiTopicDetail.setTopicFromCate(cateId, forumId, topic, 0) ;
						uiTopicDetail.setIdPostView("top") ;
						uiTopicDetailContainer.getChild(UITopicPoll.class).updateFormPoll(cateId, forumId , topic.getId()) ;
						forumService.updateTopicAccess(forumPortlet.getUserProfile().getUserId(),  topic.getId()) ;
						forumPortlet.getUserProfile().setLastTimeAccessTopic(topic.getId(), ForumUtils.getInstanceTempCalendar().getTimeInMillis()) ;
						forumPortlet.getChild(UIForumLinks.class).setValueOption((cateId + "/" + forumId + " "));
						event.getRequestContext().addUIComponentToUpdateByAjax(forumPortlet) ;
					}
				} else isErro = true ;
			}
			if(isErro) {
				Object[] args = { };
				UIApplication uiApp = uiForm.getAncestorOfType(UIApplication.class) ;
				uiApp.addMessage(new ApplicationMessage("UIShowBookMarkForm.msg.link-not-found", args, ApplicationMessage.WARNING)) ;
				return;
			}
			if(!isRead) {
				UIApplication uiApp = uiForm.getAncestorOfType(UIApplication.class) ;
				String[] s = new String[]{};
				uiApp.addMessage(new ApplicationMessage("UIForumPortlet.msg.do-not-permission", s, ApplicationMessage.WARNING)) ;
				return;
			}
			forumPortlet.cancelAction() ;
		}
	}
	
}
