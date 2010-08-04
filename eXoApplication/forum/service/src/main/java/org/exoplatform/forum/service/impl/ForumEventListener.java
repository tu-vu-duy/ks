/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.forum.service.impl;

import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.forum.service.Category;
import org.exoplatform.forum.service.Forum;
import org.exoplatform.forum.service.ForumEventLifeCycle;
import org.exoplatform.forum.service.Post;
import org.exoplatform.forum.service.Topic;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com Jul
 * 15, 2010
 */
public abstract class ForumEventListener extends BaseComponentPlugin implements ForumEventLifeCycle {

  public abstract void saveCategory(Category category);

  public abstract void saveForum(Forum forum);

  public abstract void savePost(Post post, String forumId);

  public abstract void saveTopic(Topic topic, String forumId);
}