Mozz Android
===================
作者：hsllany， demonknoxkyo

MozzHttp
-------------------
普通网络请求，可用HttpUtils即可。若涉及到文件下载，应用DownloadHttpUtils.

HttpUtils和DownloadHttpUtils均为异步机制，每个任务执行时是属于不同线程，所以不应在HttpListener或HttpDownloadListener中操纵UI，应运用Handler。

###get 方法###
```java
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
```java
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
```java
HttpUtils httpUtils = new HttpUtils();
HttpUtils.download("http://www.test.com/a.file", new HttpDownloadListener() {
			
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

###Release resources###
After you finish all Http request, you should explicitly invoke ```HttpUtils.shutdown```to finish the job. This operation will cancel the task that has not been executed currently.

###Known bugs###
Because the HttpUtils holds the references of all kinds of HttpListener, which may has implicit reference of outer Activity, so HttpUtils may delay the garbage collector to destroy the Activity object, which may cause performance problems. If you want to do heavy operation through Http request, Android Loader may be a good choice. 

MozzDB
--------------------
使用前，步骤如下：

- 1.创建表格；

**注：Mozz框架运行的表中，默认含有字段"id"，表示主键。**
```java
String[] columnNames = { "name", "gender", "age", "extra" };
ColumnType[] columnTypes = { ColumnType.TYPE_TEXT,
				ColumnType.TYPE_TEXT, ColumnType.TYPE_INTEGER,
				ColumnType.TYPE_BLOB };
Eloquent.create("students", columnNames, columnTypes, this);
```

- 2.编写DAO（Data Access Object）对象（继承Model），其属性对应表中数据，会被Mozz自动解析。

（带有final，static以及natvie，以及带有@Ingnore注解的属性，不会被Mozz处理）。

**注：必须包含一个无参数的构造方法。**
```java
public class Student extends Model{
	//需要一个不带参数的构造方法
	public Student(){};
	
	public String name;
	public int age;
	public Object extra;
	
	//如果存在不被Mozz框架解析的属性，需要加上Ingnore注解
	@Ingnore
	public int otherIntField = 3;
	
	//static不会被映射到表格中
	public static int staticIntField = 5;
}
```

- 3.为每一个表格新建一个类继承Eloquent， 且该类名的命名规则：表名 + Eloquent；

并在modelClass中，将该表格对应的Model类型返回。

**注：注意命名应和数据库中表明对应。**

```java
//对应数据库中students表格
class StudentsEloquent extends Eloquent{
	public StudentsEloquent(Context context) {
		super(context, clazz);
	}
	
	@Override
	protected Class<? extends Model> modelClass() {
		//在这里把表格对应的Model类返回
		return Student.class;
	}
}
```

若要使用其他的table name，应加上TableName注解
```java
@TableName(tablename = "YouTable")
class StudentsEloquenet extends Eloquenet{
...
}
```

- 4.在你的程序中中创建StudentsEloquent的实例：
```java
StudentsEloquent studentsTable = new StudentsEloquent(this);

//可打开调试模式，这样所有sql语句都将打印
studentsTable.setDebug(true);
```

之后，运用如下：

###查询所有：###
```java
//获取表中所有数据，存入List中。从List
List<Model> studentsResult = students.all().get();
```

###复杂查询###
```java
//获取所有name为zhangdao的数据
List<Model> result = studentsTable
						.where("name = 'zhangdao'")
						.get();
						
//只获取name字段，此时应注意，model对象中的字段应复以合适初值，否则会产生难以预料的错误。
List<Model> result = studentsTable.select("name").
						.where("name = 'zhangdao'").orderBy("id").
						.get();

//获取单条数据
Model model = studentsTable.where("grade = 3").first();

//获取Cursor
Cursor cursor = studentsTable.where("grade = 3").cursor();

//获取条数
int boyNum = studentsTable.select("name").
						.where("gender = 'M'")
						.count();
```


###通过id查找student，并更新###
```java
Student student = studentsTable.find(321);
student.name = "zhangdao";
studentsTable.save(student);
```

###插入单条新数据###
```java
Student student = new Student();
student.name = "zhangdao";
studentsTable.save(student);
```

###批量插入数据
Mozz运用了事务机制及预处理机制，大批量数据建议使用insertMany()方法，能达到很好的效率。
```java
List<Student> class4 = new ArrayList<Student>();
for(int i = 0; i < 1000; i++){
	Student student = new Student();
	student.name = "student no." + i;
	student.age = 13
}

