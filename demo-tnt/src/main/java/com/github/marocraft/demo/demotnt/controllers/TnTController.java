package com.github.marocraft.demo.demotnt.controllers;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.marocraft.trackntrace.annotation.Trace;
import com.github.marocraft.trackntrace.domain.LogLevel;

@RestController
public class TnTController {
	@Trace(level = LogLevel.INFO,message = "get Hello TnT controller")
	@GetMapping("/hello")
	public String getHelloTnT() {
		logpath();
		return "hello tnt";
	}
	public void logpath() {
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeMxBean.getInputArguments();
		System.out.println("all args");
		for (String args : arguments) {
			System.out.println("args: "+args);
		}
		//arguments.forEach(System.out::println);
	}

}
