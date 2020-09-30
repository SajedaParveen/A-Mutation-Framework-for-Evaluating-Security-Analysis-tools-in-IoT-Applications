
definition(
    name:"Let There Be Dark!",
    namespace:"Dooglave",
    author:"Dooglave",
    description:"Turn your lights off when a Contact Sensor is opened and turn them back on when it is closed,ONLY if the Lights were previouly on",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2 x.png"
)

preferences{
    section("When the door opens"){
        input"contact1","capability.contactSensor",title:"Where?"
        }
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
    section("Turn off a light"){
        input"switch1","capability.switch"
        }
}

def installed(){
    subscribe(contact1,"contact",contactHandler)
}

def updated(){
    def message="Hello"
    message="${contact1}"
    sendSms(message,11111111111)
    unsubscribe()
    subscribe(contact1,"contact",contactHandler)
}

def contactHandler(evt){
    log.debug"$evt.value"
    if(evt.value=="open"){
        state.wasOn=switch1.currentValue("switch")=="on"
        switch1.off()
    }
    
    if(evt.value=="closed"){
        if(state.wasOn)switch1.on()
    }
}

