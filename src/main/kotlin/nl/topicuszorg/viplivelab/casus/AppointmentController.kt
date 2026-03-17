package nl.topicuszorg.viplivelab.casus

import nl.topicuszorg.viplivelab.casus.repository.AgendaRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RestController
@RequestMapping("/api/v1/appointment")
class AppointmentController (val repository: AgendaRepository){

    var dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")


    // /all - All appointments
    @GetMapping("/all")
    fun getAll() : Iterable<Appointment> {
        return repository.findAll()
    }
    // /{date} - All from Date
    @GetMapping("/{date}")
    fun getAllFromDate(@PathVariable("date") date: String) : Iterable<Appointment>? {

        return repository.findAllByDate(LocalDate.parse(date, dateFormatter)) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Appointments not found");
    }
    // /next - next from current time
    @GetMapping("/next")
    fun getNext() : Appointment? {
        val nextAppointment = repository.findNextAppointment(LocalDate.now(), LocalTime.now());

        if(nextAppointment.isNullOrEmpty()) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");

        return nextAppointment.first();
    }


    // /open/next
    @GetMapping("/open/next")
    fun getNextOpenTime(@RequestBody() request: NextOpenAppointmentMomentValues) : NextOpenAppointmentMoment? {
        //Return next open spot for appointment
        val allAppointments = repository.findByStartTimeBetweenOrderByDateAscStartTimeAsc(request.minStartTime, request.maxStartTime)

        //Get the first possible moment if there is no other appointments
        if(allAppointments.isEmpty()) {
            if(LocalTime.now() > request.minStartTime && LocalTime.now() < request.maxStartTime) {
                return NextOpenAppointmentMoment(request.duration, LocalTime.now(), LocalDate.now())
            }
            else return NextOpenAppointmentMoment(request.duration, request.minStartTime, LocalDate.now().plusDays(1))
        }

        // Check if there is time before the very first appointment
        if(minutesBetweenTimes(LocalTime.now(), allAppointments.get(0).startTime) > request.duration){
            return NextOpenAppointmentMoment(request.duration, LocalTime.now(), LocalDate.now())
        }


        if(allAppointments.count() > 1){
            //If there is more than 1, check in between. if they are on different days, check right after the previous or before the next appointment if there is time
            for (i in 0 until allAppointments.count() - 1){
                val current = allAppointments.get(i)
                val next = allAppointments.get(i+1)

                if(current.date != next.date)
                {
                    if(current.endTime < request.maxStartTime) return NextOpenAppointmentMoment(request.duration, current.endTime.plusMinutes(1), current.date)
                    if(next.startTime > request.minStartTime.plusMinutes(request.duration.toLong())) return NextOpenAppointmentMoment(request.duration, request.minStartTime, next.date)
                }

                if(ChronoUnit.MINUTES.between(current.endTime.plusMinutes(1), next.startTime.minusMinutes(1)) >= request.duration){
                    return NextOpenAppointmentMoment(request.duration, current.endTime.plusMinutes(1), current.date)
                }
            }
        }

        //Checks the last appointment (might also be the first)
        val last = allAppointments.last()
        if(last.endTime.plusMinutes(1) < request.maxStartTime) return NextOpenAppointmentMoment(request.duration, last.endTime.plusMinutes(1), last.date)
        else return NextOpenAppointmentMoment(request.duration, request.minStartTime, last.date.plusDays(1))
    }

    fun minutesBetweenTimes(start: LocalTime, end: LocalTime) : Long
    {
        return ChronoUnit.MINUTES.between(start, end);
    }

    //@PostMapping()
    //Create new appointment
    @PostMapping()
    fun createNewAppointment (@RequestBody request: Appointment){
        request.id = null;
        var startTime = LocalTime.parse(request.startTime.toString())
        var endTime = LocalTime.parse(request.startTime.toString()).plusMinutes(request.duration.toLong())

        var list = repository.findOverlappingAppointments(LocalDate.parse(request.date.toString()), startTime, endTime)

        if(list?.count() != 0){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Time would overlap with another appointment")
        }
        else {
            request.endTime = endTime;
            repository.save(request)
        };
    }

    //@PutMapping()
    @PutMapping("/{id}")
    fun updateAppointment(@PathVariable("id") id: Long, @RequestBody request: Appointment){
        var existingAppointment = repository.findById(id).orElseThrow {  throw ResponseStatusException(HttpStatus.NOT_FOUND) }
        existingAppointment = request;
        repository.save(request);

    }
    @DeleteMapping("/{id}")
    fun deleteAppointment(@PathVariable("id") id: Long){
        repository.deleteById(id)
    }

}

data class NextOpenAppointmentMomentValues(
    var minStartTime: LocalTime = LocalTime.parse("00:00"),
    var maxStartTime: LocalTime = LocalTime.parse("23:59"),
    var duration: Int
)
data class NextOpenAppointmentMoment(
    var duration: Int,
    var startTime: LocalTime,
    var date : LocalDate,
    var endTime: LocalTime? = startTime.plusMinutes(duration.toLong())
)
