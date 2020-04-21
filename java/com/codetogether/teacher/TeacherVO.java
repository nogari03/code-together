package com.codetogether.teacher;

public class TeacherVO {

	String member_id;
	String teacher_id;
	String image;
	String introduce;


	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}
	public String getTeacher_id() {
		return teacher_id;
	}
	public void setTeacher_id(String teacher_id) {
		this.teacher_id = teacher_id;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getIntroduce() {
		return introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	@Override
	public String toString() {
		return "TeacherVO [member_id=" + member_id + ", teacher_id=" + teacher_id + ", image=" + image + ", introduce="
				+ introduce + "]";
	}

}
