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

import java.util.List;

import org.exoplatform.forum.service.DataStorage;
import org.exoplatform.forum.service.Topic;

/**
 * Created by The eXo Platform SAS
 * Author : thanh_vucong
 *          thanh_vucong@exoplatform.com
 * Sep 13, 2012  
 */
public class TopicListAccess extends AbstractListAccess<Topic> {

  private TopicFilter  filter = null;
  private Type  type = Type.ALL;
  
  public enum Type {
    ALL,
    TOPIC_LIST,
    TOPIC_PAGE,
    TOPIC_TAG
  }
  
  public TopicListAccess(Type type, DataStorage  storage, TopicFilter filter) {
    this.storage = storage;
    this.filter = filter;
    this.type = type;
  }
  
  @Override
  public Topic[] load(int offset, int limit) throws Exception, IllegalArgumentException {
    List<Topic> got = null; 
    switch (type) {
      case TOPIC_LIST :
        got = storage.getTopics(filter, offset, limit);
        break;
      case TOPIC_PAGE:
        got = storage.getPageTopic(filter, offset, limit);
        break;
      case TOPIC_TAG:
        got = storage.getTopicByTag(filter, offset, limit);
        break;
    }
    
    //
    reCalculate(offset, limit);
    
    return got.toArray(new Topic[got.size()]);
  
  }
  
  @Override
  public int getSize() throws Exception {
    if (size < 0) {
      switch (type) {
        case TOPIC_LIST :
          size = storage.getTopicsCount(filter);
          break;
        case TOPIC_PAGE:
          size = storage.getPageTopicCount(filter);
          break;
        case TOPIC_TAG:
          size = storage.getTopicByTagCount(filter);
          break;
      }
    }
    return size;
  }

  public TopicFilter getFilter() {
    return filter;
  }
  
  @Override
  public Topic[] load(int pageSelect) throws Exception, IllegalArgumentException {
    int offset = getOffset(pageSelect);
    int limit = getPageSize();
    return load(offset, limit);
  }
  
}