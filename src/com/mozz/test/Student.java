package com.mozz.test;

import com.mozzandroidutils.sqlite.Ingnore;
import com.mozzandroidutils.sqlite.Model;

public class Student extends Model {

	public Student() {
	}

	public String name = "";
	public int age = 1;
	public String gender;

	@Ingnore
	public int noneRelatedField = 3;
}
