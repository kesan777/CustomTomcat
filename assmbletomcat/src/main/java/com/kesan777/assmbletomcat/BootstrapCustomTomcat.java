package com.kesan777.assmbletomcat;


import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.File;


public class BootstrapCustomTomcat {

	public static void main(String[] args) throws LifecycleException {

		int port = 8080;

		if(args.length == 1){
			try {
				port = Integer.parseInt(args[0]);
			}catch (NumberFormatException ex){
				System.out.println("错误的端口号，将使用默认的端口");
			}
		}


		String docBase = "d:"+ File.separator + "asia_movie";

		Tomcat tomcat = new Tomcat();

		//创建用于处理连接和I/O事件的Connector
		configureConnector(tomcat, port);

		// 初始化Engine,使用默认的Engine
		Engine engine  = tomcat.getEngine();



		// 使用默认的Host
		Host host = tomcat.getHost();
		host.setAutoDeploy(true);

		// 设置默认host
		engine.setDefaultHost("localhost");



		// 配置Context
		Context context = new StandardContext();
		context.setName("");
		context.setDocBase(docBase);
		context.setPath("");
		context.setDisplayName("Kesan");

		// 必须配置生命周期监听器否则无法启动Tomcat,以下是Tomcat文档中对其的注释
		/*
			Fix startup sequence - required if you don't use web.xml.
			The start() method in context will set 'configured' to false - and expects a listener to set it back to true.
		 	简单的来说这是必须步骤，如果你使用了嵌入式的Tomcat容器就必须注册此监听器
		 	此监听器会调用context.setConfigured(true)将Context设置为已设置
		 	并向Context的pipeline中添加NonLoginAuthenticator
		 */
		context.addLifecycleListener(new Tomcat.FixContextListener());


		host.addChild(context);

		// 添加Servlet到Context中，因此需要
		Wrapper wrapper = context.createWrapper();
		wrapper.setName("test");
		wrapper.setServlet(new SimpleServlet());



		context.addChild(wrapper);
		context.addServletMappingDecoded("/test", "test");


		tomcat.start();

		tomcat.getServer().await();
	}

	private static void configureConnector(Tomcat tomcat,int port) {
		// 默认使用 org.apache.coyote.http11.Http11NioProtocol
		// 即创建一个基于JavaNIO且支持HTTP1.1协议的Connector
		Connector connector = new Connector();
		connector.setPort(port);

		// 为默认的Service配置Connector
		tomcat.getService().addConnector(connector);
	}

	public static class SimpleServlet extends HttpServlet {

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.getWriter().println("Deep dark tomcat");
		}

	}
}
