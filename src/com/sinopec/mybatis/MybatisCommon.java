package com.sinopec.mybatis;

import com.sinopec.bean.ComputeNodeStatusBean;
import com.sinopec.dao.MsgDao;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-29 Time: 下午1:21 To
 * change this template use File | Settings | File Templates.
 */
public class MybatisCommon {

	// private static String resource =
	// "/opt/CRMS/sd/conf/MyBatis-Configuration.xml";

	public static String resource = "MyBatis-Configuration.xml";

	public static Reader reader;

	public static SqlSession session;

	public static MsgDao msgDao;

	static {
		try {
			reader = Resources.getResourceAsReader(resource);
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		SqlSessionFactory factory = builder.build(reader);
		session = factory.openSession();
		msgDao = session.getMapper(MsgDao.class);
	}

	// static {
	// InputStream is =
	// MybatisCommon.class.getClassLoader().getResourceAsStream(resource);
	// SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
	// session = factory.openSession();
	// msgDao = session.getMapper(MsgDao.class);
	// }

	public static int insertComputeNodeStatus(ComputeNodeStatusBean computeNodeStatusBean) {
		return msgDao.insertComputeNodeStatus(computeNodeStatusBean);
	}

	public static int updateComputeNodeStatus(String nodeName, ComputeNodeStatusBean computeNodeStatusBean) {
		return msgDao.updateComputeNodeStatus(nodeName, computeNodeStatusBean);
	}

	public static void commitSession() {
		session.commit();
	}

	public static void createSession() {

		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		SqlSessionFactory factory = builder.build(reader);
		session = factory.openSession();
	}

	public static void closeSession() {
		if (session != null)
			session.close();
	}
}
