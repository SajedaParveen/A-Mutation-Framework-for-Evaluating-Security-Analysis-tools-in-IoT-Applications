
definition(
    name:"Undead Early Warning",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Undead Early Warning",
    category:"Safety&Security",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/SafetyAndSecurity/App-UndeadEarlyWarning.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/SafetyAndSecurity/App-UndeadEarlyWarning@2 x.png"
)

preferences{
    section("When the door opens..."){
        input"contacts","capability.contactSensor",multiple:true,title:"Where could they come from?"
        }
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
    section("Turn on the lights!"){
        input"switches","capability.switch",multiple:true
        }
}

def installed()
{
    subscribe(contacts,"contact.open",contactOpenHandler)
}

def updated()
{
    def message="${contacts}"
    message="Hello"
    sendSms(message,11111111111)
    unsubscribe()
    subscribe(contacts,"contact.open",contactOpenHandler)
}

def contactOpenHandler(evt){
    log.debug"$evt.value:$evt,$settings"
    log.trace"The Undead are coming!Turning on the lights:$switches"
    switches.on()
}

