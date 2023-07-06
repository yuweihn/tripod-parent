package com.yuweix.tripod.sequence.base;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.yuweix.tripod.sequence.dao.SequenceDao;
import com.yuweix.tripod.sequence.exception.SequenceException;

import com.yuweix.tripod.sequence.utils.FieldUtil;
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
	public SequenceBeanFactory(String sequenceClzName) {
		this(sequenceClzName, null);
	}
	public SequenceBeanFactory(Class<? extends AbstractSequence> sequenceClz, List<Property> constructArgList) {
		this(sequenceClz.getName(), constructArgList);
	}
	/**
	 * @param sequenceClzName                         准备实例化的Sequence实现类
	 * @param constructArgList                        Sequence实现类的构造函数参数序列
	 */
	public SequenceBeanFactory(String sequenceClzName, List<Property> constructArgList) {
		this.sequenceClz = forName(sequenceClzName);
		this.constructArgList = constructArgList;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends AbstractSequence> forName(String clzName) {
		try {
			Class<?> clz = Class.forName(clzName);
			if (!AbstractSequence.class.isAssignableFrom(clz)) {
				throw new SequenceException("[" + clzName + "] is not the subclass of " + AbstractSequence.class.getName() + ".");
			}
			return (Class<? extends AbstractSequence>) clz;
		} catch (ClassNotFoundException e) {
			throw new SequenceException(e);
		}
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
		if (bean instanceof BeanMap) {
			registerBeans(this.sequenceClz, (BeanMap) bean);
			done = true;
		} else if (!done) {
			beanFactory.getBean(BeanMap.class);
		}
		return bean;
	}

	/**
	 * 注册一系列[Sequence Bean]
	 */
	private void registerBeans(Class<? extends AbstractSequence> defaultClz, Map<String, String> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		Field defaultSeqDaoField = checkSequenceDao(defaultClz);
		Field defaultSeqNameField = checkSequenceName(defaultClz);
		Field defaultSeqMinValField = checkSequenceMinValue(defaultClz);
		String[] seqDaoBeanNames = beanFactory.getBeanNamesForType(SequenceDao.class);
		if (seqDaoBeanNames.length != 1) {
			throw new SequenceException("[SequenceDao] not found in spring context.");
		}
		String seqDaoBeanName = seqDaoBeanNames[0];

		for (Entry<String, String> entry : beans.entrySet()) {
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
			long minValue = 0;
			if (arr.length >= 2) {
				try {
					minValue = Long.parseLong(arr[1]);
				} catch (Exception ignored) {}
			}

			Class<? extends AbstractSequence> clz = null;
			Field seqDaoField = null;
			Field seqNameField = null;
			Field seqMinValField = null;
			if (arr.length >= 3) {
				clz = forName(arr[2]);
				seqDaoField = checkSequenceDao(clz);
				seqNameField = checkSequenceName(clz);
				seqMinValField = checkSequenceMinValue(clz);
			} else {
				clz = defaultClz;
				seqDaoField = defaultSeqDaoField;
				seqNameField = defaultSeqNameField;
				seqMinValField = defaultSeqMinValField;
			}

			List<Property> propList = new ArrayList<>();
			propList.add(new Property(seqDaoField.getName(), seqDaoBeanName, Property.TYPE_REFERENCE));
			propList.add(new Property(seqNameField.getName(), seqName, Property.TYPE_VALUE));
			propList.add(new Property(seqMinValField.getName(), minValue, Property.TYPE_VALUE));
			registerBean(clz, beanName, this.constructArgList, propList);
		}
	}
	private void registerBean(Class<? extends AbstractSequence> clz, String beanName, List<Property> constArgList, List<Property> propList) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clz);
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
	 * 检查注解：{@link SeqField#isDao()}
	 */
	private Field checkSequenceDao(Class<? extends AbstractSequence> clz) {
		List<Field> fields = FieldUtil.getAllFieldsList(clz);
		if (fields == null || fields.size() <= 0) {
			throw new SequenceException("Field [sequenceDao] not Found.");
		}
		for (Field f: fields) {
			SeqField seqField = f.getAnnotation(SeqField.class);
			if (seqField != null && seqField.isDao()) {
				return f;
			}
		}
		throw new SequenceException("Field [sequenceDao] not Found.");
	}
	/**
	 * 检查注解：{@link SeqField#isName()}
	 */
	private Field checkSequenceName(Class<? extends AbstractSequence> clz) {
		List<Field> fields = FieldUtil.getAllFieldsList(clz);
		if (fields == null || fields.size() <= 0) {
			throw new SequenceException("Field [name] not Found.");
		}
		for (Field f: fields) {
			SeqField seqField = f.getAnnotation(SeqField.class);
			if (seqField != null && seqField.isName()) {
				return f;
			}
		}
		throw new SequenceException("Field [name] not Found.");
	}
	/**
	 * 检查注解：{@link SeqField#isMinValue()}
	 */
	private Field checkSequenceMinValue(Class<? extends AbstractSequence> clz) {
		List<Field> fields = FieldUtil.getAllFieldsList(clz);
		if (fields == null || fields.size() <= 0) {
			throw new SequenceException("Field [minValue] not Found.");
		}
		for (Field f: fields) {
			SeqField seqField = f.getAnnotation(SeqField.class);
			if (seqField != null && seqField.isMinValue()) {
				return f;
			}
		}
		throw new SequenceException("Field [minValue] not Found.");
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
