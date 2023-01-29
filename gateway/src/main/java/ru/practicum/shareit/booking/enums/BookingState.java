package ru.practicum.shareit.booking.enums;

public enum BookingState {
	ALL, CURRENT, FUTURE, PAST, REJECTED, WAITING;

	public static BookingState getBookingState(String stringState) {
		for (BookingState state : values()) {
			if (state.name().equalsIgnoreCase(stringState)) {
				return state;
			}
		}
		throw new IllegalArgumentException("Unknown state: " + stringState);
	}
}