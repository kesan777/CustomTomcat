package com.kesan777.sessionmanager;


import org.apache.catalina.Session;
import org.apache.catalina.session.ManagerBase;
import redis.clients.jedis.Jedis;

import java.io.IOException;

/**
 * @author kesan777
 */
public class RedisSessionManager extends ManagerBase {

	@Override
	public Session findSession(String id) throws IOException {
		Session session = this.sessions.get(id);
		if( session != null ){
			return session;
		}

		return null;
	}




	public void load() throws ClassNotFoundException, IOException {

	}

	public void unload() throws IOException {

	}
}
