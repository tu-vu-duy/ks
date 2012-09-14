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

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.forum.service.DataStorage;
import org.exoplatform.forum.service.Post;

/**
 * Created by The eXo Platform SAS
 * Author : thanh_vucong
 *          thanh_vucong@exoplatform.com
 * Sep 13, 2012  
 */
public class PostListAccess implements ListAccess<Post> {

  private DataStorage  storage = null;
  
  private PostFilter  filter = null;
  
  private int currentPage = 1;
  
  private int totalPage = 1;

  private int pageSize = 0;
  
  private int size = -1;
  
  public PostListAccess(DataStorage  storage, PostFilter filter) {
    this.storage = storage;
    this.filter = filter;
  }
  
  @Override
  public Post[] load(int offset, int limit) throws Exception, IllegalArgumentException {
    List<Post> got = storage.getPosts(filter, offset, limit);
    
    if (offset >= 0) {
      currentPage = (offset + 1)/limit;
      if ((offset + 1) % limit > 0)
        currentPage++;
    }
    
    return got.toArray(new Post[got.size()]);
  
  }

  @Override
  public int getSize() throws Exception {
    if (size < 0) {
      size = storage.getPostsCount(filter);
    }
    return size;
  }

  public PostFilter getFilter() {
    return filter;
  }
  
  public int getTotalPages() throws Exception {
    this.totalPage = getSize() / pageSize;
    if (getSize() % pageSize > 0)
      this.totalPage++;
    return this.totalPage;
  }
  
  public int getPageSize() {
    return this.pageSize;
  }
  
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }
  
  public int getCurrentPage() throws Exception {
    return currentPage;
  }
  
  public void setCurrentPage(int page) throws Exception {
    if (page > totalPage) {
      currentPage = totalPage;
    } else if (page <= 0) {
      currentPage = 1;
    } else {
      currentPage = page;
    }
  }

}
