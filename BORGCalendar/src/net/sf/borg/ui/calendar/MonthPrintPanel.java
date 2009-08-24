/*
This file is part of BORG.
 
    BORG is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
 
    BORG is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 
    You should have received a copy of the GNU General Public License
    along with BORG; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 
Copyright 2003 by Mike Berger
 */
package net.sf.borg.ui.calendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.Serializable;
import java.text.AttributedString;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sf.borg.common.Errmsg;
import net.sf.borg.common.PrefName;
import net.sf.borg.common.Prefs;
import net.sf.borg.common.PrintHelper;
import net.sf.borg.common.Resource;
import net.sf.borg.model.Day;
import net.sf.borg.model.entity.CalendarEntity;

// monthPanel handles the printing of a single month
public class MonthPrintPanel extends JPanel implements Printable {

	private int year_;

	private int month_;

	private int pages_ = 1;

	public void setPages(int p) {
		pages_ = p;
	}

	// print does the actual formatting of the printout
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {

		if (pageIndex > pages_ - 1)
			return Printable.NO_SUCH_PAGE;

		return (drawIt(g, pageFormat.getWidth(), pageFormat.getHeight(),
				pageFormat.getImageableWidth(),
				pageFormat.getImageableHeight(), pageFormat.getImageableX(),
				pageFormat.getImageableY(), pageIndex));
	}

