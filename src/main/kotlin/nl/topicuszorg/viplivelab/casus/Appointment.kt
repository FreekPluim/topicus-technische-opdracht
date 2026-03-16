package nl.topicuszorg.viplivelab.casus

import tools.jackson.databind.util.UniqueId
import java.time.LocalDate
import java.time.LocalTime

data class Appointment(
    var date : LocalDate,
    var startTime: LocalTime,
    var duration: Int,
    var location : String? = null
)
