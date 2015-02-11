MozzAndroidUtils
===================
作者：hsllany@163.com, everyknoxkyo@gmail.com
HttpUtils 用法
-------------------
HttpUtils运用了多线程，每个任务执行时是属于不同进程，所以不要在HttpListener或HttpDownloadListener中操纵UI,应运用Handler。

###get方法###
```
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
```

###post 方法###
```
//装入Post参数
Map<String, String> postData = new HashMap<String, String>();
postData.put("email", "test@t.com");

httpUtils.post("http://www.baidu.com", new HttpListener() {

		@Override
		public void onSuccess(HttpResponse response) {
		if (response.status == HTTP_OK)
			System.out.println(response.html);
	}

		@Override
		public void onFail(HttpResponse response) {
			// TODO Auto-generated method stub
		}
	}, postData);
```

###文件下载###
```
DownloaderHttpUtils downloader = new DownloaderHttpUtils();
downloader.download("http://www.test.com/a.file", new HttpDownloadListener() {
			
	@Override
	public void onSuccessFinish() {
		// TODO Auto-generated method stub
				
	}
			
	@Override
	public void onStart(int fileSize) {
		// TODO Auto-generated method stub
				
	}
			
	@Override
	public void onFail() {
		// TODO Auto-generated method stub
				
	}
			
	@Override
	public void onDownloading(int downloadSize) {
		//可以计算已经下载的downloadSize
				
	}
}, SDCard.sdCardDir() + "/saveDir");
```

DB用法
--------------------
*由Mozz框架运行的表中，必须含有字段"_id",表示主键。*

首先继承Model类，此类代表的是表中每行的数据，在其添加与表中字段一致的属性（_id不用添加）。

```
class Student extends Model {
	private String name;

public void setName(String nm) {
		this.name = nm;
	}
}
```

首先继承Eloquent（代表数据库中的表）, 类名的规则是：表名 + Eloquent

```
class StudentsEloquent extends Eloquent<Student>{

}
```

此后就可以调用了。

###查询所有：###
```
StudentsEloquent studentTable = new StudentsEloquent();
Cursor cursor = students.all();
```

###带Where的查找###
```
Cursor cursor = studentTable.where({'name'},{'zhangdao'});
```

###查找id,并更新###
```
Student student = studentTable.find(1, new Student());
student.name = "zhangdao";
studentTable.save(student);
```

###插入新数据###
```
Student student = new Student();
student.name = "zhangdao";
studentTable.save(student);
```

###删除数据###
```
studentTable.delete(student);
```

###创建表###
```
Eloquent.create("student", new String[] { "name", "age" },
				new COLUMN_TYPE[] { COLUMN_TYPE.TYPE_TEXT,
						COLUMN_TYPE.TYPE_INTEGER }, this);
```
