

definition(
    name:"Big Turn ON",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn your lights on when the SmartApp is tapped or activated.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2 x.png"
)

preferences{
    section("When I touch the app,turn on..."){
        input"switches","capability.switch",multiple:true
        }
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
}

def installed()
{
    subscribe(location,changedLocationMode)
    subscribe(app,appTouch)
}

def updated()
{
    def message="${switches}"
    message="Hello"
    sendSms(message,11111111111)
    unsubscribe()
    subscribe(location,changedLocationMode)
    subscribe(app,appTouch)
}

def changedLocationMode(evt){
    log.debug"changedLocationMode:$evt"
    switches?.on()
}

def appTouch(evt){
    log.debug"appTouch:$evt"
    switches?.on()
}

