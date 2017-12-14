package com.lynn.config;

import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class MybatisConfig {

    @Bean
    public BasicDataSource dataSource() {
        //获取当前操作系统(servers_os为服务器设置的属性JAVA_OPTS=%JAVA_OPTS% -Dservers_os=online84)
        String servers_os = System.getProperty("servers_os") == null ? "" : System.getProperty("servers_os");

        /** 
         * 连接池配置解释：(详细说明见：http://www.zhimengzhe.com/shujuku/other/320389.html)
         * 
         * 配置timeBetweenEvictionRunsMillis = "30000"后，每30秒运行一次空闲连接回收器（独立线程）。并每次检查3个连接，如果连接空闲时间超过30分钟就销毁。
         * 销毁连接后，连接数量就少了，如果小于minIdle数量，就新建连接，维护数量不少于minIdle，过行了新老更替
         * 
         * testWhileIdle = "true" 表示每30秒，取出3条连接，使用validationQuery = "SELECT 1" 中的SQL进行测试 ，测试不成功就销毁连接。销毁连接后，连接数量就少了，如果小于minIdle数量，就新建连接。
         * 
         * testOnBorrow = "false" 一定要配置，因为它的默认值是true。false表示每次从连接池中取出连接时，不需要执行validationQuery = "SELECT 1" 中的SQL进行测试。
         * 若配置为true,对性能有非常大的影响，性能会下降7-10倍。所在一定要配置为false.
         * 
         * 每30秒，取出numTestsPerEvictionRun条连接（本例是3，也是默认值），发出"SELECT 1" SQL语句进行测试 ，测试过的连接不算是“被使用”了，还算是空闲的。连接空闲30分钟后会被销毁。
         */
        
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setInitialSize(5);//设置数据库的初始连接数
        dataSource.setMaxIdle(80);//最大空闲连接数 (maxIdle值与maxActive值应配置的接近。)
        dataSource.setMaxTotal(100);//最大连接数
        dataSource.setMinIdle(10);//设置数据库中最少有10个空闲连接，在数据库连接池中保存的最少的空闲连接的数量
        dataSource.setMaxWaitMillis(1000*6);//获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常,小于零:阻塞不确定的时间,默认-1
        dataSource.setValidationQuery("SELECT 1");//验证连接是否可用，使用的SQL语句
        dataSource.setTestWhileIdle(true);//指明连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除
        dataSource.setTestOnBorrow(false);//借出连接时不要测试，否则很影响性能
        dataSource.setTimeBetweenEvictionRunsMillis(30000);//每30秒运行一次空闲连接回收器
        dataSource.setMinEvictableIdleTimeMillis(1800000);//池中的连接空闲30分钟后被回收,默认值就是30分钟
        dataSource.setNumTestsPerEvictionRun(3);//在每次空闲连接回收器线程(如果有)运行时检查的连接数量，默认值就是3.
        
        if ("online84".equals(servers_os.toLowerCase())) {
            
        } 
        else {
            dataSource.setUsername("bdm274886599");
            dataSource.setPassword("911huqi911");
            dataSource.setUrl("jdbc:mysql://bdm274886599.my3w.com/bdm274886599_db?useUnicode=true&amp;characterEncoding=utf-8");
        }
        
        return dataSource;
    }
    
    @Bean
    MapperScannerConfigurer mpperScannnerConfigurer() {
        MapperScannerConfigurer msc = new MapperScannerConfigurer();
        msc.setSqlSessionFactoryBeanName("sqlSessionFactory");
        msc.setBasePackage("com.lynn.mapper");
        return msc;
    }

    @Bean(name = "sqlSessionFactory")
    SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) { 
        SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
        ssfb.setDataSource(dataSource); 
        ssfb.setTypeAliasesPackage("com.lynn.bean");
        return ssfb;
    }

    @Bean
    PlatformTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

}
