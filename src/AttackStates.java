
public enum AttackStates {
	/**
	 * when an attack lands in open water and registers as a light-blue circle
	 */
	MISS,
	/**
	 * when the player attempts to attack out of the board
	 */
	OUT_OF_BOUNDS,
	/**
	 * when the player attempts to attack a spot that was already hit but ship not destroyed
	 */
	ALREADY_HIT,
	/**
	 * ALREADY_HIT, except if the ship with the hit is destroyed
	 * @see ALREADY_HIT
	 */
	ALREADY_HIT_AND_DESTROYED,
	/***
	 * when the player tries to attack a spot registered as MISS
	 * @see MISS
	 */
	ALREADY_MISS,
	/**
	 * when the player sucessfully hits a location containing a part of a ship that has not been attacked before
	 */
	HIT,
	/**
	 * HIT but the attacked ship was destroyed
	 * @see HIT
	 */
	HIT_AND_DESTROYED,
	/**
	 * when the player attempts to attack themself, or a defeated player
	 */
	WRONG_TARGET,
	/**
	 * default state if something went wrong
	 */
	ERROR;
}
