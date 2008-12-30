/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
 **/
package org.exoplatform.faq.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.exoplatform.services.jcr.util.IdGenerator;
import org.hibernate.usertype.UserVersionType;

// TODO: Auto-generated Javadoc
/**
 * Data of question node is stored in question object which is used in processings:
 * add new question, edit question and reponse question.
 * 
 * @author : Hung Nguyen Quang
 */

public class Question {
  
  /** The id. */
  private String id ;
  
  /** The language. */
  private String language ;
  
  private String question;
  
  /** The question. */
  private String detail ;
  
  /** The author. */
  private String author ;
  
  /** The email. */
  private String email ;
  
  /** The is activated. */
  private boolean isActivated = true ;
  
  /** The is approved. */
  private boolean isApproved = true ;
  
  /** The created date. */
  private Date createdDate ;
  
  /** The category id. */
  private String categoryId ;
  
  /** The responses. */
  private String[] responses = null;
  
  /** The relations. */
  private String[] relations ;
  
  /** The response by. */
  private String[] responseBy = null ;
  
  /** The date response. */
  private Date[] dateResponse ;
  
  private Boolean[] activateAnswers;
  
  private Boolean[] approvedAnswers;
  
  /** link to question. */
  private String link = "";
  
  /** The list attachments. */
  private List<FileAttachment> listAttachments = new ArrayList<FileAttachment>() ;
  
  /** language of question which is not yet answer. */
  private String languagesNotYetAnswered = "";
  
  /** The name attachs. */
  private String[] nameAttachs ;
  
  /** The comments. */
  private String[] comments;
  
  /** The comment by. */
  private String[] commentBy;
  
  /** The date comment. */
  private Date[] dateComment;
  
  /** The users vote. */
  private String[] usersVote;
  
  /** The mark vote. */
  private double markVote = 0;
  
  /** The users watch. */
  private String[] usersWatch = null;
  
  /** The emails watch. */
  private String[] emailsWatch = null;
  
  /** The users vote answer. */
  private String[] usersVoteAnswer;
  
  /** The marks vote answer. */
  private double[] marksVoteAnswer;
  
  /** The pos. */
  private int pos[];
  
  /**
   * Class constructor specifying id of object is created.
   */
  public Question() {
    id = "Question" + IdGenerator.generate() ;
  }
  
  /**
   * Get id of Question object.
   * 
   * @return  question's id
   */
  public String getId() { return id ; }
  
  /**
   * Set an id for Question object.
   * 
   * @param id  the id of question object
   */
  public void setId(String id) { this.id = id ; }
  
  /**
   * Get content of Question which is wanted answer.
   * 
   * @return  the content of question
   */
  public String getDetail() { return detail ; }
  
  /**
   * Set content for Question object.
   * 
   * @param name  the content of question which is wanted answer
   */
  public void setDetail(String name) { this.detail = name ; }

  /**
   * Set language for question, a language may be have multi languages but
   * all of languages is used must be supported in portal. And the language
   * which is useing in portal will be auto setted for this quetsion.
   * 
   * @param language the language is default language is used in system
   */
  public void setLanguage(String language) { this.language = language; }
  
  /**
   * Get question's language, this is default language of system
   * when created question.
   * 
   * @return language the language is default of system and question
   */
  public String getLanguage() { return language; }
  
  /**
   * Set name for property author of this question, author is person who
   * write question and wait an answer.
   * 
   * @param author ther author of question
   */
	public void setAuthor(String author) { this.author = author; }
  
  /**
   * Get question's author who write question and wait an answer.
   * 
   * @return author  the name of question's author
   */
	public String getAuthor() { return author; }

  /**
   * Get content of question's response.
   * 
   * @return  the response of questions
   */
	public String getResponses() {return responses[0];}
	
	/**
	 * Get content of question's response.
	 * 
	 * @return  the response of questions
	 */
	public String[] getAllResponses() {return responses;}
  
  /**
   * Registers response for question, only admin or moderator can response for this quetsion.
   * 
   * @param responses the content of question's response which addmin or
   * morderator answer for this question
   */
	public void setResponses(String[] responses) {this.responses = responses;}

  /**
   * Get list questions in system which are like, related or support for this question.
   * 
   * @return  relations return list question's content is like this question
   */
	public String[] getRelations() {return relations;}
  
  /**
   * Registers list questions is related or supported for this question.
   * 
   * @param relations list questions have related with this question
   */
	public void setRelations(String[] relations) {this.relations = relations;}
	