studentsTable.insertMany(class4);
```

###删除数据###
```java
studentsTable.delete(student);
```

###批量删除###
```java
studentsTable.where("age > 13").delete();
```

客户端升级Upgrader
--------------------
使用Upgrader，可灵活对客户端进行升级。

服务器应对应配置升级用json，示例如下：
```json
{"versionCode":3, "versionName":"1.1.1", "des":"2015年的新版本", "downloadurl":"http://test.com/test.apk"}
```
这里code为版本对应编号，应大于AndroidManifest。xml中的versionCode，否则不会触发onCheckNewVersion中hasNew为true的情况，具体见下：
```java
		//定义upgrader，传入升级网址
		final Upgrader upgrader = new Upgrader(
				"http://test.com/upgrade.json", this);
				
		//定义回调接口，处理升级
		upgrader.setOnUpgradeListener(new UpgradeListener() {
			@Override
			public void onNewVersion(int serverVersionCode,
					String serverVersion, String serverVersionDescription) {

				try {
					upgrader.download(null,
							MozzConfig.getAppAbsoluteDir(MainActivity.this),
							"newVersion.apk");
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNoNewVersion() {
				Log.d("Upgrader", "onNoNewVersion");
			}

			@Override
			public void onCheckFailed() {
				Log.d("Upgrader", "onCheckFailed:");
			}
			
		});

		//检查新版本
		upgrader.checkNewVersion();
```
检测完新版本后，如果确认有新版本，可调用upgrader.download()直接发起下载。**但若无新版本，调用download()，会触发IllegalAccessException**
```java
try {
			//确保在检测到新版本后发起
			upgrader.download(new HttpDownloadListener() {

				@Override
				public void onDownloading(int downloadSize) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDownloadSuccess() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDownloadStart(int fileSize) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDownloadFailed() {
					// TODO Auto-generated method stub

				}
			}, "/AppDir", "newVersion.apk");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
```

MozzCache
------------------

缓存可以提高应用的速度，并减少网络请求。Mozz提供2种缓存机制，版本号缓存及Expire Time缓存。

```java
//获取缓存对象
FileCache cache = FileCache.instance(this);
Player playerToCache = new Player();
//将Serializable对象（必须实现Serializable接口）存入缓存，
//以版本号机制存入，取时用新版本号取，若新版本号小于等于缓存版本号，则能取到相关数据；否则null
cache.putWithVersion("cacheKey", playerToCache, 1L, new PutCallback() {
	@Override
	public void onSuccess() {
		Log.d("CACHE", "success");
	}

	@Override
	public void onFail() {
		Log.d("CACHE", "fail");
	}
});

//以ExpireTime机制存入，5秒后过期
cache.putWithExpireTime("cacheKey2", playerToCache, 5000, new PutCallback() {
	@Override
	public void onSuccess() {
		Log.d("CACHE", "success");
	}

	@Override
	public void onFail() {
		Log.d("CACHE", "fail");
	}
});

//取缓存
cache.getOrOldversion("cacheKey", 2L, new GetCallback() {
	@Override
	public void onSuccess(Object item) {
		if (item != null) {
			Log.d("CACHE", "get same version" + ((Player) item).mId);
		} else {
			Log.d("CACHE", "version too old");
		}
	}

	@Override
	public void onFail() {
		Log.d("CACHE", "failed");
	}
});

cache.getOrExpired("cacheKey2", new GetCallback() {
	@Override
	public void onSuccess(Object item) {
		if (item != null) {
			Log.d("CACHE", "get" + ((Player) item).mId);
		} else {
			Log.d("CACHE", "expired");
		}
	}

	@Override
	public void onFail() {
		Log.d("CACHE", "failed");
	}
});
```

MozzSearch
------------------
Mozz search实现了通过从某数据源读取数据，和关键字keyword进行字符串匹配，并根据匹配权值输出List序列。

Mozz的搜索策略有
- MODE_STRICT_EQUAL: 和关键字严格相等
- MODE_CONTAIN_KEYWORD：包含关键字
- MODE_LCSUBSEQUENCE：和关键字有最长子序列
- MODE_LCSUBSTRING：和关键字拥有最长子串

Mozz在搜索策略及关键字不变的前提下，按匹配权值的大小，正序及逆序的输出搜索结果，并利用平衡二叉树动态的维护该结果。