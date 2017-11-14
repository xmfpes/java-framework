package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import core.exception.BeanCreationException;

public class BeanFactory {
	private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);

	private Set<Class<?>> preInstanticateBeans;

	private Map<Class<?>, Object> beans = Maps.newHashMap();

	public BeanFactory(Set<Class<?>> preInstanticateBeans) {
		this.preInstanticateBeans = preInstanticateBeans;
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> requiredType) {
		return (T) beans.get(requiredType);
	}

	public void initialize() {
		preInstanticateBeans.stream().forEach(c -> {
			log.debug("clazz " + c);
			if(!beans.containsKey(c)) {
				beans.put(c, instanticate(c));
			}
		});
	}
	public Object instanticate(Class<?> clazz) {
		Object obj = beans.get(clazz);
		log.debug("incate " + obj);
		return beans.containsKey(clazz) ? beans.get(clazz) : instantiateClass(clazz) ;
	}
	
	private Object instantiateClass(Class<?> clazz) {
		Class<?> convertClazz = BeanFactoryUtils.findConcreteClass(clazz, preInstanticateBeans);
		Optional<Constructor<?>> cons = Optional.ofNullable(BeanFactoryUtils.getInjectedConstructor(convertClazz));

		try {
			log.debug("op " + cons);
			log.debug("c;a" + convertClazz);
			
			Object obj = cons.isPresent() ? instantiateConstructor(cons.get()) : convertClazz.newInstance();	
			log.debug("get object " + obj);
			return obj;
		} catch (Exception e) {
			throw new BeanCreationException();
		}
    }
	
	private Object instantiateConstructor(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        List<Object> args = Lists.newArrayList();
        for (Class<?> clazz : parameterTypes) {
        		Object bean = instanticate(clazz);
            args.add(bean);
        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
    }
}
