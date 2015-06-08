package com.dataart.android.devicehive.device;

import com.dataart.android.devicehive.Notification;

import java.util.HashMap;

/**
 * Represents a {@link Notification} which is usually sent by an
 * {@link Equipment} to update its state.
 */
public class EquipmentNotification extends Notification {

	/**
	 * Construct a notification with given equipment code and additional
	 * equipment parameters dictionary.
	 * 
	 * @param equipmentCode
	 *            Equipment code.
	 * @param parameters
	 *            Equipment parameters dictionary.
	 */
	public EquipmentNotification(String equipmentCode,
			HashMap<String, Object> parameters) {
		super(equipmentCode, equipmentParameters(equipmentCode, parameters));
	}

	private static HashMap<String, Object> equipmentParameters(
			String equipmentCode, HashMap<String, Object> parameters) {
		final HashMap<String, Object> equipmentParameters = new HashMap<String, Object>(
				parameters);
		parameters.put("equipment", equipmentCode);
		return equipmentParameters;
	}
}
