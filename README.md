MozzAndroidUtils
===================

HttpUtils 用法
-------------------
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.get("http://www.baidu.com", new HttpListener() {

			@Override
			public void onSuccess(HttpResponse response) {
				if (response.status == HTTP_OK)
					System.out.println(response.html);

			}

			@Override
			public void onFail(HttpResponse response) {
				// TODO Auto-generated method stub

			}
		});
'

DB用法
--------------------
由Mozz框架运行的表中，必须含有字段"_id",表示主键。

首先继承Model类，此类代表的是表中每行的数据，在其添加与表中字段一致的属性（_id不用添加）。
		class Student extends Model {
		private String name;

		public void setName(String nm) {
			this.name = nm;
		}
		}
首先继承Eloquent, 类名的规则是表名 + Eloquent
'class StudentsEloquent extends Eloquent<Student>{

}
'

此后就可以调用了。

查询所有：
'
StudentsEloquent students = new StudentsEloquent();
Cursor = students.all();
'
查找id,并更新
'Student student = students.find(1, new Student());
student.name = "zhangdao";
student.save();
'
