/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.forum.service.impl.model;

/**
 * Created by The eXo Platform SAS Author : thanh_vucong
 * thanh_vucong@exoplatform.com Sep 13, 2012
 */
public class TopicFilter {

  private String categoryId;

  private String forumId;

  private String xpathConditions;

  private String strOrderBy;
  
  private String userIdAndtagId;
  
  public TopicFilter(String categoryId, String forumId, String xpathConditions, String strOrderBy) {
    this.categoryId = categoryId;
    this.forumId = forumId;
    this.xpathConditions = xpathConditions;
    this.strOrderBy = strOrderBy;
  }
  
  public TopicFilter(String userIdAndtagId, String strOrderBy) {
    this.userIdAndtagId = userIdAndtagId;
    this.strOrderBy = strOrderBy;
  }

  public String getUserIdAndtagId() {
    return userIdAndtagId;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public String getForumId() {
    return forumId;
  }

  public String getXpathConditions() {
    return xpathConditions;
  }

  public String getStrOrderBy() {
    return strOrderBy;
  }

}
