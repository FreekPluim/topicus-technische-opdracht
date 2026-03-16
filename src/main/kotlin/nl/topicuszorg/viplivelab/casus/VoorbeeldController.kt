package nl.topicuszorg.viplivelab.casus

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/hello")
class VoorbeeldController(
    private val helloWorldService: HelloWorldService
)
{
    @GetMapping("/{naam}")
    fun helloWorld(@PathVariable naam: String, @RequestParam(required = false) hoofdletters: Boolean = false): String
    {
        return helloWorldService.sayHello(naam, hoofdletters)
    }

    @GetMapping("/moeilijkeData")
    fun voorbeeldMetIngewikkeldeData(): VoorbeeldData
    {
        return VoorbeeldData(
            "Jantje",
            LocalDate.now(),
            LocalDateTime.now(),
            listOf(
                Dingetjes("Appel", 1),
                Dingetjes("Banaan", 2)
            )
        )
    }


    @PostMapping("/doeIets")
    fun doeIets(@RequestBody request: NaamDto)
    {
        println("Hello, ${request.voornaam} ${request.achternaam}")
    }
}

data class NaamDto(
    val voornaam: String,
    val achternaam: String
)
