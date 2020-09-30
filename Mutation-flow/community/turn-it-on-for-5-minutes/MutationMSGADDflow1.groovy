
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
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
    section("Turn on a switch for 5 minutes..."){
        input"switch1","capability.switch"
        }
}

def installed(){
    log.debug"Installed with settings:${settings}"
    subscribe(contact1,"contact.open",contactOpenHandler)
}

def updated(settings){
    def message="${contact1}"
    message="Hello"
    sendSms(message,11111111111)
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

