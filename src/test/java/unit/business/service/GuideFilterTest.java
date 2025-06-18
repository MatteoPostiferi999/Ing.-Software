package unit.business.service;

import business.service.GuideFilter;
import model.trip.Trip;
import model.user.Guide;
import model.user.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuideFilterTest {

    private Guide guide;
    private Trip trip1;
    private Trip trip2;
    private GuideFilter filter;
    private LocalDate today;
    private Set<Skill> guideSkills;
    private List<Skill> tripSkills;

    @BeforeEach
    void setUp() {
        guide = mock(Guide.class);
        trip1 = mock(Trip.class);
        trip2 = mock(Trip.class);

        today = LocalDate.now();

        guideSkills = new HashSet<>(Arrays.asList(
                new Skill(1, "Arrampicata", "Abilità di arrampicata"),
                new Skill(2, "Nuoto", "Abilità di nuoto")
        ));

        tripSkills = Arrays.asList(
                new Skill(1, "Arrampicata", "Abilità di arrampicata")
        );

        filter = new GuideFilter(guide, null, null);
    }

    @Test
    void testFilterTripsWithMatchingSkills() {
        // Arrange
        when(guide.getSkills()).thenReturn(guideSkills);
        when(trip1.getRequiredSkills()).thenReturn(tripSkills);
        when(trip1.getDate()).thenReturn(today);

        // Act
        List<Trip> result = filter.filterTrips(List.of(trip1));

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(trip1));
    }

    @Test
    void testFilterTripsWithNoMatchingSkills() {
        // Arrange
        Set<Skill> nonMatchingSkills = new HashSet<>(List.of(
                new Skill(3, "Corsa", "Abilità di corsa")
        ));
        when(guide.getSkills()).thenReturn(nonMatchingSkills);
        when(trip1.getRequiredSkills()).thenReturn(tripSkills);
        when(trip1.getDate()).thenReturn(today);

        // Act
        List<Trip> result = filter.filterTrips(List.of(trip1));

        // Assert
        assertTrue(result.isEmpty());
    }

    // ... resto dei test invariato ...
}