	private int drawIt(Graphics g, double width, double height,
			double pageWidth, double pageHeight, double pagex, double pagey,
			int pageIndex) {

		int year;
		int month;

		// see if color printout option set
		String cp = "false";
		try {
			cp = Prefs.getPref(PrefName.COLORPRINT);
		} catch (Exception e) {
		}

		// set up default and small fonts
		Graphics2D g2 = (Graphics2D) g;

		Font def_font = g2.getFont();
		// Font sm_font = def_font.deriveFont(6f);
		Font sm_font = Font.decode(Prefs.getPref(PrefName.MONTHVIEWFONT));
		Map<TextAttribute, Serializable> stmap = new HashMap<TextAttribute, Serializable>();
		stmap.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		stmap.put(TextAttribute.FONT, sm_font);

		g2.setColor(Color.white);
		g2.fillRect(0, 0, (int) width, (int) height);

		// set color to black
		g2.setColor(Color.black);

		// get font sizes
		int fontHeight = g2.getFontMetrics().getHeight();
		int fontDesent = g2.getFontMetrics().getDescent();

		// translate coordinates based on the amount of the page that
		// is going to be printable on - in other words, set upper right
		// to upper right of printable area - not upper right corner of
		// paper
		g2.translate(pagex, pagey);
		Shape s = g2.getClip();

		// determine month title
		GregorianCalendar cal = new GregorianCalendar(year_, month_, 1);
		cal.add(Calendar.MONTH, pageIndex);
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		cal.setFirstDayOfWeek(Prefs.getIntPref(PrefName.FIRSTDOW));

		Date then = cal.getTime();
		SimpleDateFormat sd = new SimpleDateFormat("MMMM yyyy");
		String title = sd.format(then);

		// determine placement of title at correct height and centered
		// horizontally on page
		int titlewidth = g2.getFontMetrics().stringWidth(title);
		int caltop = fontHeight + fontDesent;
		int daytop = caltop + fontHeight + fontDesent;
		g2.drawString(title, ((int) pageWidth - titlewidth) / 2, fontHeight);

		// calculate width and height of day boxes (6x7 grid)
		int rowheight = ((int) pageHeight - daytop) / 6;
		int colwidth = (int) pageWidth / 7;

		// calculate the bottom and right edge of the grid
		int calbot = 6 * rowheight + daytop;
		int calright = 7 * colwidth;

		// draw the day names centered in each column - no boxes drawn yet
		SimpleDateFormat dfw = new SimpleDateFormat("EEE");
		cal.add(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek()
				- cal.get(Calendar.DAY_OF_WEEK));
		for (int col = 0; col < 7; col++) {

			int colleft = (col * colwidth);
			String dayofweek = dfw.format(cal.getTime());
			int swidth = g2.getFontMetrics().stringWidth(dayofweek);
			g2.drawString(dayofweek, colleft + (colwidth - swidth) / 2, caltop
					+ fontHeight);
			cal.add(Calendar.DAY_OF_WEEK, 1);
		}

		// reset calendar
		cal.set(year, month, 1);
		int fdow = cal.get(Calendar.DAY_OF_WEEK) - cal.getFirstDayOfWeek();

		// print the days - either grayed out or containing a number and appts
		for (int box = 0; box < 42; box++) {

			// month length
			int mlen = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			int boxcol = box % 7;
			int boxrow = box / 7;
			int rowtop = (boxrow * rowheight) + daytop;
			int colleft = boxcol * colwidth;
			int dow = cal.getFirstDayOfWeek() + boxcol;
			if (dow == 8)
				dow = 1;

			// if box in grid is before first day or after last, just draw a
			// gray box
			if (box < fdow || box > fdow + mlen - 1) {
				// gray
				if (cp.equals("false")) {
					g2.setColor(new Color(235, 235, 235));
					g2.fillRect(colleft, rowtop, colwidth, rowheight);
					g2.setColor(Color.black);
				}
			} else {
				int date = box - fdow + 1;

				// set small font for appt text
				g2.setFont(sm_font);
				int smfontHeight = g2.getFontMetrics().getHeight();
				// int smfontDesent=g2.getFontMetrics().getDescent();

				// set clip to the day box to truncate long appointment text
				g2.clipRect(colleft, rowtop, colwidth, rowheight);
				try {

					// get the appointment info for the given day
					GregorianCalendar gc = new GregorianCalendar(year, month,
							date);
					Day di = Day.getDay(year, month, date
							);
					if (di != null) {
						if (cp.equals("true")) {
							if (di.getVacation() != 0) {
								g2.setColor(new Color(225, 255, 225));
							} else if (di.getHoliday() == 1) {
								g2.setColor(new Color(255, 225, 195));
							} else if (dow == Calendar.SUNDAY
									|| dow == Calendar.SATURDAY) {
								g2.setColor(new Color(255, 225, 195));
							} else {
								g2.setColor(new Color(255, 245, 225));
							}

							g2.fillRect(colleft, rowtop, colwidth, rowheight);
							g2.setColor(Color.black);
						}
						Collection<CalendarEntity> appts = di.getItems();
						if (appts != null) {

							Iterator<CalendarEntity> it = appts.iterator();

							// determine X,Y coords of first appt text
							int apptx = colleft + 2 * fontDesent;
							int appty = rowtop + fontHeight + smfontHeight;

							while (it.hasNext()) {
								CalendarEntity ai = it.next();

								// change color for a single appointment based
								// on
								// its color - only if color print option set
								if (cp.equals("false"))
									g2.setColor(Color.black);
								else if (ai.getColor().equals("red"))
									g2.setColor(Color.red);
								else if (ai.getColor().equals("green"))
									g2.setColor(Color.green);
								else if (ai.getColor().equals("blue"))
									g2.setColor(Color.blue);

								if (ApptBoxPanel.isStrike(ai, gc.getTime())) {

									if (Prefs.getBoolPref(PrefName.HIDESTRIKETHROUGH))
										continue;
									
									// need to use AttributedString to work
									// around a bug
									AttributedString as = new AttributedString(
											ai.getText(), stmap);
									g2.drawString(as.getIterator(), apptx,
											appty);
								} else {

									g2.drawString(ai.getText(), apptx, appty);
								}

								// increment the Y coord
								appty += smfontHeight;

								
								// reset to black
								g2.setColor(Color.black);
							}
						}
					}

					g2.setClip(s);

				} catch (Exception e) {
					Errmsg.errmsg(e);
				}

				// draw date
				g2.setFont(def_font);
				g2.drawString(Integer.toString(date), colleft + fontDesent,
						rowtop + fontHeight);

			}

		}

		// draw the lines last

		// top of calendar - above day names
		g2.drawLine(0, caltop, calright, caltop);
		for (int row = 0; row < 7; row++) {
			int rowtop = (row * rowheight) + daytop;

			// horizontal lines from below day names to bottom
			g2.drawLine(0, rowtop, calright, rowtop);
		}

		for (int col = 0; col < 8; col++) {
			int colleft = (col * colwidth);

			// vertical lines
			g2.drawLine(colleft, caltop, colleft, calbot);

		}

		return Printable.PAGE_EXISTS;
	}

	static private final double prev_scale = 1.5;

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			Graphics2D g2 = (Graphics2D) g;
			g2.scale(prev_scale, prev_scale);
			drawIt(g, getWidth() / prev_scale, getHeight() / prev_scale,
					getWidth() / prev_scale - 20,
					getHeight() / prev_scale - 20, 10, 10, 0);

		} catch (Exception e) {
			Errmsg.errmsg(e);
		}
	}

	public static void printMonths(int month, int year) throws Exception {

		// use the Java print service
		// this relies on monthPanel.print to fill in a Graphic object and
		// respond to the Printable API
		MonthPrintPanel cp = new MonthPrintPanel(month, year);
		Object options[] = { new Integer(1), new Integer(2), new Integer(3),
				new Integer(4), new Integer(5), new Integer(6), new Integer(7),
				new Integer(8), new Integer(9), new Integer(10),
				new Integer(11), new Integer(12) };
		Object choice = JOptionPane.showInputDialog(null, Resource
				.getResourceString("nummonths"), Resource
				.getResourceString("Print_Chooser"),
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (choice == null)
			return;
		Integer i = (Integer) choice;
		cp.setPages(i.intValue());

		PrintHelper.printPrintable(cp);

	}

	public MonthPrintPanel(int month, int year) {
		year_ = year;
		month_ = month;

	}
}
