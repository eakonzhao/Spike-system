# Spike-system

#### 一个基于Spring MVC、Spring、Mybatis框架的简单商品秒杀系统

A simple spike system  based on Spring MVC, Spring, Mybatis framework.

之前看了《大型网站技术架构·核心原理与案例分析》一书，其中介绍了一些关于网购秒杀系统架构设计相关的知识，碰巧在imooc上也看到了有关的课程。在参考了各方资料之后，个人感觉对如何设计一个秒杀系统有了基本的了解，于是打算自己也试着实现一个简单的秒杀系统。本文的秒杀系统虽然是基于Spring MVC+Spring+Mybatis框架实现，但是其中的架构思想以及处理问题的方法是语言无关的。所以使用其他编程语言做开发的同学也可以看一看。

本文主要是对秒杀系统架构设计、系统功能等进行介绍，另外提一下编码过程中遇到的一些坑，具体的编码不过多赘述，代码中都写了详细的注释。建议直接把项目down下来自己边运行边探究。

项目github地址:https://github.com/eakonzhao/Spike-system ( 喜欢的话记得给个star哦 o(^▽^)o )

####怎么把项目运行起来？
1. git clone https://github.com/eakonzhao/Spike-system
2. 打开 IDEA --> File --> New --> Open
3. 打开pom.xml，然后让Maven将所需依赖都加载进来
4. 将jdbc.properties中的数据库url、用户名和密码改成你自己的
5. 运行Tomcat
6. 在浏览器输入http://localhost:8080/seckill/list



####秒杀系统架构设计

一、秒杀活动技术挑战

1. 对现有业务造成冲击：

秒杀活动一般是网站营销的附加活动。秒杀活动的特点是持续时间短、瞬时访问量大的特点，因此在秒杀活动进行期间肯定要占用大量的网络带宽以及服务器资源等，如果和网站日常的应用部署在一起必然会对现有业务造成冲击，甚至可能会导致宕机时间的发生。在实际生产中，我们有两种选择：一个是进行适当地服务降级，在秒杀活动进行的过程中关闭一些相对来说没有那么重要的服务。例如淘宝最初在应对双11的时候，就会把确认收货以及商品评价等功能关闭，以争取给予秒杀活动尽可能多的可用资源；另一个选择是秒杀系统和现有业务系统分开部署，其实也就是业务分割部署的思想。

2. 高并发下的应用、数据库负载

用户在秒杀开始前通过不断刷新浏览器页面以保证不会错过秒杀，这些请求如果按照一般的网站应用架构，访问服务器、连接数据库、会对应用服务器和数据库服务器造成极大的负载压力。

3. 突然增加的网络及服务器带宽

秒杀活动时所需的带宽会超过网站平时使用的带宽

4. 用户未到秒杀时间直接下单

秒杀的流程应该是到了秒杀时间才能开始对商品下单购买，在此时间点之前，只能浏览商品信息而不能下单。而下单页面也是可以通过一个普通的URL获取，如果得到这个URL，那么不用等到秒杀开始就可以进行下单了。

二、秒杀系统的应对策略

1. 秒杀系统独立部署

2. 秒杀商品页面静态化

   重新设计秒杀商品页面，不使用网站原来的商品详情页面，页面内容静态化：将商品描述、商品参数、成交记录和用户评价全部写入一个静态页面，用户请求不需要经过应用服务器的业务逻辑处理，也不需要访问数据库。所以秒杀商品服务不需要部署动态的Web服务器和数据库服务器。

3. 租借秒杀网络带宽

4. 动态生成随即下单页面URL

   为了避免用户直接访问下单页面URL，需要将该URL动态化，即使秒杀系统的开发者也无法在秒杀开始之前访问下单页面的URL。解决办法是在下单页面URL加入由服务器生成的随机数作为参数，在秒杀开始的时候得到。

三、需求分析与系统架构
下面是秒杀系统的一个基本流程图：

