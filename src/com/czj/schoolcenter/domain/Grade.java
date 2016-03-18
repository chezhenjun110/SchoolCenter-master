package com.czj.schoolcenter.domain;

public class Grade {
	public String id;
	public String term;
	public String number;
	public String name;
	public String score; // ³É¼¨
	public String credit; // Ñ§·Ö
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	@Override
	public String toString() {
		return "Grade [id=" + id + ", term=" + term + ", number=" + number + ", name=" + name + ", score=" + score
				+ ", credit=" + credit + "]";
	}

}
