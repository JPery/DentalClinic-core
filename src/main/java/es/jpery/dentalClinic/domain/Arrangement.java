package es.jpery.dentalClinic.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Arrangement {

	public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	private int id;
	private String title;
	private Date date;
	private int owner;

	public Arrangement() {
	}

	public Arrangement(int id, String title, Date date, int owner) {
		this.id = id;
		this.title = title;
		this.date = date;
		this.owner = owner;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) { this.title = title; }

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) { this.date = date; }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}
}