![秒杀系统业务流程](http://upload-images.jianshu.io/upload_images/2993097-efc43e31da6abdb1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

但是在本系统中，并没有实现得那么完善，而是针对一部分方面进行了实践：

![本系统实现的功能](http://upload-images.jianshu.io/upload_images/2993097-5c16e4c260a7612a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

前端主要流程图

![秒杀系统前端流程图](http://upload-images.jianshu.io/upload_images/2993097-f7964d61c0b93ca5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

后端简化流程图(因为还会涉及到访问Redis缓存等操作)

![后端简化流程图](http://upload-images.jianshu.io/upload_images/2993097-521f9f9a1d604bf8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

在这里特别把其中的事务拿出来说一下。我们的事务由两个操作组成，分别是操作两张表，一个操作是更新某张表的数据，另一个操作是往某张表里面插入数据。其实这里有一个优化的点，就是在业务代码中将插入操作放在更新操作之前。假如插入失败就直接回滚。但是如果把更新操作(即扣库存)放在前面，在并发的环境下可能会涉及高频率的行级锁竞争问题，导致系统性能急剧下降。(由数据库的三级封锁协议可知这样确实起到了一定的优化作用)

![从用户角度针对库存业务进行分析](http://upload-images.jianshu.io/upload_images/2993097-d963cf42ba4d1ba6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![事务详情](http://upload-images.jianshu.io/upload_images/2993097-b38ef0dbc4620722.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![两张主要的数据表(Github已经放了建表的sql语句)](http://upload-images.jianshu.io/upload_images/2993097-9bd8c4a0e7335506.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

####系统功能介绍
由于上面已经给出了系统的详细流程图，所以在这里就只展示几张系统的截图

![秒杀商品列表页](http://upload-images.jianshu.io/upload_images/2993097-19331f1ea3d54d79.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![秒杀待开启](http://upload-images.jianshu.io/upload_images/2993097-cc656733a286136d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![进入秒杀页面，可以开始秒杀](http://upload-images.jianshu.io/upload_images/2993097-69a706ca4361fc73.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![由于库存为零，点击之后显示秒杀结束](http://upload-images.jianshu.io/upload_images/2993097-5690ba430c9bc232.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![重复秒杀](http://upload-images.jianshu.io/upload_images/2993097-85773e865d3c4bfb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

####实现
#####项目总体描述
本项目是基于
#####项目中应用到的技术与工具
- IDEA
- MySQL
- Spring MVC
- Spring
- Mybatis
- Redis(用户缓存部分商品信息)
- Maven
- protostuff(用于序列化对象，性能会比jdk自带的序列化好)
  ........
  其实还用到了一些技术，这里就不一一给出了。由于本项目采用Maven进行管理，所以在pom.xml文件里面都给出了所需的依赖。

#####项目骨架展示

![项目骨架展示](http://upload-images.jianshu.io/upload_images/2993097-dea1c644ae066e3b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#####遇到的一些坑
1. Spring MVC配置出错
   ![通配符很全面，但无法找到元素...](http://upload-images.jianshu.io/upload_images/2993097-6782836544fb48d2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
   ![ApplicationContext.xml的头要配置正确](http://upload-images.jianshu.io/upload_images/2993097-d29792ecf56f99d4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
2. Spring MVC参数绑定错误
   调试的时候打开浏览器控制台看到报了个400错误，后来检查之后发现原来是Spring MVC的参数绑定出错了，使用@pathVariable的时候要注意

![控制台出现400错误](http://upload-images.jianshu.io/upload_images/2993097-277e293a1f9e611e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![400 Bad Request](http://upload-images.jianshu.io/upload_images/2993097-45924ca6923b9b64.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![正确的@PathVariable语法 @PathVariable("parameter")](http://upload-images.jianshu.io/upload_images/2993097-fa425ff151157851.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
3. logback和slf4j整合出错导致无法正常打印日志信息
   ![logback报错信息](http://upload-images.jianshu.io/upload_images/2993097-9a9bff791e3127fc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
   在查看了官方手册之后发现原来logback和slf4j在整合的过程中应该注意版本的问题
   ![Logback-classic version 1.1.4 and later require slf4j-api version 1.7.15 or later.](http://upload-images.jianshu.io/upload_images/2993097-ff0ce1659bf47723.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![在pom.xml引入slf4j和logback的依赖时应该注意版本问题](http://upload-images.jianshu.io/upload_images/2993097-a708eb0b6576e304.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