  /**
   * Registers email address of question's author for this Question object.
   * 
   * @param email the email
   */
	public void setEmail(String email) { this.email = email; }
  
  /**
   * Get email address of question's author.
   * 
   * @return email  the email address of person who write question
   */
	public String getEmail() { return email; }

  /**
   * Registers  date time for Question object.
   * 
   * @param createdDate the date time when question is created
   */
	public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
  
  /**
   * Return date time when question is created or updated.
   * 
   * @return  the date time of question
   */
	public Date getCreatedDate() { return createdDate; }

  /**
   * Registers for this Question object is activated or not. This setting with
   * set approve for question will allow question is viewed or not
   * 
   * @param isActivated is <code>true</code> if this question is activated and
   * is <code>false</code> if this questiosn is not activated
   */
	public void setActivated(boolean isActivated) {	this.isActivated = isActivated ; }
  
  /**
   * Return status of this question, return <code>true</code> if
   * this question is activated and <code>false</code> if opposite.
   * 
   * @return    status of this question object, is activated or not
   */
	public boolean isActivated() { return isActivated ; }

  /**
   * Registers for this question is approved or not. This setting with
   * set activate for question will allow this question is viewed or not
   * 
   * @param isApproved  is <code>true</code> if this question is approved and
   * is <code>false</code> if this question is not approved
   */
	public void setApproved(boolean isApproved) { this.isApproved = isApproved ; }
  
  /**
   * Return status of thi question, return <code>true</code> if
   * this question is approved and <code>false</code> if opposite.
   * 
   * @return    status of question is approved or not
   */
	public boolean isApproved() { return isApproved ; }
  
  /**
   * Registers id for property categoryId of Question object,
   * each question is contained in a category and this property is used to
   * point this cateogry.
   * 
   * @param catId id of category which contain this question
   */
	public void setCategoryId(String catId) { this.categoryId = catId ; }
  
  /**
   * Get id of category which contain this question.
   * 
   * @return    an id of category which thi question
   */
	public String getCategoryId() { return categoryId; }
  
  /**
   * Registers list files will be attach to this question to description for this question.
   * Each file have size is less than 10MB and larger 0B
   * 
   * @param listFile  list files are attached to question.
   */
  public void setAttachMent(List<FileAttachment> listFile) { this.listAttachments = listFile ; }
  
  /**
   * Get list files are attached to this quetsion.
   * 
   * @return    list files are attached to this quetsion
   * 
   * @see       FileAttachment
   */
  public List<FileAttachment> getAttachMent(){return this.listAttachments ; }

  /**
   * Get date time when question is responsed, it's the lastest time . A question can be
   * rereponse some time, but system only lastest time is saved in to quetsion.
   * 
   * @return  the date time when question is response
   */
  public Date[] getDateResponse() { return dateResponse; }
  
  /**
   * Registers date time for question object when addmin or moderator response for this question,
   * this date time is automatic set for quetsion by system.
   * 
   * @param dateResponse the date time when question is responsed
   */
  public void setDateResponse(Date dateResponse[]) { this.dateResponse = dateResponse; }

  /**
   * Get name of admin or moderator who responsed this question.
   * 
   * @return    the name of admin or moderator
   */
  public String[] getResponseBy() { return responseBy; }
  
  /**
   * Registers name of person who response for this question, system auto set this
   * property for question when admin or moderator response a question.
   * 
   * @param responseBy  the name of admin or moderator
   */
  public void setResponseBy(String[] responseBy) { this.responseBy = responseBy; }

  /**
   * Get list languages of question which are not yet answered.
   * 
   * @return list languages
   */
	public String getLanguagesNotYetAnswered() {
  	return languagesNotYetAnswered;
  }

	/**
	 * Registers language is not yet answered.
	 * 
	 * @param languagesNotYetAnswered the languages not yet answered
	 * 
	 * @return the question
	 */
	public Question setLanguagesNotYetAnswered(String languagesNotYetAnswered) {
  	this.languagesNotYetAnswered = languagesNotYetAnswered;
  	return this;
  }
	
	/**
	 * Gets the name attachs.
	 * 
	 * @return the name attachs
	 */
	public String[] getNameAttachs() {return nameAttachs;}
	
	/**
	 * Sets the name attachs.
	 * 
	 * @param nameAttachs the new name attachs
	 */
	public void setNameAttachs(String[] nameAttachs) { this.nameAttachs = nameAttachs;}
	
	/**
	 * Get link to question, this link is used to send mail notify,
	 * when user click in to this link, will be jump to FAQ and view this question.
	 * 
	 * @return link to question
	 */
	public String getLink() {return link;}
	
