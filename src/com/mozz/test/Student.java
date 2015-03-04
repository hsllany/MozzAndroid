package com.mozz.test;

import com.mozz.sqlite.Ingnore;
import com.mozz.sqlite.Model;

public class Student extends Model {

	public Student() {
	}

	public String name = "";
	public int age = 1;
	public String gender;

	@Ingnore
	public int noneRelatedField = 3;
}
