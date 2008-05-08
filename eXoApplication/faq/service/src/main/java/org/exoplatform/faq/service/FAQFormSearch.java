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
package org.exoplatform.faq.service;

import java.util.Date;

/**
 * Created by The eXo Platform SARL
 * Author : Truong Nguyen
 *					truong.nguyen@exoplatform.com
 * May 7, 2008, 4:44:24 PM
 */
public class FAQFormSearch {
	private String id;
	private String type ;
	private String name ;
	private String icon ;
	private Date createdDate ;
	public FAQFormSearch() {}
	
	public String getId() {
  	return id;
  }
	public void setId(String id) {
  	this.id = id;
  }
	public String getType() {
  	return type;
  }
	public void setType(String type) {
  	this.type = type;
  }
	public String getName() {
  	return name;
  }
	public void setName(String name) {
  	this.name = name;
  }
	public Date getCreatedDate() { return createdDate ; }
	
  public void setCreatedDate(Date date) { this.createdDate = date ; }
  
	public String getIcon() {
  	return icon;
  }

	public void setIcon(String icon) {
  	this.icon = icon;
  }
}

