/*
 * This file is part of BORG.
 *
 * BORG is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * BORG is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * BORG; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 *
 * Copyright 2003 by Mike Berger
 */
package net.sf.borg.model.entity;

import java.util.Vector;



public class Appointment extends KeyedEntity<Appointment> implements CalendarEntity, java.io.Serializable {

	
	private static final long serialVersionUID = 7225675837209156249L;
	private java.util.Date Date_;
	public java.util.Date getDate() { return( Date_ ); }
	public void setDate( java.util.Date xx ){ Date_ = xx; }

	private Integer Duration_;
	public Integer getDuration() { return( Duration_ ); }
	public void setDuration( Integer xx ){ Duration_ = xx; }

	private String Text_;
	public String getText() { return( Text_ ); }
	public void setText( String xx ){ Text_ = xx; }

	private Vector<String> SkipList_;
	public Vector<String> getSkipList() { return( SkipList_ ); }
	public void setSkipList( Vector<String> xx ){ SkipList_ = xx; }

	private java.util.Date NextTodo_;
	public java.util.Date getNextTodo() { return( NextTodo_ ); }
	public void setNextTodo( java.util.Date xx ){ NextTodo_ = xx; }

	private Integer SN_;
	public Integer getSN() { return( SN_ ); }
	public void setSN( Integer xx ){ SN_ = xx; }

	private Integer Vacation_;
	public Integer getVacation() { return( Vacation_ ); }
	public void setVacation( Integer xx ){ Vacation_ = xx; }

	private Integer Holiday_;
	public Integer getHoliday() { return( Holiday_ ); }
	public void setHoliday( Integer xx ){ Holiday_ = xx; }

	private boolean Private_;
	public boolean getPrivate() { return( Private_ ); }
	public void setPrivate( boolean xx ){ Private_ = xx; }

	private Integer Times_;
	public Integer getTimes() { return( Times_ ); }
	public void setTimes( Integer xx ){ Times_ = xx; }

	private String Frequency_;
	public String getFrequency() { return( Frequency_ ); }
	public void setFrequency( String xx ){ Frequency_ = xx; }

	private boolean Todo_;
	public boolean getTodo() { return( Todo_ ); }
	public void setTodo( boolean xx ){ Todo_ = xx; }

	private String Color_;
	public String getColor() { return( Color_ ); }
	public void setColor( String xx ){ Color_ = xx; }

	private boolean RepeatFlag_;
	public boolean getRepeatFlag() { return( RepeatFlag_ ); }
	public void setRepeatFlag( boolean xx ){ RepeatFlag_ = xx; }

	private String Category_;
	public String getCategory() { return( Category_ ); }
	public void setCategory( String xx ){ Category_ = xx; }

	private boolean New_;
	public boolean getNew() { return( New_ ); }
	public void setNew( boolean xx ){ New_ = xx; }

	private boolean Modified_;
	public boolean getModified() { return( Modified_ ); }
	public void setModified( boolean xx ){ Modified_ = xx; }

	private boolean Deleted_;
	public boolean getDeleted() { return( Deleted_ ); }
	public void setDeleted( boolean xx ){ Deleted_ = xx; }

	private String Alarm_;
	public String getAlarm() { return( Alarm_ ); }
	public void setAlarm( String xx ){ Alarm_ = xx; }

	private String ReminderTimes_;
	public String getReminderTimes() { return( ReminderTimes_ ); }
	public void setReminderTimes( String xx ){ ReminderTimes_ = xx; }
	
	private String Untimed_;
	public String getUntimed() { return( Untimed_ ); }
	public void setUntimed( String xx ){ Untimed_ = xx; }

	
	@SuppressWarnings("unchecked")
	protected Appointment clone() {
		Appointment dst = new Appointment();
		dst.setKey( getKey());
		dst.setDate( getDate() );
		dst.setDuration( getDuration() );
		dst.setText( getText() );
		Vector<String> v = getSkipList();
		if( v != null )
		{		
			dst.setSkipList((Vector<String>)v.clone());
		}
				
		dst.setNextTodo( getNextTodo() );
		dst.setSN( getSN() );
		dst.setVacation( getVacation() );
		dst.setHoliday( getHoliday() );
		dst.setPrivate( getPrivate() );
		dst.setTimes( getTimes() );
		dst.setFrequency( getFrequency() );
		dst.setTodo( getTodo() );
		dst.setColor( getColor() );
		dst.setRepeatFlag( getRepeatFlag() );
		dst.setCategory( getCategory() );
		dst.setNew( getNew() );
		dst.setModified( getModified() );
		dst.setDeleted( getDeleted() );
		dst.setAlarm( getAlarm() );
		dst.setReminderTimes( getReminderTimes() );
		dst.setUntimed( getUntimed() );
		return(dst);
	}
}