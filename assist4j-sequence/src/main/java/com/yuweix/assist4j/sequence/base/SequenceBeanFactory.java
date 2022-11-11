package com.yuweix.assist4j.sequence.base;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.yuweix.assist4j.sequence.dao.SequenceDao;
import com.yuweix.assist4j.sequence.exception.SequenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;


/**
 * 注册一系列{@link Sequence}实例到Spring容器中
 * @author yuwei
 */
public class SequenceBeanFactory implements BeanDefinitionRegistryPostProcessor, BeanPostProcessor {
	private static final Logger log = LoggerFactory.getLogger(SequenceBeanFactory.class);

	private static final String DELIMITER = ",";

	private Class<? extends AbstractSequence> sequenceClz;
	private List<Property> constructArgList;

	private ConfigurableListableBeanFactory beanFactory;
	private BeanDefinitionRegistry registry;
	private boolean done = false;


	public SequenceBeanFactory(Class<? extends AbstractSequence> sequenceClz) {
		this(sequenceClz, null);
	}

	/**
	 * @param sequenceClz                             准备实例化的Sequence实现类
	 * @param constructArgList                        Sequence实现类的构造函数参数序列
	 */
	public SequenceBeanFactory(Class<? extends AbstractSequence> sequenceClz, List<Property> constructArgList) {
		this.sequenceClz = sequenceClz;
		this.constructArgList = constructArgList;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		this.registry = registry;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof SequenceBeanHolder) {
			registerBeans(((SequenceBeanHolder) bean).getSequenceMap());
			done = true;
		} else if (!done) {
			beanFactory.getBean(SequenceBeanHolder.class);
		}
		return bean;
	}

	/**
	 * 注册一系列[Sequence Bean]
	 */
	private void registerBeans(Map<String, String> seqMap) {
		if (seqMap == null || seqMap.isEmpty()) {
			return;
		}

		Field seqDaoField = checkSequenceDao();
		Field seqNameField = checkSequenceName();
		Field seqMinValField = checkSequenceMinValue();
		String[] seqDaoBeanNames = beanFactory.getBeanNamesForType(SequenceDao.class);
		if (seqDaoBeanNames.length != 1) {
			throw new SequenceException("SequenceDao not found in spring context.");
		}
		Property seqDaoProperty = new Property(seqDaoField.getName(), seqDaoBeanNames[0], Property.TYPE_REFERENCE);

		for (Entry<String, String> entry : seqMap.entrySet()) {
			String beanName = entry.getKey();
			String seqNameValue = entry.getValue();

			if (beanName == null || "".equals(beanName.trim())) {
				continue;
			}
			beanName = beanName.trim();

			if (seqNameValue == null || "".equals(seqNameValue.trim())) {
				seqNameValue = beanName.trim();
			} else {
				seqNameValue = seqNameValue.trim();
			}

			String[] arr = seqNameValue.split(DELIMITER);
			String seqName = arr[0];
			long minValue = arr.length == 2 ? Long.parseLong(arr[1]) : 0;

			List<Property> propList = new ArrayList<>();
			propList.add(seqDaoProperty);
			propList.add(new Property(seqNameField.getName(), seqName, Property.TYPE_VALUE));
			propList.add(new Property(seqMinValField.getName(), minValue, Property.TYPE_VALUE));
			registerBean(beanName, this.constructArgList, propList);
		}
	}
	private void registerBean(String beanName, List<Property> constArgList, List<Property> propList) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(sequenceClz);
		if (constArgList != null && constArgList.size() > 0) {
			for (Property constructorArg: constArgList) {
				if (Property.TYPE_VALUE == constructorArg.getType()) {
					builder.addConstructorArgValue(constructorArg.getValue());
				} else if (Property.TYPE_REFERENCE == constructorArg.getType()) {
					builder.addConstructorArgReference(constructorArg.getValue().toString());
				} else {
					throw new RuntimeException("Error parameter [type] in constructorArgList!");
				}
			}
		}

		if (propList != null && propList.size() > 0) {
			for (Property prop: propList) {
				if (Property.TYPE_VALUE == prop.getType()) {
					builder.addPropertyValue(prop.getPropertyName(), prop.getValue());
				} else if (Property.TYPE_REFERENCE == prop.getType()) {
					builder.addPropertyReference(prop.getPropertyName(), prop.getValue().toString());
				} else {
					throw new RuntimeException("Error parameter [type] in propertyList!");
				}
			}
		}
		registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
	}

	/**
	 * 检查注解：{@link AnnSequenceDao}
	 */
	private Field checkSequenceDao() {
		Field[] fields = this.sequenceClz.getDeclaredFields();
		if (fields == null || fields.length <= 0) {
			throw new SequenceException("Field SequenceDao not Found.");
		}
		for (Field f: fields) {
			AnnSequenceDao annSeqDao = f.getAnnotation(AnnSequenceDao.class);
			if (annSeqDao != null) {
				return f;
			}
		}
		throw new SequenceException("Field SequenceDao not Found.");
	}
	/**
	 * 检查注解：{@link AnnSequenceName}
	 */
	private Field checkSequenceName() {
		Field[] fields = this.sequenceClz.getDeclaredFields();
		if (fields == null || fields.length <= 0) {
			throw new SequenceException("Field Name not Found.");
		}
		for (Field f: fields) {
			AnnSequenceName annSeqName = f.getAnnotation(AnnSequenceName.class);
			if (annSeqName != null) {
				return f;
			}
		}
		throw new SequenceException("Field Name not Found.");
	}
	/**
	 * 检查注解：{@link AnnSequenceMinValue}
	 */
	private Field checkSequenceMinValue() {
		Field[] fields = this.sequenceClz.getDeclaredFields();
		if (fields == null || fields.length <= 0) {
			throw new SequenceException("Field MinValue not Found.");
		}
		for (Field f: fields) {
			AnnSequenceMinValue annSeqMinVal = f.getAnnotation(AnnSequenceMinValue.class);
			if (annSeqMinVal != null) {
				return f;
			}
		}
		throw new SequenceException("Field MinValue not Found.");
	}

	public static class Property {
		private String propertyName;
		private Object value;
		private byte type;

		public static final byte TYPE_VALUE = 0;
		public static final byte TYPE_REFERENCE = 1;

		public Property(String propertyName, Object value, byte type) {
			this.propertyName = propertyName;
			this.value = value;
			this.type = type;
		}
		public String getPropertyName() {
			return propertyName;
		}
		public Object getValue() {
			return value;
		}
		public byte getType() {
			return type;
		}
	}
}
