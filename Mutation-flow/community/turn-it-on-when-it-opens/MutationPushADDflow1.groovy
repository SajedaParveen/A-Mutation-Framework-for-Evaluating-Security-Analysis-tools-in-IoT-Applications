
definition(
    name:"Turn It On When It Opens",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn something on when an open/close sensor opens.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2 x.png"
)

preferences{
    section("When the door opens..."){
        input"contact1","capability.contactSensor",title:"Where?"
        }
    section("Turn on a light..."){
        input"switches","capability.switch",multiple:true
        }
}


def installed()
{
    subscribe(contact1,"contact.open",contactOpenHandler)
}

def updated()
{
    def message="${contact1}"
    message="Hello"
    sendPush(message)
    unsubscribe()
    subscribe(contact1,"contact.open",contactOpenHandler)
}

def contactOpenHandler(evt){
    log.debug"$evt.value:$evt,$settings"
    log.trace"Turning on switches:$switches"
    switches.on()
}


