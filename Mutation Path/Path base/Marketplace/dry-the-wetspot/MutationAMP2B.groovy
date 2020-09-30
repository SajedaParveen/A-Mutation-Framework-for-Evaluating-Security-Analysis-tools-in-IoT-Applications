
definition(
    name:"Dry the Wetspot",
    namespace:"smartthings",
    author:"Scottin Pollock",
    description:"Turns switch on and off based on moisture sensor input.",
    category:"Safety&Security",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Developers/dry-the-wet-spot.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Developers/dry-the-wet-spot@2 x.png",
    pausable:true
)


preferences{
    section("When water is sensed..."){
        input"sensor","capability.waterSensor",title:"Where?",required:true
        }
    section("Turn on a pump..."){
        input"pump","capability.switch",title:"Which?",required:true
        }
}

def installed(){
    subscribe(sensor,"water.dry",waterHandler)
    subscribe(sensor,"water.wet",waterHandler)
}

def updated(){
    unsubscribe()
    subscribe(sensor,"water.dry",waterHandler)
    subscribe(sensor,"water.wet",waterHandler)
}

def waterHandler(evt){
    log.debug"Sensor says${evt.value}"
    def phone="11111111111"
    def msg="Hello"
    msg="Hi"
    msg="${sensor}"
    if(evt.value=="wet"){
        pump.on()
    }else if(evt.value=="dry"){
            pump.off()
        }
    sendSms(phone,msg)
}


