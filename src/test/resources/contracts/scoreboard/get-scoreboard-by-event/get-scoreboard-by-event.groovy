package scoreboard

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("get scoreboard by event")
    request {
        method'GET'
        url '/scoreboard-service/api/1/scoreboard-events?event=A vs B'
    }
    response {
        status 200
        body(file("get-scoreboard-by-event.response.json"))
        headers {
            contentType(applicationJson())
        }
    }
}