	/**
	 * Register link to question, this link is used to send mail notify,
	 * when user click in to this link, will be jump to FAQ and view this question.
	 * 
	 * @param link the link
	 */
	public void setLink(String link) { this.link = link;}

	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	public String[] getComments() {
		return comments;
	}

	/**
	 * Sets the comments.
	 * 
	 * @param comments the new comments
	 */
	public void setComments(String[] comments) {
		this.comments = comments;
	}

	/**
	 * Gets the comment by.
	 * 
	 * @return the comment by
	 */
	public String[] getCommentBy() {
		return commentBy;
	}

	/**
	 * Sets the comment by.
	 * 
	 * @param commentBy the new comment by
	 */
	public void setCommentBy(String[] commentBy) {
		this.commentBy = commentBy;
	}

	/**
	 * Gets the date comment.
	 * 
	 * @return the date comment
	 */
	public Date[] getDateComment() {
		return dateComment;
	}

	/**
	 * Sets the date comment.
	 * 
	 * @param dateComment the new date comment
	 */
	public void setDateComment(Date[] dateComment) {
		this.dateComment = dateComment;
	}

	/**
	 * Gets the users vote.
	 * 
	 * @return the users vote
	 */
	public String[] getUsersVote() {
		return usersVote;
	}

	/**
	 * Sets the users vote.
	 * 
	 * @param usersVote the new users vote
	 */
	public void setUsersVote(String[] usersVote) {
		this.usersVote = usersVote;
	}

	/**
	 * Gets the mark vote.
	 * 
	 * @return the mark vote
	 */
	public double getMarkVote() {
		return markVote;
	}

	/**
	 * Sets the mark vote.
	 * 
	 * @param markVote the new mark vote
	 */
	public void setMarkVote(double markVote) {
		this.markVote = markVote;
	}

	/**
	 * Gets the users watch.
	 * 
	 * @return the users watch
	 */
	public String[] getUsersWatch() {
		return usersWatch;
	}

	/**
	 * Sets the users watch.
	 * 
	 * @param usersWatch the new users watch
	 */
	public void setUsersWatch(String[] usersWatch) {
		this.usersWatch = usersWatch;
	}

	/**
	 * Gets the emails watch.
	 * 
	 * @return the emails watch
	 */
	public String[] getEmailsWatch() {
		return emailsWatch;
	}

	/**
	 * Sets the emails watch.
	 * 
	 * @param emailsWatch the new emails watch
	 */
	public void setEmailsWatch(String[] emailsWatch) {
		this.emailsWatch = emailsWatch;
	}

	/**
	 * Gets the users vote answer.
	 * 
	 * @return the users vote answer
	 */
	public String[] getUsersVoteAnswer() {
		return usersVoteAnswer;
	}

	/**
	 * Sets the users vote answer.
	 * 
	 * @param usersVoteAnswer the new users vote answer
	 */
	public void setUsersVoteAnswer(String[] usersVoteAnswer) {
		this.usersVoteAnswer = usersVoteAnswer;
	}

	/**
	 * Gets the marks vote answer.
	 * 
	 * @return the marks vote answer
	 */
	public double[] getMarksVoteAnswer() {
		return marksVoteAnswer;
	}

	/**
	 * Sets the marks vote answer.
	 * 
	 * @param marksVoteAnswer the new marks vote answer
	 */
	public void setMarksVoteAnswer(double[] marksVoteAnswer) {
		this.marksVoteAnswer = marksVoteAnswer;
	}

	/**
	 * Gets the pos.
	 * 
	 * @return the pos
	 */
	public int[] getPos() {
		return pos;
	}

	/**
	 * Sets the pos.
	 * 
	 * @param pos the new pos
	 */
	public void setPos(int[] pos) {
		this.pos = pos;
	}
	
	/**
	 * Sets the pos.
	 * 
	 * @param pos the new pos
	 */
	public void setPos() {
		if(marksVoteAnswer != null) {
			pos = new int[marksVoteAnswer.length];
			for(int i = 0; i < marksVoteAnswer.length; i ++){
				pos[i] = i;
			}
		}
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String title) {
		this.question = title;
	}

	public Boolean[] getActivateAnswers() {
		return activateAnswers;
	}

	public void setActivateAnswers(Boolean[] activateAnswers) {
		this.activateAnswers = activateAnswers;
	}

	public Boolean[] getApprovedAnswers() {
		return approvedAnswers;
	}

	public void setApprovedAnswers(Boolean[] approvedAnswers) {
		this.approvedAnswers = approvedAnswers;
	}
}




