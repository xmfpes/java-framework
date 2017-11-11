package core.nmvc;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.reflections.ReflectionUtils;

import com.google.common.collect.Maps;

import core.annotation.ComponentScan;
import core.annotation.RequestMapping;
import core.annotation.RequestMethod;
import next.controller.LegacyController;

@ComponentScan({"next.controller","core.nmvc"})
public class AnnotationHandlerMapping implements HandlerMapping {
	private Object[] basePackage;
	
	private Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

	public AnnotationHandlerMapping() {
		ComponentScan cs = this.getClass().getAnnotation(ComponentScan.class);
		this.basePackage = cs.value();
	}

	@SuppressWarnings("unchecked")
	public void initialize() {
		ControllerScanner controllerScanner = new ControllerScanner(basePackage);
		controllerScanner.getControllerKeySet().stream()
		.forEach(clazz -> {
			ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(RequestMapping.class))
			.forEach(classMethod -> {
				RequestMapping rm = classMethod.getAnnotation(RequestMapping.class);
				handlerExecutions.put(new HandlerKey(rm.value(), rm.method()), new HandlerExecution(clazz, classMethod));
			});
		});
	}

	public Optional<LegacyController> getHandler(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		RequestMethod rm = RequestMethod.valueOf(request.getMethod().toUpperCase());
		return Optional.ofNullable(handlerExecutions.get(new HandlerKey(requestUri, rm)));
	}
}
