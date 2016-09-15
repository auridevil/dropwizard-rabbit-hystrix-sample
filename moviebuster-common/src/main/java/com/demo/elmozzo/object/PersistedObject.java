package com.demo.elmozzo.object;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.Objects;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class PersistedObject, with the common getter and setters.
 */
public abstract class PersistedObject implements Serializable, IPersistedObject {

	/** The Constant serialVersionUID. To-override */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new persisted object.
	 */
	public PersistedObject() {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof IPersistedObject)) {
			return false;
		} else if (this.getClass() != o.getClass()) {
			return false;
		} else {
			final IPersistedObject that = (IPersistedObject) o;
			// compare members values
			try {
				final PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(this.getClass(), Object.class).getPropertyDescriptors();
				for (final PropertyDescriptor descriptor : propertyDescriptors) {
					// cycle all descriptor of the classes
					final Object descriptorValueThis = descriptor.getReadMethod().invoke(this);
					final Object descriptorValueThat = descriptor.getReadMethod().invoke(that);
					if (!Objects.equals(descriptorValueThis, descriptorValueThat)) {
						// at the first different couple of values return
						return false;
					}
				}
				return true;
				// throw up
			} catch (final IntrospectionException e) {
				throw new IllegalStateException(e);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			} catch (final InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.IPersistedObject#getId()
	 */
	@Override
	public long getId() {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.IPersistedObject#getInsertDate()
	 */
	@Override
	public Date getInsertDate() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.IPersistedObject#getUpdateDate()
	 */
	@Override
	public Date getUpdateDate() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.getId(), this.getInsertDate(), this.getUpdateDate());
	}

	/**
	 * Merge from a source object. If source object has a new value, it is updated
	 * in the local
	 *
	 * @param source
	 *          the source object
	 * @return the movie merged (this)
	 */
	public IPersistedObject mergeFrom(IPersistedObject source) {
		if (source == null) {
			// no source no merge
			return this;
		} else if (this.getClass() != source.getClass()) {
			// different pojo classes, no merge
			throw new IllegalArgumentException();
		} else {
			try {
				// copy values from source using introspectors
				final PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(this.getClass(), Object.class).getPropertyDescriptors();
				for (final PropertyDescriptor descriptor : propertyDescriptors) {
					if (descriptor.getName().equals("id") == false) {
						final Object descriptorValue = descriptor.getReadMethod().invoke(source);
						if (descriptorValue != null) {
							// if the source has a value for the descriptor, update on this
							descriptor.getWriteMethod().invoke(this, descriptorValue);
						}
					}
				}
			} catch (final IntrospectionException e) {
				throw new IllegalStateException(e);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			} catch (final InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
			return this;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.demo.elmozzo.object.IPersistedObject#setId(long)
	 */
	@Override
	public void setId(long id) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.demo.elmozzo.object.IPersistedObject#setInsertDate(java.sql.Date)
	 */
	@Override
	public void setInsertDate(Date insertDate) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.demo.elmozzo.object.IPersistedObject#setUpdateDate(java.sql.Date)
	 */
	@Override
	public void setUpdateDate(Date updateDate) {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final String serialized = mapper.writeValueAsString(this);
			return serialized;
		} catch (final Exception ex) {
			LoggerFactory.getLogger(this.getClass()).error("Error in serializing:", ex);
			return ex.getMessage();
		}
	}

}
