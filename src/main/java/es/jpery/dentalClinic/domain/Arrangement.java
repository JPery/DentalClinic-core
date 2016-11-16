package es.jpery.dentalClinic.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Arrangement {

	public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	private int id;
	private int kindOfIntervention;
	private Date date;
	private int owner;
	private String comment;

	public Arrangement() {
	}

	public Arrangement(int id, int kindOfIntervention, Date date, int owner, String comment) {
		this.id = id;
		this.kindOfIntervention = kindOfIntervention;
		this.date = date;
		this.owner = owner;
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getKindOfIntervention() {
		return kindOfIntervention;
	}

	public void setKindOfIntervention(int kindOfIntervention) { this.kindOfIntervention = kindOfIntervention; }

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
