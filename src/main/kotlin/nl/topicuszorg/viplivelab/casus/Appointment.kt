package nl.topicuszorg.viplivelab.casus

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "appointment")
public class Appointment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var date : LocalDate,
    var startTime: LocalTime,
    var duration: Int,
    var endTime: LocalTime = startTime.plusMinutes(duration.toLong()),
    var location : String? = null
)
