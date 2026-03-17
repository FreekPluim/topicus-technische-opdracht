package nl.topicuszorg.viplivelab.casus.repository

import nl.topicuszorg.viplivelab.casus.Appointment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalTime


public interface AgendaRepository : JpaRepository<Appointment, Long> {
    fun findAllByDate(date : LocalDate) : Iterable<Appointment>?

    @Query("""
    SELECT a
    FROM Appointment a
    WHERE (a.date >= :date AND a.startTime >= :startTime)
    ORDER BY a.date ASC, a.startTime ASC
""")
    fun findNextAppointment(@Param("date") date: LocalDate, @Param("startTime") startTime: LocalTime) : List<Appointment>?

    @Query("""SELECT a FROM Appointment a 
        Where a.date = :date
        AND (((:startTime BETWEEN a.startTime AND a.endTime) OR (:endTime BETWEEN a.startTime AND a.endTime) 
        OR ((a.startTime BETWEEN :startTime AND :endTime) OR (a.endTime BETWEEN :startTime AND :endTime))))""")
    fun findOverlappingAppointments(date: LocalDate, startTime: LocalTime, endTime: LocalTime) : Iterable<Appointment>?

    fun findByStartTimeBetweenOrderByDateAscStartTimeAsc(startTime: LocalTime, endTime: LocalTime) : List<Appointment>
}