CLASSPATH=./bin
CLASSPATH=$CLASSPATH:./lib/activeobjects/activeobjects-0.8.2.jar
CLASSPATH=$CLASSPATH:./lib/apache-commons/commons-beanutils-1.8.0.jar
CLASSPATH=$CLASSPATH:./lib/apache-commons/commons-collections-3.2.1.jar
CLASSPATH=$CLASSPATH:./lib/apache-commons/commons-logging-1.1.1.jar
CLASSPATH=$CLASSPATH:./lib/c3p0/c3p0-0.9.1.2.jar
CLASSPATH=$CLASSPATH:./lib/derby/derby.jar
CLASSPATH=$CLASSPATH:./lib/derby/derbyLocale_zh_CN.jar
CLASSPATH=$CLASSPATH:./lib/jetty/jetty-6.1.14.jar
CLASSPATH=$CLASSPATH:./lib/jetty/jetty-util-6.1.14.jar
CLASSPATH=$CLASSPATH:./lib/jetty/servlet-api-2.5-6.1.14.jar
CLASSPATH=$CLASSPATH:./lib/log4j/log4j-1.2.15.jar
CLASSPATH=$CLASSPATH:./lib/navel/navel-3.0-src.jar
CLASSPATH=$CLASSPATH:./lib/navel/navel-3.0.jar
CLASSPATH=$CLASSPATH:./lib/zk/zcommon.jar
CLASSPATH=$CLASSPATH:./lib/zk/zcommons-el.jar
CLASSPATH=$CLASSPATH:./lib/zk/zhtml.jar
CLASSPATH=$CLASSPATH:./lib/zk/zk.jar
CLASSPATH=$CLASSPATH:./lib/zk/zkex.jar
CLASSPATH=$CLASSPATH:./lib/zk/zkplus.jar
CLASSPATH=$CLASSPATH:./lib/zk/zul.jar
CLASSPATH=$CLASSPATH:./lib/zk/zweb.jar
CLASSPATH=$CLASSPATH:./lib/zk/ext/bsh.jar
CLASSPATH=$CLASSPATH:./lib/zk/ext/commons-fileupload.jar
CLASSPATH=$CLASSPATH:./lib/zk/ext/commons-io.jar
CLASSPATH=$CLASSPATH:./lib/jasperreports/jasperreports-2.0.5.jar

echo $CLASSPATH

../jre1.6.0_13/bin/java -cp $CLASSPATH net.thinkbase.tunxi.Main