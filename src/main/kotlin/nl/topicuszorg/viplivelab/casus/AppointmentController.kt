package nl.topicuszorg.viplivelab.casus

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries.localTime

@RestController
@RequestMapping("/api/v1/appointment")
class AppointmentController {

    var dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    val appointments =
        mutableListOf(
            Appointment(
                LocalDate.parse("16-03-2026", dateFormatter),
                LocalTime.parse("17:00:00"),
                60))

    // /all - All appointments
    @GetMapping("/all")
    fun getAll() : MutableList<Appointment> {
        return appointments
    }
    // /{date} - All from Date
    @GetMapping("/{date}")
    fun getAllFromDate(@PathVariable("date") date: String) : List<Appointment> {

        val filteredAppointments = appointments.filter { appointment -> appointment.date == LocalDate.parse(date, dateFormatter) }

        if(filteredAppointments.isEmpty()) throw ResponseStatusException(HttpStatus.NOT_FOUND)

        return filteredAppointments;
    }
    // /next - next from current time
    @GetMapping("/next")
    fun getNext() : Appointment {
        return appointments.firstOrNull { appointment -> appointment.date >= LocalDate.now() && appointment.startTime >= LocalTime.now() } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }


    // /open/next
    @GetMapping("/open/next")
    fun getNextOpenTime(@RequestBody request: AppointmentDurationDto){
        //Return next open spot for appointment
    }
    // /open/{date}
    @GetMapping("/open/{date}")
    fun getNextOpenTimeOnDate (@PathVariable("date") date: String){
        //Return next appointment
    }

    //@PostMapping()
    //Create new appointment
    @GetMapping()
    fun createNewAppointment (@RequestBody request: Appointment){
        appointments.add(request);
    }

    //@PutMapping()
    // /{startTime}
    @PutMapping("/{startTime}")
    fun updateAppointment(@PathVariable("startTime") startTime: String, @RequestBody request: Appointment){
        val appointmentToEdit = appointments.indexOfFirst { appointment -> appointment.startTime == LocalTime.parse(startTime) }

        appointments[appointmentToEdit] = request;
    }
    //@DeleteMapping()
    // /{startTime}

}

data class AppointmentDurationDto(
    val duration: Int
)