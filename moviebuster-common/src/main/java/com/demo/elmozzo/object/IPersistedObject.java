package com.demo.elmozzo.object;

import java.sql.Date;

/**
 * The Interface IPersistedObject.
 */
public interface IPersistedObject {

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId();

	/**
	 * Gets the insert date.
	 *
	 * @return the insert date
	 */
	public Date getInsertDate();

	/**
	 * Gets the update date.
	 *
	 * @return the update date
	 */
	public Date getUpdateDate();

	/**
	 * Sets the id.
	 *
	 * @param id
	 *          the new id
	 */
	public void setId(long id);

	/**
	 * Sets the insert date.
	 *
	 * @param insertDate
	 *          the new insert date
	 */
	public void setInsertDate(Date insertDate);

	/**
	 * Sets the update date.
	 *
	 * @param updateDate
	 *          the new update date
	 */
	public void setUpdateDate(Date updateDate);

}
