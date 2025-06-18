package unit.business.service;

import business.service.GuideFilter;
import model.trip.Trip;
import model.user.Guide;
import model.user.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuideFilterTest {

    private Guide guide;
    private GuideFilter filter;
    private LocalDate today;
    private List<Skill> guideSkills;
    private List<Skill> nonMatchingSkills;

    @BeforeEach
    void setUp() {
        guide = mock(Guide.class);
        today = LocalDate.of(2025, 6, 18);
        // guide con due skill
        guideSkills = List.of(
                new Skill(1, "Arrampicata", "Abilità di arrampicata"),
                new Skill(2, "Nuoto",       "Abilità di nuoto")
        );
        nonMatchingSkills = List.of(
                new Skill(3, "Corsa", "Abilità di corsa")
        );
        filter = new GuideFilter(guide, null, null);
    }

    @Test
    void shouldReturnEmptyListWhenNoTripsProvided() {
        when(guide.getSkills()).thenReturn(guideSkills);
        List<Trip> result = filter.filterTrips(Collections.emptyList());
        assertTrue(result.isEmpty(), "Empty input should yield empty output");
    }

    @Test
    void shouldThrowWhenNullListProvided() {
        when(guide.getSkills()).thenReturn(guideSkills);
        assertThrows(NullPointerException.class, () -> filter.filterTrips(null));
    }

    @Test
    void shouldFilterOutTripsBeforeMinDate() {
        filter.setMinDate(today);
        Trip before = mock(Trip.class);
        Trip onDate = mock(Trip.class);
        Trip after = mock(Trip.class);

        when(guide.getSkills()).thenReturn(Collections.emptyList());
        // tutte le richieste di skill vuote → passano
        when(before.getRequiredSkills()).thenReturn(Collections.emptyList());
        when(onDate .getRequiredSkills()).thenReturn(Collections.emptyList());
        when(after .getRequiredSkills()).thenReturn(Collections.emptyList());
        when(before.getDate()).thenReturn(today.minusDays(1));
        when(onDate .getDate()).thenReturn(today);
        when(after .getDate()).thenReturn(today.plusDays(1));

        List<Trip> result = filter.filterTrips(List.of(before, onDate, after));
        assertEquals(List.of(onDate, after), result);
    }

    @Test
    void shouldFilterOutTripsAfterMaxDate() {
        filter.setMaxDate(today);
        Trip before = mock(Trip.class);
        Trip onDate = mock(Trip.class);
        Trip after = mock(Trip.class);

        when(guide.getSkills()).thenReturn(Collections.emptyList());
        when(before.getRequiredSkills()).thenReturn(Collections.emptyList());
        when(onDate .getRequiredSkills()).thenReturn(Collections.emptyList());
        when(after .getRequiredSkills()).thenReturn(Collections.emptyList());
        when(before.getDate()).thenReturn(today.minusDays(1));
        when(onDate .getDate()).thenReturn(today);
        when(after .getDate()).thenReturn(today.plusDays(1));

        List<Trip> result = filter.filterTrips(List.of(before, onDate, after));
        assertEquals(List.of(before, onDate), result);
    }

    @Test
    void shouldFilterTripsWithinDateRange() {
        filter.setMinDate(today.minusDays(1));
        filter.setMaxDate(today.plusDays(1));
        Trip before = mock(Trip.class);
        Trip onMin  = mock(Trip.class);
        Trip onMax  = mock(Trip.class);
        Trip after  = mock(Trip.class);

        when(guide.getSkills()).thenReturn(Collections.emptyList());
        when(before.getRequiredSkills()).thenReturn(Collections.emptyList());
        when(onMin .getRequiredSkills()).thenReturn(Collections.emptyList());
        when(onMax .getRequiredSkills()).thenReturn(Collections.emptyList());
        when(after .getRequiredSkills()).thenReturn(Collections.emptyList());
        when(before.getDate()).thenReturn(today.minusDays(2));
        when(onMin .getDate()).thenReturn(today.minusDays(1));
        when(onMax .getDate()).thenReturn(today.plusDays(1));
        when(after .getDate()).thenReturn(today.plusDays(2));

        List<Trip> result = filter.filterTrips(List.of(before, onMin, onMax, after));
        assertEquals(List.of(onMin, onMax), result);
    }

    @Test
    void shouldFilterBySkillsAndDateTogether() {
        filter.setMinDate(today.minusDays(1));
        filter.setMaxDate(today.plusDays(1));

        Trip combo1 = mock(Trip.class); // matching skills & date
        Trip combo2 = mock(Trip.class); // skills match, date out
        Trip combo3 = mock(Trip.class); // skills fail, date ok

        when(guide.getSkills()).thenReturn(guideSkills);

        when(combo1.getRequiredSkills()).thenReturn(List.of(guideSkills.get(0)));
        when(combo2.getRequiredSkills()).thenReturn(List.of(guideSkills.get(0)));
        when(combo3.getRequiredSkills()).thenReturn(List.of(new Skill(9, "Altro", "…")));

        when(combo1.getDate()).thenReturn(today);
        when(combo2.getDate()).thenReturn(today.plusDays(5));
        when(combo3.getDate()).thenReturn(today);

        List<Trip> result = filter.filterTrips(List.of(combo1, combo2, combo3));
        assertEquals(1, result.size());
        assertTrue(result.contains(combo1));
    }

    @Test
    void shouldPassTripsWithNoRequiredSkillsWhenGuideHasNone() {
        when(guide.getSkills()).thenReturn(Collections.emptyList());

        Trip noReq = mock(Trip.class);
        Trip withReq = mock(Trip.class);

        when(noReq.getRequiredSkills()).thenReturn(Collections.emptyList());
        when(withReq.getRequiredSkills()).thenReturn(List.of(new Skill(7, "X", "Y")));
        when(noReq.getDate()).thenReturn(today);
        when(withReq.getDate()).thenReturn(today);

        List<Trip> result = filter.filterTrips(List.of(noReq, withReq));
        assertEquals(1, result.size());
        assertTrue(result.contains(noReq));
    }

    @Test
    void shouldThrowWhenTripHasNullRequiredSkills() {
        when(guide.getSkills()).thenReturn(Collections.emptyList());
        Trip bad = mock(Trip.class);
        when(bad.getRequiredSkills()).thenReturn(null);
        when(bad.getDate()).thenReturn(today);

        assertThrows(NullPointerException.class, () ->
                filter.filterTrips(List.of(bad))
        );
    }

    @Test
    void shouldHandleLargeTripListEfficiently() {
        when(guide.getSkills()).thenReturn(Collections.emptyList());
        Trip t = mock(Trip.class);
        when(t.getRequiredSkills()).thenReturn(Collections.emptyList());
        when(t.getDate()).thenReturn(today);

        List<Trip> bigList = IntStream.range(0, 10_000)
                .mapToObj(i -> t)
                .collect(Collectors.toList());

        List<Trip> result = filter.filterTrips(bigList);
        assertEquals(10_000, result.size());
    }

    @Test
    void shouldReflectMinDateChangeSequentially() {
        // initial: no minDate → all pass
        when(guide.getSkills()).thenReturn(Collections.emptyList());
        Trip trip = mock(Trip.class);
        when(trip.getRequiredSkills()).thenReturn(Collections.emptyList());
        when(trip.getDate()).thenReturn(today);

        List<Trip> first = filter.filterTrips(List.of(trip));
        assertEquals(1, first.size());

        // now set minDate in future → none pass
        filter.setMinDate(today.plusDays(1));
        List<Trip> second = filter.filterTrips(List.of(trip));
        assertTrue(second.isEmpty());
    }
}
