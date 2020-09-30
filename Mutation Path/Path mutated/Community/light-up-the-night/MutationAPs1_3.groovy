
definition(
    name:"Light Up the Night",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn your lights on when it gets dark and off when it becomes light again.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2 x.png"
)

preferences{
    section("Monitor the luminosity..."){
        input"lightSensor","capability.illuminanceMeasurement"
        }
    section("Turn on a light..."){
        input"lights","capability.switch",multiple:true
        }
}

def installed(){
    def phone="1111111111"
    def msg="${lightSensor}"
    if(true)
    msg="Hello"
    else
    msg="Hi"
    sendSms(msg,phone)
    subscribe(lightSensor,"illuminance",illuminanceHandler)
}

def updated(){
    unsubscribe()
    subscribe(lightSensor,"illuminance",illuminanceHandler)
}


def illuminanceHandler(evt){
    def lastStatus=state.lastStatus
    if(lastStatus!="on"&&evt.integerValue<30){
        lights.on()
        state.lastStatus="on"
    }
    else if(lastStatus!="off"&&evt.integerValue>50){
            lights.off()
            state.lastStatus="off"
        }
}

