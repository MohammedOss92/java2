package com.sarrawi.myapplication;

public class ResultRow {
	public String from;
	public String to;
	public String toCode;
	public String fromCode;
	public String fromText;
	public String toText;
	public int id;
	public ResultRow(int id, String from, String to, String fromText, String toText,String toCode,String fromCode) {
		this.id = id;
		this.from = from;
		this.fromText = fromText;
		this.to = to;
		this.toText = toText;
		this.toCode = toCode;
		this.fromCode = fromCode;
	}
}
