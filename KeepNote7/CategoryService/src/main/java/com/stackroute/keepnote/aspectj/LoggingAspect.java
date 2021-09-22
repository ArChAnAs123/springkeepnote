package com.stackroute.keepnote.aspectj;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
/* Annotate this class with @Aspect and @Component */
@Aspect
@Component
public class LoggingAspect {
	private static Logger log = LoggerFactory.getLogger(LoggingAspect.class);
	/*
	 * Write loggers for each of the methods of Category controller, any particular method
	 * will have all the four aspectJ annotation
	 * (@Before, @After, @AfterReturning, @AfterThrowing).
	 */
	
	@Before("within(com.stackroute.keepnote..*)")
	public void logBefore(JoinPoint joinpoint) {
		log.info("Before execution of CategoryService Advice : "+joinpoint.getSignature().getName());
	}
	
	@After("within(com.stackroute.keepnote..*)")
	public void logAfter(JoinPoint joinpoint) {
		log.info("After execution of CategoryService Advice : "+joinpoint.getSignature().getName());
	}
	
	@AfterReturning(pointcut="within(com.stackroute.keepnote..*)", returning="result")
	public void logAfterReturning(JoinPoint joinPoint, Object result) {
		log.info("Execution of After Returning CategoryService Advice : "+joinPoint.getSignature().getName()+" : "+result);
	}
	
	@AfterThrowing(pointcut="within(com.stackroute.keepnote..*)", throwing="error")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
		log.info("Execution of After throwing CategoryService Advice : "+joinPoint.getSignature().getName()+" : "+error);
	}
}