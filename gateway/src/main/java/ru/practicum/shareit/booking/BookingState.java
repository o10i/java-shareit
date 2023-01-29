package ru.practicum.shareit.booking;

public enum BookingState {
	// Все
	ALL,
	// Текущие
	CURRENT,
	// Будущие
	FUTURE,
	// Завершенные
	PAST,
	// Отклоненные
	REJECTED,
	// Ожидающие подтверждения
	WAITING;

	public static BookingState getBookingState(String stringState) {
		for (BookingState state : values()) {
			if (state.name().equalsIgnoreCase(stringState)) {
				return state;
			}
		}
		throw new IllegalArgumentException("Unknown state: " + stringState);
	}
}