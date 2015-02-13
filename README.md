MozzAndroidUtils
===================
作者：hsllany@163.com, everyknoxkyo@gmail.com
HttpUtils 用法
-------------------
HttpUtils为异步返回，每个任务执行时是属于不同线程，所以不应在HttpListener或HttpDownloadListener中操纵UI，应运用Handler。

###get 方法###
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
	public void onDownloadSuccess() {
		//下载成功
				
	}
			
	@Override
	public void onStartDownload(int fileSize) {
		//开始下载，fileSize为要下载文件的大小
				
	}
			
	@Override
	public void onDownloadFailed() {
		//下载失败
				
	}
			
	@Override
	public void onDownloading(int downloadSize) {
		//正则下载，downloadSize为已经下载的大小
				
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

然后，继承Eloquent（代表数据库中的表）, 类名的规则是：表名 + Eloquent。注意命名应和数据库中表明对应。

```
class StudentsEloquent extends Eloquent<Student>{

}
```
之后，运用如下：

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

客户端升级Upgrader
--------------------
使用Upgrader，可灵活对客户端进行升级。

示例：
```
final Upgrader upgrader = new Upgrader(
				"http://182.92.150.3/upgrade.json", this);
		upgrader.setOnUpgradeListener(new UpgradeListener() {

			@Override
			public void onDownloadSuccess() {
				Log.d("Upgrader", "successfully download");

			}

			@Override
			public void onDownloadStart(int fileSize) {
				Log.d("Upgrader", "onStartDownload:" + fileSize);

			}

			@Override
			public void onDownloadFailed() {
				Log.d("Upgrader", "onFailDownload:");
			}

			@Override
			public void onDownloading(int downloadSize) {

			}

			@Override
			public void onCheckNewVersion(boolean hasNew,
					int serverVersionCode, String serverVersion,
					String serverVersionDescription) {
				Log.d("Upgrader", "onCheckNewVersion:hasNew=" + hasNew
						+ ",serverVersionCode=" + serverVersionCode
						+ ",serverVersion=" + serverVersion + ",des="
						+ serverVersionDescription);

				if (hasNew)
					upgrader.download();
			}

			@Override
			public void onCheckFailed() {
				Log.d("Upgrader", "onCheckFailed:");
			}
		});

		upgrader.checkNewVersion();
	```
