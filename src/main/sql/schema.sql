#Initializing the database script(对数据库脚本进行初始化)

#Creating database(创建数据库)
CREATE DATABASE seckill;
#Using database(使用数据库)
use seckill;
#Creating seckill inventory table(创建秒杀库存表)
CREATE TABLE seckillInventory(
  `seckill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'commodity inventory id(商品库存id)',
  `name` VARCHAR(120) NOT NULL COMMENT 'commodity name(商品名称)',
  `number` INT NOT NULL COMMENT 'inventory mount(库存数量)',
  `start_time` TIMESTAMP NOT NULL COMMENT 'seckilling begin time(秒杀开始时间)',
  `end_time` TIMESTAMP NOT NULL COMMENT 'seckilling end time(秒杀结束时间)',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create time(创建时间)',
  PRIMARY KEY (seckill_id),
  key idx_start_time(start_time),
  key idx_end_time(end_time),
  key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='seckill inventory table(秒杀库存表)';

  #The initialize data(初始化数据)
INSERT INTO
  seckillInventory(name,number,start_time,end_time)
VALUES
  ('真皮钱包统统20块统统20块','100','2017-05-04 00:00:00','2017-05-07 00:00:00'),
  ('OPPO R9s今天大清仓今天大清仓','250','2017-05-04 00:00:00','2017-05-07 00:00:00'),
  ('华为P10全部免费送全部免费送','100','2017-05-04 00:00:00','2017-05-07 00:00:00'),
  ('苹果手机一律不要钱一律不要钱','100','2017-05-04 00:00:00','2017-05-07 00:00:00'),
  ('锤子手机想拿就拿想拿就拿','100','2017-05-04 00:00:00','2017-05-07 00:00:00');

#Creating a table which used to record the detail message when user has spiked successfully(秒杀成功明细表)
CREATE TABLE success_killed(
  `seckill_id` BIGINT NOT NULL COMMENT 'spike commodity id(秒杀商品id)',
  `user_phone` BIGINT NOT NULL COMMENT 'user phone number(用户手机号)',
  `state` TINYINT NOT NULL DEFAULT 0 COMMENT 'status indentification(-1:invalid,0:success,1: payment completed) 状态标识(-1:无效 0：成功 1：已经付款)',
  `create_time` TIMESTAMP NOT NULL COMMENT 'create time(创建时间)',
  PRIMARY KEY(seckill_id,user_phone), /*union primary key(联合主键)*/
  key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='spike success detail table(秒杀成功明细表)';
